package server;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import controller.Debug;

public class ServerTest{	
	public static void main(String args[]){
		try {
			Debug.isDebug = true;
			
			GameServer server = new GameServer(6000);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
