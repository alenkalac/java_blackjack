package model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import view.Card;
import view.Player;

public class GameModel {
	private Socket socket = null;
	private DataOutputStream outStream;
	private DataInputStream inStream;
	private ArrayList<Player> players;
	private Player dealer;
	private ArrayList<Card> cards;
	
	/**
	 * Constructor for the game model. initialises things
	 */
	public GameModel() {
		players = new ArrayList<>();
		cards = new ArrayList<>();
		dealer = new Player(-1, 20000);
		dealer.setName("Dealer");
		for(int i = 0; i < 4; i++) {
			players.add(null);
		}
	}
	
	/**
	 * Set the socket that the client will use
	 * @param socket
	 * @throws IOException
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
		try {
			outStream = new DataOutputStream(this.socket.getOutputStream());
			inStream = new DataInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns an active socket for this client
	 * @return socket
	 */
	public Socket getSocket() {
		return this.socket;
	}
	
	/**
	 * Send a message to the server. this is a string message
	 * @param message
	 */
	public void sendMessage(String message) {
		try {
			outStream.writeUTF(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the message from the incoming stream from the server
	 * @return 
	 */
	public String getMessage() {
		try {
			return inStream.readUTF();
		} catch (IOException e) {}
		
		return "";
	}
	
	/**
	 * add player to the array list
	 * @param index location to add a player to
	 * @param p player instance
	 */
	public void addPlayer(int index, Player p) {
		players.set(index, p);
	}
	
	/**
	 * Get all the players as the array list
	 * @return list of players
	 */
	public ArrayList<Player> getPlayers() {
		return this.players;
	}

	/**
	 * get all the cards currently in play
	 * @return a list of cards
	 */
	public ArrayList<Card> getCards() {
		return cards;
	}

	/**
	 * Add a card to the array list
	 * @param card
	 */
	public void addCard(Card card) {
		this.cards.add(card);
	}

	/**
	 * Set cards array to another array
	 * @param cards
	 */
	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}

	/**
	 * return the dealer instance
	 * @return
	 */
	public Player getDealer() {
		return this.dealer;
	}
}
