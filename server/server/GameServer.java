package server;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import view.Card;
import view.Player;
import controller.PlayerController;

/**
 * Base server class that encapsulates all server related things
 * 
 * @author Alen Kalac
 *
 */
@SuppressWarnings("serial")
public class GameServer extends JFrame {

	private ServerSocket serv;
	private GameServer frame;
	private ArrayList<PlayerController> players;
	private Player dealer;
	private boolean inGame = false;
	private GameServer gameServ;
	private JTextArea jta;

	private final int MAX_NUMBER_PLAYERS = 4;

	/**
	 * Constructor
	 * 
	 * @param port
	 *            port to bind to
	 * @throws IOException
	 */
	GameServer(int port) throws IOException {
		init(port);
	}

	/**
	 * Initialise the server, bind to the port and creates a new instance of the
	 * ServerSocket
	 * 
	 * @param port
	 *            port number to bind to, passed down from the constructor
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private void init(int port) throws IOException {

		setupGui();

		this.gameServ = this;
		this.serv = new ServerSocket(port);
		this.players = new ArrayList<PlayerController>(4);
		this.dealer = new Player(-1, 5000000);
		for (int i = 0; i < MAX_NUMBER_PLAYERS; i++) {
			players.add(i, null);
			writeMessage("init " + i + " seat");
		}

		Thread acceptConnections = new Thread(new Listener());
		acceptConnections.start();

		@SuppressWarnings("unused")
		Scanner keyboard = new Scanner(System.in);
	}

	/**
	 * Sets up the gui for the server.
	 */
	private void setupGui() {

		jta = new JTextArea();
		jta.setEditable(false);

		JScrollPane jsp = new JScrollPane(jta);

		this.setSize(350, 150);
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("BlackJack Server");
		this.add(jsp, BorderLayout.CENTER);
		this.frame = this;

		this.setVisible(true);
	}

	/**
	 * Write a message in the gui
	 * 
	 * @param String
	 *            message to add to the text area.
	 */
	public void writeMessage(String msg) {
		this.jta.append(msg + "\n\r");
		this.jta.setAutoscrolls(true);
		this.jta.setCaretPosition(jta.getDocument().getLength());
	}

	/**
	 * true if is in game, false if not in game
	 * 
	 * @return boolean
	 */
	public boolean isInGame() {
		return this.inGame;
	}

	/**
	 * sets weather the game has started or not, so it doesnt start twice
	 * 
	 * @param boolean ingame
	 */
	public void setInGame(boolean ingame) {
		this.inGame = ingame;
	}

	/**
	 * gets the list of all players
	 * 
	 * @return ArrayList
	 */
	public ArrayList<PlayerController> getAllPlayers() {
		return this.players;
	}

	/**
	 * Listener class that will listen for new connections
	 * 
	 * @author Alen
	 *
	 */
	class Listener implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Socket socket = serv.accept();
					addPlayerToPlayerList(socket);
					// TODO: Handle room full
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	/**
	 * Adds the player to the list array
	 * 
	 * @param s
	 *            socket for the player
	 */
	private void addPlayerToPlayerList(Socket socket) {
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) == null) {
				PlayerController p = new PlayerController(i, socket, gameServ);
				players.set(i, p);

				writeMessage("Player has Joined at seat " + i);

				break;
			}
		}

		if (!isInGame()) {
			setInGame(true);
			new Game();

		}
	}

	/**
	 * Inner class to handle the game server sided.
	 * 
	 * @author Alen
	 *
	 */
	class Game implements Runnable {

		/**
		 * Constructor
		 */
		public Game() {
			new Thread(this).start();
		}

		@Override
		public void run() {
			try {
				while (true) {

					// Step 0: Manage disconnected players, remove them from the
					// array
					checkForDisconnecredPlayersAndRemove();

					// STEP 1: start the count down
					startGameCountdown();

					// STEP 2: ask for bets
					startBettingPhase();

					// STEP 3: wait for all the bets
					checkAllBetsPlaced();

					// STEP 4: deal 2 cards to each player
					dealCardsToAllPlayers();
					dealCardsToAllPlayers();

					// STEP 5: ask what player wants to do
					requestActionFromPlayers();

					// STEP LAST: clear all bets
					clearPlayerBets();
				}
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}

		/**
		 * Remove disconnected Players from the arrayList so that other players
		 * can join
		 */
		private void checkForDisconnecredPlayersAndRemove() {
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i) == null)
					continue;
				if (players.get(i).isDisconnected()) {
					writeMessage("Player "
							+ players.get(i).getPlayer().getName()
							+ " has disconnected.");
					players.set(i, null);
				}
			}
		}

		/**
		 * Request action from players, such as hit, stand or double
		 */
		private void requestActionFromPlayers() {
			for (PlayerController player : players) {
				if (playerNotAvailable(player))
					continue;

				while (player.getPlayer().getHand().getCardValue() < 21) {
					player.send("ACTIONREQUEST");
					
					//wait for player action using a while loop 
					while (player.getAction() == 0) {
						try { Thread.sleep(33); } catch (InterruptedException e) {}
					}

					
					switch (player.getAction()) {
						case 1:
							break;
						case 2:
							dealCardToPlayer(player);
							break;
						case 3:
							player.getPlayer().setBettingAmount( player.getPlayer().getBettingAmount() * 2);
							dealCardToPlayer(player);
							continue;
						default:
							break;
					}
					
				}

			}
			for (int i = 0; i < players.size(); i++) {
				if (players.get(i) == null)
					continue;
				if (players.get(i).isDisconnected()) {
					writeMessage("Player "
							+ players.get(i).getPlayer().getName()
							+ " has disconnected.");
					players.set(i, null);
				}
			}
		}

		/**
		 * Counts down from 30 to allow other clients to connect
		 * 
		 * @throws InterruptedException
		 */
		private void startGameCountdown() throws InterruptedException {
			int timeToStart = 30;

			while (timeToStart > 0) {
				timeToStart--;
				for (PlayerController p : players) {
					if (playerNotAvailable(p))
						continue;
					p.send("GAMESTART " + timeToStart);
					p.getPlayer().setIsPlaying(true);
				}
				Thread.sleep(1000);
			}
		}

		/**
		 * Sends a bet packet to all players who are currently connected so that
		 * they can place a bet
		 */
		private void startBettingPhase() {
			for (PlayerController p : players) {
				if (playerNotAvailable(p))
					continue;
				p.send("PLACEBET");
				p.setCanBet(true);
			}
		}

		/**
		 * Waits for all players to place a bet.
		 * 
		 * @throws InterruptedException
		 */
		private void checkAllBetsPlaced() throws InterruptedException {
			for (PlayerController p : players) {
				if (playerNotAvailable(p))
					continue;
				if (!p.getCanBet())
					continue;
				while (!p.getPlayer().hasBet()) {
					Thread.sleep(500);
				}
			}
		}
	}

	public void dealCardToPlayer(PlayerController p) {
		int cardSuit = new Random().nextInt(3) + 1;
		int cardValue = new Random().nextInt(12) + 1;

		Card card = new Card(cardValue);
		p.getPlayer().addToHand(card);

		p.sendToAllPlayers("DEAL " + p.getPlayer().getId() + " " + cardSuit
				+ " " + cardValue);
	}

	private void dealCardsToAllPlayers() {
		for (PlayerController p : players) {
			if (playerNotAvailable(p))
				continue;

			dealCardToPlayer(p);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		int cardSuit = new Random().nextInt(3) + 1;
		int cardValue = new Random().nextInt(12) + 1;
		dealer.addToHand(new Card(cardValue));
		sendToAllPlayers("DEAL -1 " + cardSuit + " " + cardValue);
	}

	/**
	 * Clear all the players so that a new game can start
	 */
	private void clearPlayerBets() {
		for (PlayerController p : players) {
			if (playerNotAvailable(p))
				continue;
			p.getPlayer().setIsPlaying(false);
			p.setCanBet(false);
			p.send("ENDGAME");
			p.getPlayer().setBettingAmount(0);
		}
	}

	private boolean playerNotAvailable(PlayerController p) {
		if (p == null)
			return true;
		if (p.isDisconnected())
			return true;
		return false;
	}

	private void sendToAllPlayers(String msg) {
		for (PlayerController p : players) {
			if (playerNotAvailable(p))
				continue;
			p.send(msg);
		}
	}
}