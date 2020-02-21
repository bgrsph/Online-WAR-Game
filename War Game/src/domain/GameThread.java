package domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class GameThread extends Thread {

	private ArrayList<Socket> clientSockets;
	protected ArrayList<BufferedReader> inputStreams;
	protected ArrayList<PrintWriter> outputStreams;
	private String fullMessageFromClient = new String();
	private int NUMBER_OF_PLAYERS;
	private int GAME_ID;
	private int lastClientID;

	public GameThread(int gameID, int clientCounter, ArrayList<Socket> clientSockets,
			ArrayList<BufferedReader> inputStreams, ArrayList<PrintWriter> outputStreams) {
		this.inputStreams = inputStreams;
		this.outputStreams = outputStreams;
		this.clientSockets = clientSockets;
		this.NUMBER_OF_PLAYERS = clientSockets.size();
		this.GAME_ID = gameID;
		this.lastClientID = clientCounter;

		System.out.println("[GAME CONTROLLER " + this.GAME_ID + "] Game started.");
	}

	public void communicateWithClients() throws IOException {

		BufferedReader inputStream;
		PrintWriter outputStream;
		String fullMessageFromClient;
		int clientID;

		for (int i = 0; i < this.NUMBER_OF_PLAYERS; i++) {

			inputStream = inputStreams.get(i);
			outputStream = outputStreams.get(i);
			clientID = (this.lastClientID - 1) + i;
			outputStream.println("[GAME CONTROLLER " + this.GAME_ID + "] Hello Client " + clientID);
			outputStream.flush();

			while (true) {

				fullMessageFromClient = inputStream.readLine();
				if (fullMessageFromClient != null) {

					System.out.println("[GAME CONTROLLER " + this.GAME_ID + "] New Message from Client " + clientID
							+ " : " + fullMessageFromClient);

					if (fullMessageFromClient.equals("I know.")) {

						break;
					}

				}
			}

		}

	}

	public void run() {

		try {
			communicateWithClients();
		} catch (IOException e) {
			System.err.println("ServerThread.run: Communication with clients has failed");
		}

	}
}
