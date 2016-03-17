package view;

import java.awt.image.BufferedImage;

public class Card {
	
	private int value = 0;
	private BufferedImage image = null;
	private int xpos = 0;
	private int ypos = 0;
	private int playerId = 0;

	
	public Card(int value, BufferedImage graphic) {
		this.value = value;
		this.image = graphic;
	}
	
	public Card(int value) {
		if(value < 10)
			this.value = value;
		else
			this.value = 10;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public BufferedImage getImage() {
		return this.image;
	}

	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
}
