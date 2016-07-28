package game;

import java.awt.*;
import java.util.ArrayList;


/**
 * Created by shuorenwang on 2016-07-24.
 */
public class World {
    private int width, height;  //number of tiles
    private int mouseX, mouseY;
    private Game game;
    private int[][] boardIDs;
    private Entity[][] boardEntities;
    private Entity currentEntity;
    private Entity switchedEntity;
    private Rectangle bounds;
    private ArrayList<Entity> horizontalNeighborList;
    private ArrayList<Entity> verticalNeighborList;

    boolean pressedInBoundary = false;

    World(Game game, String path) {

        this.game = game;

        loadWorld(path);

        init();

        bounds = new Rectangle(0, 0, width * Entity.WIDTH, height * Entity.HEIGHT);

        verticalNeighborList = new ArrayList<>();
        horizontalNeighborList=new ArrayList<>();
    }

    /**
     * initialize boardEntities[][] according to boardIDs[][]
     */
    private void init() {
        boardEntities = new Entity[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boardEntities[x][y] = new Entity(x * Entity.WIDTH, y * Entity.HEIGHT, boardIDs[x][y], game);
            }
        }
    }

    /**
     * set currentEntity and switch entity according to mouse input
     */
    public void tick() {

        setCurrentEntity();

        if (currentEntity != null) {

            currentEntity.tick();

            if (currentEntity.isRight() && checkBoundary(currentEntity.getX()+Entity.WIDTH,currentEntity.getY())){

                setSwitchedEntity(Direction.RIGHT);
                trySwitch(Direction.RIGHT);
                return;
            }
            if (currentEntity.isLeft()&& checkBoundary(currentEntity.getX()-Entity.WIDTH,currentEntity.getY())){
                setSwitchedEntity(Direction.LEFT);
                trySwitch(Direction.LEFT);
                return;
            }
            if (currentEntity.isUp() && checkBoundary(currentEntity.getX(),currentEntity.getY()-Entity.HEIGHT)){
                setSwitchedEntity(Direction.UP);
                trySwitch(Direction.UP);
                return;
            }
            if (currentEntity.isDown() && checkBoundary(currentEntity.getX(),currentEntity.getY()+Entity.HEIGHT)){
                setSwitchedEntity(Direction.DOWN);
                trySwitch(Direction.DOWN);
                return;
            }
        }

    }

    /**
     * if mouse clicked within the game boundary, set current entity to be the clicked entity
     */
    public void setCurrentEntity() {
        mouseX = game.getMouseManager().getMouseX();
        mouseY = game.getMouseManager().getMouseY();

        if (game.getMouseManager().isPressed() && checkBoundary(mouseX, mouseY)) {
            pressedInBoundary = true;

            currentEntity = boardEntities[mouseX / Entity.WIDTH][mouseY / Entity.HEIGHT];
        }

        game.getMouseManager().setPressed(false);
    }

    /**
     * switch current entity with the adjacent entity according to mouse move direction
     */
    private void switchEntity(int xChange, int yChange) {
        if (currentEntity.isRight()) {
            swap(xChange, yChange);
            currentEntity.setRightFalse();
        }else if (currentEntity.isLeft()) {
            swap(xChange, yChange);
            currentEntity.setLeftFalse();
        }else if (currentEntity.isUp()) {
            swap(xChange, yChange);
            currentEntity.setUpFalse();
        }else if (currentEntity.isDown()) {
            swap(xChange, yChange);
            currentEntity.setDownFalse();
        }
        currentEntity=null;
    }

    /**
     * if currentEntity does not exist or currentEntity is the most left entity, return;
     * otherwise, exchange the position of currentEntity with its left:
     * switch x coordinate
     * switch boardEntities[][] position
     * switch boardIDs (don't have to)
     *
     * @param xChange boardEntities[][] position change :1=right,-1=left,
     * @param yChange boardEntities[][] position change :1=down,-1=up,
     */
    private void swap(int xChange, int yChange) {
        if (currentEntity == null)
            return;

        if (!checkBoundary(currentEntity.getX() + xChange * Entity.WIDTH, currentEntity.getY() + yChange * Entity.HEIGHT))
            return;


        Entity tempCurrentEntity = new Entity(currentEntity.getX(), currentEntity.getY(), currentEntity.getId(), game);
        Entity exchangeEntity = boardEntities[getArrX() + xChange][getArrY() + yChange];

        //switch x coordinate
        tempCurrentEntity.setX(exchangeEntity.getX());
        tempCurrentEntity.setY(exchangeEntity.getY());

        exchangeEntity.setX(currentEntity.getX());
        exchangeEntity.setY(currentEntity.getY());

        //switch boardEntities[][] position
        boardEntities[getArrX()][getArrY()] = exchangeEntity;
        boardEntities[getArrX() + xChange][getArrY() + yChange] = tempCurrentEntity;

    }


    public void render(Graphics g) {
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                getEntity(x, y).render(g);
            }

        if (currentEntity != null) {
            g.setColor(new Color(1f, 0f, 0f, .2f)); //last number to set transparency
            g.fillRect(currentEntity.getX(), currentEntity.getY(), Entity.WIDTH, Entity.HEIGHT);
        }

        g.setColor(Color.cyan);
        g.fillOval(mouseX, mouseY, 10, 10);

    }


    /**
     * check if mouse move within game boundary (0~width*Entity.WIDTH,0~Entity.HEIGHT)
     *
     * @return true if in the boundary
     */
    private boolean checkBoundary(int x, int y) {
        return bounds.contains(x, y);
    }


    /**
     * switch currentEntity with rightEntity,if a texture match is formed b/w currentEntity and the rightEntity's the up and down entities
     * //switch first, if after switch, no match formed, switch back(haven't implemented)
     * empty neighborTextureIds list
     */
    private void trySwitch(Direction dir) {
        if(currentEntity==null)
            return;

        if (canSwitch()) {
            switchEntity(dir.getX(), dir.getY());
        } else {
            currentEntity.setRightFalse();
            currentEntity.setLeftFalse();
            currentEntity.setUpFalse();
            currentEntity.setDownFalse();
        }
        verticalNeighborList.clear();//////////////////////
        horizontalNeighborList.clear();////////////////////
    }



    /**
     * if the right/left/up/down switch can form a match
     * if the size of neighbors >=2,return true
     * clear neighbor list
     * @return true, if switch can form a match
     */
    private boolean canSwitch() {

        if (!checkBoundary(switchedEntity.getX(),switchedEntity.getY()))
            return false;

        if (enoughNeighbors(currentEntity,switchedEntity))
            return true;

        if (enoughNeighbors(switchedEntity,currentEntity))
            return true;

        return false;

    }


    /**
     *
     * @return true if there is a match after switch e1,e2
     */
    private boolean enoughNeighbors(Entity e1,Entity e2){

        if (getVerticalNeighbors(e1,e2).size()>=2){
            return true;
        }

        if(getHorizontalNeighbors(e1,e2).size()>=2){
            return true;
        }

        return false;


    }
    /**
     * check same pattern entity about the neibor of e
     * @return list of entities that will form a match vertically
     */

    ArrayList<Entity> getVerticalNeighbors(Entity e1,Entity e2) {
        int x = e1.getX();
        int y = Math.min(e1.getY(),e2.getY());

        //check up
        Entity e=e1.getX()>e2.getX()? e1:e2;

        for (int i = y / Entity.HEIGHT - 1; i >= 0; i--) {
            if (boardEntities[x / Entity.WIDTH][i].getId() == e.getId())
                verticalNeighborList.add(boardEntities[x / Entity.WIDTH][i]);
            else
                break;
        }

        //check down
        y = Math.max(e1.getY(),e2.getY());
        e=e1.getY()<e2.getY()? e1:e2;

        for (int j = y / Entity.HEIGHT + 1; j < height; j++) {
            if (boardEntities[x / Entity.WIDTH][j].getId() == e.getId())
                verticalNeighborList.add(boardEntities[x / Entity.WIDTH][j]);
            else
                break;
        }

        return verticalNeighborList;
    }


    ArrayList<Entity> getHorizontalNeighbors(Entity e1, Entity e2) {
        int x = Math.min(e1.getX(),e2.getX());
        int y = e1.getY();

        //check left
        Entity e=e1.getX()>e2.getX()? e1:e2;
        for (int i = x / Entity.WIDTH - 1; i >= 0; i--) {
            if (boardEntities[i][y / Entity.HEIGHT].getId() == e.getId())
                horizontalNeighborList.add(boardEntities[i][y / Entity.HEIGHT]);
            else
                break;
        }

        //check right
        x = Math.max(e1.getX(),e2.getX());
        e=e1.getX()<e2.getX()? e1:e2;

        for (int j = x / Entity.WIDTH + 1; j < width; j++) {
            if (boardEntities[j][y / Entity.HEIGHT].getId() == e.getId())
                horizontalNeighborList.add(boardEntities[j][y / Entity.HEIGHT]);
            else
                break;
        }

        return horizontalNeighborList;
    }

    /**
     * set switchedEntity
     * @param dir currentEntity move direction
     */
    public void setSwitchedEntity(Direction dir) {
        switchedEntity=boardEntities[getArrX()+dir.getX()][getArrY()+dir.getY()];
    }

    /**
     * @return currentEntity array column #
     */
    public int getArrX() {
        return currentEntity.getX()/Entity.WIDTH;
    }

    /**
     * @return currentEntity array row #
     */
    public int getArrY() {
        return currentEntity.getY()/Entity.HEIGHT;
    }


    /**
     * load double array of entity texture id from local
     * @param path
     */
    private void loadWorld(String path) {
        String file = Utils.loadFileAsString(path);
        String[] tokens = file.split("\\s+");

        width = Utils.parseInt(tokens[0]);
        height = Utils.parseInt(tokens[1]);
//        x_start=Utils.parseInt(tokens[2]);
//        y_start=Utils.parseInt(tokens[3]);

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
            boardEntities[x][y] = new Entity(x * Entity.WIDTH, y * Entity.HEIGHT, 4, game);
        }
        return boardEntities[x][y];
    }
}
