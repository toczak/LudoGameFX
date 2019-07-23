package model.player;

import java.io.Serializable;

public class Player implements Serializable {

	private String nickname;
	private int color; //1 - blue, 2 - yellow, 3 - green, 4 - red
	private int score;
	private int id;
	private boolean isDrawing = false;
	private boolean isReady = false;
	private String lastAction;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public boolean isDrawing() {
		return isDrawing;
	}

	public void setDrawing(boolean drawing) {
		isDrawing = drawing;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean ready) {
		isReady = ready;
	}

	public String getLastAction() {
		return lastAction;
	}

	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}

	public String getColor() {
		switch (color){
			case 1:
				return "Niebieski";
			case 2:
				return "Żółty";
			case 3:
				return "Zielony";
			case 4:
				return "Czerwony";
		}
		return "brak";
	}

	public void setColor(int color) {
		this.color = color;
	}
}
