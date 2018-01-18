package game.network;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import game.message.*;
import game.message.playerMoveMessage.*;
import game.model.Game.GameModel;
import game.model.Game.WorldObject.Blocker;
import game.model.Game.WorldObject.Entity;
import game.model.Game.WorldObject.Shape.*;
import game.model.Game.WorldObject.TestPlayer;

import javax.swing.*;
import java.util.UUID;

public class NetworkCommon {
    static public int port = 54555;
    static public final int REMOTEMODEL = 1;
    // This registers serializes files
    public static void register (EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(Message.class);
        kryo.register(PlayerDown.class);
        kryo.register(PlayerLeft.class);
        kryo.register(PlayerRight.class);
        kryo.register(PlayerStopHorz.class);
        kryo.register(PlayerStopVert.class);
        kryo.register(PlayerUp.class);
        kryo.register(ActionMessage.class);
        kryo.register(RemoveEntityMessage.class);
        kryo.register(SetEntityMessage.class);
        kryo.register(SetTileMessage.class);
        kryo.register(ToClientMessage.class);
        kryo.register(ToServerMessage.class);

        kryo.register(TestPlayer.class);
        kryo.register(Blocker.class);
        kryo.register(Entity.class);

        kryo.register(Shape.class);
        kryo.register(CellShape.class);
        kryo.register(CircleShape.class);
        kryo.register(CompositeShape.class);
        kryo.register(RectShape.class);

        kryo.register(UUID.class);



        kryo.register(StartGameMsg.class);

        kryo.register(RemoteConnection.GameConnection.class);
    }

    public static class StartGameMsg {
        public StartGameMsg () {
        }
    }

}

