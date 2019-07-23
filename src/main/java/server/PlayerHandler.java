package server;

import model.packet.ClientPacket;
import model.packet.ServerPacket;
import model.player.Actions;

import java.io.*;
import java.net.Socket;

public class PlayerHandler implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private Room room;
    private Server server;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private ClientPacket packet;
    private ServerPacket serverPacket = new ServerPacket();

    @Override
    public void run() {
        try {
            server.setPlayersOnline(server.getPlayersOnline() + 1);
            room.setPlayersConnected(room.getPlayersConnected() + 1);
            server.setPlayerJoining(true);
            System.out.println("Dołączył nowy gracz.");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            room.getObjectOutputStreams().add(objectOutputStream);
            while (true) {
//                Thread.sleep(500);
                packet = (ClientPacket) objectInputStream.readObject();
                switch (packet.getPlayer().getLastAction()) {
                    case (Actions.CHAT):
                        break;
                    case (Actions.THROW):


                        for (ObjectOutputStream oos : room.getObjectOutputStreams()) {
                            if (oos.equals(objectOutputStream)) continue;
                            serverPacket.setAction(Actions.UPDATE_BOARD);
                            serverPacket.setPawns(packet.getPawns());
                            oos.reset();
                            oos.writeObject(serverPacket);
                        }
                        room.setFinishRoundPlayer(true);

//                        room.setLastWinnerNickname(packet.getPlayer().getNickname());
                        break;
                    case (Actions.READY):
                        if (packet.getPlayer().isReady()) room.setPlayersReady(room.getPlayersReady() + 1);
                        else room.setPlayersReady(room.getPlayersReady() - 1);
                        break;
//                    case (Actions.IMAGE):
//                        for (ObjectOutputStream oos : room.getObjectOutputStreams()) {
//                            if (oos.equals(objectOutputStream)) continue;
//                            serverPacket.setAction(Actions.IMAGE);
//                            serverPacket.setImage(packet.getImage());
//                            oos.reset();
//                            oos.writeObject(serverPacket);
//                        }
//                        break;
                    case (Actions.CONNECTED):
                        room.getPlayers().add(packet.getPlayer());
                        break;
                }


            }
        } catch (IOException | ClassNotFoundException  e) {
            e.printStackTrace();
        }
    }

    public PlayerHandler(Server server, Room room, Socket socket) {
        this.socket = socket;
        this.room = room;
        this.server = server;
    }
}
