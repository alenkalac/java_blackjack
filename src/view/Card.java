package view;

import java.awt.image.BufferedImage;

public class Card {
	
	private int value = 0;
	private BufferedImage image = null;
	private int xpos = 0;
	private int ypos = 0;
	private int playerId = 0;

	/**
	 * Card constructor taking in a value and image of the card
	 * @param value
	 * @param graphic
	 */
	public Card(int value, BufferedImage graphic) {
		this.value = value;
		this.image = graphic;
	}
	
	/**
	 * overloaded constructor for the card, just taking a value
	 * @param value
	 */
	public Card(int value) {
		if(value < 10)
			this.value = value;
		else
			this.value = 10;
	}
	
	/**
	 * return the value of the card
	 * @return
	 */
	public int getValue() {
		return this.value;
	}
	
	/**
	 * return the graphic of the card
	 * @return
	 */
	public BufferedImage getImage() {
		return this.image;
	}

	/**
	 * get the x pos of the card
	 * @return
	 */
	public int getXpos() {
		return xpos;
	}

	/**
	 * set the x position for the card
	 * @param xpos
	 */
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	/**
	 * get y pos of the card
	 * @return
	 */
	public int getYpos() {
		return ypos;
	}

	/**
	 * set the y pos of the card
	 * @param ypos
	 */
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	/**
	 * get the player id who owns this card
	 * @return
	 */
	public int getPlayerId() {
		return playerId;
	}

	/**
	 * set the player id for the card
	 * @param playerId
	 */
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
}
