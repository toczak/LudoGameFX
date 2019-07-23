package model.packet;

import model.message.ChatMessage;

import java.io.Serializable;

public class Packet implements Serializable {

	private ChatMessage chatText = new ChatMessage();
	private int value;

	public ChatMessage getChatText() {
		return chatText;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
