
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


/**
 * Created by shuorenwang on 2016-07-24.
 */
public class World {
    private int width, height;  //number of tiles
    private int mouseX,mouseY;
    private Game game;
    private int[][] boardIDs;
    private Entity[][] boardEntities;
    private Entity currentEntity;
    private Rectangle bounds;
    private ArrayList<Entity> adjEntityNeighborList;
    private ArrayList<Entity> currentEntityNeighborList;
    private ArrayList<Entity> neighborList3;

    boolean pressedInBoundary=false;

    World(Game game,String path){


        this.game=game;

        loadWorld(path);

        init();

        bounds=new Rectangle(0,0,width*Entity.WIDTH,height*Entity.HEIGHT);

        adjEntityNeighborList=new ArrayList<>();
        currentEntityNeighborList=new ArrayList<>();
        neighborList3=new ArrayList<>();
    }

    /**
     * initialize boardEntities[][] according to boardIDs[][]
     */
    private void init(){
        boardEntities = new Entity[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boardEntities[x][y] = new Entity(x * Entity.WIDTH, y * Entity.HEIGHT, boardIDs[x][y],game);
            }
        }
    }

    /**
     * if mouse clicked within the game boundary,
     * set current entity to be the clicked entity
     * if mouse move to certain direction, switch the switch current entity with the adjacent entity with in boundary
     */
    public void tick(){
        mouseX = game.getMouseManager().getMouseX();
        mouseY = game.getMouseManager().getMouseY();

        if(game.getMouseManager().isPressed() && checkBoundary(mouseX,mouseY)){
            pressedInBoundary=true;

            currentEntity = boardEntities[mouseX / Entity.WIDTH][mouseY / Entity.HEIGHT];
            game.getMouseManager().setPressed(false);

        }

      //  switchEntity();
        if(currentEntity!=null && game.getMouseManager().right==true)
            realSwitchToRight();
    }

    /**
     * switch current entity with the adjacent entity according to mouse move direction
     */
    private void switchEntity(){
        if(game.getMouseManager().right==true){
            swap(1,0);
            game.getMouseManager().right=false;
        }
        if(game.getMouseManager().left==true){
            swap(-1,0);
            game.getMouseManager().left=false;
        }
        if(game.getMouseManager().up==true){
            swap(0,-1);
            game.getMouseManager().up=false;
        }
        if(game.getMouseManager().down==true){
            swap(0,1);
            game.getMouseManager().down=false;
        }

    }

    /**
     * if currentEntity does not exist or currentEntity is the most left entity, return;
     * otherwise, exchange the position of currentEntity with its left:
     * switch x coordinate
     * switch boardEntities[][] position
     * switch boardIDs (don't have to)
     * @param xChange  boardEntities[][] position change :1=right,-1=left,
     * @param yChange boardEntities[][] position change :1=down,-1=up,
     */
    private void swap(int xChange,int yChange) {
        if(currentEntity==null)
            return;

        if(!checkBoundary(currentEntity.getX()+xChange*Entity.WIDTH,currentEntity.getY()+yChange*Entity.HEIGHT))
            return;

        int x=currentEntity.getX();
        int y=currentEntity.getY();

        Entity tempCurrentEntity=new Entity(x, y, currentEntity.getId(),game);
        Entity exchangeEntity=boardEntities[x/Entity.WIDTH +xChange][y/Entity.HEIGHT+yChange];

        //switch x coordinate
        tempCurrentEntity.setX(exchangeEntity.getX());
        tempCurrentEntity.setY(exchangeEntity.getY());

        exchangeEntity.setX(x);
        exchangeEntity.setY(y);

        //switch boardEntities[][] position
        boardEntities[x/Entity.WIDTH ][y/Entity.HEIGHT]=exchangeEntity;
        boardEntities[x/Entity.WIDTH +xChange][y/Entity.HEIGHT+yChange]=tempCurrentEntity;
    }


    public void render(Graphics g){
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                getEntity(x,y).render(g);
            }
        if(currentEntity!=null){
            g.setColor(new Color(1f,0f,0f,.2f )); //last number to set transparency
            g.fillRect(currentEntity.getX(),currentEntity.getY(),Entity.WIDTH,Entity.HEIGHT);
        }

        g.setColor(Color.cyan);
        g.fillRect(mouseX,mouseY,10,10);


    }

    private void loadWorld(String path) {
        String file = Utils.loadFileAsString(path);
        String[] tokens = file.split("\\s+");

        width = Utils.parseInt(tokens[0]);
        height = Utils.parseInt(tokens[1]);
//        x=Utils.parseInt(tokens[2]);
//        y=Utils.parseInt(tokens[3]);

        boardIDs = new int[width][height];
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                boardIDs[x][y] = Utils.parseInt(tokens[(x + y * width) + 4]);
            }
    }

    /**
     * if (x,y) is outside the map, return grass,
     * otherwise, return the corresponding entity
     *
     * @param x index of tile on the x direction (column)
     * @param y index of tile on the y direction (row)
     * @return entity
     */
    public Entity getEntity(int x, int y) {
        if (boardEntities[x][y] == null) {
            boardEntities[x][y]= new Entity(x * Entity.WIDTH, y * Entity.HEIGHT, 4,game);
        }
        return boardEntities[x][y];
    }

    /**
     * check if mouse move within game boundary (0~width*Entity.WIDTH,0~Entity.HEIGHT)
     * @return true if in the boundary
     */
    private boolean checkBoundary(int x,int y){
        return bounds.contains(x,y);
    }

    /**
     * switch currentEntity with rightEntity,if a texture match is formed b/w currentEntity and the rightEntity's the up and down entities
     //switch first, if after switch, no match formed, switch back(haven't implemented)
     * empty neighborTextureIds list
     */
    private void realSwitchToRight(){
            if (canSwitch()){
                switchEntity();
            }else{
                game.getMouseManager().right=false;
            }
        adjEntityNeighborList.clear();
        currentEntityNeighborList.clear();
        neighborList3.clear();

    }


    /**
     * if the right switch can form a match
     * if the size of neighbors >=3,return ture
     * @return  true, if switch can form a match
     */
    private boolean canSwitch(){
        if(!checkBoundary(currentEntity.getX()+Entity.WIDTH,currentEntity.getY()))
            return false;
        return getMoveRightPatternNeighbors_2().size()>=2 || getMoveRightPatternNeighbors_1().size()>=2 || getMoveRightPatternNeighbors_3().size()>=2;
    }

    /**
     * a list of entities align with currentEntity have the same pattern with rightEntity
     * @return a list of entity that will form a match with rightEntity
     */

    ArrayList<Entity> getMoveRightPatternNeighbors_2(){
        int x=currentEntity.getX();
        int y=currentEntity.getY();

        Entity rightEntity=boardEntities[x/Entity.WIDTH +1][y/Entity.HEIGHT];

        //check up
        for(int i=y/Entity.HEIGHT-1;i>=0;i--){
            if(boardEntities[x/Entity.WIDTH][i].getId()==rightEntity.getId())
                currentEntityNeighborList.add(boardEntities[x/Entity.WIDTH][i]);
            else
                break;
        }

        //check down
        for(int j=y/Entity.HEIGHT+1;j<height;j++){
            if(boardEntities[x/Entity.WIDTH][j].getId()==rightEntity.getId())
                currentEntityNeighborList.add(boardEntities[x/Entity.WIDTH][j]);
            else
                break;
        }

        return currentEntityNeighborList;
    }

    /**
     * a list of entities align with rightEntity have the same pattern with currentEntity
     * @return a list of entity that will form a match with currentEntity
     */
    ArrayList<Entity> getMoveRightPatternNeighbors_1(){
        int x=currentEntity.getX();
        int y=currentEntity.getY();

        //check up
        for(int i=y/Entity.HEIGHT-1;i>=0;i--){
            if(boardEntities[x/Entity.WIDTH +1][i].getId()==currentEntity.getId())
                adjEntityNeighborList.add(boardEntities[x/Entity.WIDTH +1][i]);
            else
                break;
        }

        //check down
        for(int j=y/Entity.HEIGHT+1;j<height;j++){
            if(boardEntities[x/Entity.WIDTH +1][j].getId()==currentEntity.getId())
                adjEntityNeighborList.add(boardEntities[x/Entity.WIDTH +1][j]);
            else
                break;
        }

        return adjEntityNeighborList;
    }


    /**
     * a list of entities align with rightEntity have the same pattern with currentEntity
     * @return a list of entity that will form a match with currentEntity
     */
    ArrayList<Entity> getMoveRightPatternNeighbors_3(){
        int x=currentEntity.getX();
        int y=currentEntity.getY();

        //check right
        for(int j=x/Entity.WIDTH+2;j<width;j++){
            if(boardEntities[j][y/Entity.HEIGHT].getId()==currentEntity.getId())
                neighborList3.add(boardEntities[j][y/Entity.HEIGHT]);
            else
                break;
        }

        return neighborList3;
    }
}
