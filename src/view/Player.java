package view;

import controller.Debug;

public class Player {
	private int id;
	private int credit;
	private int bet = 0;
	private boolean playing;
	private boolean canBet;

	private Hand hand;
	private String name;

	/**
	 * Constructor for the class
	 * 
	 * @param id
	 * @param credit
	 */
	public Player(int id, int credit) {
		init(id, credit);
	}

	/**
	 * Inits the class and sets values to private properties
	 * 
	 * @param id
	 * @param credit
	 */
	private void init(int id, int credit) {
		this.credit = credit;
		this.playing = false;
		this.id = id;
		this.hand = new Hand();
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
	 * returns if the player is betting
	 * 
	 * @return boolean
	 */
	public boolean hasBet() {
		if (bet > 0)
			return true;

		return false;
	}
	
	/**
	 * get the betting amount made by the player
	 * 
	 * @return
	 */
	public int getBettingAmount() {
		return this.bet;
	}

	/**
	 * set the betting value
	 * 
	 * @param value
	 */
	public void setBettingAmount(int value) {
		this.bet = value;
		Debug.print("PROCESSDATA - PLAYERCONTROLLER - BET", "Bet amount set "
				+ this.bet + " by player " + this.getName());
	}

	/**
	 * Set if the player is currently playing a game.
	 * 
	 * @param playing
	 */
	public void setIsPlaying(boolean playing) {
		this.playing = playing;
	}

	/**
	 * Get if the player is currently in a game
	 * 
	 * @return
	 */
	public boolean getIsPlaying() {
		return this.playing;
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
	 * Sets if the player is able to bet, if they join too late, they cant bet
	 * until a new game starts
	 * 
	 * @param b
	 */
	public void setCanBet(boolean b) {
		this.canBet = b;
	}

	/**
	 * returns if the player is able to place a bet.
	 * 
	 * @return boolean
	 */
	public boolean getCanBet() {
		return this.canBet;
	}

	/**
	 * returns player's ID
	 * 
	 * @return
	 */
	public int getId() {
		return this.id;
	}

	public void newHand() {
		this.hand = new Hand();
		
	}
}