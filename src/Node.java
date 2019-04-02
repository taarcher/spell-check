


/**
 * Node.java
 * 
 * class to create a node used to form the linked list for the bucket in the hash
 * table.
 * 
 * @author Thomas "Andy" Archer
 *
 */
public class Node {
	
	//String of the word in the dictionary
	String entry;
	
	//Next node in the bucket
	Node next;

	/**
	 * Constructor for the Node object. 
	 * 
	 * @param entry - String - entry for the dictionary
	 */
	public Node (String entry){
		this.entry = entry;
		next = null;
	}

	/**
	 * gets the id of the Node.  This is the vertex the list is for.
	 * 
	 * 
	 * @return - String - dictionary entry stored in this node
	 */
	public String getEntry(){
		return entry;
	}
	
	/**
	 * sets the next node for the next dictionary entry in the bucket
	 * 
	 * @param next - Node - node to be set as the next in the bucket
	 */
	public void setNext( Node next ){
		this.next = next;
	}
	
	/**
	 * gets the next node linked in this bucket
	 * 
	 * @return Node - next node in the linked list
	 */
	public Node getNext(){
		return next;
	}
}
