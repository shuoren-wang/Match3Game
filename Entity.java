package game;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by shuorenwang on 2016-07-24.
 */
public class Entity{
    public static final int SPEED=2;
    public static final int EMPTY_TILE_ID=6;
    protected Game game;
    private int x;
    private int y;
    public static final int WIDTH=60,HEIGHT=60;
    public BufferedImage texture=Assets.sapphire;
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
    //pink 0, purple 1, sky 2,orange 3,sapphire 4, green 5
    public BufferedImage getTexture() {
       return texture;
    }

    /**
     * set texture according to id
     * @param id
     */
    private void setTexture(int id) {
        switch (id){
            case 0:
                texture= Assets.pink;
                return;
            case 1:
                texture= Assets.purple;
                return;
            case 2:
                texture= Assets.sky;
                return;
            case 3:
                texture= Assets.orange;
                return;
            case 4:
                texture= Assets.green;
                return;
            case 5:
                texture= Assets.sapphire;
                return;
            default:
                texture= Assets.blank;
                return;
        }

    }

    //GETTERS and SETTERS

    public int getId() {
        return id;
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

    public int decreaseY(){
        y=y+SPEED;
        return y;
    }

    public void setId(int id) {
        this.id = id;
        setTexture(id);
    }
}
