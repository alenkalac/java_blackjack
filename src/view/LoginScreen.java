package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class LoginScreen extends JFrame {
	
	private JTextField username;
	private JButton loginBtn;
	private JFrame frame;
	
	/**
	 * Default constructor
	 */
	public LoginScreen() {
		init();
	}
	
	/**
	 * set the action for the login button
	 * @param event
	 */
	public void setLoginButtonAction(ActionListener event) {
		this.loginBtn.addActionListener(event);
	}
	
	/**
	 * get the user name from the input field
	 * @return
	 */
	public String getUserName() {
		return this.username.getText();
	}
	
	/**
	 * hide the window
	 */
	public void hideWindow() {
		this.setVisible(false);
	}
	
	/**
	 * Initialises a GUI
	 */
	private void init() {
		
		username = new JTextField();
		loginBtn = new JButton("Login");
		
		this.frame = this;
		
		this.setSize(300,80);
		this.setTitle("BlackJack Login Screen");
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(1,2));
		
		p.add(new JLabel("Username"));
		p.add(username);
		this.add(p, BorderLayout.CENTER);
		this.add(loginBtn, BorderLayout.SOUTH);
		
		this.setVisible(true);
	}

}
