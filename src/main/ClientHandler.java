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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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
	String vaccine1_date;
	String vaccine2_date;
	String vaccine3_date;

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
	
	public boolean isDateValid1(String datum) {
		try {
			Date date;
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
			sdf.setLenient(false);
			date = sdf.parse(datum);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			int year = cal.get(Calendar.YEAR);
			if(year != 2021) {
				clientOutput.println("Datum vakcinacije nije ispravno unet. Godina datuma vakcinacije mora biti 2021.");
				return false;
			}
			return true;
		} catch (ParseException e) {
			clientOutput.println("Datum vakcinacije nije ispravno unet.");
			return false;
		}
	}
	
	public boolean isDateValid2(String datum1, String datum2) {
		try {
			Date date1;
			Date date2;
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
			sdf.setLenient(false);
			date1 = sdf.parse(datum1);
			date2 = sdf.parse(datum2);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date2);
			int year = cal.get(Calendar.YEAR);
			if(year != 2021) {
				clientOutput.println("Datum vakcinacije nije ispravno unet. Godina datuma vakcinacije mora biti 2021.");
				return false;
			}
			long diff = date2.getTime() - date1.getTime();
			long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
			if(days  < 21) {
				clientOutput.println("Datum vakcinacije nije ispravno unet. Da biste primili drugu dozu vakcine, mora proci minimum 3 nedelje od primanja prve doze vakcine.");
				return false;
			}
			return true;
		} catch (ParseException e) {
			clientOutput.println("Datum vakcinacije nije ispravno unet.");
			return false;
		}
	}

	public boolean isDateValid3(String datum2, String datum3) {
		try {
			Date date2;
			Date date3;
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
			sdf.setLenient(false);
			date2 = sdf.parse(datum2);
			date3 = sdf.parse(datum3);
			Calendar cal3 = Calendar.getInstance();
			cal3.setTime(date3);
			int year3 = cal3.get(Calendar.YEAR);
			if(year3 != 2021) {
				clientOutput.println("Datum vakcinacije nije ispravno unet. Godina datuma vakcinacije mora biti 2021.");
				return false;
			}
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(date2);
			cal2.add(Calendar.MONTH, 6);
			if(cal2.before(cal3) || cal2.equals(cal3)) {
				return true;
			} else {
				clientOutput.println("Datum vakcinacije nije ispravno unet. Da biste primili trecu dozu vakcine, mora proci minimum 6 meseci od primanja prve doze vakcine.");
				return false;
			}
		} catch (ParseException e) {
			clientOutput.println("Datum vakcinacije nije ispravno unet.");
			return false;
		}
	}
	
	public boolean isVaccined(int i) throws IOException {
		String response;
		while(true) {
			clientOutput.println(">>> Da li ste primili " + i + ". dozu vakcine? (da/ne)");
			response = clientInput.readLine();
			if(response.equals("***quit")) {
				isQuit = true;
				return false;
			}
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
						while(true) {
							clientOutput.println(">>> Unesite datum kada ste primili vakcinu (u formatu: dd.MM.yyyy. kao npr. 01.04.2021.)");
							vaccine1_date = clientInput.readLine();
							if(vaccine1_date.equals("***quit")) {
								isQuit = true;
								return false;
							}
							if(isDateValid1(vaccine1_date)) {
								return true;
							} else {
								continue;
							}
						}
					
					case 2:
						response = clientInput.readLine();
						switch(response) {
						case "1":
							if(!vaccine1.equals("Fajzer")) {
								clientOutput.println("Niste uneli ispravno podatak za drugu primljenu vakcinu. Mora biti od istog proizvodjaca kao i prva primljena vakcina.");
								continue;
							}
							vaccine2 = "Fajzer";
							break;
						case "2":
							if(!vaccine1.equals("Sputnjik")) {
								clientOutput.println("Niste uneli ispravno podatak za drugu primljenu vakcinu. Mora biti od istog proizvodjaca kao i prva primljena vakcina.");
								continue;
							}
							vaccine2 = "Sputnjik";
							break;
						case "3":
							if(!vaccine1.equals("Sinofarm")) {
								clientOutput.println("Niste uneli ispravno podatak za drugu primljenu vakcinu. Mora biti od istog proizvodjaca kao i prva primljena vakcina.");
								continue;
							}
							vaccine2 = "Sinofarm";
							break;
						case "4":
							if(!vaccine1.equals("Oksford/AstraZeneka")) {
								clientOutput.println("Niste uneli ispravno podatak za drugu primljenu vakcinu. Mora biti od istog proizvodjaca kao i prva primljena vakcina.");
								continue;
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
						while(true) {
							clientOutput.println(">>> Unesite datum kada ste primili vakcinu (u formatu: dd.MM.yyyy. kao npr. 01.04.2021.)");
							vaccine2_date = clientInput.readLine();
							if(vaccine2_date.equals("***quit")) {
								isQuit = true;
								return false;
							}
							if(isDateValid2(vaccine1_date, vaccine2_date)) {
								return true;
							} else {
								continue;
							}
						}
						
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
						while(true) {
							clientOutput.println(">>> Unesite datum kada ste primili vakcinu (u formatu: dd.MM.yyyy. kao npr. 01.04.2021.)");
							vaccine3_date = clientInput.readLine();
							if(vaccine3_date.equals("***quit")) {
								isQuit = true;
								return false;
							}
							if(isDateValid3(vaccine2_date, vaccine3_date)) {
								return true;
							} else {
								continue;
							}
						}
						
					default:
						break;
					}
				}	
				
			} else if(response.toLowerCase().equals("ne")) {
				return false;
			} else {
				clientOutput.println("Niste uspesno odgovorili na postavljeno pitanje.\n>>> Ukoliko budete zeleli da prekinete sesiju, upisite ***quit");
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
					clientOutput.println("Greska tokom registracije prilikom povezivanja sa bazom podataka!");
					System.out.println("Greska tokom registracije prilikom povezivanja sa bazom podataka!");
					return false;
				}
				statement = connect.createStatement();
				resultSet = statement.executeQuery("SELECT EXISTS(SELECT * FROM `rmt-domaci2`.appuser WHERE binary username='"  + username + "')");
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
				resultSet = statement.executeQuery("SELECT EXISTS(SELECT * FROM `rmt-domaci2`.appuser WHERE binary jmbg='"  + jmbg + "')");
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
				gender = gender.toLowerCase();
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
				if(!email.contains("@") || !email.contains(".") || email.contains(" ") || email.endsWith(".")) {
					email=null;
					clientOutput.println(">>> Niste uspesno uneli email.\n>>> Ukoliko budete zeleli da prekinete sesiju, upisite ***quit");
					continue;
				}
				break;
			}
			
			String query = "insert into appuser (username,password,name,surname,jmbg,gender,email) values(?,?,?,?,?,?,?)";
		    preparedStatement = connect.prepareStatement(query);
		    preparedStatement.setString(1, username);
		    preparedStatement.setString(2, password);
		    preparedStatement.setString(3, name);
		    preparedStatement.setString(4, surname);
		    preparedStatement.setString(5, jmbg);
		    preparedStatement.setString(6, gender);
		    preparedStatement.setString(7, email);
		    preparedStatement.executeUpdate();
			
			for(int i = 1; i <= 3; i++) {
				if(isVaccined(i) == false) {
					if(isQuit == true) {
						return false;
					}
					break;
				}
			}
		    
			if(vaccine1 == null) {
				return true;
			} else if(vaccine2 == null) {
				statement = connect.createStatement();
				statement.executeUpdate("UPDATE `rmt-domaci2`.appuser SET vaccine1='" + vaccine1 + "', vaccine1_date='" + vaccine1_date + "' WHERE binary username='" + username + "'");
			} else if(vaccine3 == null) {
				statement = connect.createStatement();
				statement.executeUpdate("UPDATE `rmt-domaci2`.appuser SET vaccine1='" + vaccine1 + "', vaccine1_date='" + vaccine1_date + "', vaccine2='" + vaccine2 + "', vaccine2_date='" + vaccine2_date + "' WHERE binary username='" + username + "'");
			} else {
				statement = connect.createStatement();
				statement.executeUpdate("UPDATE `rmt-domaci2`.appuser SET vaccine1='" + vaccine1 + "', vaccine1_date='" + vaccine1_date + "', vaccine2='" + vaccine2 + "', vaccine2_date='" + vaccine2_date + "', vaccine3='" + vaccine3 + "', vaccine3_date='" + vaccine3_date + "' WHERE binary username='" + username + "'");
			}
		    
			return true;	
		} catch (IOException e) {
			clientOutput.println("Greska pri registraciji.");
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			clientOutput.println("Greska tokom registracije prilikom povezivanja sa bazom podataka.");
			System.out.println("Greska tokom registracije prilikom povezivanja sa bazom podataka.");
			
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
					clientOutput.println("Nastala je greska pri uspostavljanju konekcije sa bazom podataka.");
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
								resultSet = statement.executeQuery("SELECT vaccine2 FROM `rmt-domaci2`.appuser WHERE binary jmbg='"  + jmbg + "'");
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
									clientOutput.println(username + ", broj primljenih vakcina: " + broj_primljenih_vakcina);
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
								resultSet = statement.executeQuery("SELECT vaccine2, count(*) as brojVakcinisanih FROM appuser WHERE binary vaccine2 = \"Fajzer\";");
								if(resultSet.next()) {
									brojVakcinisanih = resultSet.getInt("brojVakcinisanih");
									clientOutput.println("Fajzer: " + brojVakcinisanih);
								} else {
									clientOutput.println("Greska! Korisnik nije u bazi.");
									continue;
								}
								statement = connect.createStatement();
								resultSet = statement.executeQuery("SELECT vaccine2, count(*) as brojVakcinisanih FROM appuser WHERE binary vaccine2 = \"Oksford/AstraZeneka\";");
								if(resultSet.next()) {
									brojVakcinisanih = resultSet.getInt("brojVakcinisanih");
									clientOutput.println("Oksford/AstraZeneka: " + brojVakcinisanih);
								} else {
									clientOutput.println("Greska! Korisnik nije u bazi.");
									continue;
								}
								statement = connect.createStatement();
								resultSet = statement.executeQuery("SELECT vaccine2, count(*) as brojVakcinisanih FROM appuser WHERE binary vaccine2 = \"Sinofarm\";");
								if(resultSet.next()) {
									brojVakcinisanih = resultSet.getInt("brojVakcinisanih");
									clientOutput.println("Sinofarm: " + brojVakcinisanih);
								} else {
									clientOutput.println("Greska! Korisnik nije u bazi.");
									continue;
								}
								statement = connect.createStatement();
								resultSet = statement.executeQuery("SELECT vaccine2, count(*) as brojVakcinisanih FROM appuser WHERE binary vaccine2 = \"Sputnjik\";");
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
					resultSet = statement.executeQuery("SELECT EXISTS(SELECT * FROM `rmt-domaci2`.appuser WHERE binary username='"  + username + "' AND binary password='" + password + "')");
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
							resultSet = statement.executeQuery("SELECT vaccine1, vaccine2, vaccine3, vaccine1_date, vaccine2_date FROM `rmt-domaci2`.appuser WHERE binary username='"  + username + "'");
							if(resultSet.next()) {
								vaccine1 = resultSet.getString("vaccine1");
								vaccine2 = resultSet.getString("vaccine2");
								vaccine3 = resultSet.getString("vaccine3");	
								vaccine1_date = resultSet.getString("vaccine1_date");
								vaccine2_date = resultSet.getString("vaccine2_date");
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
								statement.executeUpdate("UPDATE `rmt-domaci2`.appuser SET vaccine1='" + vaccine1 + "', vaccine1_date='" + vaccine1_date + "' WHERE binary username='" + username + "'");
							} else if(vaccine3 == null) {
								statement = connect.createStatement();
								statement.executeUpdate("UPDATE `rmt-domaci2`.appuser SET vaccine1='" + vaccine1 + "', vaccine1_date='" + vaccine1_date + "', vaccine2='" + vaccine2 + "', vaccine2_date='" + vaccine2_date + "' WHERE binary username='" + username + "'");
							} else {
								statement = connect.createStatement();
								statement.executeUpdate("UPDATE `rmt-domaci2`.appuser SET vaccine1='" + vaccine1 + "', vaccine1_date='" + vaccine1_date + "', vaccine2='" + vaccine2 + "', vaccine2_date='" + vaccine2_date + "', vaccine3='" + vaccine3 + "', vaccine3_date='" + vaccine3_date + "' WHERE binary username='" + username + "'");
							}
							continue;
						} else if(response.equals("2")) {
							//PROVERA ZA PROPUSNICU
							statement = connect.createStatement();
							resultSet = statement.executeQuery("SELECT vaccine2 FROM `rmt-domaci2`.appuser WHERE binary username='"  + username + "'");
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
										resultSet = statement.executeQuery("SELECT name, surname, jmbg, vaccine1, vaccine2, vaccine3, vaccine1_date, vaccine2_date, vaccine3_date"
												+ " FROM `rmt-domaci2`.appuser WHERE binary username='"  + username + "'");
										if(resultSet.next()) {
											name = resultSet.getString("name");
											surname = resultSet.getString("surname");
											jmbg = resultSet.getString("jmbg");
											vaccine1 = resultSet.getString("vaccine1");
											vaccine2 = resultSet.getString("vaccine2");
											vaccine3 = resultSet.getString("vaccine3");
											vaccine1_date = resultSet.getString("vaccine1_date");
											vaccine2_date = resultSet.getString("vaccine2_date");
											vaccine3_date = resultSet.getString("vaccine3_date");
											} else {
												clientOutput.println("Greska! Korisnik nije u bazi.");
												break;
											}
										if(vaccine3 == null) {
											String propusnica = "KOVID PROPUSNICA\n" + name + " " + surname + " (JMBG: " + jmbg + ")\nPrva primljena doza vakcine: " + vaccine1 + ", " + vaccine1_date + "\n"
													+ "Druga primljena doza vakcine: " + vaccine2 + ", " + vaccine2_date;
											clientOutput.println(">>> Zahtev za generisanjem kovid propusnice je prihvacen.");
											clientOutput.println(propusnica + "$");
											
											break;
										} else {
											String propusnica = "KOVID PROPUSNICA\n" + name + " " + surname + " (JMBG: " + jmbg + ")\nPrva primljena doza vakcine: " + vaccine1 + ", " + vaccine1_date + "\n"
													+ "Druga primljena doza vakcine: " + vaccine2 + ", " + vaccine2_date + "\nTreca primljena doza vakcine: " + vaccine3 + ", " + vaccine3_date;
											clientOutput.println(">>> Zahtev za generisanjem kovid propusnice je prihvacen.");
											clientOutput.println(propusnica + "$");
											
											break;
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
			
			if(connect != null) {
				connect.close();							// zatvaram konekciju sa bazom podataka
			}			
			socketCommunication.close();		// zatvaram konekciju servera sa klijentom
			
		} catch (IOException e) {
			clientOutput.println("Greska prilikom konekcije servera i klijenta.");
			System.out.println("Greska prilikom konekcije servera i klijenta.");
		} catch (SQLException e) {
			clientOutput.println("Greska prilikom konekcije sa bazom podataka.");
			System.out.println("Greska prilikom konekcije sa bazom podataka.");
			clientOutput.println(">>> Dovidjenja.");
			try {
				socketCommunication.close();	
			} catch (IOException e1) {
				clientOutput.println("Greska prilikom konekcije servera i klijenta.");
				System.out.println("Greska prilikom konekcije servera i klijenta.");
			}	
		}
	}
	
	
}