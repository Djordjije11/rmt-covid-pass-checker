package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
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
			clientOutput.println(">>> Da li ste primili " + i + ". dozu vakcine? (da/ne)");
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
						case "***quit":
							isQuit = true;
							return false;
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
						case "***quit":
							isQuit = true;
							return false;
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
						case "***quit":
							isQuit = true;
							return false;
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
					if(isQuit == true) {
						return false;
					}
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
					clientOutput.println(">>> Unesite korisnicko ime: ");
					username = clientInput.readLine();
					if(username.equals("***quit")) {
						isQuit = true;
						break;
					}
					clientOutput.println(">>> Unesite sifru: ");
					password = clientInput.readLine();
					if(password.equals("***quit")) {
						isQuit = true;
						break;
					}
					//ZA ADMINA DEO
					if(username.equals("admin") && password.equals("admin")) {
						
						while(true) {
							clientOutput.println(">>>ADMIN<<< >>> Ukoliko budete zeleli da se odjavite, upisite ***logout. Ukoliko budete zeleli da prekinete sesiju, upisite ***quit"
									+ "\n>>> Ukucajte odgovarajuci broj kako biste odabrali opciju koju zelite:\n1.Pogledati da li odredjeni korisnik poseduju validnu kovid propusnicu"
									+ "\n2.Pogledati listu svih korisnika i njihove statuse vakcinacije"
									+ "\n3.Pogledati ukupan broj korisnika vakcinisianih samo prvom dozom, sa ukupno dve doze ili sa tri doze"
									+ "\n4.Pogledati za svaku vakcinu koliko ju je korisnika primilo (ne racunajuci trecu dozu)");
							response = clientInput.readLine();
							if(response.equals("***quit")) {
								isQuit = true;
								break;
							} else if(response.equals("***logout")) {
								break;
							} else if(response.equals("1")) {
								clientOutput.println(">>> Upisite JMBG trazenog korisnika: ");
								jmbg = clientInput.readLine();
								if(jmbg.equals("***quit")) {
									isQuit = true;
									break;
								}
								if(jmbg.equals("***logout")) {
									response = "***logout";
									break;
								}
								statement = connect.createStatement();
								resultSet = statement.executeQuery("SELECT vaccine2 FROM `rmt-domaci2`.appuser WHERE jmbg='"  + jmbg + "'");
								if(resultSet.next()) {
									vaccine2 = resultSet.getString("vaccine2");	 
									} else {
										clientOutput.println("Greska! Korisnik nije u bazi.");
										continue;
									}
								if(vaccine2 == null) {
									clientOutput.println(">>> Trazeni korisnik nema validnu kovid propusnicu.");
								} else {
									clientOutput.println(">>> Trazeni korisnik ima validnu kovid propusnicu.");
								}
								continue;	
							} else if(response.equals("2")) {
								int broj_primljenih_vakcina;
								clientOutput.println(">>> Lista svih korisnika i njihov status vakcinacije:");
								statement = connect.createStatement();
								resultSet = statement.executeQuery("SELECT username,\r\n"
										+ "case when vaccine1 is not null then 1 else 0 end +\r\n"
										+ "case when vaccine2 is not null then 1 else 0 end +\r\n"
										+ "case when vaccine3 is not null then 1 else 0 end\r\n"
										+ "as broj_primljenih_vakcina\r\n"
										+ "FROM `rmt-domaci2`.appuser;");
								while(resultSet.next()) {
									username = resultSet.getString("username");
									broj_primljenih_vakcina = resultSet.getInt("broj_primljenih_vakcina");
									clientOutput.println(username + ": " + broj_primljenih_vakcina + " primljenih doza vakcine");
								}
								continue;
							} else if(response.equals("3")) {
								int brojVakcinisanih1, brojVakcinisanih2, brojVakcinisanih3;
								statement = connect.createStatement();
								resultSet = statement.executeQuery("SELECT count(*) as brojVakcinisanih1 FROM `rmt-domaci2`.appuser WHERE vaccine1 is not null AND vaccine2 is null;");
								if(resultSet.next()){
									brojVakcinisanih1 = resultSet.getInt("brojVakcinisanih1");
								} else {
									clientOutput.println("Greska! Korisnik nije u bazi.");
									continue;
								}
								statement = connect.createStatement();
								resultSet = statement.executeQuery("SELECT count(*) as brojVakcinisanih2 FROM `rmt-domaci2`.appuser WHERE vaccine2 is not null AND vaccine3 is null;");
								if(resultSet.next()){
									brojVakcinisanih2 = resultSet.getInt("brojVakcinisanih2");
								} else {
									clientOutput.println("Greska! Korisnik nije u bazi.");
									continue;
								}
								statement = connect.createStatement();
								resultSet = statement.executeQuery("SELECT count(*) as brojVakcinisanih3 FROM `rmt-domaci2`.appuser WHERE vaccine3 is not null;");
								if(resultSet.next()){
									brojVakcinisanih3 = resultSet.getInt("brojVakcinisanih3");
								} else {
									clientOutput.println("Greska! Korisnik nije u bazi.");
									continue;
								}
								clientOutput.println(">>>\nBroj korisnika vakcinisanih jednom dozom: " + brojVakcinisanih1 + 
										"\nBroj korisnika vakcinisanih sa dve doze: " + brojVakcinisanih2 + "\nBroj korisnika vakcinisanih sa tri doze: " + brojVakcinisanih3);
								continue;
							} else if(response.equals("4")) {
								clientOutput.println(">>>");
								int brojVakcinisanih;
								statement = connect.createStatement();
								resultSet = statement.executeQuery("SELECT vaccine2, count(*) as brojVakcinisanih FROM appuser WHERE vaccine2 = \"Fajzer\";");
								if(resultSet.next()) {
									brojVakcinisanih = resultSet.getInt("brojVakcinisanih");
									clientOutput.println("Fajzer: " + brojVakcinisanih);
								} else {
									clientOutput.println("Greska! Korisnik nije u bazi.");
									continue;
								}
								statement = connect.createStatement();
								resultSet = statement.executeQuery("SELECT vaccine2, count(*) as brojVakcinisanih FROM appuser WHERE vaccine2 = \"Oksford/AstraZeneka\";");
								if(resultSet.next()) {
									brojVakcinisanih = resultSet.getInt("brojVakcinisanih");
									clientOutput.println("Oksford/AstraZeneka: " + brojVakcinisanih);
								} else {
									clientOutput.println("Greska! Korisnik nije u bazi.");
									continue;
								}
								statement = connect.createStatement();
								resultSet = statement.executeQuery("SELECT vaccine2, count(*) as brojVakcinisanih FROM appuser WHERE vaccine2 = \"Sinofarm\";");
								if(resultSet.next()) {
									brojVakcinisanih = resultSet.getInt("brojVakcinisanih");
									clientOutput.println("Sinofarm: " + brojVakcinisanih);
								} else {
									clientOutput.println("Greska! Korisnik nije u bazi.");
									continue;
								}
								statement = connect.createStatement();
								resultSet = statement.executeQuery("SELECT vaccine2, count(*) as brojVakcinisanih FROM appuser WHERE vaccine2 = \"Sputnjik\";");
								if(resultSet.next()) {
									brojVakcinisanih = resultSet.getInt("brojVakcinisanih");
									clientOutput.println("Sputnjik: " + brojVakcinisanih);
								} else {
									clientOutput.println("Greska! Korisnik nije u bazi.");
									continue;
								}
								continue;
							} else {
								clientOutput.println("Niste ispravno ukucali odgovarajuci broj. Odabrana opcija ne postoji.");
								continue;
							}
						}
						if(isQuit == true) {
							break;
						}
						if(response.equals("***logout")) {
							continue;
						}
						continue;
					}
					
					statement = connect.createStatement();
					resultSet = statement.executeQuery("SELECT EXISTS(SELECT * FROM `rmt-domaci2`.appuser WHERE username='"  + username + "' AND password='" + password + "')");
					if(resultSet.next()) {
						if(resultSet.getInt(1) == 0) {
							username = null;
							clientOutput.println(">>> Niste uspesno uneli trazene parametre. Korisnik nije registrovan.");
							continue;	
						}
					}
					response = null;
					while(true) {
						clientOutput.println(">>> Ukoliko budete zeleli da se odjavite, upisite ***logout. Ukoliko budete zeleli da prekinete sesiju, upisite ***quit");
						clientOutput.println(">>> Ukucajte broj (1 ili 2) za odgovarajucu opciju. Da li zelite da:\n1.Izmenite podatke o primljenim vakcinama\n2.Proverite da li posedujete validnu kovid propusnicu");
						response = clientInput.readLine();
						if(response.equals("***logout")) {
							break;
						} else if(response.equals("***quit")) {
							break;
						} else if(response.equals("1")) {
							//IZMENA PODATAKA O PRIMLJENIM VAKCINAMA
							statement = connect.createStatement();
							resultSet = statement.executeQuery("SELECT vaccine1, vaccine2, vaccine3 FROM `rmt-domaci2`.appuser WHERE username='"  + username + "'");
							if(resultSet.next()) {
								vaccine1 = resultSet.getString("vaccine1");
								vaccine2 = resultSet.getString("vaccine2");
								vaccine3 = resultSet.getString("vaccine3");		 
								} else {
									clientOutput.println("Greska! Korisnik nije u bazi.");
									break;
								}

							if(vaccine1 == null) {
								for(int i = 1; i <= 3; i++) {
									if(isVaccined(i)==false) {
										break;
									}
								}
							} else if(vaccine2 == null) {
								for(int i = 2; i <= 3; i++) {
									if(isVaccined(i)==false) {
										break;
									}
								}
							} else if(vaccine3 == null) {
								isVaccined(3);
							} else {
								clientOutput.println(">>> Vec ste primili sve doze vakcina koje su dostupne.");
								continue;
							}
							
							if(isQuit == true) {
								break;
							}
							
							if(vaccine1 == null) {
								continue;
							} else if(vaccine2 == null) {
								statement = connect.createStatement();
								statement.executeUpdate("UPDATE `rmt-domaci2`.appuser SET vaccine1='" + vaccine1 + "' WHERE username='" + username + "'");
							} else if(vaccine3 == null) {
								statement = connect.createStatement();
								statement.executeUpdate("UPDATE `rmt-domaci2`.appuser SET vaccine1='" + vaccine1 + "', vaccine2='" + vaccine2 + "' WHERE username='" + username + "'");
							} else {
								statement = connect.createStatement();
								statement.executeUpdate("UPDATE `rmt-domaci2`.appuser SET vaccine1='" + vaccine1 + "', vaccine2='" + vaccine2 + "', vaccine3='" + vaccine3 + "' WHERE username='" + username + "'");
							}
							continue;
						} else if(response.equals("2")) {
							//PROVERA ZA PROPUSNICU
							statement = connect.createStatement();
							resultSet = statement.executeQuery("SELECT vaccine2 FROM `rmt-domaci2`.appuser WHERE username='"  + username + "'");
							if(resultSet.next()) {
								vaccine2 = resultSet.getString("vaccine2");	 
								} else {
									clientOutput.println("Greska! Korisnik nije u bazi.");
									break;
								}
							if(vaccine2 != null) {
								while(true) {
									clientOutput.println(">>> Posedujete validnu kovid propusnicu.\nDa li zelite da je generisete? (da/ne)");
									response = clientInput.readLine();
									if(response.equals("***quit")) {
										isQuit = true;
										break;
									}
									if(response.equals("***logout")) {
										break;
									}
									if(response.toLowerCase().equals("da")) {
										//GENERISANJE KOVID PROPUSNICE
										statement = connect.createStatement();
										resultSet = statement.executeQuery("SELECT name, surname, jmbg, vaccine3 FROM `rmt-domaci2`.appuser WHERE username='"  + username + "'");
										if(resultSet.next()) {
											name = resultSet.getString("name");
											surname = resultSet.getString("surname");
											jmbg = resultSet.getString("jmbg");
											vaccine3 = resultSet.getString("vaccine3");		 
											} else {
												clientOutput.println("Greska! Korisnik nije u bazi.");
												break;
											}
										if(vaccine3 == null) {
											try(FileWriter fileWriter = new FileWriter(username + "_kovid_propusnica.txt");
													BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
													PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
														printWriter.println("KOVID PROPUSNICA\n" + name + " " + surname + " (JMBG: " + jmbg + ")\nvakcinisan/a je sa dve doze " + vaccine2 + " vakcine.");
														clientOutput.println(">>> Kovid propusnica je uspesno generisana.");
														break;
											} catch(Exception e) {
												System.out.println("Greska prilikom generisanja kovid propusnice.");
												clientOutput.println("Greska prilikom generisanja kovid propusnice.");
												continue;
											}
										} else {
											try(FileWriter fileWriter = new FileWriter(username + "_kovid_propusnica.txt");
													BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
													PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
														printWriter.println("KOVID PROPUSNICA\n" + name + " " + surname + " (JMBG: " + jmbg + ")\nvakcinisan/a je sa dve doze " 
													+ vaccine2 + " vakcine i jednom dozom (booster) " + vaccine3 + " vakcine.");
														clientOutput.println(">>> Kovid propusnica je uspesno generisana.");
														break;
											} catch(Exception e) {
												System.out.println("Greska prilikom generisanja kovid propusnice.");
												clientOutput.println("Greska prilikom generisanja kovid propusnice.");
												continue;
											}
										}
									} else if(response.toLowerCase().equals("ne")) {
										break;
									} else {
										clientOutput.println("Niste ispravno ukucali odgovarajuci broj. Odabrana opcija ne postoji.");
										continue;
									}
								}
							} else {
								clientOutput.println(">>> Ne posedujete validnu kovid propusnicu.");
								continue;
							}
							if(isQuit == true || response.equals("***quit") || response.equals("***logout")) {
								break;
							}
							continue;
						} else {
							clientOutput.println("Niste ukucali ispravno. Ne postoji opcija za uneti broj.");
							continue;
						}
					}
					
					if(response.equals("***logout")) {
						continue;
					}
					if(response.equals("***quit") || isQuit == true) {
						break;
					}
					
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
			clientOutput.println(">>> Dovidjenja.");
			try {
				socketCommunication.close();																										// OVO SI ISPRAVLJAO
			} catch (IOException e1) {
				clientOutput.println("Greska prilikom konekcija servera i klijenta.");
				System.out.println("Greska prilikom konekcija servera i klijenta.");
			}	
		}
		
	}
	
}