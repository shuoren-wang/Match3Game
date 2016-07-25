import java.awt.*;


/**
 * Created by shuorenwang on 2016-07-24.
 */
public class World {
    Game game;
    public static int WIDTH=60,HEIGHT=60;
    public static int X=100, Y=100;
    private Rectangle A;
    private Rectangle B;
    private int aX,aY;
    private int bX,bY;
    private boolean aPressed;
    private Color aColor=Color.blue;
    private Color bColor=Color.red;

    World(Game game){
        this.game=game;

        aX=X;
        aY=Y;

        bX=X+WIDTH;
        bY=Y;

        A=new Rectangle(aX,aY,WIDTH,HEIGHT);
        B=new Rectangle(bX,bY,WIDTH,HEIGHT);
    }


    /**
     * if A is selected and mouse move to the direction of B
     * change A,B coordinate
     */
    public void tick(){

        if(game.getMouseManager().isPressed()==true &&
            A.contains(game.getMouseManager().getMouseX(),game.getMouseManager().getMouseY()))//A is selected
        {
            aPressed=true;
        }
        if(  aPressed==true && game.getMouseManager().left)     //A is move to the left
        {
            System.out.println("left==true");
            swap();
            aPressed=false;
        }
    }

    private void swap() {
        A.x=bX;
        B.x=aX;
    }

    public void render(Graphics g){
        g.setColor(aColor);
        g.fillRect(A.x,A.y,WIDTH,HEIGHT);


        g.setColor(bColor);
        g.fillRect(B.x,B.y,WIDTH,HEIGHT);


        g.fillRect(game.getMouseManager().getMouseX(),
                game.getMouseManager().getMouseY(),
                10,10);


    }
}
