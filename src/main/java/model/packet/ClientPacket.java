package model.packet;

import model.Pawn;
import model.player.Player;

import java.util.ArrayList;

public class ClientPacket extends Packet {

	private Player player;
	private ArrayList<Pawn> pawnsList;

	public ClientPacket(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public ArrayList<Pawn> getPawns() {
		return pawnsList;
	}

	public void setPawns(ArrayList<Pawn> pawnsList) {
		this.pawnsList = pawnsList;
	}

}
