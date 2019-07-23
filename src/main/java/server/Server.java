package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

    private volatile int playersOnline = 0;
    static final int MIN_PLAYERS = 2;
    static final int MAX_PLAYERS = 4;
    private volatile boolean isPlayerJoining = true;
    private List<Room> roomsList = new ArrayList<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new Thread(new Server()).start();
    }

    @Override
    public void run() {
        System.out.println("Oczekiwanie na połączenie...");
        try (ServerSocket serverSocket = new ServerSocket(5054)) {
//            if (playersOnline % MAX_PLAYERS == 0 && isPlayerJoining) {
//                Room room = new Room(this, serverSocket);
//                					roomsList.add(room);
//
//                new Thread(room).start();
//                isPlayerJoining = false;
//            }
            int i = 0;
			while (true) {
				if (i==0) {

					Room room = new Room(this, serverSocket);
//					roomsList.add(room);
					new Thread(room).start();
					i++;
					isPlayerJoining = false;
				}
			}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setPlayersOnline(int playersOnline) {
        this.playersOnline = playersOnline;
    }

    public synchronized int getPlayersOnline() {
        return playersOnline;
    }

    public synchronized void setPlayerJoining(boolean val) {
        this.isPlayerJoining = val;
    }

    public synchronized List<Room> getRoomsList() {
        return roomsList;
    }
}
