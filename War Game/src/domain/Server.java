package domain;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


//TODO: Fix the throw declarations

public class Server {
	
	public static final int NUM_OF_PLAYERS = 2;
	protected int serverPort;
	private ServerSocket serverSocket;
	private ArrayList<Socket> clientSockets;
	private int portNumber;
	private String serverIP;
	public boolean verbose;
	

	public Server(int portNumber, String serverIP) throws IOException {
		clientSockets = new ArrayList<Socket>();
		this.portNumber = portNumber;
		this.serverIP = serverIP;

		try {
			serverSocket = new ServerSocket(portNumber);

		} catch (IOException e) {

			e.printStackTrace();
			System.err.println("Server Socket cannot be initialized");

		}
		
		while(true) {
			connectWithClients();
		}
	}

	private void connectWithClients() throws IOException {
		//TODO: one thread 2 clients!
		Socket clientSocket;
		
		for (int i = 0; i < NUM_OF_PLAYERS; i++) {

			System.out.println("Server is waiting for Client " + i + "...");
			clientSocket = serverSocket.accept();
			clientSockets.add(clientSocket);
			System.out.println("Client " + i + " has connected");
		}
		ServerThread serverThread = new ServerThread(clientSockets);
		serverThread.start();

	}

}
