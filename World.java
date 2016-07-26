
import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * Created by shuorenwang on 2016-07-24.
 */
public class World {
    private int width, height;  //number of tiles
    private int mouseX,mouseY;
    private BufferedImage texture;
    private Game game;
    private int[][] boardIDs;
    private Entity[][] boardEntities;
    private Entity currentEntity;

    boolean changeToBlack=false;

    World(Game game,String path){


        this.game=game;

        loadWorld(path);

        boardEntities = new Entity[width][height];

        init();
    }

    /**
     * initialize boardEntities[][] according to boardIDs[][]
     */
    private void init(){
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boardEntities[x][y] = new Entity(x * Entity.WIDTH, y * Entity.HEIGHT, boardIDs[x][y],game);
            }
        }


    }

    /**
     * if A is selected and mouse move to the direction of B
     * change A,B coordinate
     */
    public void tick(){
        mouseX = game.getMouseManager().getMouseX();
        mouseY = game.getMouseManager().getMouseY();


    }


    public void render(Graphics g){
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                boardEntities[x][y].render(g);
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

//        if (boardEntities[x][y] == null) {
//            boardEntities[x][y]= new Entity(x * Entity.WIDTH, y * Entity.HEIGHT, 4,game);
//        }
        return boardEntities[x][y];
    }

    /**
     * set texture according to id
     * @param id
     */
    private BufferedImage getTexture(int id) {
        switch (id){
            case 0:
                texture= Assets.tree;
                break;
            case 1:
                texture= Assets.dirt;
                break;
            case 2:
                texture= Assets.lizard;
                break;
            case 3:
                texture= Assets.wall;
                break;
            case 4:
                texture= Assets.grass;
                break;
            case 5:
                texture= Assets.stone;
                break;
            default:
                texture= Assets.grass;
        }

        return texture;
    }
}
