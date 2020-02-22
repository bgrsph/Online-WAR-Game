package domain;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents the state of the game
 * 
 * @author Buğra Sipahioğlu
 */

public class GameState {

	private static int NUMBER_OF_PLAYERS;
	private static int NUMBER_OF_ROUNDS_PLAYED;
	private static HashMap<String, ArrayList<String>> REMAINING_CARDS;
	private static HashMap<String, Integer> SCORE_OF_PLAYERS;

	/**
	 * Constructor of the Game State object. Initializes its fields.
	 * @param numPlayers number of players in the game
	 * @param numRounds number of rounds played so far
	 * @param remainingCards Hash Map that holds {'PlayerName': [List of remaining cards]}
	 * @param scores Hash Map that holds {'PlayerName': [List of remaining cards]}
	 */
	public GameState(int numPlayers, int numRounds, HashMap<String, ArrayList<String>> remainingCards,
			HashMap<String, Integer> scores) {

		NUMBER_OF_PLAYERS = numPlayers;
		NUMBER_OF_ROUNDS_PLAYED = numRounds;
		REMAINING_CARDS = remainingCards;
		SCORE_OF_PLAYERS = scores;
	}
	
	
	
}
