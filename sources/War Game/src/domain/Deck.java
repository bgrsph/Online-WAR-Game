package domain;
/**
 * Represents the deck that hold cards.
 * @author Buğra Sipahioğlu
 */
import java.util.ArrayList;
import java.util.Collections;

public class Deck {
	
	public static final int NUMBER_OF_CARDS =52;
	ArrayList<Integer> cards;
	
	ArrayList<String> firstHalfOfDeck;
	ArrayList<String> secondHalfOfDeck;

	/**
	 * Constructor of the deck object. Creates cards and split them into two halves in a random manner. 
	 */
	public Deck() {
		
		cards = new ArrayList<Integer>();
		firstHalfOfDeck = new ArrayList<String>();
		secondHalfOfDeck = new ArrayList<String>();

		
		for (int i = 0; i < NUMBER_OF_CARDS; i++ ) {
			cards.add(i);
		}
		
		Collections.shuffle(cards);
	
		for (int i = 0; i < NUMBER_OF_CARDS; i++)
		{
			if (i < (NUMBER_OF_CARDS + 1)/2)
				firstHalfOfDeck.add(Integer.toString(cards.get(i)));
			else
				secondHalfOfDeck.add(Integer.toString(cards.get(i)));
		}
		 
	}
	
	/**
	 * 
	 * @return the full deck, which is an array of numbers from 0 to 51.
	 */
	public ArrayList<Integer> getFullDeck() {
		
		return this.cards;
	}

	/**
	 * 
	 * @return the random half of the deck
	 */
	public String getFirstHalfOfDeck() {
		
		StringBuilder strBuilder = new StringBuilder("");
		
		for (int i = 0; i < firstHalfOfDeck.size(); i++ ) {
			strBuilder.append("-");
			strBuilder.append(firstHalfOfDeck.get(i));
		}
		
		return strBuilder.toString();
	}
	
	/**
	 * 
	 * @return the random half of the deck
	 */
	public String getSecondHalfOfDeck() {
		
		StringBuilder strBuilder = new StringBuilder("");
		
		for (int i = 0; i < secondHalfOfDeck.size(); i++ ) {
			strBuilder.append("-");
			strBuilder.append(secondHalfOfDeck.get(i));
		}
		
		return strBuilder.toString();
	}
	
}
