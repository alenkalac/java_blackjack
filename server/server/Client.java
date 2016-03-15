package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	Client(String address, int port) throws UnknownHostException, IOException {
		Socket socket = new Socket(address, port);
		
		DataOutputStream str = new DataOutputStream(socket.getOutputStream());
		str.writeUTF("Hello");
	
	}

}
