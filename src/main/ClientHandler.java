package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClientHandler extends Thread {

	BufferedReader clientInput = null;
	PrintStream clientOutput = null;
	Socket socketCommunication = null;
	
	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	
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
	
		try {
			clientInput = new BufferedReader(new InputStreamReader(socketCommunication.getInputStream()));
			clientOutput = new PrintStream(socketCommunication.getOutputStream());
			
			connect = DriverManager.getConnection("jdbc:mysql:// localhost:3306/rmt-domaci2", "root", "admin");
			
		} catch (SQLException e) {
			System.out.println("Greska prilikom konekcije sa bazom podataka.");
			clientOutput.println("Greska prilikom konekcije sa bazom podataka.");
		} catch (IOException e) {
			System.out.println("Greska prilikom konekcija servera i klijenta.");
			clientOutput.println("Greska prilikom konekcija servera i klijenta.");
		} 
		
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
				
				if(connect == null) {
					clientOutput.println("Greska tokom registracije prilikom povezivanja sa bazom podataka123");
					System.out.println("Greska tokom registracije prilikom povezivanja sa bazom podataka123");
					return false;
				}
				statement = connect.createStatement();
				resultSet = statement.executeQuery("SELECT EXISTS(SELECT * FROM `rmt-domaci2`.appuser WHERE username='"  + username + "')");
				if(resultSet.next()) {
					if(resultSet.getInt(1) == 1) {
						username = null;
						clientOutput.println(">>> Niste uspesno uneli korisnicko ime. Korisnicko ime je zauzeto.\n>>> Ukoliko budete zeleli da prekinete sesiju, upisite ***quit");
						continue;	
					} else {
						break;
					}
				} else {
					return false;
				}
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
			if(name.equals("***quit")) {
				name = null;
				isQuit = true;
				return false;
			}
			
			
			clientOutput.println(">>> Unesite prezime: ");
			surname = clientInput.readLine();
			if(surname.equals("***quit")) {
				surname = null;
				isQuit = true;
				return false;
			}
			
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
				
				statement = connect.createStatement();
				resultSet = statement.executeQuery("SELECT EXISTS(SELECT * FROM `rmt-domaci2`.appuser WHERE jmbg='"  + jmbg + "')");
				if(resultSet.next()) {
					if(resultSet.getInt(1) == 1) {
						jmbg = null;
						clientOutput.println(">>> Niste uspesno uneli JMBG. JMBG je zauzet.\n>>> Ukoliko budete zeleli da prekinete sesiju, upisite ***quit");
						continue;	
					} else {
						break;
					}
				} else {
					return false;
				}
				
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
			clientOutput.println("Greska pri registraciji.");
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			clientOutput.println("Greska tokom registracija prilikom povezivanja sa bazom podataka.");
			System.out.println("Greska tokom registracija prilikom povezivanja sa bazom podataka.");
			return false;
		}
		finally {
				try {
					if(statement != null) {
						statement.close();
					}
					if(resultSet != null) {
						resultSet.close();
					}
				} catch (SQLException e) {
					System.out.println("Greska pri zatvaranju statement-a.");
				}
		}
		
	}
	
	@Override
	public void run() {
		
		try {
			
			String response;
			
			while(true) {
				
				if(connect == null) {
					break;
				}
				
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
					
					//System.out.println(username +  " " + password + " " + name + " " + surname + " " + jmbg + " " + gender + " " + email);	
					//clientOutput.println(">>> IZABRALI STE DRUGU OPCIJU");
					
				} else {
					clientOutput.println(">>> Niste uneli ispravno.");
					continue;
					}
				
			}
				
			clientOutput.println(">>> Dovidjenja.");
			
			//Server.onlineUsers.remove(this);
			
			if(connect != null) {
				connect.close();							// zatvaram konekciju sa bazom podataka
			}			
			socketCommunication.close();		// zatvaram konekciju servera sa klijentom
			
			
		} catch (IOException e) {
			
			clientOutput.println("Greska prilikom konekcija servera i klijenta.");
			System.out.println("Greska prilikom konekcija servera i klijenta.");
			//Server.onlineUsers.remove(this);
			
		} catch (SQLException e) {
			clientOutput.println("Greska prilikom konekcije sa bazom podataka.");
			System.out.println("Greska prilikom konekcije sa bazom podataka.");
		}
		
	}
	
	
}
