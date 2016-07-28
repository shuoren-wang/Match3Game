package game;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by shuorenwang on 2016-07-24.
 */
public class Entity{
    protected Game game;
    private int x;
    private int y;
    public static final int WIDTH=60,HEIGHT=60;
    protected Rectangle bounds;
    public BufferedImage texture=Assets.grass;
    protected int id;
    protected boolean up, down, left, right;


    public Entity(int x, int y, int id, Game game){
        this.game=game;
        this.x=x;
        this.y=y;
        this.id=id;
        Assets.init();
        setTexture(id);

        up =false;
        down =false;
        left =false;
        right =false;


        bounds=new Rectangle(0,0,WIDTH,HEIGHT);
    }

    private void getInput(){
        up =false;
        down =false;
        left =false;
        right =false;

        if(game.getMouseManager().left){
            left =true;
            return;
        }
        if(game.getMouseManager().right){
            right =true;
            return;
        }
        if(game.getMouseManager().up){
            up =true;
            return;
        }
        if(game.getMouseManager().down){
            down =true;
            return;
        }
    }

    public void tick(){
        getInput();
    }

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

    public int getId() {
        return id;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isUp() {
        return up;
    }

    /**

     * Set up false
     */
    public void setUpFalse() {  up = false;}

    public boolean isDown() {
        return down;
    }

    /**
     * set down false
     */
    public void setDownFalse() {
        down =false;
    }

    public boolean isLeft() {
        return left;
    }

    /**
     * set MoveLeft false
     */
    public void setLeftFalse() {
        left = false;
    }

    public boolean isRight() {
        return right;
    }

    /**
     * set right false
     */
    public void setRightFalse() {
        right =false;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        if (x != entity.x) return false;
        return y == entity.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
