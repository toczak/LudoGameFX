package server;

import model.Pawn;
import model.packet.ServerPacket;
import model.player.Actions;
import model.player.Player;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.*;


public class Room implements Runnable {

    private ServerSocket socket;
    private Server server;
    private List<ObjectOutputStream> objectOutputStreams = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> sortedPlayers = new ArrayList<>();
    private ServerPacket serverPacket = new ServerPacket();
    private volatile int playersReady = 0;
    private volatile int playersConnected = 1;
    private boolean finishRoundPlayer = false;
    private boolean endOfTheGame = false;


    public void run() {
        System.out.println("Nowa gra!");
        serverPacket.getChatText().setNickname("Server");
        waitForBeginning();
        while (true) {
            try {
                new Thread(new PlayerHandler(server, this, socket.accept())).start();
                if (playersConnected - server.MAX_PLAYERS == 0) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Room(Server server, ServerSocket socket) {
        this.server = server;
        this.socket = socket;
    }

    private void waitForBeginning() {
        new Thread(() -> {
            while (true) {
                if (getPlayersReady() - server.MIN_PLAYERS >= 0 && playersReady != 0) {
                    try {
                        sendToAll(Actions.CHAT, Actions.GAME_STARTED);
                        sendToAll(Actions.GAME_STARTED, null);
                        sortedPlayers.addAll(players);
                        serverPacket.setPlayers(sortedPlayers);
                        sendToAll(Actions.CHAT, "Gra rozpocznie się za 10 sekund...");
                        break;
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                waitTimeAndSetColorsToPlayers();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void waitTimeAndSetColorsToPlayers() throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;

        while (elapsedTime < 10000) {
            elapsedTime = (new Date()).getTime() - startTime;
        }

        for (int i = 0; i < players.size(); i++) {
            serverPacket.setAction(Actions.SET_COLOR);
            if (i == 0) serverPacket.setValue(1);
            if (i == 1) serverPacket.setValue(3);
            if (i == 2) serverPacket.setValue(2);
            if (i == 3) serverPacket.setValue(4);
            objectOutputStreams.get(i).reset();
            objectOutputStreams.get(i).writeObject(serverPacket);
        }
        sendToAll(Actions.BEGIN_GAME, null);
        initFigures();
        playTheGame();
    }

    private void initFigures() throws IOException {
        ArrayList<Pawn> pawns = new ArrayList<>();
        pawns.add(new Pawn(28, 533, 1));
        pawns.add(new Pawn(28, 588, 1));
        pawns.add(new Pawn(83, 533, 1));
        pawns.add(new Pawn(83, 588, 1));
        pawns.add(new Pawn(28, 34, 2));
        pawns.add(new Pawn(28, 89, 2));
        pawns.add(new Pawn(83, 34, 2));
        pawns.add(new Pawn(83, 89, 2));
        pawns.add(new Pawn(530, 34, 3));
        pawns.add(new Pawn(533, 88, 3));
        pawns.add(new Pawn(588, 34, 3));
        pawns.add(new Pawn(588, 88, 3));
        pawns.add(new Pawn(533, 533, 4));
        pawns.add(new Pawn(533, 588, 4));
        pawns.add(new Pawn(588, 533, 4));
        pawns.add(new Pawn(588, 588, 4));

        for (ObjectOutputStream oos : objectOutputStreams) {
            serverPacket.setAction(Actions.GET_BOARD);
            serverPacket.setPawns(pawns);
            oos.reset();
            oos.writeObject(serverPacket);
        }
    }

    private void playTheGame() throws IOException, InterruptedException {
        for (int i = 0; i < players.size(); i++) {
            if (endOfTheGame) break;
            serverPacket.setAction(Actions.START_ROUND);
            objectOutputStreams.get(i).reset();
            objectOutputStreams.get(i).writeObject(serverPacket);
            Thread.sleep(25);
            sendToAll(Actions.CHAT, "Teraz rzuca: " + players.get(i).getNickname());
            Thread.sleep(25);
            waitForEndRound();
            if (i + 1 >= players.size()) i = -1;
        }
    }


    private void sendToAll(String action, String msg) throws IOException, InterruptedException {
        for (ObjectOutputStream oos : objectOutputStreams) {
            serverPacket.setAction(action);
            serverPacket.getChatText().setMessage(msg);
            oos.reset();
            oos.writeObject(serverPacket);
        }
    }

    private void waitForEndRound() throws IOException, InterruptedException {
        while (true) {
            synchronized (this) {
                if (finishRoundPlayer) {
//                    sendToAll(Actions.UPDATE_PLAYERS, null);
                    sendToAll(Actions.END_OF_ROUND, null);
//                    sendToAll(Actions.CLEAR_CANVAS, null);
                    Thread.sleep(50);
                    finishRoundPlayer = false;
                    break;
                }
            }
        }
    }

    public synchronized List<ObjectOutputStream> getObjectOutputStreams() {
        return objectOutputStreams;
    }

    public synchronized int getPlayersReady() {
        return playersReady;
    }

    public synchronized void setPlayersReady(Integer playersReady) {
        this.playersReady = playersReady;
    }

    public synchronized int getPlayersConnected() {
        return playersConnected;
    }

    public synchronized void setPlayersConnected(int playersConnected) {
        this.playersConnected = playersConnected;
    }

    public synchronized List<Player> getPlayers() {
        return players;
    }

    public synchronized void setFinishRoundPlayer(boolean finishRoundPlayer) {
        this.finishRoundPlayer = finishRoundPlayer;
    }

}
