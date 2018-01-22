package game.model.Game.WorldObject.Entity;

import game.model.Game.Map.Tile;
import game.model.Game.Model.GameModel;
import game.model.Game.WorldObject.Shape.CircleShape;
import game.model.Game.WorldObject.Shape.Shape;
import game.model.Game.WorldObject.Team;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.IOException;

import static Util.Const.TILE_PIXELS;

public class TestPlayer extends Entity {

    @Override
    public void copyOf(Entity other) {
        assert (other instanceof TestPlayer);
        TestPlayer o = (TestPlayer) other;
        this.shape = o.shape;
        this.dx = o.dx;
        this.dy = o.dy;
        super.copyOf(other);
    }

    private CircleShape shape;
    private double dx = 0;
    private double dy = 0;

    public TestPlayer() {
        super();
        shape = new CircleShape(0.0, 0.0, 0.5);
    }

    public TestPlayer(GameModel model, double x, double y) {
        super(model);
        shape = new CircleShape(x, y, 0.5);
    }

    @Override
    public void collision(GameModel model, Entity collidesWith) {
        double xdiff = Math.abs(this.getX() - collidesWith.getX());
        double ydiff = Math.abs(this.getY() - collidesWith.getY());

        System.out.println("X: " + (shape.getX()) + ", Y: " + (shape.getY()) + ", radius: " + shape.getRadius());

        for(Shape.GridCellReference g : shape.getCellsReference())
            System.out.println("Cell X: " + g.x + ", cell Y: " + g.y);

        System.out.println("BLocker X, y: " + collidesWith.getX() + ", " + collidesWith.getY());

        // TODO issue with both directions?
        while (shape.testCollision(collidesWith.getShape()))
//            shape.shift(-0.05 * shape.getRadius() * Math.cos(getMovementAngle()), 0.05 * shape.getRadius() * Math.sin(getMovementAngle()));
            shape.shift(-(xdiff - shape.getRadius()) * Math.cos(getMovementAngle()), (ydiff - shape.getRadius()) * Math.sin(getMovementAngle()));
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    static Image imageRedKing;
    static Image imageBlueKing;

    static {
        try {
            imageRedKing = new Image(Tile.class.getResource("king_red_1.png").openStream());
            imageBlueKing = new Image(Tile.class.getResource("king_blue_1.png").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        //gc.setFill(this.getTeam().color);
        //shape.draw(gc);
        if (this.getTeam() == Team.ONE) {
            gc.drawImage(this.imageRedKing, this.getX() * TILE_PIXELS - TILE_PIXELS / 2,
                this.getY() * TILE_PIXELS - TILE_PIXELS / 2, TILE_PIXELS, 1.5 * TILE_PIXELS);
        } else {
            gc.drawImage(this.imageBlueKing, this.getX() * TILE_PIXELS - TILE_PIXELS / 2,
                this.getY() * TILE_PIXELS - TILE_PIXELS / 2, TILE_PIXELS, 1.5 * TILE_PIXELS);
        }
    }

    @Override
    public double getDrawZ() {
        return getY();
    }

    @Override
    public void update(long time, GameModel model) {
        // shape.shift(dx * time * 1e-9 * 10, dy * time * 1e-9 * 10); TODO for testing
        shape.shift(dx * 0.25, dy * 0.25);
    }

    public void up() {
        dy = -1;
        setMovementAngle(Math.PI / 2);
    }

    public void left() {
        dx = -1;
        setMovementAngle(Math.PI);
    }

    public void right() {
        dx = 1;
        setMovementAngle(0);
    }

    public void down() {
        dy = 1;
        setMovementAngle(3 * Math.PI / 2);
    }

    public void stopVert() {
        dy = 0;
    }

    public void stopHorz() {
        dx = 0;
    }
}
