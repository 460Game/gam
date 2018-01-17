package game.message;

import game.model.Game.GameModel;
import game.model.Game.WorldObject.Entity;

import java.util.UUID;

public class RemoveEntityMessage implements ToClientMessage {

    UUID entityID;

    RemoveEntityMessage(Entity entity) {
        this.entityID = entity.getUuid();
    }

    @Override
    public void execute(GameModel model) {
        model.removeByID(entityID);
    /*
    should remove references to the corasponding local entity from the model
     */
    }
}