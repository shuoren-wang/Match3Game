package game;

import javax.swing.*;
import java.awt.*;

/**
 * Created by shuorenwang on 2016-07-24.
 */
public class Gui extends JFrame{
    public static int WIDTH=500,HEIGHT=400;

    private Canvas canvas;


    public Gui(){
        super("Test");

        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

        canvas=new Canvas();
        canvas.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        canvas.setMaximumSize(new Dimension(WIDTH,HEIGHT));
        canvas.setMinimumSize(new Dimension(WIDTH,HEIGHT));
        canvas.setFocusable(false);

        add(canvas);
        pack();
    }


    public Canvas getCanvas() {
        return canvas;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    public static int getWIDTH() {
        return WIDTH;
    }


}
