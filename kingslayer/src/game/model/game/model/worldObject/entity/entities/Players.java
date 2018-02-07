package game.model.game.model.worldObject.entity.entities;

import game.model.game.model.GameModel;
import game.model.game.model.worldObject.Team;
import game.model.game.model.worldObject.entity.Entity;
import game.model.game.model.worldObject.entity.aiStrat.AIDoNothingStrat;
import game.model.game.model.worldObject.entity.collideStrat.HardCollisionStrat;
import game.model.game.model.worldObject.entity.collideStrat.SoftCollisionStrat;
import game.model.game.model.worldObject.entity.collideStrat.UnitCollisionStrat;
import game.model.game.model.worldObject.entity.collideStrat.hitbox.CircleHitbox;
import game.model.game.model.worldObject.entity.drawStrat.DirectionAnimationDrawStrat;
import game.model.game.model.worldObject.entity.drawStrat.ImageDrawStrat;
import game.model.game.model.worldObject.entity.updateStrat.StillStrat;
import images.Images;

public class Players {

    private static final double PLAYER_RADIUS = 0.5;
    
    static private CircleHitbox hitbox = new CircleHitbox(PLAYER_RADIUS);

    public static Entity makeSlayer(Double x, Double y) {
        return new Entity(x, y,
            Team.ONE,
            StillStrat.SINGLETON,
            UnitCollisionStrat.SINGLETON,
            hitbox,
            DirectionAnimationDrawStrat.RED_SLAYER_ANIMATION, //TODDO draw strat
            AIDoNothingStrat.SINGLETON);
    }

    public static Entity makeKing(Double x, Double y) {
        return new Entity(x, y,
            Team.ONE,
            StillStrat.SINGLETON,
            UnitCollisionStrat.SINGLETON,
            hitbox,
            DirectionAnimationDrawStrat.RED_KING_ANIMATION, //TODDO draw strat
            AIDoNothingStrat.SINGLETON);
    }
}