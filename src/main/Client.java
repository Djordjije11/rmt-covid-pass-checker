package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

	static Socket socketCommunication = null;
	static BufferedReader serverInput = null;
	static PrintStream serverOutput = null;
	static BufferedReader console = null;
	
	public static void main(String[] args) {
		
		try {
			
			socketCommunication = new Socket("localhost", 9001);	// SETI SE OVO ZA LOCALHOST DA VIDIS DA IZMENIS DA RADI I ZA DRUGE RACUNARE
			
			serverInput = new BufferedReader(new InputStreamReader(socketCommunication.getInputStream()));
			serverOutput = new PrintStream(socketCommunication.getOutputStream());
			
			console = new BufferedReader(new InputStreamReader(System.in));
			
			new Thread(new Client()).start();
			
			String input;
			
			while(true) {
				input = serverInput.readLine();
				System.out.println(input);
						
				if(input.equals(">>> Dovidjenja.")) {
					break;
				}
				
				if(input.equals(">>> Zahtev za generisanjem kovid propusnice je prihvacen.")) {
					String propusnica = "";
					while(true) {
						input = serverInput.readLine();
						if(propusnica.equals("")) {
							propusnica = propusnica + input;
						} else {
							propusnica = propusnica + "\n" + input;
						}
						if(input.endsWith("$")) {
							propusnica = propusnica.substring(0, propusnica.length() - 1);
							break;
						}
					}
					
					try(FileWriter fileWriter = new FileWriter("kovid_propusnica.txt");
							BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
							PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
						
						printWriter.print(propusnica);
						System.out.println(">>> Kovid propusnica je uspesno generisana");
						
					} catch(Exception e) {
						System.out.println("Greska prilikom generisanja kovid propusnice.");
					}
					
				}
				
			}
			
			socketCommunication.close();
			
		} catch (UnknownHostException e) {
			System.out.println("NEPOZNAT HOST!");
		} catch (IOException e) {
			System.out.println("SERVER JE PAO!");
		}
	}
	
	@Override
	public void run() {
		String message;
		while(true) {
			try {
				message = console.readLine();
				serverOutput.println(message);
				
				if(message.equals("***quit")) {
					break;
				}
			} catch (IOException e) {
				System.out.println("Greska nastala prilikom koriscenja konzole.");
			}
		}
	}
	
}
