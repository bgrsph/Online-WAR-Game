package domain;

/**
 * Represents the player. Handles the network concepts as well as the game concepts. 
 * @author Buğra Sipahioğlu
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client extends Thread {

	public static final String DEFAULT_SERVER_ADDRESS = "localhost";
	public static final int DEFAULT_SERVER_PORT = 4444;
	private static boolean IS_FIRST_TIME;

	private Socket socket;
	protected BufferedReader inputStream;
	protected PrintWriter outputStream;
	protected String serverIP;
	protected int serverPort;

	public static ArrayList<String> DECK;
	private static int NUM_CARDS_PLAYED;
	private static int TOTAL_NUM_CARDS;

	public String name;

	/**
	 * Work description of the client thread. Starts to execute its content when
	 * Client.start() method is called.
	 */
	public void run() {

		try {

			communicateWithServer();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Constructor of the client object. Initializes variables.
	 * 
	 * @param serverPort port of the server to be connected.
	 * @param serverIP   IP address of the server to be connected.
	 * @param name       name of the client who is in play.
	 */
	public Client(int serverPort, String serverIP, String name) {
		this.serverPort = serverPort;
		this.serverIP = serverIP;
		this.name = name;
		IS_FIRST_TIME = true;
		NUM_CARDS_PLAYED = 0;
	}

	/**
	 * Connects the server from its IP and port.
	 */
	public void connect() {

		try {
			socket = new Socket(this.serverIP, this.serverPort);
			inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputStream = new PrintWriter(socket.getOutputStream());
			System.out.println("Successfully connected to " + this.serverIP + " on port " + this.serverPort);
		} catch (IOException e) {
			System.err.println("Error: no server has been found on " + this.serverIP + "/" + serverPort);
		}
	}

	/**
	 * Listens and respond the server. This is where the game played.
	 * 
	 * @throws IOException
	 */
	public void communicateWithServer() throws IOException {
		String fullMessageFromGameController;

		askInitialActionsToPlayer();

		while (true) {

			fullMessageFromGameController = inputStream.readLine();

			if (fullMessageFromGameController != null) {

				if (fullMessageFromGameController.contains("gamestart")) {

					startGame(fullMessageFromGameController);
				}

				if (fullMessageFromGameController.contains("newgame")) {

					restartGame();
				}

				if (fullMessageFromGameController.contains("autowin")) {

					automaticWin();
				}

				if (fullMessageFromGameController.contains("playresult")) {

					String playResult = extractResult(fullMessageFromGameController);

					if (playResult.equals("0")) {
						System.out.println("------> ROUND " + NUM_CARDS_PLAYED + ": YOU WIN.");
					}

					if (playResult.equals("1")) {
						System.out.println("------> ROUND " + NUM_CARDS_PLAYED + ": DRAW.");
					}

					if (playResult.equals("2")) {
						System.out.println("------> ROUND " + NUM_CARDS_PLAYED + ": YOU LOOSE.");
					}
					if (NUM_CARDS_PLAYED == TOTAL_NUM_CARDS) {
						System.out.println("Game is finished. Waiting server to response...");
					} else {
						DECK = playCard(DECK);
					}

				}

				if (fullMessageFromGameController.contains("gameresult")) {

					String playResult = extractResult(fullMessageFromGameController);

					if (playResult.equals("0")) {
						System.out.println("------> GAME FINISHED: YOU WIN.");
					}

					if (playResult.equals("1")) {
						System.out.println("------> GAME FINISHED: DRAW.");
					}

					if (playResult.equals("2")) {
						System.out.println("------> GAME FINISHED: YOU LOOSE.");

					}
					System.out.println("Disconnecting...");
					System.exit(0);
				}
			}
		}
	}

	/**
	 * prints the information and triggers state deletion function
	 */
	private void restartGame() {
		System.out.println("----------------GAME IS RESTARTED----------------");
		removePreviousGameState();
	}

	/**
	 * starts the game by parsing the deck and playing it.
	 * @param fullMessageFromGameController the message that has the full deck.
	 * @throws IOException
	 */
	private void startGame(String fullMessageFromGameController) throws IOException {
		DECK = extractDeck(fullMessageFromGameController);
		System.out.println("CARDS RECEIVED: " + DECK);
		TOTAL_NUM_CARDS = DECK.size();
		DECK = playCard(DECK);
	}

	/**
	 * triggered when other player quits the game.
	 */
	private void automaticWin() {
		System.out.println("Other client quitted the game. YOU WIN. ");
		System.out.println("Disconnecting...");
		System.exit(0);
	}

	/**
	 * 
	 * @param fullMessageFromGameController respond from the game controller thread.
	 * @return 0 if win, 1 if draw, 2 if loose. This information is coming from game
	 *         controller thread.
	 */
	private String extractResult(String fullMessageFromGameController) {

		return new ArrayList<String>(Arrays.asList(fullMessageFromGameController.split("-"))).get(1);
	}

	/**
	 * asks user which card to play, then plays the card by sending the card to the
	 * game controller thread.
	 * 
	 * @param deck the list of cards that the client has
	 * @return the updated deck, which is one element less than the previous one
	 *         since the card is used.
	 * @throws IOException
	 */
	private ArrayList<String> playCard(ArrayList<String> deck) throws IOException {

		String userCardSelection = "";

		while (!deck.contains(userCardSelection)) {
			userCardSelection = Integer.toString(parseOptions("ingame", deck));

			if (deck.contains(userCardSelection)) {
				deck.remove(userCardSelection);
				sendMessageToGameController("playcard-" + userCardSelection);
				NUM_CARDS_PLAYED++;
				break;
			} else {
				System.out.println("The card you've select is not in deck.");
				continue;
			}
		}
		return deck;

	}

	/**
	 * extracts the deck given by server.
	 * 
	 * @param fullMessageFromGameController the message that has the half of the
	 *                                      full deck.
	 * @return the deck from the full message since the first character represents
	 *         the meaning of the message.
	 */
	private ArrayList<String> extractDeck(String fullMessageFromGameController) {
		ArrayList<String> deck = new ArrayList<String>(Arrays.asList(fullMessageFromGameController.split("-")));
		deck.remove(0);
		return deck;
	}

	/**
	 * In the start of the game, asks user which action to be chosen.
	 * 
	 * @throws IOException
	 */
	private void askInitialActionsToPlayer() throws IOException {
		if (IS_FIRST_TIME) {
			int userRequest = parseOptions("initial", null);
			if (userRequest == 1) {
				sendMessageToGameController("wantgame");
			} else {
				System.out.println("Disconnecting...");
				System.exit(0);
			}
			IS_FIRST_TIME = false;
		}
	}

	/**
	 * Prints the user options and parses the user input.
	 * 
	 * @param type print pre-game options if "initial", else print the specified
	 *             options in the project description.
	 * @param deck
	 * @return user selection
	 * @throws IOException
	 */
	private int parseOptions(String type, ArrayList<String> deck) throws IOException {
		Scanner input = new Scanner(System.in);
		int mode = -1;
		if (type.equals("initial")) {
			System.out.println("----------------OPTIONS----------------");
			System.out.println();
			System.out.println("1- PLAY THE GAME");
			System.out.println("2- EXIT THE GAME");
			mode = input.nextInt();

			while (mode != 1 && mode != 2) {
				System.out.println("Invalid Option. Please re-enter: ");
				mode = input.nextInt();
			}
		}

		if (type.equals("ingame")) {
			System.out.println("\nYOUR DECK: " + deck);
			System.out.println();
			System.out.println("----------------OPTIONS----------------");
			System.out.println();
			System.out.println("1- PLAY THE CARD");
			System.out.println("2- START A NEW GAME");
			System.out.println("3- QUIT THE GAME");
			mode = input.nextInt();

			while (mode != 1 && mode != 2 && mode != 3) {
				System.out.println("Invalid Option. Please re-enter: ");
				mode = input.nextInt();
			}
			if (mode == 1) {
				System.out.println("Enter the card you want to play");
				mode = input.nextInt();
			}

			if (mode == 2) {
				sendMessageToGameController("newgame");
			}

			if (mode == 3) {

				quitGame();
			}

		}

		return mode;
	}

	private void quitGame() throws IOException {

		sendMessageToGameController("quitgame");
		System.out.println("Disconnecting...");
		System.exit(0);

	}

	private void removePreviousGameState() {
		DECK.clear();
		IS_FIRST_TIME = true;
		NUM_CARDS_PLAYED = 0;
		TOTAL_NUM_CARDS = 0;
	}

	/**
	 * prints the message into output pipe.
	 * 
	 * @param message string that is to be send to game controller thread, i.e. the
	 *                server.
	 * @throws IOException
	 */
	public void sendMessageToGameController(String message) throws IOException {

		outputStream.println(message);
		outputStream.flush();

	}

}
