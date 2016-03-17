package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JTextField;

import player.Player;
import model.GameModel;
import model.GraphicLibrary;
import view.Card;
import view.Frame;

public class GameController {

	private Frame gameView;
	private GameModel gameModel;
	private final int CARD_OFFSET = 40;

	private int seat1, seat2, seat3, seat4, dealer = 0;

	/**
	 * Constructor for the controller
	 * 
	 * @param gameView
	 * @param gameModel
	 */
	public GameController(Frame gameView, GameModel gameModel) {
		this.gameModel = gameModel;
		this.gameView = gameView;

		this.gameView.setChatBoxAction(new ChatBoxListener());
		this.gameView.getGamePanel().setDealer(gameModel.getDealer());

		new Thread(new GameThread()).start();
	}

	/**
	 * A thread class that runs the game on a seperate thread
	 * 
	 * @author Alen
	 *
	 */
	public class GameThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				processMessage();
			}
		}
		
		/**
		 * Add a new player to the table. 
		 * @param id Player ID
		 * @param name Player Name
		 * @return returns null if player has no name or returns a player obj if player is added
		 */
		private Player addNewPlayer(String id, String name) {
			
			if(name.equals("NULL"))
				return null;
			
			Debug.print("PROCESSMESSAGE JOIN", "id-" + id + " name-" + name);
			
			int pid = Integer.parseInt(id);
			Player p = new Player(pid, 2000);
			p.setName(name);
			gameModel.addPlayer(pid, p);
			gameView.getGamePanel().setPlayers(gameModel.getPlayers());
			
			return p;
			
		}
		
		/**
		 * Resets all the seats in the client window
		 */
		private void resetSeats() {
			seat1 = 0;
			seat2 = 0;
			seat3 = 0;
			seat4 = 0;
			dealer = 0;
		}

		/**
		 * Process a message that's sent by the server and figure out what to do
		 */
		public void processMessage() {

			String data = gameModel.getMessage();
			String[] tokens = data.split("\\s");
			if (data.isEmpty())
				return;

			if(tokens[0].equals("PLAYERLIST")) {
				for(int i = 1; i < tokens.length; i++){
					String sid = "" + (i-1);
					this.addNewPlayer(sid, tokens[i]);
				}
			}
			
			else if (tokens[0].equals("GAMESTART")) {
				gameView.getGamePanel().printText("Game is starting in " + tokens[1]);
				if (tokens[1].equals("0"))
					gameView.getGamePanel().printText("");
			}

			else if (tokens[0].equals("PLACEBET")) {
				String amount = gameView.getGamePanel().showBetDialog();
				//Debug.print("PROCESSMESSAGE PLACEBET", amount);
				gameModel.sendMessage("BET " + amount);
			}
			
			else if(tokens[0].equals("JOIN")) {
				//JOIN SEAT_ID NAME
				Player p = this.addNewPlayer(tokens[1], tokens[2]);
				gameView.getGamePanel().addChatLineToArray("Server: Player " + p.getName() + " has Joined the room ");
			}

			else if (tokens[0].equals("BETMADE")) {
				int index = Integer.parseInt(tokens[1]);
				int amount = Integer.parseInt(tokens[2]);
				gameModel.getPlayers().get(index).setBettingAmount(amount);
				Debug.print("PROCESSMESSAGE PLACEBET " + amount);
			}
			
			else if(tokens[0].equals("DEAL")) {
				//DEAL ID SUIT VALUE
				int cardSuit = Integer.parseInt(tokens[2]);
				int cardValue = Integer.parseInt(tokens[3]);
				int playerId = -1;
				int x = 0, y = 0;
				
				if(!tokens[1].equals("-1"))
					playerId = Integer.parseInt(tokens[1]);
				
				switch(playerId) {
				case -1:
					x = 370+ (dealer * CARD_OFFSET); 
					y = 70;
					dealer++;
					break;
				case 0:
					x = 645+ (seat1 * CARD_OFFSET); 
					y = 390 ;
					seat1++;
					break;
				case 1:
					x = 480 + (seat2 * CARD_OFFSET); 
					y = 455;
					seat2++;
					break;
				case 2: 
					x = 250 + (seat3 * CARD_OFFSET); 
					y = 455;
					seat3++;
					break;
				case 3:
					x = 90 + (seat4 * CARD_OFFSET); 
					y = 390;
					seat4++;
					break;
				default:
					break;
			}
				
				int actualCardValue = (cardValue > 10) ? 10 : cardValue;
				Card card = new Card(actualCardValue, GraphicLibrary.getCardGraphic(cardSuit, cardValue));
				card.setPlayerId(playerId);
				card.setXpos(x);
				card.setYpos(y);
				if(playerId == -1)
					gameModel.getDealer().addToHand(card);
				else
					gameModel.getPlayers().get(playerId).addToHand(card);
				
				gameModel.addCard(card);
				gameView.getGamePanel().setCards(gameModel.getCards());
			}

			else if (tokens[0].equals("CHAT")) {
				gameView.getGamePanel().addChatLineToArray(data.substring(5));
			}
			
			else if(tokens[0].equals("WIN")) {
				int index = Integer.parseInt(tokens[1]);
				Player p = gameModel.getPlayers().get(index);
				
				p.credit(p.getBettingAmount() * 2);
			}
			
			else if(tokens[0].equals("PUSH")) {
				int index = Integer.parseInt(tokens[1]);
				Player p = gameModel.getPlayers().get(index);
				
				p.credit(p.getBettingAmount());
			}
			
			else if(tokens[0].equals("ENDGAME")) {
				gameModel.setCards(new ArrayList<Card>());
				gameView.getGamePanel().setCards(gameModel.getCards());
				
				gameModel.getDealer().newHand();
				
				for(int i = 0; i < gameModel.getPlayers().size(); i++) {
					if(gameModel.getPlayers().get(i) == null)
						continue;
					gameModel.getPlayers().get(i).newHand();
				}
				
				this.resetSeats();
			}
			
			else if (tokens[0].equals("ACTIONREQUEST")) {
				String option = gameView.getGamePanel().showActionDialog();
				option = option.toUpperCase();
				gameModel.sendMessage("ACTION " + option);
			}
		}
	}

	/**
	 * Class for chat box listener
	 * 
	 * @author Alen
	 *
	 */
	public class ChatBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent inputField) {
			JTextField chatInput = (JTextField) inputField.getSource();
			gameModel.sendMessage("CHAT " + chatInput.getText());
			chatInput.setText("");

		}

	}

}
