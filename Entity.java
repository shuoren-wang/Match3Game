
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by shuorenwang on 2016-07-24.
 */
public class Entity{
    protected Game game;
    protected int x;
    protected int y;
    public static final int WIDTH=60,HEIGHT=60;
    protected Rectangle bounds;
    public BufferedImage texture=Assets.grass;
    protected int id;
    private boolean pressed;
    protected boolean moveUp, moveDown,moveLeft,moveRight;


    public Entity(int x, int y, int id, Game game){
        this.game=game;
        this.x=x;
        this.y=y;
        this.id=id;
        Assets.init();
        setTexture(id);

        pressed=false;
        moveUp=false;
        moveDown=false;
        moveLeft=false;
        moveRight=false;


        bounds=new Rectangle(0,0,WIDTH,HEIGHT);
    }

    private void getInput(){
        pressed=false;
        moveUp=false;
        moveDown=false;
        moveLeft=false;
        moveRight=false;

        if(game.getMouseManager().isPressed())
            pressed=true;
        if(game.getMouseManager().left)
            moveLeft=true;
        if(game.getMouseManager().right)
            moveRight=true;
        if(game.getMouseManager().up)
            moveUp=true;
        if(game.getMouseManager().down)
            moveDown=true;

    }

    public void tick(){}

    public void render(Graphics g){
        g.drawImage(texture,x,y,WIDTH,HEIGHT,null);
    }


    //GETTERS and SETTERS
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }


    //GETTER and SETTER
    //tree 0, dirt 1, lizard 2,wall 3,grass 4, stone 5
    public BufferedImage getTexture() {
        switch (id){
            case 0:
                return Assets.tree;
            case 1:
                return Assets.dirt;
            case 2:
                return Assets.lizard;
            case 3:
                return Assets.wall;
            case 4:
                return Assets.grass;
            case 5:
                return Assets.stone;
            default:
                return Assets.grass;
        }

    }

    /**
     * set texture according to id
     * @param id
     */
    private void setTexture(int id) {
        switch (id){
            case 0:
                texture= Assets.tree;
                return;
            case 1:
                texture= Assets.dirt;
                return;
            case 2:
                texture= Assets.lizard;
                return;
            case 3:
                texture= Assets.wall;
                return;
            case 4:
                texture= Assets.grass;
                return;
            case 5:
                texture= Assets.stone;
                return;
            default:
                texture= Assets.grass;
                return;
        }

    }

    //GETTERS and SETTERS
    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }

    public int getId() {
        return id;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public boolean isMoveUp() {
        return moveUp;
    }

    public void setMoveUp(boolean moveUp) {
        this.moveUp = moveUp;
    }

    public boolean isMoveDown() {
        return moveDown;
    }

    public void setMoveDown(boolean moveDown) {
        this.moveDown = moveDown;
    }

    public boolean isMoveLeft() {
        return moveLeft;
    }

    public void setMoveLeft(boolean moveLeft) {
        this.moveLeft = moveLeft;
    }

    public boolean isMoveRight() {
        return moveRight;
    }

    public void setMoveRight(boolean moveRight) {
        this.moveRight = moveRight;
    }
}
