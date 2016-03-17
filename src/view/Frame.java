package view;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Frame extends JFrame {
	
	private GamePane gamePanel;
	private JTextField chatInput;
	
	/**
	 * Constructor for the class
	 * @param name 
	 */
	public Frame(String name) {
		
		gamePanel = new GamePane();
		this.setSize(800, 650);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setTitle("Black Jack Game " + name);
		this.add(gamePanel, BorderLayout.CENTER);
		
		chatInput = new JTextField();
		this.add(chatInput, BorderLayout.SOUTH);
		
		this.setVisible(true);
	}
	
	/**
	 * returns the game panel, used in the controller
	 * @return
	 */
	public GamePane getGamePanel() {
		return this.gamePanel;
	}
	
	/**
	 * Set the action listener to the chat box to listen for enter key
	 * @param event
	 */
	public void setChatBoxAction(ActionListener event) {
		this.chatInput.addActionListener(event);
	}
}