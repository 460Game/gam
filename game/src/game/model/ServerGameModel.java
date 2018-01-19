package game.model;

import game.model.Game.GameModel;
import game.model.Game.ServerMapGenerator;

import java.util.Collection;

public class ServerGameModel extends GameModel {

    public ServerGameModel(Collection<? extends IModel> clients) {
        super(true, clients, new ServerMapGenerator(GameModel.GRID_X_SIZE, GameModel.GRID_Y_SIZE));

    }

}
