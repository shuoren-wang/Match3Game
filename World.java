package game;

import java.awt.*;
import java.util.ArrayList;


/**
 * Created by shuorenwang on 2016-07-24.
 */
public class World {
    private int width, height;  //number of tiles
    private int arrX,arrY;        //column, row
    private int mouseX, mouseY;
    private Game game;
    private int[][] boardIDs;
    private Entity[][] boardEntities;
    private Entity currentEntity;
    private Rectangle bounds;
    private ArrayList<Entity> neighborList;

    boolean pressedInBoundary = false;

    World(Game game, String path) {


        this.game = game;

        loadWorld(path);

        init();

        bounds = new Rectangle(0, 0, width * Entity.WIDTH, height * Entity.HEIGHT);

        neighborList = new ArrayList<>();
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

            if (currentEntity.isRight()){
                realXSwitch(Direction.RIGHT.getX(), Direction.RIGHT.getY());
                return;
            }
            if (currentEntity.isLeft()){
                realXSwitch(Direction.LEFT.getX(), Direction.LEFT.getY());
                return;
            }
            if (currentEntity.isUp()){
                realYSwitch(Direction.UP.getX(), Direction.UP.getY());
                return;
            }
            if (currentEntity.isDown()){
                realYSwitch(Direction.DOWN.getX(), Direction.DOWN.getY());
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
            System.out.println("switchEntity to right");
            swap(xChange, yChange);
            currentEntity.setRightFalse();
            currentEntity=null;
            return;
        }
        if (currentEntity.isLeft()) {
            swap(xChange, yChange);
            currentEntity.setLeftFalse();
            currentEntity=null;
            return;
        }
        if (currentEntity.isUp()) {
            swap(xChange, yChange);
            currentEntity.setUpFalse();
            currentEntity=null;
            return;
        }
        if (currentEntity.isDown()) {
            swap(xChange, yChange);
            currentEntity.setDownFalse();
            currentEntity=null;
            return;
        }

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
            boardEntities[x][y] = new Entity(x * Entity.WIDTH, y * Entity.HEIGHT, 4, game);
        }
        return boardEntities[x][y];
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
    private void realXSwitch(int xChange, int yChange) {
        if(currentEntity==null)
            return;

        if (canHorizontalSwitch(xChange, yChange)) {
            switchEntity(xChange, yChange);
        } else {
            currentEntity.setRightFalse();
            currentEntity.setLeftFalse();
        }
        neighborList.clear();
    }

    /**
     * switch currentEntity with rightEntity,if a texture match is formed b/w currentEntity and the rightEntity's the up and down entities
     * //switch first, if after switch, no match formed, switch back(haven't implemented)
     * empty neighborTextureIds list
     */
    private void realYSwitch(int xChange, int yChange) {
        if (canVerticalSwitch(xChange, yChange)) {
            switchEntity(xChange, yChange);
        } else {
            currentEntity.setDownFalse();
            currentEntity.setUpFalse();
        }

        neighborList.clear();

    }


    /**
     * if the right/left switch can form a match
     * if the size of neighbors >=2,return true
     *
     * @return true, if switch can form a match
     */
    private boolean canHorizontalSwitch(int xChange, int yChange) {

        if (!checkBoundary(currentEntity.getX() + xChange * Entity.WIDTH, currentEntity.getY() + yChange * Entity.HEIGHT))
            return false;

        if (getXMoveVerticalPatternNeighbors(currentEntity, boardEntities[getArrX() + xChange][getArrY() + yChange]).size() >= 2)
            return true;

        neighborList.clear();

        if (getXMoveVerticalPatternNeighbors(boardEntities[getArrX() + xChange][getArrY() + yChange], currentEntity).size() >= 2)
            return true;

        neighborList.clear();

        if (getXMoveHorizontalPatternNeighbors(currentEntity, boardEntities[getArrX() + xChange][getArrY() + yChange]).size() >= 2)
            return true;

        neighborList.clear();

        if (getXMoveHorizontalPatternNeighbors(boardEntities[getArrX() + xChange][getArrY() + yChange], currentEntity).size() >= 2)
            return true;

        return false;
    }


    /**
     * if the up/down switch can form a match
     * if the size of neighbors >=2,return true
     *
     * @return true, if switch can form a match
     */
    private boolean canVerticalSwitch(int xChange, int yChange) {

        if (!checkBoundary(currentEntity.getX() + xChange * Entity.WIDTH, currentEntity.getY() + yChange * Entity.HEIGHT))
            return false;

        if (getYMoveHorizontalPatternNeighbors(currentEntity, boardEntities[getArrX() + xChange][getArrY() + yChange]).size() >= 2)
            return true;

        neighborList.clear();

        if (getYMoveHorizontalPatternNeighbors(boardEntities[getArrX() + xChange][getArrY() + yChange], currentEntity).size() >= 2)
            return true;

        neighborList.clear();

        if (getYMoveVerticalPatternNeighbors(currentEntity, boardEntities[getArrX() + xChange][getArrY() + yChange]).size() >= 2)
            return true;

        neighborList.clear();

        if (getYMoveVerticalPatternNeighbors(boardEntities[getArrX() + xChange][getArrY() + yChange], currentEntity).size() >= 2)
            return true;

        return false;
    }

    /**
     * a list of entities align with currentEntity have the same pattern with rightEntity
     *
     * @return a list of entity that will form a match with rightEntity
     */

    ArrayList<Entity> getXMoveVerticalPatternNeighbors(Entity e1, Entity e2) {
        int x = e1.getX();
        int y = e1.getY();

        //check up
        for (int i = y / Entity.HEIGHT - 1; i >= 0; i--) {
            if (boardEntities[x / Entity.WIDTH][i].getId() == e2.getId())
                neighborList.add(boardEntities[x / Entity.WIDTH][i]);
            else
                break;
        }

        //check down
        for (int j = y / Entity.HEIGHT + 1; j < height; j++) {
            if (boardEntities[x / Entity.WIDTH][j].getId() == e2.getId())
                neighborList.add(boardEntities[x / Entity.WIDTH][j]);
            else
                break;
        }

        return neighborList;
    }


    /**
     * a list of entities align with rightEntity have the same pattern with currentEntity
     *
     * @return a list of entity that will form a match with currentEntity
     */
    ArrayList<Entity> getXMoveHorizontalPatternNeighbors(Entity e1, Entity e2) {
        int x = e1.getX();
        int y = e1.getY();

        //check right
        for (int j = x / Entity.WIDTH + 2; j < width; j++) {
            if (boardEntities[j][y / Entity.HEIGHT].getId() == e1.getId())
                neighborList.add(boardEntities[j][y / Entity.HEIGHT]);
            else
                break;
        }


        //if right does not have a match, empty neighborList
        if (neighborList.size() < 2) {
            neighborList.clear();
        } else {
            return neighborList;
        }


        //check left
        x = e2.getX();
        y = e2.getY(); //not change
        for (int j = x / Entity.WIDTH - 1; j >= 0; j--) {
            if (boardEntities[j][y / Entity.HEIGHT].getId() == e2.getId())
                neighborList.add(boardEntities[j][y / Entity.HEIGHT]);
            else
                break;
        }
        return neighborList;
    }


    /**
     * a list of entities align with currentEntity have the same pattern with rightEntity
     *
     * @return a list of entity that will form a match with rightEntity
     */

    ArrayList<Entity> getYMoveHorizontalPatternNeighbors(Entity e1, Entity e2) {
        int x = e1.getX();
        int y = e1.getY();

        //check left
        for (int i = x / Entity.WIDTH - 1; i >= 0; i--) {
            if (boardEntities[i][y / Entity.HEIGHT].getId() == e2.getId())
                neighborList.add(boardEntities[i][y / Entity.HEIGHT]);
            else
                break;
        }

        //check right
        for (int j = x / Entity.WIDTH + 1; j < width; j++) {
            if (boardEntities[j][y / Entity.HEIGHT].getId() == e2.getId())
                neighborList.add(boardEntities[j][y / Entity.HEIGHT]);
            else
                break;
        }

        return neighborList;
    }


    /**
     * a list of entities align with rightEntity have the same pattern with currentEntity
     *
     * @return a list of entity that will form a match with currentEntity
     */
    ArrayList<Entity> getYMoveVerticalPatternNeighbors(Entity e1, Entity e2) {
        int x = e1.getX();
        int y = e1.getY();

        //check down
        for (int j = y / Entity.HEIGHT + 2; j < height; j++) {
            if (boardEntities[x / Entity.WIDTH][j].getId() == e1.getId())
                neighborList.add(boardEntities[x / Entity.WIDTH][j]);
            else
                break;
        }


        //if down does not have a match, empty neighborList
        if (neighborList.size() < 2) {
            neighborList.clear();
        } else {
            return neighborList;
        }

        //check up
        x = e2.getX();
        y = e2.getY(); //not change
        for (int j = y / Entity.HEIGHT - 1; j >= 0; j--) {
            if (boardEntities[x / Entity.WIDTH][j].getId() == e2.getId())
                neighborList.add(boardEntities[x / Entity.WIDTH][j]);
            else
                break;
        }
        return neighborList;
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
}
