package com.home.ascii;

import com.xuggle.xuggler.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kalpak44 on 08.03.16.
 */
public class VideoDecoder {
    private String filename;
    private IContainer container;
    private int numStreams, videoStreamId = -1;
    private IStreamCoder videoCoder;
    public VideoDecoder(String filename){
        this.filename = filename;
        this.init();
        this.findVideoStreams();
        this.openDecoder();
    }

    private void init() {
        container = IContainer.make();
        if (container.open(filename, IContainer.Type.READ, null) < 0)
            throw new IllegalArgumentException("could not open file: "
                    + filename);
        this.numStreams = container.getNumStreams();
        this.videoStreamId = -1;
    }

    private int findVideoStreams(){
        for (int i = 0; i < numStreams; i++) {
            IStream stream = container.getStream(i);
            IStreamCoder coder = stream.getStreamCoder();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                videoStreamId = i;
                videoCoder = coder;
                break;
            }
        }
        return  videoStreamId;
    }
    private void openDecoder(){
        if (videoStreamId == -1)
            throw new RuntimeException("could not find video stream in container: "
                    + filename);

        // пытаемся открыть кодек
        if (videoCoder.open() < 0)
            throw new RuntimeException(
                    "could not open video decoder for container: " + filename);
    }
    public ArrayList<BufferedImage> getFrames(long start,long end, long step){
        IPacket packet = IPacket.make();
        ArrayList<BufferedImage> result = new ArrayList<>();

        END: while (container.readNextPacket(packet) >= 0) {
            if (packet.getStreamIndex() == videoStreamId) {
                IVideoPicture picture = IVideoPicture.make(
                        videoCoder.getPixelType(), videoCoder.getWidth(),
                        videoCoder.getHeight());
                int offset = 0;
                while (offset < packet.getSize()) {
                    int bytesDecoded = videoCoder.decodeVideo(picture, packet,
                            offset);
                    // if have any problems
                    if (bytesDecoded < 0)
                        throw new RuntimeException("got error decoding video in: "
                                + filename);
                    offset += bytesDecoded;

                    if (picture.isComplete()) {
                        IVideoPicture newPic = picture;
                        //in ms
                        long timestamp = picture.getTimeStamp();
                        if (timestamp > start) {
                            // get standart BufferedImage
                            BufferedImage javaImage = Utils
                                    .videoPictureToImage(newPic);
                            result.add(javaImage);
                            start += step;
                        }
                        if (timestamp > end) {
                            break END;
                        }
                    }
                }
            }
        }
        if (videoCoder != null) {
            videoCoder.close();
            videoCoder = null;
        }
        if (container != null) {
            container.close();
            container = null;
        }
        return result.isEmpty()?null:result;
    }
}
