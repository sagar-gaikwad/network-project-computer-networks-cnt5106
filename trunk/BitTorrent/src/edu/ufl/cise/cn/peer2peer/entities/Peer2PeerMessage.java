package edu.ufl.cise.cn.peer2peer.entities;

// TODO: Auto-generated Javadoc
/**
 * The Class Peer2PeerMessage.
 *
 * @author sagar
 */
public class Peer2PeerMessage implements PeerMessage{
	

	private int messageLength;
	private int messgageType;
	private int pieceIndex;
	private Piece data;
	
	/**
	 * Instantiates a new peer2 peer message.
	 */
	private Peer2PeerMessage(){
		
	}
	
	/**
	 * Gets the single instance of Peer2PeerMessage.
	 *
	 * @return single instance of Peer2PeerMessage
	 */
	public static Peer2PeerMessage getInstance(){
		Peer2PeerMessage message = new Peer2PeerMessage();
		boolean isSuccessful = message.init();
		
		if(isSuccessful == false){
			message.close();
			message = null;
		}
		
		return message;
	}
	
	/**
	 * Inits the.
	 *
	 * @return true, if successful
	 */
	private boolean init(){
		return true;
	}
	
	/**
	 * Close.
	 */
	public void close(){
		
	}
	
	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public byte[] getMessage(){
		return null;
	}

	public int getType() {
		// TODO Auto-generated method stub
		return this.messgageType;
	}

	public int getMessageLength() {
		// TODO Auto-generated method stub
		return this.messageLength;
	}
	
	public int getMessgageType() {
		return messgageType;
	}

	public void setMessgageType(int messgageType) {
		this.messgageType = messgageType;
	}

	public int getPieceIndex() {
		return pieceIndex;
	}

	public void setPieceIndex(int pieceIndex) {
		this.pieceIndex = pieceIndex;
	}

	public Piece getData() {
		return data;
	}

	public void setData(Piece data) {
		this.data = data;
	}

	public void setMessageLength(int messageLength) {
		this.messageLength = messageLength;
	}

}
