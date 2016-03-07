package com.home.ascii;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by kalpak44 on 08.03.16.
 */
public class ASCIIConverter {
    public static final String newline = System.getProperty("line.separator");
    private static BufferedImage image;

    public ASCIIConverter() {
    }

    private static char getAsciiPixel(int x, int y, int blockWidth, int blockHeight) {
        int intensity = 0;

        for (int i = 0; i < blockHeight; i++) {
            for (int j = 0; j < blockWidth; j++) {
                Color color = new Color(image.getRGB(x + j, y + i));
                intensity += (color.getRed() + color.getGreen() + color.getBlue()) / 3;
            }
        }

        intensity /= blockWidth * blockHeight;

        if (intensity > 240) {
            return ' ';
        }
        else if (intensity > 200 && intensity <= 240) {
            return '.';
        }
        else if (intensity > 160 && intensity <= 200) {
            return '*';
        }
        else if (intensity > 120 && intensity <= 160) {
            return 'x';
        }
        else if (intensity > 80 && intensity <= 120) {
            return '$';
        }
        else if (intensity > 40 && intensity <= 80) {
            return '#';
        }
        else {
            return '@';
        }
    }

    private static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    }

    public static String toAsciString(BufferedImage bImage) {
        if(bImage != null) {
            image = bImage;
            image = resize(image, 140, 80);
            int width = image.getWidth(), height = image.getHeight();
            int blockWidth = width / 80, blockHeight = height / 30;
            String art = "";

            for (int i = 0; i < height; i += blockHeight) {
                for (int j = 0; j < width; j += blockWidth) {
                    art += getAsciiPixel(j, i, j + blockWidth < width ? blockWidth : width - j, i + blockHeight < height ? blockHeight : height - i);
                }

                art += newline;
            }
            return art;
        }
        return "";

    }

}