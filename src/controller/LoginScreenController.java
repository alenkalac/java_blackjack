package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;

import model.GameModel;
import view.Frame;
import view.GamePane;
import view.LoginScreen;

/**
 * Class that is in charge of controlling the main login form. 
 * @author Alen
 */
public class LoginScreenController {
	
	private LoginScreen loginScreen;
	private GameModel gameModel;
	
	/**
	 * Constructor for the LoginScreenController
	 * @param login
	 * @param gameModel 
	 */
	public LoginScreenController(LoginScreen login, GameModel gameModel) {
		this.loginScreen = login;
		this.gameModel = gameModel;
		
		login.setLoginButtonAction(new LoginButtonClickedAction());
	}
	
	/**
	 * Class that handles the login button click in the login screen
	 * @author Alen
	 */
	public class LoginButtonClickedAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton button = (JButton) e.getSource();
			button.removeActionListener(this);
			String username = loginScreen.getUserName();
			
			try {
				gameModel.setSocket(new Socket("localhost", 6000));
				gameModel.sendMessage("SETNAME " + username);
				gameModel.sendMessage("GETUSERLIST");
				loginScreen.hideWindow();
				
				startMainGame();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		private void startMainGame() {
			Frame gameView = new Frame();
			GameController gameController = new GameController(gameView, gameModel);
		}
	}
	
}
