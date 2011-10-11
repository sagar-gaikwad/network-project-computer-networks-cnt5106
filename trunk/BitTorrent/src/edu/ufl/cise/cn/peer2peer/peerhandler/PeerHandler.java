package edu.ufl.cise.cn.peer2peer.peerhandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import edu.ufl.cise.cn.peer2peer.Controller;
import edu.ufl.cise.cn.peer2peer.entities.HandshakeMessage;
import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;
import edu.ufl.cise.cn.peer2peer.entities.PeerMessage;
import edu.ufl.cise.cn.peer2peer.messagehandler.MessageManager;
import edu.ufl.cise.cn.peer2peer.utility.Constants;

// TODO: Auto-generated Javadoc
/**
 * The Class PeerHandler.
 * Communication with each neighboring peer is handled through this class.
 *
 * @author sagarg
 */
public class PeerHandler implements Runnable{
	
	/** The peer id. */
	private String peerID;
	
	/** The neighbor peer socket. */
	private Socket neighborPeerSocket;
	
	/** The neighbor peer input stream. */
	private InputStream neighborPeerInputStream;
	
	/** The neighbor peer output stream. */
	private OutputStream neighborPeerOutputStream;
	
	/** The message manager. */
	private MessageManager messageManager;
	
	/** The controller. */
	private Controller controller;
	
	/**
	 * Instantiates a new peer handler.
	 */
	private PeerHandler(){
		
	}
	
	/**
	 * Gets the single instance of PeerHandler.
	 *
	 * @param socket the socket
	 * @param controller the controller
	 * @return single instance of PeerHandler
	 */
	synchronized public static PeerHandler getInstance(Socket socket, Controller controller){
		PeerHandler peerHandler = new PeerHandler();
		
		peerHandler.neighborPeerSocket = socket;
		peerHandler.controller = controller;
		
		boolean isInitialized = peerHandler.init();
		
		if(isInitialized == false){
			peerHandler.close();
			peerHandler = null;
		}
		return peerHandler;
	}
	
	/**
	 * Inits peerHandler.
	 *
	 * @return true, if successful
	 */
	synchronized private boolean init(){
		if(neighborPeerSocket == null){
			return false;
		}
		
		try {
			neighborPeerOutputStream = neighborPeerSocket.getOutputStream();
			neighborPeerInputStream = neighborPeerSocket.getInputStream();			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		messageManager = MessageManager.getInstance();
		
		if(messageManager == null){
			close();
			return false;
		}
		
		if(controller == null){
			close();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Close all open streams.
	 */
	synchronized public void close(){
		try {
			if(neighborPeerInputStream != null){
				neighborPeerInputStream.close();
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if(neighborPeerOutputStream != null){
				neighborPeerOutputStream.close();
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		byte[] rawData = new byte[Constants.RAW_DATA_SIZE];
		ByteBuffer buffer = ByteBuffer.allocate(Constants.MAX_MESSAGE_SIZE);
		try {
			
			while(controller.isOperationCompelete() == false){
				int numbeOfBytesRead = -1;
				System.out.println("Reading data from peer");
				numbeOfBytesRead = neighborPeerInputStream.read(rawData,0,Constants.RAW_DATA_SIZE);
//				neighborPeerInputStream.reset();
				while(numbeOfBytesRead > 0){
					System.out.println("rawData: "+new String(rawData,0,numbeOfBytesRead));
					buffer.put(rawData);
					numbeOfBytesRead = neighborPeerInputStream.read(rawData,0,Constants.RAW_DATA_SIZE);
				}
				
				System.out.println("Data received: "+buffer.toString());
				
				PeerMessage message = messageManager.parseMessage(buffer.array());
				
				if(message.getType() == Constants.HANDSHAKE_MESSAGE){					
					if(message instanceof Peer2PeerMessage){
						System.out.println("Received handshake message");
						HandshakeMessage handshakeMessage = (HandshakeMessage)message;
						handleHandshakeMessage(handshakeMessage);
					}else{
						// send some invalid data
					}
				}else if(message.getType() == Constants.REQUEST_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message; 
					handleRequestMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.BITFIELD_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handleBitFieldMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.CHOKE_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handleChokeMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.HAVE_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handleHaveMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.INTERESTED_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handleInterestedMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.NOT_INTERESTED_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handleNotInterestedMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.PIECE_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handlePieceMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.UNCHOKE_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handleUnchockMessage(peer2PeerMessage);
				}
				break;
			}
			close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Handle unchock message.
	 *
	 * @param peer2PeerMessage the peer2 peer message
	 */
	private void handleUnchockMessage(Peer2PeerMessage peer2PeerMessage) {
		
	}

	/**
	 * Handle piece message.
	 *
	 * @param peer2PeerMessage the peer2 peer message
	 */
	private void handlePieceMessage(Peer2PeerMessage peer2PeerMessage) {
		
	}

	/**
	 * Handle choke message.
	 *
	 * @param peer2PeerMessage the peer2 peer message
	 */
	private void handleChokeMessage(Peer2PeerMessage peer2PeerMessage) {
		
	}

	/**
	 * Handle bit field message.
	 *
	 * @param peer2PeerMessage the peer2 peer message
	 */
	private void handleBitFieldMessage(Peer2PeerMessage peer2PeerMessage) {
		
	}

	/**
	 * Handle handshake message.
	 *
	 * @param handshakeMessage the handshake message
	 */
	synchronized private void handleHandshakeMessage(HandshakeMessage handshakeMessage){
		// that means we are received request for creating peer connection. (We have not requested explicitly to this peer)
		if(peerID == null){
			peerID = handshakeMessage.getPeerID();
			sendBitFieldMessage(controller.getBitFieldMessage());
			sendHandshakeMessage(peerID);
		}else{
			// We have explicitly connected to this peer and sent handshake message earlier. We will just sent bitfield message in response
			sendBitFieldMessage(controller.getBitFieldMessage());
		}
	}
	
	/**
	 * Handle request message.
	 *
	 * @param message the message
	 */
	synchronized private void handleRequestMessage(Peer2PeerMessage message){
		
	}
	
	/**
	 * Handle have message.
	 *
	 * @param message the message
	 */
	synchronized private void handleHaveMessage(Peer2PeerMessage message){
		
	}
	
	/**
	 * Handle interested message.
	 *
	 * @param message the message
	 */
	synchronized private void handleInterestedMessage(Peer2PeerMessage message){
		
	}
	
	/**
	 * Handle not interested message.
	 *
	 * @param message the message
	 */
	synchronized private void handleNotInterestedMessage(Peer2PeerMessage message){
		
	}
	
	/**
	 * Send handshake message.
	 *
	 * @param peerID the peer id
	 * @return true, if successful
	 */
	public synchronized boolean sendHandshakeMessage(String peerID){
		try {
			
			byte[] data = controller.getHandshakeMessage(peerID);
			
			if(data != null){
				
				System.out.println("Sending handshake message to "+peerID);
				
				neighborPeerOutputStream = neighborPeerSocket.getOutputStream();
				neighborPeerOutputStream.write(data,0,data.length);
				neighborPeerOutputStream.flush();
				neighborPeerOutputStream.close();
				System.out.println("sent data to peer: "+peerID);
			}
			
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Gets the neighbor peer id.
	 *
	 * @return the neighbor peer id
	 */
	public void getNeighborPeerID(){
		try {
			ByteBuffer buffer = ByteBuffer.allocate(32);
			
			byte[] rawData = new byte[Constants.RAW_DATA_SIZE];
			
			int numbeOfBytesRead = neighborPeerInputStream.read(rawData,0,Constants.RAW_DATA_SIZE);
			
			while(numbeOfBytesRead > 0){
				System.out.println("rawData: "+new String(rawData,0,numbeOfBytesRead));
				buffer.put(rawData);
				numbeOfBytesRead = neighborPeerInputStream.read(rawData,0,Constants.RAW_DATA_SIZE);
			}
			
			PeerMessage message = messageManager.parseMessage(buffer.array());
		
			if(message instanceof HandshakeMessage){
				HandshakeMessage handshakeMessage = (HandshakeMessage) message; 
				peerID = handshakeMessage.getType()+"";			
			}
			
			byte[] bitFieldMessage = controller.getBitFieldMessage();
			
			sendBitFieldMessage(bitFieldMessage);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Send bit field message.
	 *
	 * @param bitFieldMessage the bit field message
	 * @return true, if successful
	 */
	private boolean sendBitFieldMessage(byte[] bitFieldMessage) {
		try {
			neighborPeerOutputStream = neighborPeerSocket.getOutputStream();
			neighborPeerOutputStream.write(bitFieldMessage);
			neighborPeerOutputStream.flush();
			neighborPeerOutputStream.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
