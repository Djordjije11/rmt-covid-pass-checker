package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
//import java.util.LinkedList;

public class Server {

	//public static LinkedList<ClientHandler> onlineUsers = new LinkedList<>();
	
	
	public static void main(String[] args) {
		
		int port = 9001;
		ServerSocket serverSocket = null;
		Socket socketCommunication = null;
		
		try {
			
			serverSocket = new ServerSocket(port);
			
			while(true) {
				System.out.println("Cekam na konekciju...");
				socketCommunication = serverSocket.accept();
				System.out.println("Doslo je do konekcije!");
				ClientHandler client = new ClientHandler(socketCommunication);
				
				//onlineUsers.add(client);
				
				client.start();
				
			}
			
		} catch (IOException e) {
			System.out.println("Greska prilikom pokretanja servera.");
		}
		

	}

}
