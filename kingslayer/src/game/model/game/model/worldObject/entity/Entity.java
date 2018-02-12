package game.model.game.model.worldObject.entity;

import game.message.toClient.NewEntityMessage;
import game.model.game.grid.GridCell;
import game.model.game.model.worldObject.entity.aiStrat.AIStrat;
import game.model.game.model.worldObject.entity.aiStrat.AIable;
import game.model.game.model.worldObject.entity.collideStrat.CollisionStrat;
import game.model.game.model.worldObject.entity.collideStrat.hitbox.Hitbox;
import game.model.game.model.worldObject.entity.drawStrat.DirectionAnimationDrawStrat;
import game.model.game.model.worldObject.entity.drawStrat.DrawStrat;
import game.model.game.model.worldObject.entity.entities.Entities;
import game.model.game.model.worldObject.entity.updateStrat.UpdateStrat;
import util.Util;
import game.model.game.model.GameModel;
import game.model.game.model.team.Team;
import javafx.scene.canvas.GraphicsContext;

import java.util.Set;

import static util.Util.toDrawCoords;

/**
 * Represents any entity in the game world. Each entity knows the cells it is in
 * and knows its game-relevant data. Each entity has its own way of updating, colliding,
 * and drawing.
 */
public class Entity implements Updatable, Drawable, AIable {

    /**
     * TODO
     */
    final AIStrat aiStrat;

    /**
     * The way this entity is drawn on the game map.
     */
    final DrawStrat drawStrat;

    /**
     * The way this entity is updated in the game.
     */
    final UpdateStrat updateStrat;

    /**
     * The way this entity collides with other entities in the game.
     */
    final CollisionStrat collisionStrat;

    /**
     * Checks if this entity is currently colliding with another entity.
     */
    public transient boolean inCollision = false;

    /**
     * Records the x-coordinate when this entity was last updated.
     */
    public transient double prevX = -1;

    /**
     * Records the y-coordinate when this entity was last updated.
     */
    public transient double prevY = -1;

    /**
     * Used to track which cells this entity is in in the LOCAL model.
     * Never sent across the network! May be null (empty set).
     */
    public transient Set<GridCell> containedIn = null;

    /**
     * Team of this entity.
     */
    public final Team team;

    /**
     * ID of this entity.
     */
    public final long id;

    /**
     * Holds all the game-relevant data about this entity.
     */
    public EntityData data;

    /**
     * Constructor of an entity, given all of its game data.
     * @param x x-coordinate of the center of its position
     * @param y y-coordinate of the center of its position
     * @param team team corresponding to this entity
     * @param updateStrat method with which this entity updates
     * @param collisionStrat method with which this entity collides.
     * @param hitbox hitbox of this entity
     * @param drawStrat method with which this entity is drawn
     * @param aiStrat TODO
     */
    public Entity(double x, double y,
                  Team team,
                  UpdateStrat updateStrat,
                  CollisionStrat collisionStrat,
                  Hitbox hitbox,
                  DrawStrat drawStrat,
                  AIStrat aiStrat) {
        id = Util.random.nextLong();
        this.team = team;
        this.updateStrat = updateStrat;
        this.collisionStrat = collisionStrat;
        this.drawStrat = drawStrat;
        this.aiStrat = aiStrat;
        this.data = new EntityData(hitbox, aiStrat.makeAIData(), drawStrat.initDrawData(),
                updateStrat.initUpdateData(), x, y);
    }

    /**
     * Default constructor needed for serialization.
     */
    private Entity() {
        this.updateStrat = null;
        this.collisionStrat = null;
        this.drawStrat = null;
        this.aiStrat = null;
        team = null;
        id = 0;
    }

    @Override
    public void updateAI(GameModel model) {
        this.aiStrat.updateAI(this, model);
    }

    @Override
    public void draw(GraphicsContext gc) {
        this.drawStrat.draw(this, gc);
    }

    @Override
    public double getDrawZ() {
        return this.drawStrat.getDrawZ(data);
    }

    /**
     * Performs collisions with another entity based off of this
     * entity's colliding strategy.
     * @param model current model of the game
     * @param b the entity being collided with
     */
    public void collision(GameModel model, Entity b) {
        inCollision = true;
        this.collisionStrat.collision(model, this, b);
    }

    @Override
    public void update(GameModel model) {
        if (this.team != Team.NEUTRAL) {
            ((DirectionAnimationDrawStrat) drawStrat).update(this);
        }
        inCollision = false;
        this.updateStrat.update(this, model);
    }

    /**
     * Gets the collision type of this entity.
     * @return the collision type of this entity
     */
    public CollisionStrat.CollideType getCollideType() {
        return collisionStrat.getCollideType();
    }

    /**
     * Update the cells that this entity is currently in.
     * @param model current model of the game
     */
    public void updateCells(GameModel model) {
        this.data.hitbox.updateCells(this, model);
    }

    /**
     * TODO
     * @param commandID
     * @param model
     */
    public void runCommand(int commandID, GameModel model) {
        if (this.team != Team.NEUTRAL) {
//            if (drawStrat instanceof DirectionAnimationDrawStrat.RedKingDirectionAnimationDrawStrat ||
//                drawStrat instanceof DirectionAnimationDrawStrat.BlueKingDirectionAnimationDrawStrat) {
                switch (commandID) {
                    case 0:
                        ((DirectionAnimationDrawStrat) drawStrat).togglePlacementBox();
                        break;
                    case 1:
                        ((DirectionAnimationDrawStrat) drawStrat).togglePlacementBox();
                        double[] dir = {0, 0};
                        if (((DirectionAnimationDrawStrat) drawStrat).drawData.direction == 'N')
                            dir[1] = -1;
                        else if (((DirectionAnimationDrawStrat) drawStrat).drawData.direction == 'E')
                            dir[0] = 1;
                        else if (((DirectionAnimationDrawStrat) drawStrat).drawData.direction == 'S')
                            dir[1] = 1;
                        else
                            dir[0] = -1;
                        System.out.println("in entity: " + data.x + " " + data.y);
                        model.processMessage(new NewEntityMessage(Entities.makeBuiltWall(Math.floor(data.x) + 0.5 + dir[0],
                            Math.floor(data.y) + 0.5 + dir[1])));
                        break;
                    default:
                        System.out.println("Unknown command");
                }
//            }

        }
    }

    public String toString() {
        return "" + this.id + ": " + this.data.x + ", " + this.data.y;
    }
}
