
import java.awt.*;
import java.awt.image.BufferStrategy;

/**
 * Created by shuorenwang on 2016-07-24.
 */
public class Game implements Runnable{

    private Gui gui;
    private Thread th;

    private BufferStrategy bufferStrategy;
    private Graphics g;

    private World world;

    private MouseManager mouseManager;

    Game(){
        world=new World(this,"res/level1.txt");
        mouseManager=new MouseManager();
        init();


    }

    private void init(){
        gui=new Gui();
        gui.addMouseListener(mouseManager);
        gui.addMouseMotionListener(mouseManager);
        gui.getCanvas().addMouseListener(mouseManager);
        gui.getCanvas().addMouseMotionListener(mouseManager);

        Assets.init();

        th=new Thread(this);
        th.start();

    }


    public void tick(){
        world.tick();
    }

    public void render(){
        bufferStrategy=gui.getCanvas().getBufferStrategy();
        if(bufferStrategy==null){
            gui.getCanvas().createBufferStrategy(3);
            return;
        }

        g=bufferStrategy.getDrawGraphics();

        //Clear Screen
        g.clearRect(0,0,gui.getWidth(),gui.getHeight());

        //Draw Here


        world.render(g);

        //End Draw

        bufferStrategy.show();
        g.dispose();
    }
    @Override
    public void run() {
        while (true){
            tick();
            render();
        }
    }


    public MouseManager getMouseManager() {
        return mouseManager;
    }
}
