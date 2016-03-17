package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Player.Dealer;
import Player.Player;
import model.GraphicLibrary;

@SuppressWarnings("serial")
public class GamePane extends JPanel implements Runnable {
	
	private final int CHAT_NUM_LINES_TO_DISPLAY = 6;
	
	private String textToDisplay = "";
	private ArrayList<String> chatMessageArray;
	private ArrayList<Player> players;
	private ArrayList<Card> cards;
	private Dealer dealer;
	
	/**
	 * Constructor for the class
	 */
	public GamePane() {
		players = new ArrayList<>();
		cards = new ArrayList<>();
		start();
	}
	
	/**
	 * update the panel and repaint it. this is done every 33ms, or about 60 times a second
	 */
	public void update() {
		this.repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paintComponents(g);
		
		//clear screen
		clearScreen(g);
		
		//paint
		
		//Back layer
		paintBackground(g);
		paintChatTextIndicator(g);
		
		//Mid Layer
		paintPlayers(g);
		paintCards(g);
		paintChat(g);
		
		//Front Layer
		paintText(g);
	}
	
	/**
	 * printing text to the game screen
	 * @param Graphics g graphics instance 
	 */
	private void paintText(Graphics g) {
		if(textToDisplay.equals(""))
			return;
		Font f = new Font("Arial", Font.BOLD, 50);
		g.setFont(f);
		g.setColor(Color.GREEN);
		
		FontMetrics m = g.getFontMetrics(f);
		
		int x = (800 - m.stringWidth(textToDisplay)) / 2;
		int y = ((600 - m.getHeight()) / 2);
		
		g.drawString(textToDisplay, x, y);
	}
	
	/**
	 * writes a chat box just above the chat box area
	 * @param g
	 */
	private void paintChatTextIndicator(Graphics g){ 
		Font f = new Font("Arial", Font.BOLD, 30);
		g.setFont(f);
		
		g.setColor(Color.RED);
		g.fillRect(0, 570, 150, 100);
		
		g.setColor(Color.BLACK);
		g.drawString("Chat Box", 5, 595);
	}
	
	/**
	 * paint the background, this is a playing table
	 * @param g
	 */
	private void paintBackground(Graphics g) {
		g.drawImage(GraphicLibrary.getBackgroundGraphic(), 0, 0, null);
	}
	
	/**
	 * clear screen and remove everything off it
	 * @param g
	 */
	private void clearScreen(Graphics g) { 
		g.clearRect(0, 0, 800, 600);
	}
	
	/**
	 * Used to render names on the screen.
	 * TODO: render player's names to the screen
	 * @param g
	 */
	private void paintPlayers(Graphics g) {
		for(Player p : players) {
			if(p == null) continue;
			
			int x = 0, y = 0;
			g.setColor(Color.BLACK);
			switch(p.getId()) {
			case 0:
				x = 630;
				y = 360;
				break;
			case 1:
				x = 460;
				y = 420;
				break;
			case 2:
				x = 235;
				y = 420;
				break;
			case 3:
				x = 80;
				y = 360;
				break;
			default: 
				break;
			}
			g.drawString(p.getHand().getCardValue() + " " + p.getName(), x, y);
			g.drawString("$" + p.getCredit() + " ", x, y + 150);
			g.drawString(dealer.getHand().getCardValue() + " " + dealer.getName(), 350, 35);
		}
	}
	
	/**
	 * paint the cards in the array to the screen
	 * TODO: paint cards to the screen. cards added through some sort of an array
	 * 
	 * @param g
	 */
	private void paintCards(Graphics g) {
		for(Card c : cards) {
			g.drawImage(c.getImage(), c.getXpos(), c.getYpos(), null);
		}
	}
	
	/**
	 * Print the chat to the screen
	 * @param g
	 */
	private void paintChat(Graphics g) {
		Font f = new Font("Arial", Font.PLAIN, 16);
		g.setFont(f);
		
		g.setColor(Color.BLACK);
		for(int i = 0; i < chatMessageArray.size(); i++) {
			if(i > CHAT_NUM_LINES_TO_DISPLAY)
				break;
			g.drawString(chatMessageArray.get(i), 500, 50 + (20 * i));
			
		}
	}
	
	/**
	 * Sets the players.
	 * @param players
	 */
	public void setPlayers(ArrayList<Player> players ) {
		this.players = players;
	}
	
	/**
	 * Returns the list of cards on the table
	 * @return ArrayList<Card>
	 */
	public ArrayList<Card> getCards() {
		return cards;
	}

	/**
	 * Sets the cards on the table. 
	 * @param cards
	 */
	public void setCards(ArrayList<Card> cards) {
		this.cards = cards;
	}
	
	/**
	 * add a chat to the array to be printed to the screen
	 * @param chatLine
	 */
	public void addChatLineToArray(String chatLine) {
		chatMessageArray.add(0, chatLine);
	}
	
	/**
	 * print text in the middle of the screen, this is for alerts like game starting or black jacks
	 * @param text
	 */
	public void printText(String text) {
		this.textToDisplay = text;
	}
	
	/**
	 * Pop up a message dialog box to choose what option
	 * @return
	 */
	public String showBetDialog() {
		String[] options = {"10", "20", "50", "100"};
		int choice = JOptionPane.showOptionDialog(this, "How much would you like to bet?", "Bet", JOptionPane.DEFAULT_OPTION, 0, null, options, options[2]);
		return options[choice];
	}
	
	/**
	 * Start called from the constructor to initialise the variables
	 */
	public void start() {
		this.chatMessageArray = new ArrayList<String>(10);
		new Thread(this).start();
	}

	@Override
	public void run() {
		while(true) {
			update();
			try {
				Thread.sleep(33);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}

	public String showActionDialog() {
		String[] options = {"Stay", "Hit", "Double"};
		int choice = JOptionPane.showOptionDialog(this, "What would you like to do?", "Action", JOptionPane.DEFAULT_OPTION, 0, null, options, options[1]);
		return options[choice];
		
	}

	public Dealer getDealer() {
		return dealer;
	}

	public void setDealer(Dealer dealer) {
		this.dealer = dealer;
	}
}
