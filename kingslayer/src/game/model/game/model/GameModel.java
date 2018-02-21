package game.model.game.model;

import game.message.Message;
import game.model.game.grid.GridCell;
import game.model.game.map.MapGenerator;
import game.model.game.map.Tile;
import game.model.game.model.gameState.GameState;
import game.model.game.model.worldObject.entity.Drawable;
import game.model.game.model.worldObject.entity.Entity;
import game.model.game.model.worldObject.entity.EntityData;
import game.model.game.model.worldObject.entity.drawStrat.ShapeDrawStrat;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import static util.Const.*;

public abstract class GameModel implements Model {

    /**
     * grid of the game map. Each tile on the map is represented by a 1x1 cell.
     */
    private GridCell[][] grid;// = new GridCell[util.Const.GRID_X_SIZE][util.Const.GRID_Y_SIZE];

    private Collection<GridCell> allCells;

    private LinkedBlockingQueue<Message> messageQueue;

    private final Map<Long, Entity> entities;

    protected void queueMessage(Message message) {
        messageQueue.add(message);
    }

    public abstract void execute(Consumer<ServerGameModel> serverAction, Consumer<ClientGameModel> clientAction);

    /**
     * Constructor for the game model.
     * @param generator map generator for this game model
     */
    public GameModel(MapGenerator generator) {
        super();

        messageQueue = new LinkedBlockingQueue<>();
        grid = new GridCell[util.Const.GRID_X_SIZE][util.Const.GRID_Y_SIZE];

        entities = new HashMap<>();
        allCells = new ArrayList<>();

        for (int i = 0; i < util.Const.GRID_X_SIZE; i++)
            for (int j = 0; j < util.Const.GRID_Y_SIZE; j++)
                grid[i][j] = new GridCell(i, j);

        for (int i = 0; i < util.Const.GRID_X_SIZE; i++)
            for (int j = 0; j < util.Const.GRID_Y_SIZE; j++)
                grid[i][j].setTile(generator.makeTile(i, j), this);


        for (int i = 0; i < util.Const.GRID_X_SIZE; i++)
            for (int j = 0; j < util.Const.GRID_Y_SIZE; j++)
                allCells.add(grid[i][j]);

        generator.makeStartingEntities().forEach(e -> entities.put(e.id ,e));
    }

    /**
     * Gets the map width in terms of number of grid cells.
     * @return the number of grid cells in the width of the map
     */
    public int getMapWidth() {
        return util.Const.GRID_X_SIZE;
    }

    /**
     * Gets the map height in terms of number of grid cells.
     * @return the number of grid cells in the height of the map
     */
    public int getMapHeight() {
        return util.Const.GRID_Y_SIZE;
    }

    /**
     * Gets the cell at the specified coordinates. The coordinates represent
     * the upper left corner of the cell.
     * @param x x-coordinate
     * @param y y-coordinate
     * @return the cell with the given upper left coordinates
     */
    public GridCell getCell(int x, int y) {
        return grid[x][y];
    }

    /**
     * Gets the tile at the specified coordinates. The coordinates represent
     * the upper left corner of the cell.
     * @param x x-coordinate
     * @param y y-coordinate
     * @return tile of the cell with the given upper left coordinates
     */
    public Tile getTile(int x, int y) {
        if (x >= getMapWidth() || x < 0 || y >= getMapHeight() || y < 0) {
            return Tile.DEEP_WATER;
        }
        return grid[x][y].getTile();
    }

    public Entity getEntityAt(int x, int y) {
        for (Entity e: getAllEntities()) {
            if ((int) e.data.x == x && (int) e.data.y == y)
                return e;
        }
        return null;
    }

    /**
     * Removes the entity with the given ID from every tile on the game map.
     *
     * @param entityID ID of the entity to be removed
     */
    public void removeByID(long entityID) {
        if(entities.containsKey(entityID))
            remove(entities.get(entityID));
    }

    public void remove(Entity entity) {
        if(entity.containedIn != null)
            entity.containedIn.forEach(cell -> cell.removeContents(entity));
        entities.remove(entity.id);
    }

    public void setTile(int x, int y, Tile tile) {
        grid[x][y].setTile(tile, this);
    }

    public void update() {
        ArrayList<Message> list = new ArrayList<>();
        messageQueue.drainTo(list);
        list.forEach(m -> m.execute(this));

        entities.values().forEach(e -> e.update(this));
        entities.values().forEach(e -> e.updateCells(this));
        allCells.forEach(cell -> cell.collideContents(this));
    }

    public Collection<GridCell> getAllCells() {
        return allCells;
    }

    /*
    returns true on success
    returns false if unknown entity
     */
    public boolean trySetEntityData(long id, EntityData data) {
        if(entities.containsKey(id)) {
            entities.get(id).data = data;
            return true;
        } else {
            return false;
        }
    }

    public void setEntity(Entity entity) {
        Entity e = entities.get(entity.id);
        if(e != null)
            e.data = entity.data;
        else
            entities.put(entity.id, entity);
    }

    /**
     * returns approximately all the entities inside of the box centered at x,y with width, height
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     */
    public void drawForeground(GraphicsContext gc, double x, double y, double w, double h) {
        allCells.stream().flatMap(GridCell::streamContents).sorted(Comparator.comparingDouble(Entity::getDrawZ)).forEach(a -> a.draw(gc));

        if(DEBUG_DRAW)
            allCells.stream().flatMap(GridCell::streamContents).forEach(a -> a.data.hitbox.draw(gc, a));
    }

    public void writeBackground(WritableImage image, boolean b) {
        allCells.forEach(cell -> cell.draw(image.getPixelWriter(), this, true));
    }

    public Collection<Entity> getAllEntities() {
        return entities.values();
    }

    public Entity getEntity(long entity) {
        if (!entities.containsKey(entity))
            return null;
        return entities.get(entity);
    }

    private long AINanoTime = -1;

    /*
    Server only mthod for updating the
     */
    public void updateAI(ServerGameModel serverGameModel) {
        if (AINanoTime == -1) {
            AINanoTime = System.nanoTime();
            return;
        }
        long cur = System.nanoTime();
        double elapsed = NANOS_TO_SECONDS * (cur - AINanoTime);
        AINanoTime = cur;
        entities.values().forEach(e -> e.updateAI(serverGameModel, elapsed));;
    }
}
