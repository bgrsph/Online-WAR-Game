package app;

import java.io.IOException;
import java.util.Scanner;

import domain.Client;
import domain.Server;

//TODO: Get all the parameters from configuration file
//TODO: Cite the code taken from PS
//TODO: Don't forget to flush
//TODO: Make the code "JavaDoc"able
//TODO: Fix the throw declarations
//TODO: Don't exit when selection is invalid. Ask Again. 

public class Application {

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
			System.out.println("CLIENT SCREEN");
			Client client = new Client(444, "localhost");
			client.connect();
			client.start();
		}

		else {
			System.out.println("Invalid Selection.");
			System.exit(1);
		}
	}

}
