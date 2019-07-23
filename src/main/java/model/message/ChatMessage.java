package model.message;

import java.io.Serializable;

public class ChatMessage implements Serializable {

	private String nickname;
	private String message;

	@Override
	public String toString() {
		return message;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
