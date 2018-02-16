package network;

import com.esotericsoftware.kryonet.*;
import com.esotericsoftware.minlog.Log;
import game.message.Message;
import game.model.game.model.*;
import game.model.game.model.team.Role;
import game.model.game.model.team.Team;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

import static java.lang.Thread.sleep;

/**
 * For client => construct => start => connectToServer, start game: makeRemoteModels =>
 */
public class RemoteConnection {
    static {
        Log.set(Log.LEVEL_INFO);
    }

    public void notifyMadeModel() {
        if (isServer) return;//server should not use this
        client.sendTCP(new NetworkCommon.ClientFinishMakingModelMsg());
    }

    public void restartFromReadyPage() {
        if (isServer) {
            readyClients.clear();
            server.sendToAllTCP(new NetworkCommon.AllClientConnectMsg());
        }
        else {
            Log.error("Client should not call restartFromReadyPage");
            return;
        }
    }

    // This holds per connection state.
    public static class GameConnection extends Connection {
        public String usrName;
        public GameConnection(String name) {
            usrName = name;
        }
    }

    boolean isServer;
    NetWork2LobbyAdaptor adaptor;

    Server server;
    ConcurrentHashMap<Integer, GameConnection> clientList;

    ConcurrentHashMap<Integer, GameConnection> readyClients = new ConcurrentHashMap<>();
    int cntClientModelsMade = 0;

    Client client;

    long latencty = 0;
    Long clientStartTime = null;
    Long serverStartTime = null;
    Long t0 = null;
    Long t1 = null;

    int numOfPlayer = 1;

    Map<Integer, LinkedBlockingQueue<Message>> messageQueues;
    LinkedBlockingQueue<Message> toBeConsumeMsgQueue;
    Set<RemoteModel> remoteModels;

    long delta = (long) 10E6;


    public void setNumOfPlayer(int num) {
        numOfPlayer = num;
    }

    public RemoteConnection(boolean isServer, NetWork2LobbyAdaptor adaptor) throws IOException {
        Log.set(Log.LEVEL_INFO);
        this.isServer = isServer;
        this.adaptor = adaptor;

        messageQueues = new HashMap<>();
        toBeConsumeMsgQueue = new LinkedBlockingQueue<>();

        if (isServer) {
//            //TODO change this later
            clientList = new ConcurrentHashMap<>();
            server = new Server(10000000, 10000000 /2) {
                protected Connection newConnection() {
                    return new RemoteConnection.GameConnection("ServerName");
                }
            };
        } else {
//            //TODO change this later
            client = new Client(10000000, 10000000 /2);
        }
        start();
    }

    public void start() throws IOException {
        if (isServer) {//is server receives it
            NetworkCommon.register(server);

            server.addListener(new Listener() {
                public void received (Connection c, Object obj) {
                    Log.info("Server received from " + c.getID() + " " + obj.toString());
                    GameConnection connection = (GameConnection)c;

                    //init a queue when have a new client
                    if (!clientList.containsKey(connection.getID())) {
                        clientList.putIfAbsent(connection.getID(), connection);
                        messageQueues.put(connection.getID(), new LinkedBlockingQueue<>());

                        if (clientList.size() == numOfPlayer) {//all clients connected
                            server.sendToAllTCP(new NetworkCommon.AllClientConnectMsg());
//                            adaptor.makeModel();
                        }
                    }

                    if (obj instanceof NetworkCommon.ClientFinishMakingModelMsg) {
                        cntClientModelsMade++;
                        if (cntClientModelsMade == clientList.size()) {
                            adaptor.serverInit();
                        }
                    }

                    if (obj instanceof NetworkCommon.ClientReadyMsg) {
                        NetworkCommon.ClientReadyMsg readyMsg = (NetworkCommon.ClientReadyMsg) obj;

                        //TODO: change it to be defensive here
                        //TODO: important line added here!!!!!!!!!!!!!!!!
                        adaptor.serverLobbyComfirmTeamAndRole(connection.getID(), readyMsg.getTeam(), readyMsg.getRole());
                        
                        readyClients.put(connection.getID(), connection);

                        if (readyClients.size() == clientList.size()) {
                            //works fine here
                            adaptor.makeModel();//server make model and tells client make models
                            //also need to start game (make model)
                        }
                    }

                    if (obj instanceof ArrayList) {
                        for (Message msg : (ArrayList<Message>) obj) {
                            adaptor.getMsg(msg);
                        }
                    }

                    if (obj instanceof Message) {
                        adaptor.getMsg((Message) obj);
                    }

                    if (obj instanceof NetworkCommon.SyncClockMsg) {
                        long guessServerTime = ((NetworkCommon.SyncClockMsg) obj).getServerTime();
                        long curTime = System.nanoTime();
                        if (Math.abs(guessServerTime - curTime) > delta) {
                            server.sendToTCP(c.getID(), new NetworkCommon.SyncClockMsg(curTime));
                        }
                        Log.info(curTime + " " + guessServerTime);
                    }

                }

                //TODO: implement disconnected
                public void disconnected (Connection c) {
                    server.stop();
                    System.exit(0);
                }
            });

            server.bind(NetworkCommon.port);
            server.start();
            Log.debug("Server started");
        } else {//if the client receives it
            NetworkCommon.register(client);
            client.start();
            client.addListener(new Listener() {
                public void connected (Connection connection) {
                    Log.info("Client " + connection.getID() + " connected");
                    client.sendTCP("Client " + connection.getID() + " connected");
                    //use client ID for the queue for client use
                    messageQueues.put(client.getID(), new LinkedBlockingQueue<>());
                }

                public void received (Connection connection, Object obj) {


                    Log.debug("Client " + client.getID() + "received " + obj.toString());
                    if (obj instanceof NetworkCommon.ClientMakeModelMsg) {
                        adaptor.makeModel(); //make clientModel
//                        client.sendTCP(new NetworkCommon.ClientReadyMsg()); //trigger by sth else now
                    }

                    if (obj instanceof NetworkCommon.ClientStartModelMsg) {
                        adaptor.clientInit();
                    }

                    if (obj instanceof NetworkCommon.AllClientConnectMsg) {
                        adaptor.showLobbyTeamChoice();
                    }

                    if (obj instanceof ArrayList) {
                        //enqueue the message
                        toBeConsumeMsgQueue.addAll((Collection<? extends Message>) obj);
                    }

                    if (obj instanceof Message) {
                        adaptor.getMsg((Message) obj);
                    }

                    if (obj instanceof NetworkCommon.SyncClockMsg) {
                        if (clientStartTime == null) {
                            clientStartTime = System.nanoTime();
                            serverStartTime = ((NetworkCommon.SyncClockMsg) obj).getServerTime();
                            client.sendTCP(new NetworkCommon.SyncClockMsg(serverStartTime));//what client think the server time is
                            Log.info("" + ((System.nanoTime() - clientStartTime + latencty) + serverStartTime));
                            t0 = clientStartTime;
                        }

                        else {
                            t1 = System.nanoTime();
                            latencty = (t1 - t0) / 2;
                        }
                    }

                }

                public void disconnected (Connection connection) {
                    Log.info("Disconnected");
                }
            });
        }
        (new Thread(this::sendQueueMsg, this.toString() + " Send Batched Message Thread")).start();
        (new Thread(this::consumeReceivedMsg, this.toString() + " Consume Msg Thread")).start();
    }
    private void consumeReceivedMsg() {
        while (true) {
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!toBeConsumeMsgQueue.isEmpty()) {
                adaptor.getMsg(toBeConsumeMsgQueue.poll());
            }
        }
    }
    private void sendQueueMsg() {
        while (true) {

            try {
                sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isServer) {
                for (int connectId : messageQueues.keySet()) {
                    List<Message> messageList = new ArrayList<>();
                    LinkedBlockingQueue q = messageQueues.get(connectId);
                    q.drainTo(messageList);
                    if (messageList.size() <= 0) continue;
                    server.sendToTCP(connectId, messageList);
                }
            } else {
                if (messageQueues.size() < 1) continue;
                List<Message> messageList = new ArrayList<>();
                LinkedBlockingQueue q = messageQueues.get(client.getID());
                q.drainTo(messageList);
                if (messageList.size() <= 0) continue;
                client.sendTCP(messageList);
            }
        }
    }

    private void enqueueMsg(Message msg, int conId) {
        if (isServer)
            messageQueues.get(conId).add(msg);
        else
            messageQueues.get(client.getID()).add(msg);
    }

    /**
     *
     * @param port
     * @param host
     * @throws IOException
     */
    public void connectToServer(int port, String host) throws IOException {
        if (isServer) {
            Log.error("Server should not connect to server crossing network");
            return;
        }
        Log.info("Client connect to " + host + " " + port);
//        new Thread("Connect") {
//            public void run () {
//                try {
//                    client.connect(port, host, NetworkCommon.port);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                // Server communication after connection can go here, or in Listener#connected().
//            }
//        }.start();
        client.connect(port, host, NetworkCommon.port);

    }

    public void stop() {
        if (isServer) server.stop();
        else client.stop();
    }

    public void notifyReady(Team team, Role role) {
        if (isServer) System.err.println("Server should not do this");
        else client.sendTCP(new NetworkCommon.ClientReadyMsg(team, role));
    }

    //if isServer, make client remoteModel
    public Set<RemoteModel> makeRemoteModel() {
        if (remoteModels != null) return remoteModels;
        remoteModels = new HashSet<>();
        if (isServer) {
            for (int id : clientList.keySet()) {
//                System.out.println("server remote model id: " + id);
                remoteModels.add(new RemoteModel(id));
            }
        } else {
            remoteModels.add(new RemoteModel(-1));//not using the conid
        }
        return remoteModels;
    }

    class RemoteModel implements Model {
        int connectId;

        public RemoteModel(int conid) {
            connectId = conid;
        }

        @Override
        public void processMessage(Message m) {
            if (isServer) { //then the remote is a client
                enqueueMsg(m, connectId);
            } else {
                enqueueMsg(m, connectId);
            }
        }

        @Override
        public long nanoTime() {
            //clientStartTime should actually start before the received startTime, so plus the latency
            return (System.nanoTime() - clientStartTime + latencty) + serverStartTime;
        }

        /**
         *
         * @return the connect id of this connection
         */
        public int getConnectId() {//TODO: need to double what connect id means
//            if (isServer) return -1;
//            return connectId;
            return connectId;
        }

        public void clientMakeModel() {
            if (isServer) { // means it is server call this method on remote model
                Log.debug("Server click start the game");
                server.sendToTCP(connectId, new NetworkCommon.ClientMakeModelMsg());
            }
            else {// a client should not make server to start
                Log.error("ERROR: client start the game");
            }
        }

        //call by server to ask the client to start the model
        public void startModel() {
            if (!isServer) return;
            server.sendToTCP(connectId, new NetworkCommon.ClientStartModelMsg());
        }

        public void syncClock() {
            if (isServer) server.sendToAllTCP(new NetworkCommon.SyncClockMsg(System.nanoTime()));

        }
    }

}

