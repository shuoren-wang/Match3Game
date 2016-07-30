package game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by shuorenwang on 2016-07-20.
 */
public class Assets {
    private static final int width=100, height=100; // real width/height in pixel for each image
    public static BufferedImage spriteSheet, purple, sapphire, orange, pink, green, sky,blank;

    public static void init(){

        spriteSheet=loadImage("res/flowers.png");

        purple =spriteSheet.getSubimage(0,0,width,height);
        pink =spriteSheet.getSubimage(width,0, width,height);
        green =spriteSheet.getSubimage(2 * width,0,width,height);
        sapphire =spriteSheet.getSubimage(3 * width,0,width,height);
        orange =spriteSheet.getSubimage(4*width,0,width,height);
        sky =spriteSheet.getSubimage(0,height,width,height);
        blank=spriteSheet.getSubimage(width,height,width,height);
    }

    /**
     * load image from local
     * @param path
     */
    private  static BufferedImage loadImage(String path){
        try{
            return ImageIO.read(new File(path));
        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }


}