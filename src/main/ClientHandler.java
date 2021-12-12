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
	String vaccine1 = null;
	String vaccine2 = null;
	String vaccine3 = null;
	
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
	
	public boolean isVaccined(int i) throws IOException {
		String response;
		while(true) {
			clientOutput.println(">>> Da li ste primili " + i + ". vakcinu? (da/ne)");
			response = clientInput.readLine();
			if(response.toLowerCase().equals("da")) {
				
				while(true) {
					clientOutput.println(">>> Koju ste vakcinu primili? Ukucajte odgovarajuci broj.\n1.Fajzer\n2.Sputnjik\n3.Sinofarm\n4.Oksford/AstraZeneka");
					switch (i) {
					case 1:
						response = clientInput.readLine();
						switch(response) {
						case "1":
							vaccine1 = "Fajzer";
							break;
						case "2":
							vaccine1 = "Sputnjik";
							break;
						case "3":
							vaccine1 = "Sinofarm";
							break;
						case "4":
							vaccine1 = "Oksford/AstraZeneka";
							break;
						default:
							clientOutput.println("Niste ispravno uneli odgovarajuci broj.");
							continue;
						}
						return true;
					
					case 2:
						response = clientInput.readLine();
						switch(response) {
						case "1":
							if(!vaccine1.equals("Fajzer")) {
								clientOutput.println("Niste uneli ispravno podatak za drugu primljenu vakcinu. Mora biti od istog proizvodjaca kao i prva primljena vakcina.");
								return false;
							}
							vaccine2 = "Fajzer";
							break;
						case "2":
							if(!vaccine1.equals("Sputnjik")) {
								clientOutput.println("Niste uneli ispravno podatak za drugu primljenu vakcinu. Mora biti od istog proizvodjaca kao i prva primljena vakcina.");
								return false;
							}
							vaccine2 = "Sputnjik";
							break;
						case "3":
							if(!vaccine1.equals("Sinofarm")) {
								clientOutput.println("Niste uneli ispravno podatak za drugu primljenu vakcinu. Mora biti od istog proizvodjaca kao i prva primljena vakcina.");
								return false;
							}
							vaccine2 = "Sinofarm";
							break;
						case "4":
							if(!vaccine1.equals("Oksford/AstraZeneka")) {
								clientOutput.println("Niste uneli ispravno podatak za drugu primljenu vakcinu. Mora biti od istog proizvodjaca kao i prva primljena vakcina.");
								return false;
							}
							vaccine2 = "Oksford/AstraZeneka";
							break;
						default:
							clientOutput.println("Niste ispravno uneli odgovarajuci broj.");
							continue;
						}
						return true;
						
					case 3:
						response = clientInput.readLine();
						switch(response) {
						case "1":
							vaccine3 = "Fajzer";
							break;
						case "2":
							vaccine3 = "Sputnjik";
							break;
						case "3":
							vaccine3 = "Sinofarm";
							break;
						case "4":
							vaccine3 = "Oksford/AstraZeneka";
							break;
						default:
							clientOutput.println("Niste ispravno uneli odgovarajuci broj.");
							continue;
						}
						return true;
						
					default:
						break;
					}
				}
			} else if(response.toLowerCase().equals("ne")) {
				return false;
			} else {
				clientOutput.println(">>> Niste uspesno odgovorili na postavljeno pitanje.\n>>> Ukoliko budete zeleli da prekinete sesiju, upisite ***quit");
				continue;
			}
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
				if(!gender.toLowerCase().equals("musko") && !gender.toLowerCase().equals("zensko")) {
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
			
			for(int i = 1; i <= 3; i++) {
				if(isVaccined(i)==false) {
					break;
				}
			}
			
			String query = "insert into appuser values(?,?,?,?,?,?,?,?,?,?)";
		    preparedStatement = connect.prepareStatement(query);
		    preparedStatement.setString(1, username);
		    preparedStatement.setString(2, password);
		    preparedStatement.setString(3, name);
		    preparedStatement.setString(4, surname);
		    preparedStatement.setString(5, jmbg);
		    preparedStatement.setString(6, gender);
		    preparedStatement.setString(7, email);
		    preparedStatement.setString(8, vaccine1);
		    preparedStatement.setString(9, vaccine2);
		    preparedStatement.setString(10, vaccine3);
		    preparedStatement.executeUpdate();
		    
			return true;
			
		} catch (IOException e) {
			clientOutput.println("Greska pri registraciji.");
			return false;
		} catch (SQLException e) {
			//e.printStackTrace();
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
				if(connect == null) {	// ako nije uspesno uspostavljena konekcija sa bazom podataka (koja se uspostavlja u konstruktoru)
					break;
				}
				clientOutput.println(">>> Ukoliko budete zeleli da prekinete sesiju, upisite ***quit\n Upisite broj (1 ili 2) za zeljenu opciju:\n1.Login\n2.Registracija");
				response = clientInput.readLine();
				if(response.equals("***quit")) {
					break;
				} else if(response.equals("1")) {
					//LOGIN
					//clientOutput.println(">>> Unesite korisnicko ime: ");
					
					//clientOutput.println(">>> Unesite sifru: ");
					
					
					
				} else if(response.equals("2")){
					//REGISTRACIJA
						boolean isRegistered = registerClient();
						if(isQuit == true) {
							break;
						}
						if(isRegistered == false) {
							break;
						}
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
