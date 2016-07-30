package game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


/**
 * Created by shuorenwang on 2016-07-24.
 */
public class World {
    private int width, height;  //number of tiles
    private int mouseX, mouseY;
    private Game game;
    private int score;
    private int[][] boardIDs;
    private Entity[][] boardEntities;
    private Entity currentEntity;
    private Entity switchedEntity;
    private Rectangle bounds;
    private ArrayList<Entity> horizontalNeighborList1;
    private ArrayList<Entity> horizontalNeighborList2;
    private ArrayList<Entity> verticalNeighborList1;
    private ArrayList<Entity> verticalNeighborList2;

    private ArrayList<Entity> fillEmptyList;


    private static final int MAX_DETECT_TIME = 500;
    private int emptyTime = 0;
    private int detectTime = 0;
    private int clearTime = 0;


    boolean pressedInBoundary = false;

    World(Game game, String path) {

        this.game = game;
        score = 0;

        loadWorld(path);

        init();

        bounds = new Rectangle(0, 0, width * Entity.WIDTH, height * Entity.HEIGHT);

        verticalNeighborList1 = new ArrayList<>();
        verticalNeighborList2 = new ArrayList<>();
        horizontalNeighborList1 = new ArrayList<>();
        horizontalNeighborList2 = new ArrayList<>();
        fillEmptyList = new ArrayList<>();

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

            if (currentEntity.isDown() || currentEntity.isLeft() || currentEntity.isRight() || currentEntity.isUp()) {
                if (currentEntity.isRight() && checkBoundary(currentEntity.getX() + Entity.WIDTH, currentEntity.getY())) {
                    setSwitchedEntity(Direction.RIGHT);
                    trySwitch(Direction.RIGHT);

                } else if (currentEntity.isLeft() && checkBoundary(currentEntity.getX() - Entity.WIDTH, currentEntity.getY())) {
                    setSwitchedEntity(Direction.LEFT);
                    trySwitch(Direction.LEFT);

                } else if (currentEntity.isUp() && checkBoundary(currentEntity.getX(), currentEntity.getY() - Entity.HEIGHT)) {
                    setSwitchedEntity(Direction.UP);
                    trySwitch(Direction.UP);

                } else if (currentEntity.isDown() && checkBoundary(currentEntity.getX(), currentEntity.getY() + Entity.HEIGHT)) {
                    setSwitchedEntity(Direction.DOWN);
                    trySwitch(Direction.DOWN);
                }

                delMatchedLists();

                currentEntity = null;
                switchedEntity = null;

            }

        }else{

            if (clearTime < MAX_DETECT_TIME) {
                clearTime++;
                return;
            } else {
                clearTime = 0;
            }

            dropTiles();



        }
    }


    /**
     * delete tiles in all the neighbor lists
     */
    private void delMatchedLists() {
        delMatchedList(verticalNeighborList1);
        delMatchedList(verticalNeighborList2);
        delMatchedList(horizontalNeighborList1);
        delMatchedList(horizontalNeighborList2);


    }

    /**
     * delete matched tiles in the neighborList
     *
     * @param neighborList
     */
    private void delMatchedList(ArrayList<Entity> neighborList) {
        if (neighborList.size() >= 2) {
            if (currentEntity != null && neighborList.get(0).getId() == currentEntity.getId())
                boardEntities[currentEntity.getX() / Entity.WIDTH][currentEntity.getY() / Entity.HEIGHT] = null;

            else if (switchedEntity != null && neighborList.get(0).getId() == switchedEntity.getId())
                boardEntities[switchedEntity.getX() / Entity.WIDTH][switchedEntity.getY() / Entity.HEIGHT] = null;


            for (int i = 0; i < neighborList.size(); i++) {
                int x = neighborList.get(i).getX() / Entity.WIDTH;
                int y = neighborList.get(i).getY() / Entity.HEIGHT;
                boardEntities[x][y] = null;
            }

            neighborList.clear();
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
        } else if (currentEntity.isLeft()) {
            swap(xChange, yChange);
            currentEntity.setLeftFalse();
        } else if (currentEntity.isUp()) {
            swap(xChange, yChange);
            currentEntity.setUpFalse();
        } else if (currentEntity.isDown()) {
            swap(xChange, yChange);
            currentEntity.setDownFalse();
        }

    }

    /**
     * if currentEntity does not exist or currentEntity is the most left entity, return;
     * otherwise, exchange the position of currentEntity with its left:
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


        //update currentEntity and switchedEntity
        currentEntity = exchangeEntity;
        switchedEntity = tempCurrentEntity;
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
        if (currentEntity == null)
            return;

        if (canSwitch()) {
            score++;
            System.out.println("Current score is " + score);
            switchEntity(dir.getX(), dir.getY());
        } else {
            currentEntity.setRightFalse();
            currentEntity.setLeftFalse();
            currentEntity.setUpFalse();
            currentEntity.setDownFalse();
        }

    }


    /**
     * if the right/left/up/down switch can form a match
     * if the size of neighbors >=3 (including the currentEntity or switchedEntity),return true
     * set tileDeleted = true
     *
     * @return true, if switch can form a match
     */
    private boolean canSwitch() {

        if (!checkBoundary(switchedEntity.getX(), switchedEntity.getY()))
            return false;

        setNeighborLists();

        if (horizontalNeighborList1.size() >= 2 || verticalNeighborList1.size() >= 2 ||
                horizontalNeighborList2.size() >= 2 || verticalNeighborList2.size() >= 2) {
            return true;
        }
        return false;

    }


    /**
     * clear all neighborList whose size < 2
     * add matched entity(currentEntity or switchedEntity) to cooresponding neighborlist
     * switch coordinate of currentEntity and switchedEntity
     */
    private void setNeighborLists() {

        if (getVerticalNeighbors(verticalNeighborList1, currentEntity, switchedEntity).size() < 2)
            verticalNeighborList1.clear();


        if (getHorizontalNeighbors(horizontalNeighborList1, currentEntity, switchedEntity).size() < 2)
            horizontalNeighborList1.clear();


        if (getHorizontalNeighbors(horizontalNeighborList2, switchedEntity, currentEntity).size() < 2)
            horizontalNeighborList2.clear();


        if (getVerticalNeighbors(verticalNeighborList2, switchedEntity, currentEntity).size() < 2)
            verticalNeighborList2.clear();

    }


    /**
     * check match on vertical direction
     *
     * @param verticalNeighborList
     * @param e1                   whose neighbor need to match
     * @param e2                   entity need to match
     * @return list of match entities
     */
    ArrayList<Entity> getVerticalNeighbors(ArrayList<Entity> verticalNeighborList, Entity e1, Entity e2) {
        int x = e1.getX();
        int y = e1.getY();

        //check up

        for (int i = y / Entity.HEIGHT - 1; i >= 0; i--) {
            if (boardEntities[x / Entity.WIDTH][i] != e2 && boardEntities[x / Entity.WIDTH][i].getId() == e2.getId())
                verticalNeighborList.add(boardEntities[x / Entity.WIDTH][i]);
            else
                break;
        }

        //check down
        for (int j = y / Entity.HEIGHT + 1; j < height; j++) {
            if (boardEntities[x / Entity.WIDTH][j] != e2 && boardEntities[x / Entity.WIDTH][j].getId() == e2.getId())
                verticalNeighborList.add(boardEntities[x / Entity.WIDTH][j]);
            else
                break;
        }

        return verticalNeighborList;
    }


    /**
     * check match on horizontal direction
     *
     * @param horizontalNeighborList
     * @param e1                     whose neighbor need to match
     * @param e2                     entity need to match
     * @return list of match entities
     */
    ArrayList<Entity> getHorizontalNeighbors(ArrayList<Entity> horizontalNeighborList, Entity e1, Entity e2) {
        int x = e1.getX();
        int y = e1.getY();

        //check left
        for (int i = x / Entity.WIDTH - 1; i >= 0; i--) {
            if (boardEntities[i][y / Entity.HEIGHT] != e2 && boardEntities[i][y / Entity.HEIGHT].getId() == e2.getId())
                horizontalNeighborList.add(boardEntities[i][y / Entity.HEIGHT]);
            else
                break;
        }

        //check right
        for (int j = x / Entity.WIDTH + 1; j < width; j++) {
            if (boardEntities[j][y / Entity.HEIGHT] != e2 && boardEntities[j][y / Entity.HEIGHT].getId() == e2.getId())
                horizontalNeighborList.add(boardEntities[j][y / Entity.HEIGHT]);
            else
                break;
        }

        return horizontalNeighborList;
    }

    /**
     * set switchedEntity
     *
     * @param dir currentEntity move direction
     */
    public void setSwitchedEntity(Direction dir) {
        switchedEntity = boardEntities[getArrX() + dir.getX()][getArrY() + dir.getY()];
    }

    /**
     * @return currentEntity array column #
     */
    public int getArrX() {
        return currentEntity.getX() / Entity.WIDTH;
    }

    /**
     * @return currentEntity array row #
     */
    public int getArrY() {
        return currentEntity.getY() / Entity.HEIGHT;
    }


    /**
     * load double array of entity texture id from local
     *
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
     * if (x,y) is outside the map, return sapphire,
     * otherwise, return the corresponding entity
     *
     * @param x index of tile on the x direction (column)
     * @param y index of tile on the y direction (row)
     * @return entity
     */
    public Entity getEntity(int x, int y) {
        if (boardEntities[x][y] == null) {
            boardEntities[x][y] = new Entity(x * Entity.WIDTH, y * Entity.HEIGHT, Entity.EMPTY_TILE_ID, game);
        }
        return boardEntities[x][y];
    }


    /**
     * if there is empty tile swap with the one above and generate random tiles for empty space in the top row
     *
     * @return fillEmptyList.size()>0
     */
    public boolean dropTiles() {
        for (int x = 0; x < width; x++) {
            for (int y = height - 1; y > 0; y--) {
                if ( boardEntities[x][y].getId() == Entity.EMPTY_TILE_ID) {
                    for (int j = y; j > 0; j--) {
                        if (!fillEmptyList.contains(boardEntities[x][j])) {
                            fillEmptyList.add(boardEntities[x][j]);
                        }
                        int id = boardEntities[x][j].id;
                        boardEntities[x][j].setId(boardEntities[x][j - 1].id);
                        boardEntities[x][j - 1].setId(id);
                    }
                }
            }
            if (boardEntities[x][0].getId() == Entity.EMPTY_TILE_ID) {
                boardEntities[x][0] = createNewEntity(boardEntities[x][0].getX(), 0);
                //check chains after drop tile for each column
                fillEmptyList.add(boardEntities[x][0]);
            }
        }


        if (fillEmptyList.size() > 0) {
            return true;
        }
        return false;
    }


    /**
     * randomly create new entity using current texture
     *
     * @param xCoor x coordinate of the tile
     * @param yCoor y coordinate of the tile
     * @return
     */
    Entity createNewEntity(int xCoor, int yCoor) {
        Random r = new Random();
        Entity e = new Entity(xCoor, yCoor, r.nextInt(5), game);

        return e;
    }



    //GETTERS and SETTERS


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Entity[][] getBoardEntities() {
        return boardEntities;
    }

    public ArrayList<Entity> getFillEmptyList() {
        return fillEmptyList;
    }
}
