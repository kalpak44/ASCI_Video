package com.home.ascii;


import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Main {

    public static void main(String a[]) throws Exception {
        String filename = "/home/kalpak44/Documents/video.mp4";
        VideoDecoder v = new VideoDecoder(filename);

        // с y по z микросекунду
        long start = 1 * 1000 * 1000;
        long end = 18 * 1000 * 1000;
        // с разницей в x милисекунд
        long step = 40 * 1000;

        ArrayList<BufferedImage> biList = v.getFrames(start,end,step);


        for(BufferedImage bi : biList){

            System.out.print("\033[H\033[2J");
            System.out.flush();
            String s = ASCIIConverter.toAsciString(bi);
            System.out.print(s);
            System.out.print("\033[H\033[2J");
            System.out.flush();
            Thread.sleep(100);
        }
    }

}
