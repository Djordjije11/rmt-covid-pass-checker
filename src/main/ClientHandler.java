package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientHandler extends Thread {

	BufferedReader clientInput = null;
	PrintStream clientOutput = null;
	Socket socketCommunication = null;
	
	String username;
	String password;
	String name;
	String surname;
	String jmbg;
	String gender;
	String email;
	
	public ClientHandler(Socket socketCommunication) {
		
		this.socketCommunication=socketCommunication;
		
	}
	
	@Override
	public void run() {
		
		try {
			
			clientInput = new BufferedReader(new InputStreamReader(socketCommunication.getInputStream()));
			clientOutput = new PrintStream(socketCommunication.getOutputStream());
			
			while(true) {
				clientOutput.println(">>> Ukoliko budete zeleli da prekinete sesiju, upisite ***quit\n Upisite broj (1 ili 2) za zeljenu opciju:\n1.Login\n2.Registracija");
				
				String response = clientInput.readLine();
				
				if(response.equals("***quit")) {
					
					break;
					
				} else if(response.equals("1")) {
					
					clientOutput.println(">>> IZABRALI STE PRVU OPCIJU");
					
				} else if(response.equals("2")){
					
					clientOutput.println(">>> IZABRALI STE DRUGU OPCIJU");
					
				} else {
					clientOutput.println(">>> Niste uneli ispravno.");
					continue;
					}
				
			}
			
			clientOutput.println(">>> Dovidjenja.");
			
			//Server.onlineUsers.remove(this);
			socketCommunication.close();
			
			
		} catch (IOException e) {
			
			System.out.println("EXCEPTION U CLIENTHANDLERU PRILIKOM KONEKCIJE UHVACEN");
			//Server.onlineUsers.remove(this);
			
		}
		
	}
	
	
}
