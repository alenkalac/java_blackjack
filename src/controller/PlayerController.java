package controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import player.Player;
import server.GameServer;

public class PlayerController implements Runnable {

	/**
	 * Player instance
	 */
	private Player player;

	/**
	 * Socket instance
	 */
	private Socket socket;

	/**
	 * Game server instance to access things like list of players currently
	 * connected
	 */
	private GameServer serv;

	/**
	 * a variable to keep track if the player is connected or not.
	 */
	private boolean disconnected = false;

	private int action = 0;

	/**
	 * Constructor that accepts starting credits for a player and a socket to
	 * use to interact with a player
	 * 
	 * @param socket
	 *            a socket connection
	 */
	public PlayerController(int id, Socket socket, GameServer serv) {
		this.player = new Player(id, 2000);
		this.socket = socket;
		this.serv = serv;

		new Thread(this).start();
	}

	/**
	 * Set the player as disconnected. so it can be removed later
	 * 
	 * @param boolean disconnected
	 */
	public void setDisconnected(boolean b) {
		this.disconnected = true;
	}

	/**
	 * Return if the player is disconnected.
	 * 
	 * @return boolean disconnected
	 */
	public boolean isDisconnected() {
		return this.disconnected;
	}

	/**
	 * Getter for the socket
	 * 
	 * @return Socket socket for a player to use to communicate with the server
	 */
	public Socket getSocket() {
		return this.socket;
	}

	/**
	 * Send data from the server to the player
	 * 
	 * @param String
	 *            data to send
	 */
	public void send(String data) {
		try {
			if (this.getSocket().isConnected() || !this.getSocket().isClosed()) {
				DataOutputStream outStream = new DataOutputStream(
						this.socket.getOutputStream());
				Debug.print("PLAYERCONTROLLER - SEND ", data);
				outStream.writeUTF(data);
			}
		} catch (IOException e) {
			this.disconnected = true;
			System.out.println("Connection lost for "
					+ this.getPlayer().getName());
		}
	}

	/**
	 * Send a message to all connected players.
	 * 
	 * @param String
	 *            data to send to players
	 */
	public void sendToAllPlayers(String data) {
		ArrayList<PlayerController> players = serv.getAllPlayers();
		for (PlayerController p : players) {
			if (p == null)
				continue;
			p.send(data);
		}
	}

	/**
	 * returns a player instance
	 * 
	 * @return Player
	 */
	public Player getPlayer() {
		return this.player;
	}

	/**
	 * Set if a player can place a bet or not
	 * 
	 * @param boolean b
	 */
	public void setCanBet(boolean b) {
		this.player.setCanBet(b);
	}

	/**
	 * Returns if the played can place a bet
	 * 
	 * @return boolean
	 */
	public boolean getCanBet() {
		return this.player.getCanBet();
	}

	public void deal() {
		this.send("DEAL ");
	}

	/**
	 * Processes the data that's coming from the player
	 * 
	 * @param data
	 */
	public void processData(String data) {
		String[] tokens = data.split("\\s");

		if (tokens[0].equals("SETNAME")) {
			player.setName(tokens[1]);
			sendToAllPlayers("JOIN " + this.getPlayer().getId() + " "
					+ this.getPlayer().getName());
		}
		
		else if(tokens[0].equals("GETPLAYERLIST")) {
			String players = "";
			ArrayList<PlayerController> allPlayers = serv.getAllPlayers();
			for(PlayerController p : allPlayers) {
				if(p == null)
					players += "NULL ";
				else
					players += p.getPlayer().getName() + " ";
			}
			
			this.send("PLAYERLIST " + players);
		}

		else if (tokens[0].equals("BET")) {
			int bet = Integer.parseInt(tokens[1]);
			if (this.player.getCanBet()) {
				boolean ableToBet = this.player.setBettingAmount(bet);
				if(ableToBet)
					this.sendToAllPlayers("BETMADE " + this.player.getId() + " "
						+ bet);
				else
					this.player.setCanBet(false);
			}
		} 
		
		else if (tokens[0].equals("CHAT")) {
			String chatMessage = player.getName() + ": " + data.substring(5);
			this.sendToAllPlayers("CHAT " + chatMessage);
		}
		
		else if(tokens[0].equals("ACTION")) {
			String action = tokens[1];
			
			
			switch(action) {
			case "STAY":
				this.setAction(1);
				break;
			case "HIT":
				this.setAction(2);
				break;
			case "DOUBLE":
				this.setAction(3);
				break;
			default:
				this.setAction(0);
				break;
			}
		}

	}

	/**
	 * returns an action that the player chose from action dialog
	 * @return
	 */
	public int getAction() {
		return action;
	}

	/**
	 * sets the action for the player such as hit, stay or double
	 * @param action
	 */
	public void setAction(int action) {
		this.action = action;
	}

	@Override
	public void run() {
		while (!this.disconnected) {
			try {
				DataInputStream data = new DataInputStream(
						this.socket.getInputStream());

				String recievedData = data.readUTF();
				processData(recievedData);

				Thread.sleep(33);

			} catch (IOException | InterruptedException e) {
			}
		}
	}
}
