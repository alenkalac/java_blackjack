package player;

import controller.Debug;
import view.Card;
import view.Hand;

public class Dealer {
	protected String name;
	protected Hand hand;

	public Dealer() {
		this.hand = new Hand();
		name = "";
	}

	/**
	 * returns a player hand
	 * 
	 * @return ArrayList<Card> hand
	 */
	public Hand getHand() {
		return this.hand;
	}

	/**
	 * Adds a card to the hand
	 * 
	 * @param card
	 */
	public void addToHand(Card card) {
		this.hand.addCard(card);
	}

	/**
	 * Set the player's name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
		Debug.print("PPROCESSDATA - PLAYERCONTROLLER - SETNAME",
				"Player changed name to " + this.getName());
	}

	/**
	 * Get the player's name
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * create a new hand instance
	 */
	public void newHand() {
		this.hand = new Hand();

	}
}
