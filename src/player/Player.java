package player;

import controller.Debug;

public class Player extends Dealer {
	private int id;
	private int credit;
	private int bet = 0;
	private boolean playing;
	private boolean canBet;

	/**
	 * Constructor for the class
	 * 
	 * @param id
	 * @param credit
	 */
	public Player(int id, int credit) {
		super();
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
	public boolean setBettingAmount(int value) {

		if (getCredit() < value) {
			this.bet = 0;
			return false;
		} else {
			this.bet = value;
			Debug.print(
					"PROCESSDATA - PLAYERCONTROLLER - BET",
					"Bet amount set " + this.bet + " by player "
							+ this.getName());
			debit(value);
			return true;
		}
	}

	/**
	 * set credit to a value;
	 * 
	 * @param value
	 */
	private void setCredit(int value) {
		this.credit = value;
	}

	/**
	 * Debits the account.
	 */
	private void debit(int value) {
		if (value > 0)
			this.credit -= value;
	}
	
	public void credit(int value) {
		if(value > 0)
			this.credit += value;
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

	/**
	 * returns the current credit that the player owns
	 * @return int value for credit
	 */
	public int getCredit() {
		return this.credit;
	}
}