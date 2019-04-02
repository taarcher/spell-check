import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Proj4.java
 * 
 * This file contains the main method.
 * 
 * The user is prompted for a dictionary file (a list of words), a file to be read and compared to the dictionary,
 * and the desired name for the output file.  The output file consists of a list words suspected of being spelled
 * incorrectly.  Following the word list, a short table of statistics for the program is output.
 * 
 * It is worth noting that matching the dictionary to the text being reviewed is not case sensative.
 * 
 * @author Thomas "Andy" Archer
 *
 */
public class Proj4 {

	//number of words in the dictionary
	public int dicSize;
	
	//number of words in the file being reviewed
	public int inSize;
	
	//number of words suspected of being spelled incorrectly
	public int missed;
	
	//number of probes into the dictionary
	public int probs;
	
	//number of lookups 
	public int numUps;
	
	//constant for the hash function
	final int r = 37;
	//size of the hash table for dictionary storage
	final int m = 27940;
	
	//array of nodes making up the hash table
	public Node[] list = new Node[ m ];
	
	//string for the incorrectly spelled words output
	public String missedOut = new String();
	
	//string for the stats output
	public String stats = new String();
	
	/**
	 * Constructor for Proj4.  This is run by the main method.
	 * 
	 * @param dicName - String - file name of the dictionary to use
	 * @param inputName - String - file name of the file to be reviewed
	 * @param outputName - String - file name for the output
	 */
	public Proj4( String dicName, String inputName, String outputName ){
		
		//build the dictionary
		buildDictionary( dicName );
		
		Scanner in = null;
		try {
			in = new Scanner( new File ( inputName ));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		String word = new String();
		
		//read the file
		while( in.hasNext() ){
			word = in.next().trim();
			
			//run the spell check method one word at a time
			checkSpelling( word );
			
		}
		
		//add a space between the word list and the stats
		missedOut += "\n";
		
		//close the file being reviewed
		in.close();
		
		//add the statistics table to the output.
		stats += " RESULTS FROM THE SCAN OF " + inputName.toUpperCase() + "\n";
		
		int lineSize = stats.length();
		
		for( int i = 0; i < lineSize; i++){
			stats += "-";
		}
		
		stats += "\n";
		
		stats += "The dictionary contains " + dicSize + " words.\n";

		stats += "There were " + inSize + " words checked from the input file.\n";
		stats += "There are " + missed + " possibly misspelled words in " + inputName + ".\n";
		stats += "Number of probes into dictionary performed in review: " + probs + "\n";
		
		stats += "Average number of probes per word checked: ";
		stats += String.format("%.3g%n", ((double) probs / inSize));
		
		stats += "Average number of probes per lookUp operation: ";
		stats += String.format("%.3g%n", ((double) probs / numUps));
		
		//write the output file
		PrintWriter out = null;
		
		try {
			out = new PrintWriter( outputName );
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
		
		out.print(missedOut);
		out.print(stats);
		
		//close the output file
		out.close();
	}
	
	/**
	 * Method which checks the spelling one "word" at a time.
	 * 
	 * NOTE: It is NOT case-sensative.
	 * 
	 * @param word - String - word to be compared to dictionary entries
	 */
	public void checkSpelling(String word) {
		
		//used for review and manipulation of the word to different tense.
		boolean found = false;
		String temp = word;
		String tmd = word;
		String tmdb = tmd;
		
		//increase the variable keeping track of the size of the file being reviewed
		inSize++;
		
		//get rid of any punctuation on the front end of the word
		while(  tmd.length() > 1 && !Character.isLetterOrDigit( tmd.charAt( 0 ))){
			tmd = tmd.substring(1);
		}
		
		//get rid of any punctuation on the back end of the word
		while(  tmd.length() > 1 && !Character.isLetterOrDigit( tmd.charAt( tmd.length()-1 ))){
			tmd = tmd.substring(0, tmd.length() - 1);
		}
		
		temp = tmd;
		
		//check that any words are divided by punctuation (such as "-")
		//if so, divide the word and pass each part back through the method
		for ( int i = 0; i < tmd.length(); i++ ){
			if( tmd.length() > 1 && !Character.isLetterOrDigit( tmd.charAt( i ))){
				if ( tmd.charAt(i) != 39 ){
					String tempa = tmd.substring(0, i);
					String tempb = tmd.substring(i);
					checkSpelling( tempa );
					checkSpelling( tempb );
					inSize--;
					found = true;
				}
			}
		}
		
		//if the "word" is only one character long, check that it is a letter or number
		if (tmd.length() == 1){
			if( !Character.isLetterOrDigit( tmd.charAt(0))){
				inSize--;
				return;
			}
		}
		
		//look up the word for the first time
		if (!found ){
			found = lookUp( tmd );
			numUps++;
		}
		
		//check if the root of a possessive tense is in the dictionary
		if ( !found && tmd.length() > 2 && tmd.substring(tmd.length()-2 ).equalsIgnoreCase("'s")){
			tmd = tmd.substring(0, tmd.length() - 2);
			found = lookUp( tmd );
			numUps++;
		}
		
		//check that the root of a plural word is in the dictionary
		if( !found && tmd.length() > 1 && tmd.substring( tmd.length()-1 ).equalsIgnoreCase("s")){
			tmd = tmd.substring(0, tmd.length() - 1);
			found = lookUp( tmd );
			numUps++;
			if( !found && tmd.length() > 1 && tmd.substring( tmd.length()-1 ).equalsIgnoreCase("e")){
				tmd = tmd.substring(0, tmd.length() - 1);
				found = lookUp( tmd );
				numUps++;
			}
		}
		
		//check that the root of a past tense word is in the dictionary
		if( !found && tmd.length() > 2 && tmd.substring( tmd.length() - 2 ).equalsIgnoreCase("ed")){
			tmdb = tmd.substring(0, tmd.length() - 2);
			found = lookUp( tmdb );
			numUps++;
			if( found ){
				tmd = tmdb;
			}
			if( !found ){
				tmd = tmd.substring(0, tmd.length() - 1);
				found = lookUp( tmd );
				numUps++;
			}
		}
		
		//check that the root of a word ending in er is in the dictionary
		if( !found && tmd.length() > 2 && tmd.substring( tmd.length() - 2 ).equalsIgnoreCase("er")){
			tmdb = tmd.substring(0, tmd.length() - 2);
			found = lookUp( tmdb );
			numUps++;
			if( found ){
				tmd = tmdb;
			}
			if( !found ){
				tmd = tmd.substring(0, tmd.length() - 1);
				found = lookUp( tmd );
				numUps++;
			}
		}
		
		//check if the root of a word ending in ing is in the dictionary
		if( !found && tmd.length() > 3 && tmd.substring( tmd.length() - 3 ).equalsIgnoreCase("ing")){
			tmd = tmd.substring(0, tmd.length() - 3);
			found = lookUp( tmd );
			numUps++;
			if( !found ){
				tmd += "e";
				found = lookUp( tmd );
				numUps++;
			}
		}
		
		//check that the root word of an adverb is in the dictionary
		if ( !found && tmd.length() > 2 && tmd.substring(tmd.length()-2 ).equalsIgnoreCase("ly")){
			tmd = tmd.substring(0, tmd.length() - 2);
			found = lookUp( tmd );
			numUps++;
		}

		//if the word was not found, add it to the suspected mispelled words list
		if( !found ){
			missed++;
			missedOut += temp + "\n";
		}
		
	}

	/**
	 * looks up the word passed into the method in the dictionary.
	 * 
	 * NOTE: This method is NOT case sensative.
	 * 
	 * @param find - String - word to be found in the dictionary.
	 * 
	 * @return boolean - returns true if the word is found, false if it is not
	 */
	public boolean lookUp( String find ){
		
		Node temp;
		int poke = 0;
		int temp_hc = getHash( find );
		temp = list[ temp_hc ];
		while (temp != null){
			poke++;
			if( temp.getEntry().equalsIgnoreCase(find)){
				probs += poke;
				return true;
			}
			temp = temp.getNext();
		}
		
		return false;
		
	}
	
	/**
	 * builds the dictionary.  Uses the file name provided by the user to open a 
	 * Scanner and read the file.
	 * 
	 * @param dicName - String - name of the file to be input as the dictionary
	 */
	public void buildDictionary(String dicName) {
		Scanner d = null;
		try {
			d = new Scanner( new File ( dicName ));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String temp = new String();
		int temp_hc = -1;
		Node insert;
		while( d.hasNext() ){
			temp = d.next().trim();
			temp_hc = getHash( temp );
			if( list[temp_hc] == null ){
				list[temp_hc] = new Node( temp );
			} else {
				insert = list[temp_hc];
				while ( insert.getNext() != null){
					insert = insert.getNext();
				}
				insert.setNext( new Node( temp ));
			}
			dicSize++;
			
		}
		d.close();
	}

	/**
	 * gets the hash code for the word.
	 * 
	 * @param word - String - word to get the hash code for
	 * @return int - row in the hash table with the bucket the word should be in.
	 */
	public int getHash( String word ){
		int hc = 0;
		int wl = word.length();
		
		for( int i = 0; i < wl; i++){
			hc += hc * r + Character.getNumericValue( word.charAt( i ) ) ;
		}
		return Math.abs(hc % m);
	}
	
	/**
	 * Main method for the program.  
	 * 
	 * @param args - any commandline arguements the user may have input.  Not used.
	 */
	public static void main(String[] args) {
		Scanner user_in = new Scanner( System.in );
		String dicName;
		System.out.println("Please enter Dictionary file name:");
		dicName = user_in.nextLine().trim();
		
		String inputName;
		System.out.println("Please file to be spell checked:");
		inputName = user_in.nextLine().trim();
		
		String outputName;
		System.out.println("Please enter the output file name:");
		outputName = user_in.nextLine().trim();

		user_in.close();
		
		new Proj4( dicName, inputName, outputName );
	}

}
