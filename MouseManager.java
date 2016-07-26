import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Created by shuorenwang on 2016-07-24.
 */
public class MouseManager implements MouseListener,MouseMotionListener {
    private boolean pressed;

    private int x, y;
    private int preX;
    private int preY;

    public boolean up,down,left,right;
    private final int LIMIT=3;   //smallest movement is 3

    MouseManager(){
        x=0;
        y=0;
        preX=0;
        preY=0;

        up=false;
        down=false;
        left=false;
        right=false;
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        //MouseEvent.BUTTON1 is left mouse button
        if(e.getButton()==MouseEvent.BUTTON1){
            pressed=true;

            up=false;
            down=false;
            left=false;
            right=false;

            preX=e.getX();
            preY=e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton()==MouseEvent.BUTTON1){
            pressed=false;

            System.out.println("Press is false");

            x=e.getX();
            y=e.getY();

            if(x-preX>LIMIT){
                left=true;
            }

        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x =e.getX();
        y =e.getY();
    }

    //GETTERS

    public int getMouseX() {
        return x;
    }

    public int getMouseY() {
        return y;
    }

    public boolean isPressed() {
        return pressed;
    }

}
