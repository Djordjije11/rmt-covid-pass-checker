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
	
	boolean isQuit=false;
	
	public ClientHandler(Socket socketCommunication) {
		
		this.socketCommunication=socketCommunication;
		
	}
	
	public boolean registerClient() {
		
		
		try {
			
			while(true) {
				clientOutput.println(">>> Unesite korisnicko ime: ");
				username = clientInput.readLine();
				if(username.equals("***quit")) {
					username = null;
					isQuit = true;
					return false;
				}
				if(username.contains(" ")) {
					username=null;
					clientOutput.println(">>> Niste uspesno uneli korisnicko ime.\n>>> Ukoliko budete zeleli da prekinete sesiju, upisite ***quit");
					continue;
				}
				break;
			}
			
			while(true) {
				clientOutput.println(">>> Unesite sifru: ");
				password = clientInput.readLine();
				if(password.equals("***quit")) {
					password = null;
					isQuit = true;
					return false;
				}
				if(password.contains(" ")) {
					password=null;
					clientOutput.println(">>> Niste uspesno uneli sifru.\n>>> Ukoliko budete zeleli da prekinete sesiju, upisite ***quit");
					continue;
				}
				break;
			}
			
			clientOutput.println(">>> Unesite ime: ");
			name = clientInput.readLine();
			
			clientOutput.println(">>> Unesite prezime: ");
			surname = clientInput.readLine();
			
			while(true) {
				clientOutput.println("Unesite JMBG: ");
				jmbg = clientInput.readLine();
				if(jmbg.equals("***quit")) {
					jmbg = null;
					isQuit = true;
					return false;
				}
				if(!jmbg.matches("[0-9]+") || !(jmbg.length()==13)) {
					jmbg=null;
					clientOutput.println(">>> Niste uspesno uneli JMBG.\n>>> Ukoliko budete zeleli da prekinete sesiju, upisite ***quit");
					continue;
				}
				break;
			}
			
			while(true) {
				clientOutput.println(">>> Unesite pol (musko ili zensko): ");
				gender = clientInput.readLine();
				if(gender.equals("***quit")) {
					gender = null;
					isQuit = true;
					return false;
				}
				
				if(!gender.equals("musko") && !gender.equals("zensko")) {
					gender=null;
					clientOutput.println(">>> Niste uspesno uneli pol.\n>>> Ukoliko budete zeleli da prekinete sesiju, upisite ***quit");
					continue;
				}
				break;
			}
			
			while(true) {
				clientOutput.println(">>> Unesite email: ");
				email = clientInput.readLine();
				if(email.equals("***quit")) {
					email = null;
					isQuit = true;
					return false;
				}
				if(!email.contains("@") || !email.contains(".") || email.contains(" ")) {
					email=null;
					clientOutput.println(">>> Niste uspesno uneli email.\n>>> Ukoliko budete zeleli da prekinete sesiju, upisite ***quit");
					continue;
				}
				break;
			}
			
			
			return true;
			
		} catch (IOException e) {
			System.out.println("Greska pri registraciji.");
			return false;
		}
		
		
	}
	
	@Override
	public void run() {
		
		try {
			
			clientInput = new BufferedReader(new InputStreamReader(socketCommunication.getInputStream()));
			clientOutput = new PrintStream(socketCommunication.getOutputStream());
			
			
			String response;
			
			while(true) {
				clientOutput.println(">>> Ukoliko budete zeleli da prekinete sesiju, upisite ***quit\n Upisite broj (1 ili 2) za zeljenu opciju:\n1.Login\n2.Registracija");
				
				response = clientInput.readLine();
				
				if(response.equals("***quit")) {
					break;
				} else if(response.equals("1")) {
					
					clientOutput.println(">>> IZABRALI STE PRVU OPCIJU");
					
					
					
				} else if(response.equals("2")){
					
					
					boolean isRegistered = registerClient();
					
					if(isQuit == true) {
						break;
					}
					
					if(isRegistered == false) {
						break;
					}
					
					System.out.println(username +  " " + password + " " + name + " " + surname + " " + jmbg + " " + gender + " " + email);
					
					
						
					//clientOutput.println(">>> IZABRALI STE DRUGU OPCIJU");
					
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
