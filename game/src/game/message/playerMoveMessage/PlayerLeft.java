package game.message.playerMoveMessage;

import game.message.ActionMessage;
import game.message.SetEntityMessage;
import game.model.Game.Model.ServerGameModel;
import game.model.Game.WorldObject.Entity.Player;

/**
 * Message sent by a client to tell the server to move the player
 * leftwards on the game map.
 */
public class PlayerLeft extends ActionMessage {

    /**
     * ID to distinguish player that sent the message.
     */
    private long id;

    /**
     * Constructor for the move message.
     * @param id player ID that send the message
     */
    public PlayerLeft(long id) {
        super();
        this.id = id;
    }

    /**
     * Default constructor needed for serialization.
     */
    public PlayerLeft() {

    }

    /**
     * Moves the player leftwards.
     * @param model the game model on the game server
     */
    @Override
    public void executeServer(ServerGameModel model) {
        ((Player) model.getEntityById(id)).left();
        model.processMessage(new SetEntityMessage(model.getEntityById(id)));
    }
}
