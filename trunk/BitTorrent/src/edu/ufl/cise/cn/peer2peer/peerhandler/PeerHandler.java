package edu.ufl.cise.cn.peer2peer.peerhandler;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import edu.ufl.cise.cn.peer2peer.Controller;
import edu.ufl.cise.cn.peer2peer.entities.HandshakeMessage;
import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;
import edu.ufl.cise.cn.peer2peer.entities.PeerMessage;
import edu.ufl.cise.cn.peer2peer.filehandler.BitFieldHandler;
import edu.ufl.cise.cn.peer2peer.messagehandler.MessageManager;
import edu.ufl.cise.cn.peer2peer.utility.Constants;

/**
 * The Class PeerHandler.
 * Communication with each neighboring peer is handled through this class.
 *
 * @author sagarg
 */
public class PeerHandler implements Runnable{
	
	
	public static final String LOGGER_PREFIX = PeerHandler.class.getCanonicalName();
	
	/** The peer id. */
	private String peerID;
	
	/** The neighbor peer socket. */
	private Socket neighborPeerSocket;
	
	/** The neighbor peer input stream. */
	private ObjectInputStream neighborPeerInputStream;
	
	/** The neighbor peer output stream. */
	private ObjectOutputStream neighborPeerOutputStream;
	
	/** The message manager. */
	private MessageManager messageManager;
	
	/** The controller. */
	private Controller controller;
	
	private PeerMessageSender peerMessageSender;
	
	private ChunkRequester chunkRequester;
	
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
		
		//System.out.println(LOGGER_PREFIX+" Initializing PeerHandler");
		
		try {
			
			neighborPeerOutputStream = new ObjectOutputStream(neighborPeerSocket.getOutputStream());
			neighborPeerInputStream = new ObjectInputStream(neighborPeerSocket.getInputStream());			
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
		
		peerMessageSender = PeerMessageSender.getInstance(neighborPeerOutputStream);
		
		if(peerMessageSender == null){
			close();
			return false;
		}
		
		new Thread(peerMessageSender).start();

		chunkRequester = ChunkRequester.getInstance(controller, this);
		new Thread(peerMessageSender).start();
		
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
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run(){
		byte[] rawData = new byte[Constants.RAW_DATA_SIZE];
		ByteBuffer buffer = ByteBuffer.allocate(Constants.MAX_MESSAGE_SIZE);
		
		// as soon as peer enters into thread it will first send handshake message and receive bitfield message
		
		sendHandShakeMessageAndReceiveBitFieldMessage();
		
		try {
			
			System.out.println("Handshake and BitField Message exchanged");
			
			while(controller.isOperationCompelete() == false){

				System.out.println("Waiting for connection");
				
				PeerMessage message = (PeerMessage)neighborPeerInputStream.readObject();
				
				
				if(message.getType() == Constants.HANDSHAKE_MESSAGE){
					System.out.println(peerID+"Socket closed: "+neighborPeerSocket.isClosed());
					System.out.println(peerID+": Received handshake Message");
					if(message instanceof HandshakeMessage){
						System.out.println(peerID+": message instanceof HandshakeMessage");
						HandshakeMessage handshakeMessage = (HandshakeMessage)message;
						handleHandshakeMessage(handshakeMessage);
					}else{
						// send some invalid data
					}
				}else if(message.getType() == Constants.REQUEST_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message; 
					handleRequestMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.BITFIELD_MESSAGE){
					System.out.println(peerID+": Received bitfiedlMessage");
					handleBitFieldMessage((Peer2PeerMessage)message);
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean sendHandShakeMessageAndReceiveBitFieldMessage(){
		try {
			System.out.println(peerID+" : Sending handshake message in sendHandShakeMessageAndReceiveBitFieldMessage");
			sendHandshakeMessage(peerID);
			System.out.println(peerID+" : waiting for bitfield in sendHandShakeMessageAndReceiveBitFieldMessage");
			
			PeerMessage message = (PeerMessage)neighborPeerInputStream.readObject();
			
			if(message instanceof HandshakeMessage){
				System.out.println("Handshake message");
			}
			
			if(message instanceof BitFieldHandler){
				System.out.println("BitFieldHandler message");
			}
			
			message = (PeerMessage)neighborPeerInputStream.readObject();
			
			if(message instanceof HandshakeMessage){
				System.out.println("Handshake message");
			}
			
			if(message instanceof BitFieldHandler){
				System.out.println("BitFieldHandler message");
			}
			
			Peer2PeerMessage bitfieldManager = (Peer2PeerMessage)neighborPeerInputStream.readObject();
			handleBitFieldMessage(bitfieldManager);
			System.out.println(peerID+" : Read bitfield in sendHandShakeMessageAndReceiveBitFieldMessage");
//			handleBitFieldMessage((Peer2PeerMessage)message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
		try {
			chunkRequester.setBitField(peer2PeerMessage);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Handle handshake message.
	 *
	 * @param handshakeMessage the handshake message
	 */
	synchronized private void handleHandshakeMessage(HandshakeMessage handshakeMessage){
		sendBitFieldMessage();
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
			HandshakeMessage message = HandshakeMessage.getInstance();
			
			peerMessageSender.sendMessage(message);
			
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public synchronized boolean sendBitFieldMessage(){
		try {
			Peer2PeerMessage message = Peer2PeerMessage.getInstance();
			
			peerMessageSender.sendMessage(message);
			
			return true;
		} catch (Exception e) {
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
	/*public void getNeighborPeerID(){
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
	}*/

	public boolean exchangeHandshakeAndBitFieldMessages(){
		try {
			System.out.println(peerID+" : waiting for handshake message in exchangeHandshakeAndBitFieldMessages");
			PeerMessage message = (PeerMessage)neighborPeerInputStream.readObject();
			
			if(message instanceof HandshakeMessage){
				System.out.println("message instanceof HandshakeMessage");
			}
			
			System.out.println(peerID+" : Received handshake message in exchangeHandshakeAndBitFieldMessages");
			
			if(message instanceof HandshakeMessage){
				peerID = message.getType()+"";
			}

			System.out.println(peerID+" : Sending bitfield message in exchangeHandshakeAndBitFieldMessages");
			sendBitFieldMessage();
			System.out.println(peerID+" : Received bitfield  message in exchangeHandshakeAndBitFieldMessages");
			return true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return false;
	}
	
	/**
	 * Send bit field message.
	 *
	 * @param bitFieldMessage the bit field message
	 * @return true, if successful
	 */
	private boolean sendBitFieldMessage(byte[] bitFieldMessage) {
		try {
			/*neighborPeerOutputStream = neighborPeerSocket.getOutputStream();
			neighborPeerOutputStream.write(bitFieldMessage);
			neighborPeerOutputStream.flush();
			neighborPeerOutputStream.close();*/
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void setPeerID(String peerID) {
		this.peerID = peerID;
		
	}
}
