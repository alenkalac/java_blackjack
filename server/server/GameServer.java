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

import player.Dealer;
import player.Player;
import view.Card;
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
	@SuppressWarnings("unused")
	private GameServer frame;
	private ArrayList<PlayerController> players;
	private Dealer dealer;
	private boolean inGame = false;
	private GameServer gameServ;
	private JTextArea jta;

	private final int MAX_NUMBER_PLAYERS = 4;
	private final int TIME_BETWEEN_GAME = 20;

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
		this.dealer = new Dealer();
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
					dealCardsToDealer();
					
					dealCardsToAllPlayers();

					// STEP 5: ask what player wants to do
					requestActionFromPlayers();
					
					// STEP 6: dealer draws cards until bust or over 17
					dealerDrawUntil17OrOver();

					// STEP 7: check winnings
					checkWinnings();
					
					// STEP LAST: clear all bets
					clearPlayerBets();
				}
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
		
		/**
		 * check winning conditions for each player
		 */
		private void checkWinnings() {
			for (PlayerController player : players) {
				if (playerNotAvailable(player))
					continue;

				System.out.println("Dealers hand " + dealer.getHand().getCardValue());
				System.out.println("Player hand " + player.getPlayer().getHand().getCardValue());
				
				if(player.getPlayer().getHand().isBust()) {
					sendToAllPlayers("LOST " + player.getPlayer().getId());
					continue;
				}
				
				else if(dealer.getHand().isBust()) {
					sendToAllPlayers("WIN " + player.getPlayer().getId());
					player.getPlayer().credit(player.getPlayer().getBettingAmount() * 2);
					continue;
				}
				
				else if(player.getPlayer().getHand().getCardValue() > dealer.getHand().getCardValue()) {
					sendToAllPlayers("WIN " + player.getPlayer().getId());
					player.getPlayer().credit(player.getPlayer().getBettingAmount() * 2);
					continue;
				}
				
				else if(player.getPlayer().getHand().getCardValue() == dealer.getHand().getCardValue()){
					sendToAllPlayers("PUSH " + player.getPlayer().getId());
					player.getPlayer().credit(player.getPlayer().getBettingAmount());
					continue;
				}
				
				else {
					sendToAllPlayers("LOST " + player.getPlayer().getId());
					continue;
				}
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
				
				boolean stand = false;
				//System.out.println("r " + player.getPlayer().getName());

				while (player.getPlayer().getHand().getCardValue() < 21) {
					
					//break out of the loop if stand
					if(stand)
						break;
					
					player.send("ACTIONREQUEST");
					int pAction = 0; //default
					
					//wait for player action using a while loop 
					while (player.getAction() == 0) {
						try { Thread.sleep(33); } catch (InterruptedException e) {}
					}
					
					pAction = player.getAction();
					player.setAction(0); //reset for next round
					
					switch (pAction) {
						case 1:
							stand = true;
							break;
						case 2:
							dealCardToPlayer(player);
							continue;
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
			int timeToStart = TIME_BETWEEN_GAME;

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

	private void dealCardToPlayer(PlayerController p) {
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
			sleepForSecond();
		}
	}
	
	
	private void dealerDrawUntil17OrOver() {
		while(dealer.getHand().getCardValue() < 17) {
			dealCardsToDealer();
			sleepForSecond();
		}
	}
	
	private void dealCardsToDealer() {
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
			p.getPlayer().newHand();
			p.setAction(0);
			dealer.newHand();
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
	
	private void sleepForSecond() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}