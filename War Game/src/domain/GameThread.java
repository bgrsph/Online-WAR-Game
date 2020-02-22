package domain;

/**
 * Represents the game controller. It does every action related to game while communicating with clients.
 * @author Buğra Sipahioğlu
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GameThread extends Thread {

	private static final int MAX_NUM_ROUNDS = 26;
	private static boolean FIRST_TIME;
	private static boolean IS_GAME_ENDED;
	private static int NUMBER_OF_PLAYERS;
	private static int NUMBER_OF_ROUNDS;
	private static int GAME_ID;
	public static Map<Integer, Integer> GAME_STATUS;

	protected ArrayList<BufferedReader> inputStreams;
	protected ArrayList<PrintWriter> outputStreams;

	private ArrayList<Socket> clientSockets;
	private int lastClientID;

	/**
	 * Work description of a thread. Starts to execute its content when
	 * GameThread.start() method is called.
	 */
	public void run() {

		try {
			if (FIRST_TIME) {

				waitForClients();
				startGame();
				FIRST_TIME = false;

			}

			while (!IS_GAME_ENDED) {
				listenAndAnswer();
			}

			if (IS_GAME_ENDED) {
				System.out.println("[GAME CONTROLLER] Game ended. Game Controller Thread is now stopping.");
				return;
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("ServerThread.run: Communication with clients has failed");
		}

	}

	/**
	 * Constructor of the game controller. Handles initializations of variables.
	 * 
	 * @param gameID        Represents the number of game. Unique for each game
	 * @param clientCounter The i variable in "i'th client connected to the server"
	 * @param clientSockets Sockets of the 2 clients whom will play
	 * @param inputStreams  Input streams of the 2 clients whom will play
	 * @param outputStreams Output streams of the 2 clients whom will play
	 */
	public GameThread(int gameID, int clientCounter, ArrayList<Socket> clientSockets,
			ArrayList<BufferedReader> inputStreams, ArrayList<PrintWriter> outputStreams) {

		FIRST_TIME = true;
		NUMBER_OF_ROUNDS = 0;
		NUMBER_OF_PLAYERS = clientSockets.size();
		IS_GAME_ENDED = false;
		GAME_STATUS = new HashMap<Integer, Integer>();
		GAME_STATUS.put(0, 0);
		GAME_STATUS.put(1, 0);

		this.inputStreams = inputStreams;
		this.outputStreams = outputStreams;
		this.clientSockets = clientSockets;
		GAME_ID = gameID;
		this.lastClientID = clientCounter;
		System.out.println("[GAME CONTROLLER " + this.GAME_ID + "] Game started.");
	}

	/**
	 * Starts the game by sending "game start (1)" message to all clients with the
	 * half deck for each client.
	 */
	private void startGame() {

		PrintWriter outputStream;
		Deck deck = new Deck();

		for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {

			outputStream = outputStreams.get(i);

			if (i == 0) {
				outputStream.println("gamestart" + deck.getFirstHalfOfDeck());
			} else {
				outputStream.println("gamestart" + deck.getSecondHalfOfDeck());
			}
		}
	}

	/**
	 * Waits the message "want game (0)" from all clients.
	 * 
	 * @throws IOException
	 */
	public void waitForClients() throws IOException {
		System.out.println("Waiting players to be ready...");

		BufferedReader inputStream;
		String fullMessageFromClient;
		int clientID;
		int numOfClientsReady = 0;

		for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {

			inputStream = inputStreams.get(i);
			clientID = (this.lastClientID - 1) + i;

			while (numOfClientsReady != NUMBER_OF_PLAYERS) {
				fullMessageFromClient = inputStream.readLine();
				if (fullMessageFromClient != null) {
					if (fullMessageFromClient.equals("wantgame")) {
						System.out.println("[GAME CONTROLLER] Client " + clientID + " is ready.");
						numOfClientsReady++;
						break;
					}

				}

			}
		}

		System.out.println("[GAME CONTROLLER] All players are ready.");
	}

	/**
	 * In general, listens 2 clients and responds them. In this project, it only
	 * responds to "play card" request of clients.
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	public void listenAndAnswer() throws IOException {

		BufferedReader inputStream;
		PrintWriter outputStream;
		String fullMessageFromClient;
		int clientID;
		int numClientsWantPlayCard = 0;
		ArrayList<String> cardsPlayed = new ArrayList<String>();
		int roundResult = -1;

		for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {

			inputStream = inputStreams.get(i);
			outputStream = outputStreams.get(i);
			clientID = (this.lastClientID - 1) + i;

			while (true) {

				fullMessageFromClient = inputStream.readLine();

				if (fullMessageFromClient != null) {

					if (fullMessageFromClient.contains("playcard")) {

						cardsPlayed.add(extractCardPlayed(fullMessageFromClient));
						numClientsWantPlayCard++;

						if (numClientsWantPlayCard == NUMBER_OF_PLAYERS) {
							System.out.println("Both Clients Played Their Cards: " + cardsPlayed.toString());
							calculateWinnerAndSendPlayResults(cardsPlayed);
							NUMBER_OF_ROUNDS++;
							System.out.println("[GAME CONTROLLER] Round " + NUMBER_OF_ROUNDS + " has finished.");
						}

						System.out.println("max rounds " + MAX_NUM_ROUNDS);
						System.out.println("num rounds: " + NUMBER_OF_ROUNDS);

						if (MAX_NUM_ROUNDS == NUMBER_OF_ROUNDS) {
							endGame();
						}
						break;

					}

					if (fullMessageFromClient.contains("newgame")) {

						resetGame(clientID);

					}
					
					if (fullMessageFromClient.contains("quitgame")) {

						quitGame(clientID, i);

					}

				}

			}
			if (IS_GAME_ENDED) {
				break;
			}

		}

	}

	/**
	 * Triggered then a client quits the game. Automatically defines other player winner and reports that to that user. 
	 * @param clientID ID of the client who quit
	 * @param i index of the client in the stream list.
	 */
	private void quitGame(int clientID, int i) {
		System.out.println("Client " + clientID + " has requested to quit the game.");
		
		if (i == 0) {
			outputStreams.get(1).println("autowin");
			outputStreams.get(1).flush();
		}
		
	}

	/**
	 * removes the previous state of the game and notify all clients for them to be prepared new game.
	 * @param clientID id of the client who requested new game
	 */
	private void resetGame(int clientID) {

		System.out.println("Client " + clientID + " has requested new game.");
		removePreviousGameState();
		notifyClients("newgame");
		startGame();

	}

	/**
	 * Sends the message to all clients in communication then clears the output pipe.
	 * @param message to be send all the clients.
	 */
	private void notifyClients(String message) {
		PrintWriter outputStream;
		for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
			outputStream = outputStreams.get(i);
			outputStream.println(message);
			outputStream.flush();
		}

	}

	/**
	 * Clears the data about current game.
	 */
	private void removePreviousGameState() {
		GAME_STATUS.put(0, 0);
		GAME_STATUS.put(1, 0);
		NUMBER_OF_ROUNDS = 0;
	}

	/**
	 * Finishes the game and triggers the predicate that stops the game controller
	 * thread.
	 */
	private void endGame() {
		int winner = getGameWinner();
		System.out.println("[GAME CONTROLLER] Game is ended. Calculating the winner...");
		System.out.println("[GAME CONTROLLER] Winner Client : " + winner);
		System.out.println("[GAME CONTROLLER] Disconnecting now... Goodbye!");

		if (winner == 0) { // Client 0 won

			this.outputStreams.get(0).println("gameresult-0"); // Send win message
			this.outputStreams.get(0).flush();

			this.outputStreams.get(1).println("gameresult-2"); // Send loose message
			this.outputStreams.get(1).flush();
		}

		if (winner == 1) { // Client 1 won

			this.outputStreams.get(0).println("gameresult-2"); // Send loose message
			this.outputStreams.get(0).flush();

			this.outputStreams.get(1).println("gameresult-0"); // Send win message
			this.outputStreams.get(1).flush();

		}

		else { // Draw
			this.outputStreams.get(0).println("gameresult-1"); // Send draw message
			this.outputStreams.get(0).flush();

			this.outputStreams.get(1).println("gameresult-1"); // Send draw message
			this.outputStreams.get(1).flush();

		}

		IS_GAME_ENDED = true;
	}

	/**
	 * Calculates which player has more wins.
	 * 
	 * @return 0 if first client wins, 1 otherwise.
	 */
	private int getGameWinner() {
		if (GAME_STATUS.get(0) > GAME_STATUS.get(1)) { // Client 0 won
			return 0;
		}

		if (GAME_STATUS.get(0) < GAME_STATUS.get(1)) { // Client 0 won
			return 1;
		}

		else { // Draw
			return -1;
		}
	}

	/**
	 * Compare the two cards with respect to the mapping in the project description,
	 * and updates the GAME_STATUS which holds # of wins per client.
	 * 
	 * @param cardsPlayed holds the cards played by clients for the current round.
	 */
	private void calculateWinnerAndSendPlayResults(ArrayList<String> cardsPlayed) {

		int firstClientCard = Integer.parseInt(cardsPlayed.get(0));
		int secondClientCard = Integer.parseInt(cardsPlayed.get(1));

		if (Math.abs(firstClientCard - secondClientCard) % 13 == 0) {

			this.outputStreams.get(0).println("playresult-1"); // Send draw message
			this.outputStreams.get(1).println("playresult-1"); // Send draw message

		} else {

			if (firstClientCard > secondClientCard) {

				this.outputStreams.get(0).println("playresult-0"); // Send win message
				this.outputStreams.get(0).flush();

				this.outputStreams.get(1).println("playresult-2"); // Send loose message
				this.outputStreams.get(1).flush();

				GAME_STATUS.put(0, GAME_STATUS.get(0) + 1);
			}

			if (secondClientCard > firstClientCard) {

				this.outputStreams.get(0).println("playresult-2"); // Send loose message
				this.outputStreams.get(0).flush();

				this.outputStreams.get(1).println("playresult-0"); // Send win message
				this.outputStreams.get(1).flush();

				GAME_STATUS.put(1, GAME_STATUS.get(1) + 1);
			}

		}

	}

	/**
	 * 
	 * @param fullMessageFromClient it's the full request of the client. In this
	 *                              case, the second element is the card played by
	 *                              user.
	 * @return the integer representation of the card played by the client in
	 *         communication.
	 */
	private String extractCardPlayed(String fullMessageFromClient) {

		return new ArrayList<String>(Arrays.asList(fullMessageFromClient.split("-"))).get(1);
	}

}
