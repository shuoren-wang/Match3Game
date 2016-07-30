package game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by shuorenwang on 2016-07-30.
 */
public class ClearMatch {

    private int width, height;  //number of tiles
    private Entity[][] boardEntities;
    private Game game;
    private World world;
    private ArrayList<Entity> fillVerticalList;
    private ArrayList<Entity> fillHorizontalList;
    private boolean foundChain;

    private static final int MAX_DETECT_TIME = 500;
    private int emptyTime = 0;
    private int detectTime = 0;
    private int clearTime = 0;


    public ClearMatch(Game game, World world) {
        this.game = game;
        this.world = world;
        boardEntities = world.getBoardEntities();
        width = world.getWidth();
        height = world.getHeight();
        fillVerticalList = new ArrayList<>();
        fillHorizontalList = new ArrayList<>();

        foundChain = false;

    }


    public void tick() {

        detectChains();

    }

    /**
     * if there is empty tile swap with the one above and generate random tiles for empty space in the top row
     */
    public void dropTiles() {
        for (int x = 0; x < width; x++) {
            for (int y = height - 1; y > 0; y--) {
                if (boardEntities[x][y].getId() == Entity.EMPTY_TILE_ID) {
                    for (int j = y; j > 0; j--) {
                        int id = boardEntities[x][j].id;
                        boardEntities[x][j].setId(boardEntities[x][j - 1].id);
                        boardEntities[x][j - 1].setId(id);
                    }
                }
            }
            if (boardEntities[x][0].getId() == Entity.EMPTY_TILE_ID) {
                boardEntities[x][0] = createNewEntity(boardEntities[x][0].getX(), 0);
            }
        }

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


    /**
     * @return true if there is chain in the tile array, delete chain and  call dropTile()
     */
    public void detectChains() {
        foundChain = false;

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                if (boardEntities[x][y].getId() != Entity.EMPTY_TILE_ID) {
                    boolean dh = detectHorizontalChains(boardEntities[x][y]);
                    boolean dv = detectVerticalChains(boardEntities[x][y]);

                    if (dv || dh) {
                        clearChainList();
                        foundChain = true;
                    }
                }
            }

            dropTiles();

    }


    /**
     * delete tiles listed fillEmptyList from boardEntities[][]
     */
    private void clearChainList() {
        for (Entity nextEntity : fillHorizontalList) {
            if (boardEntities[nextEntity.getX() / Entity.WIDTH][nextEntity.getY() / Entity.HEIGHT].getId() != Entity.EMPTY_TILE_ID) {
                boardEntities[nextEntity.getX() / Entity.WIDTH][nextEntity.getY() / Entity.HEIGHT].setId(Entity.EMPTY_TILE_ID);
            }
        }

        for (Entity nextEntity : fillVerticalList) {
            if (boardEntities[nextEntity.getX() / Entity.WIDTH][nextEntity.getY() / Entity.HEIGHT].getId() != Entity.EMPTY_TILE_ID)
                boardEntities[nextEntity.getX() / Entity.WIDTH][nextEntity.getY() / Entity.HEIGHT].setId(Entity.EMPTY_TILE_ID);
        }

        fillHorizontalList.clear();
        fillVerticalList.clear();
    }


    /**
     * @return true if there is chain in the vertical direction
     */
    private boolean detectVerticalChains(Entity e) {

        int x = e.getX();
        int y = e.getY();

        //check up

        for (int i = y / Entity.HEIGHT - 1; i >= 0; i--) {
            if (boardEntities[x / Entity.WIDTH][i].getId() == e.getId())
                fillVerticalList.add(boardEntities[x / Entity.WIDTH][i]);
            else
                break;
        }

        //check down
        for (int j = y / Entity.HEIGHT + 1; j < height; j++) {
            if (boardEntities[x / Entity.WIDTH][j].getId() == e.getId())
                fillVerticalList.add(boardEntities[x / Entity.WIDTH][j]);
            else
                break;
        }

        if (fillVerticalList.size() < 2) {
            fillVerticalList.clear();
            return false;
        } else {
            fillVerticalList.add(e);
        }

        return true;
    }


    /**
     * @return true if there is chain in the horizontal direction
     */
    private boolean detectHorizontalChains(Entity e) {
        int x = e.getX();
        int y = e.getY();

        //check left
        for (int i = x / Entity.WIDTH - 1; i >= 0; i--) {
            if (boardEntities[i][y / Entity.HEIGHT].getId() == e.getId())
                fillHorizontalList.add(boardEntities[i][y / Entity.HEIGHT]);
            else
                break;
        }

        //check right
        for (int j = x / Entity.WIDTH + 1; j < width; j++) {
            if (boardEntities[j][y / Entity.HEIGHT].getId() == e.getId())
                fillHorizontalList.add(boardEntities[j][y / Entity.HEIGHT]);
            else
                break;
        }
        if (fillHorizontalList.size() < 2) {
            fillHorizontalList.clear();
            return false;
        } else {
            fillHorizontalList.add(e);
        }

        return true;
    }


    public void render(Graphics g) {
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                getEntity(x, y).render(g);
            }

    }


    public void setBoardEntities(Entity[][] boardEntities) {
        this.boardEntities = boardEntities;
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


    //Getters and Setters

    public Entity[][] getBoardEntities() {
        return boardEntities;
    }
}
