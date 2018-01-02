import java.util.Set;

/**
 * shouldnt be drawable! purely for testing for now
 */
public abstract class Shape implements Drawable {

    /**
     * this should return the set of all tiles this shape overlaps with
     * 
     * @param map
     * @return
     */
    public abstract Set<Tile> getTiles(Map map);

    public abstract boolean testCollision(Shape shape);
}
