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
	
	public GameModel() {
		players = new ArrayList<>();
		cards = new ArrayList<>();
		dealer = new Player(-1, 20000);
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
	
	public String getMessage() {
		try {
			return inStream.readUTF();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return "";
	}
	
	public void addPlayer(int index, Player p) {
		players.set(index, p);
	}
	
	public ArrayList<Player> getPlayers() {
		return this.players;
	}

	public ArrayList<Card> getCards() {
		return cards;
	}

	public void addCard(Card card) {
		this.cards.add(card);
	}

	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
		
	}

	public Player getDealer() {
		return this.dealer;
	}
}
