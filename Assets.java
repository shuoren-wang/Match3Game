import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by shuorenwang on 2016-07-20.
 */
public class Assets {
    private static final int width=108, height=108; // real width/height in pixel for each image
    public static BufferedImage spriteSheet, dirt, grass, wall, tree, stone, lizard;

    public static void init(){

        spriteSheet=loadImage("res/tile.png");

        dirt=spriteSheet.getSubimage(0,0,width,height);
        tree=spriteSheet.getSubimage(width,0, width,height);
        stone=spriteSheet.getSubimage(2 * width,0,width,height);
        grass=spriteSheet.getSubimage(3 * width,0,width,height);
        wall=spriteSheet.getSubimage(0,2*height,width,height);
        lizard=spriteSheet.getSubimage(0,height,width,height);
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