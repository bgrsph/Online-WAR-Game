package app;
/**
 * The executable file that triggers the game. Client, server and follower can be initialized from here. 
 * @author Buğra Sipahioğlu
 */
import java.io.IOException;
import java.util.Scanner;

import domain.Client;
import domain.Server;

//TODO: Get all the parameters from configuration file

public class Application {

	/**
	 * Asks the mode of the game, which can be server, client of follower. 
	 * @param args command line parameters from user
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Scanner input = new Scanner(System.in);
		System.out.println("Welcome to the WAR Game. Please choose your mode and enter the number of it. " + "\n"
				+ "1- MASTER" + "\n" + "2-FOLLOWER" + "\n" + "3-CLIENT");
		int mode = input.nextInt();

		if (mode == 1) {
			System.out.println("SERVER SCREEN");
			Server server = new Server(444, "localhost");
			server.listenAndConnect();
		}

		else if (mode == 2) {
			System.out.println("FOLLOWER SCREEN");
			// TODO: Initialize Follower
		}

		else if (mode == 3) {
			Scanner input2 = new Scanner(System.in);
			System.out.print("Please Enter Your Name: ");
			String clientName = input2.nextLine();
			System.out.println("CLIENT SCREEN (" + clientName + ")");
			Client client = new Client(444, "localhost",clientName);
			client.connect();
			client.start();
		}

		else {
			System.out.println("Invalid Selection.");
			System.exit(1);
		}
	}

}
