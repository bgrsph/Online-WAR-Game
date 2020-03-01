package domain;

/** Represents a server that waits clients to start the game, followers to connect and updates database.
 * @author Buğra Sipahioğlu
 * @author Melike Kavcıoğlu
 * @author Tanıl ?
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	public static final int NUM_OF_PLAYERS = 2; 
	
	protected int portNumber;
	protected String serverIP;
	
	private ServerSocket serverSocket;
	private ArrayList<Socket> clientSockets;
	private ArrayList<BufferedReader> inputStreams;
	private ArrayList<PrintWriter> outputStreams;


	/**
	 * Constructor of the Server object. Creates client socket, input and output streams. 
	 * @param portNumber the port number of the server.
 	 * @param serverIP   IP address of the server. 
	 */
	public Server(int portNumber, String serverIP){
		clientSockets = new ArrayList<Socket>();
		inputStreams = new ArrayList<BufferedReader>();
		outputStreams = new ArrayList<PrintWriter>();
		this.portNumber = portNumber;
		this.serverIP = serverIP;

		try {
			serverSocket = new ServerSocket(portNumber);

		} catch (IOException e) {

			System.err.println("Server.constructor: Server Socket cannot be initialized");

		}

	}
	/**
	 * Listens clients and assigns a thread to start a game when 2 clients connected.
	 */
	public void listenAndConnect(){

		Socket clientSocket;
		int clientNumber = 0;
		int gameID = 0;
		int clientNumberPrevState = -1;
		while (true) {
			try {
				System.out.println("[MAIN SERVER]Server is waiting for clients...");
				clientSocket = serverSocket.accept();
				System.out.println("[MAIN SERVER] A client is connected. Client ID: " + (++clientNumber));
				clientSockets.add(clientSocket);
				outputStreams.add(new PrintWriter(clientSocket.getOutputStream(), true));
				inputStreams.add(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));

				if (clientNumber % 2 == 0 && clientNumberPrevState != clientNumber) {
					System.out.println("[MAIN SERVER] " + clientNumber + " are connected. A game will start now. Game ID:  " + (++gameID));
					startAGame(gameID, clientNumber, clientSockets, inputStreams, outputStreams);
					clientNumberPrevState = clientNumber;
				}
				
			} catch (IOException e) {
				
				System.err.println("Server.listenAndConnect: Error occured while listening to clients");

			}

		}

	}
	
	/**
	 * Assigns a new thread for 2 clients. Clients will communicate with that thread while playing game. 
	 * @param gameID keeps the game number. Initial value is 1. 
	 * @param clientCounter keeps the number of clients connected. 
	 * @param clientSockets keeps socket for 2 clients.
	 * @param inputStreams  keeps input streams for 2 clients.
	 * @param outputStreams keeps output streams for 2 clients.
	 */
	public void startAGame(int gameID, int clientCounter, ArrayList<Socket> clientSockets,
			ArrayList<BufferedReader> inputStreams, ArrayList<PrintWriter> outputStreams) {
		System.out.println("[MAIN SERVER] Creating a thread for game control... ");
		int firstClientIndex = clientCounter - 2;
		int secondClientIndex = clientCounter - 1;

		ArrayList<Socket> sockets = new ArrayList<Socket>();
		ArrayList<BufferedReader> inputs = new ArrayList<BufferedReader>();
		ArrayList<PrintWriter> outs = new ArrayList<PrintWriter>();

		sockets.add(clientSockets.get(firstClientIndex));
		sockets.add(clientSockets.get(secondClientIndex));
		inputs.add(inputStreams.get(firstClientIndex));
		inputs.add(inputStreams.get(secondClientIndex));
		outs.add(outputStreams.get(firstClientIndex));
		outs.add(outputStreams.get(secondClientIndex));
		System.out.println("[MAIN SERVER] Game Controller thread created. ");
		GameThread gameThread = new GameThread(gameID, clientCounter, sockets, inputs, outs);
		gameThread.start();
	}

}
