package model.packet;

import model.Pawn;
import model.player.Player;

import java.util.ArrayList;

public class ServerPacket extends Packet {

	private String action;
	private ArrayList<Player> players;
	ArrayList<Pawn> pawnsList;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public ArrayList<Pawn> getPawns() {
		return pawnsList;
	}

	public void setPawns(ArrayList<Pawn> pawnsList) {
		this.pawnsList = pawnsList;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
}
