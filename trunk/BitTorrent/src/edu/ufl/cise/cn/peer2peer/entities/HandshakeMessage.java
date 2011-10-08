package edu.ufl.cise.cn.peer2peer.entities;

// TODO: Auto-generated Javadoc
/**
 * The Class HandshakeMessage.
 * This function creates handshake message and provides function to get the Handshake message. 
 */
public class HandshakeMessage {
	
	/** The hand shake header. */
	private String handShakeHeader;
	
	/** The peer id. */
	private String peerID;
	
	/**
	 * Instantiates a new handshake message.
	 */
	private HandshakeMessage(){
		
	}
	
	/**
	 * Gets the single instance of HandshakeMessage.
	 *
	 * @return single instance of HandshakeMessage
	 */
	public static HandshakeMessage getInstance(){
		HandshakeMessage handshakeMessage = new HandshakeMessage();
		boolean isSuccessful = handshakeMessage.init();
		if(isSuccessful == false){
			handshakeMessage.close();
			handshakeMessage = null;
		}
		
		return handshakeMessage;
	}
	
	/**
	 * Inits the.
	 *
	 * @return true, if successful
	 */
	private boolean init(){
		return false;
	}
	
	/**
	 * Close.
	 */
	public void close(){
		
	}
	
	/**
	 * Gets the handshake message. 
	 *
	 * @return the handshake message
	 */
	public byte[] getHandshakeMessage(){
		return null;
	}
}
