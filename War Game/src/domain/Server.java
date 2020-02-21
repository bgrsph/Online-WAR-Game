package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {

	public static final int NUM_OF_PLAYERS = 2;
	protected int serverPort;
	private ServerSocket serverSocket;
	private ArrayList<Socket> clientSockets;
	private ArrayList<BufferedReader> inputStreams;
	private ArrayList<PrintWriter> outputStreams;
	private int portNumber;
	private String serverIP;
	public boolean verbose;

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

	public void listenAndConnect(){

		Socket clientSocket;
		int clientNumber = 0;
		int gameID = 0;
		int clientNumberPrevState = -1;
		while (true) {
			try {
				System.out.println("Server is waiting for clients...");
				clientSocket = serverSocket.accept();
				System.out.println("A client is connected. Client ID: " + (++clientNumber));
				clientSockets.add(clientSocket);
				outputStreams.add(new PrintWriter(clientSocket.getOutputStream(), true));
				inputStreams.add(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));

				if (clientNumber % 2 == 0 && clientNumberPrevState != clientNumber) {
					System.out.println(clientNumber + " are connected. A game will start now. Game ID:  " + (++gameID));
					startAGame(gameID, clientNumber, clientSockets, inputStreams, outputStreams);
					clientNumberPrevState = clientNumber;
				}
				
			} catch (IOException e) {
				
				System.err.println("Server.listenAndConnect: Error occured while listening to clients");

			}

		}

	}

	public void startAGame(int gameID, int clientCounter, ArrayList<Socket> clientSockets,
			ArrayList<BufferedReader> inputStreams, ArrayList<PrintWriter> outputStreams) {

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

		ServerThread serverThread = new ServerThread(gameID, clientCounter, sockets, inputs, outs);
		serverThread.start();
	}

}
