package view;

import java.awt.Graphics;
import java.util.ArrayList;

public class Hand{

	private ArrayList<Card> cards;
	private Graphics graphics;
	
	public Hand(Graphics g) {
		this.graphics = g;
		cards = new ArrayList<>();
	}
	
	public Hand() {
		cards = new ArrayList<>();
	}
	
	public void addCard(Card c) {
		cards.add(c);
	}
	
	public void render() {
		for(int i = 0; i < cards.size(); i++) {
			this.graphics.drawImage(cards.get(i).getImage(), i * 45, 0, null);
		}
	}
	
	/**
	 * Calculate the total value of cards in the hand
	 * @return int value
	 */
	public int getCardValue() {
		int value = 0;
		for(Card c: this.cards) {
			value += c.getValue();
		}
		
		return value;
	}
	
	public boolean isBust() {
		if(this.getCardValue() > 21)
			return true;
		return false;
	}
	
}
