package edu.ufl.cise.cn.peer2peer.peerhandler;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import edu.ufl.cise.cn.peer2peer.Controller;
import edu.ufl.cise.cn.peer2peer.entities.HandshakeMessage;
import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;
import edu.ufl.cise.cn.peer2peer.entities.PeerMessage;
import edu.ufl.cise.cn.peer2peer.messagehandler.MessageManager;
import edu.ufl.cise.cn.peer2peer.utility.Constants;
import edu.ufl.cise.cn.peer2peer.utility.MessageLogger;

/**
 * The Class PeerHandler.
 * Communication with each neighboring peer is handled through this class.
 *
 * @author sagarg
 */
public class PeerHandler implements Runnable{
	
	
	public static final String LOGGER_PREFIX = PeerHandler.class.getSimpleName();
	
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
	
	private boolean isChoked;
	
	private boolean isThisPeerChokedByNeighborPeer;
	
	private boolean isHandshakeMessageReceived = false;
	
	private boolean isHandShakeMessageSent = false;
	
	private boolean isChunkRequestedStarted = false;
	
	private boolean isPieceMessageForPreviousMessageReceived = true;
	
	private long downloadStartTime = 0;
	
	private int downloadDataSize = 0;
	
	private MessageLogger logger = null;
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
		
		boolean isInitialized = peerHandler.init(controller);
		
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
	synchronized private boolean init(Controller controller){
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
		
		peerMessageSender = PeerMessageSender.getInstance(neighborPeerOutputStream,this);
		
		if(peerMessageSender == null){
			close();
			return false;
		}
		
		new Thread(peerMessageSender).start();

		chunkRequester = ChunkRequester.getInstance(controller, this);

		logger = controller.getLogger();
		
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
		
		if(peerID != null){
			sendHandshakeMessage();
		}
//		sendHandShakeMessageAndReceiveBitFieldMessage();
		
		try {
			
			//System.out.println(LOGGER_PREFIX+": "+peerID+" : Handshake Message sent");
			
			while(controller.isOperationCompelete() == false){

//				System.out.println(LOGGER_PREFIX+": "+peerID+" : Waiting for connection in while(controller.isOperationCompelete() == false){");
				
				if(controller.isOperationCompelete() == true){
					//System.out.println(LOGGER_PREFIX+": "+peerID+": Breaking from while loop");
					break;
				}				
				
				PeerMessage message = (PeerMessage)neighborPeerInputStream.readObject();
				
				
//				System.out.println(LOGGER_PREFIX+": "+peerID+": RUN : Received Message:["+message.getMessageNumber()+"]: "+Constants.getMessageName(message.getType()));
				
				if(message.getType() == Constants.HANDSHAKE_MESSAGE){					
					if(message instanceof HandshakeMessage){						
						HandshakeMessage handshakeMessage = (HandshakeMessage)message;
						handleHandshakeMessage(handshakeMessage);
					}else{
						// send some invalid data
					}
				}else if(message.getType() == Constants.REQUEST_MESSAGE){					
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message; 
					handleRequestMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.BITFIELD_MESSAGE){					
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
//					System.out.println(LOGGER_PREFIX+": "+peerID+": RUN : Received Message:["+message.getMessageNumber()+"]: "+Constants.getMessageName(message.getType()) +" Piece Index: "+peer2PeerMessage.getPieceIndex());
					handlePieceMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.UNCHOKE_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handleUnchockMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.SHUTDOWN_MESSAGE){
					Peer2PeerMessage peer2peerMessage = (Peer2PeerMessage)message;
					handleShutdownMessage(peer2peerMessage);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized boolean exchangeHandshakeAndBitFieldMessages(){
		try {
			//System.out.println(LOGGER_PREFIX+": "+peerID+" : waiting for handshake message in exchangeHandshakeAndBitFieldMessages");
			HandshakeMessage message = (HandshakeMessage)neighborPeerInputStream.readObject();
			
			//System.out.println(LOGGER_PREFIX+": "+peerID+" : Received handshake message in exchangeHandshakeAndBitFieldMessages");
			peerID = message.getPeerID();

			Thread.sleep(4000);
			
			handleHandshakeMessage(message);
			
			return true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return false;
	}
	
	synchronized private boolean sendHandShakeMessageAndReceiveBitFieldMessage(){
		try {
			
			Thread.sleep(4000);
			
			//System.out.println(LOGGER_PREFIX+": "+peerID+" : Sending handshake message in sendHandShakeMessageAndReceiveBitFieldMessage");
			sendHandshakeMessage();
			//System.out.println(LOGGER_PREFIX+": "+peerID+" : waiting for bitfield in sendHandShakeMessageAndReceiveBitFieldMessage");
								
			
			PeerMessage message = (PeerMessage)neighborPeerInputStream.readObject();
			
			if(message instanceof HandshakeMessage){
				//System.out.println(LOGGER_PREFIX+": "+peerID+": received Handshake message ["+message.getMessageNumber()+"] instead of bitfield message");
			}
			
			if(message instanceof Peer2PeerMessage){
//				System.out.println(LOGGER_PREFIX+": "+peerID+": received bitfield message");
			}
			
			Peer2PeerMessage bitfieldManager = (Peer2PeerMessage)message;
			handleBitFieldMessage(bitfieldManager);
			//System.out.println(LOGGER_PREFIX+": "+peerID+" : Read "+Constants.getMessageName(message.getType())+" ["+message.getMessageNumber()+"] in sendHandShakeMessageAndReceiveBitFieldMessage. Expecting BitField Message");
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
	private void handleUnchockMessage(Peer2PeerMessage unchokeMessage) {		
		isThisPeerChokedByNeighborPeer = false;

		logger.info("Peer ["+controller.getPeerID()+"] is unchoked by ["+peerID+"]");
		
		try {
			chunkRequester.addMessage(unchokeMessage);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Handle piece message.
	 *
	 * @param peer2PeerMessage the peer2 peer message
	 */
	private void handlePieceMessage(Peer2PeerMessage pieceMessage) {
		
		
		
		controller.insertPiece(pieceMessage, peerID);
		controller.sendHaveMessage(pieceMessage.getPieceIndex(),peerID);
		
		downloadDataSize += pieceMessage.getData().getSize();
		
//		System.out.println(LOGGER_PREFIX+": Inserted PieceMessage");		
		setPieceMessageForPreviousMessageReceived(true);
//		System.out.println(LOGGER_PREFIX+" Download Speed: "+getDownloadSpeed());
		try {			
			chunkRequester.addMessage(pieceMessage);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Handle choke message.
	 *
	 * @param peer2PeerMessage the peer2 peer message
	 */
	private void handleChokeMessage(Peer2PeerMessage peer2PeerMessage) {
		logger.info("Peer ["+controller.getPeerID()+"] is choked by ["+peerID+"]");
		isThisPeerChokedByNeighborPeer = true;		
	}

	/**
	 * Handle bit field message.
	 *
	 * @param peer2PeerMessage the peer2 peer message
	 */
	private void handleBitFieldMessage(Peer2PeerMessage peer2PeerMessage) {
		//System.out.println(LOGGER_PREFIX+": "+peerID+": BITFIELD_MESSAGE :["+peer2PeerMessage.getMessageNumber()+"]: ");
		try {
			chunkRequester.addMessage(peer2PeerMessage);
			
			// we need to start chunk request message only after complete handshake for both sides
			// when this is message is received we need to check whether handshake message from the same peer has received already
			
			if(isHandshakeMessageReceived == true && isHandShakeMessageSent == true && isChunkRequestedStarted() == false){
//				System.out.println(LOGGER_PREFIX+": "+peerID+": Starting Chunk Requested in BitField handler" );
				new Thread(chunkRequester).start();
				startMeasuringDownloadTime();

				// to avoid creation of multiple chunk requested threads.
				setChunkRequestedStarted(true) ;
			}
			
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
	private void handleHandshakeMessage(HandshakeMessage handshakeMessage){
		//System.out.println(LOGGER_PREFIX+": "+peerID+": HANDSHAKE_MESSAGE :["+handshakeMessage.getMessageNumber()+"]: ");
		peerID = handshakeMessage.getPeerID();
		sendBitFieldMessage();
		
		if(isHandShakeMessageSent == false){
			logger.info("Peer "+controller.getPeerID()+" is connected from Peer "+peerID+".");
			sendHandshakeMessage();
		}
		
		isHandshakeMessageReceived = true;		
		
		if(isHandshakeMessageReceived == true && isHandShakeMessageSent == true && isChunkRequestedStarted() == false){
//			System.out.println(LOGGER_PREFIX+": "+peerID+": Starting Chunk Requested HandShakeMessage handler" );			
			new Thread(chunkRequester).start();
			startMeasuringDownloadTime();
			// to avoid creation of multiple chunk requested threads.
			setChunkRequestedStarted(true);
		}
	}
	
	/**
	 * Handle request message.
	 *
	 * @param message the message
	 */
	private void handleRequestMessage(Peer2PeerMessage requestMessage){
//		System.out.println(LOGGER_PREFIX+": "+peerID+": REQUEST_MESSAGE ["+requestMessage.getMessageNumber()+"]: for PIECE: "+ requestMessage.getPieceIndex() );
		if(isChoked == false){
			Peer2PeerMessage pieceMessage = controller.getPieceMessage(requestMessage.getPieceIndex());
			
			if(pieceMessage != null){
				try {
					Thread.sleep(2000);
//					System.out.println(LOGGER_PREFIX+": "+peerID+": Sending PIECE: "+ pieceMessage.getPieceIndex() );
					peerMessageSender.sendMessage(pieceMessage);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Handle have message.
	 *
	 * @param message the message
	 */
	private void handleHaveMessage(Peer2PeerMessage haveMessage){
		logger.info("Peer ["+controller.getPeerID()+"] recieved the 'have' message from ["+peerID+"] for the piece"+haveMessage.getPieceIndex());
		//		System.out.println(LOGGER_PREFIX+": "+peerID+": HAVE_MESSAGE ["+haveMessage.getMessageNumber()+"]: for PIECE: "+ haveMessage.getPieceIndex() );
		try {
			chunkRequester.addMessage(haveMessage);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Handle interested message.
	 *
	 * @param message the message
	 */
	private void handleInterestedMessage(Peer2PeerMessage interestedMessage){
		logger.info("Peer ["+controller.getPeerID()+"] recieved the 'interested' message from ["+peerID+"]");
//		System.out.println(LOGGER_PREFIX+": Received interested Message from "+peerID);
	}
	
	/**
	 * Handle not interested message.
	 *
	 * @param message the message
	 */
	private void handleNotInterestedMessage(Peer2PeerMessage message){
		logger.info("Peer ["+controller.getPeerID()+"] recieved the 'not interested' message from ["+peerID+"]");
//		System.out.println(LOGGER_PREFIX+": Received not interested Message from "+peerID);
	}
	
	/**
	 * Send handshake message.
	 *
	 * @return true, if successful
	 */
	synchronized boolean sendHandshakeMessage(){
		try {
			HandshakeMessage message = HandshakeMessage.getInstance();
			message.setPeerID(controller.getPeerID());
			peerMessageSender.sendMessage(message);
			isHandShakeMessageSent = true;
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	synchronized boolean sendBitFieldMessage(){
		try {
			
			
			Peer2PeerMessage message = controller.getBitFieldMessage();
			
			peerMessageSender.sendMessage(message);

			Thread.sleep(4000);
			
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	

	public void sendInterestedMessage(Peer2PeerMessage interestedMessage){
		try {
			if(isThisPeerChokedByNeighborPeer == false){
				peerMessageSender.sendMessage(interestedMessage);
			}			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isDownloadComplete()
	{
		if(isChunkRequestedStarted() == true){
			return chunkRequester.isNeighborPeerDownloadedFile();
		}else{
			return false;
		}
	}
	public void sendNotInterestedMessage(Peer2PeerMessage notInterestedMessage){

		try {
			peerMessageSender.sendMessage(notInterestedMessage);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendRequestMessage(Peer2PeerMessage requestMessage){
		try {
			if(isThisPeerChokedByNeighborPeer == false){
				peerMessageSender.sendMessage(requestMessage);
			}			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendChokeMessage(Peer2PeerMessage chokeMessage) {		
//		System.out.println(LOGGER_PREFIX+" sending CHOKE_MESSAGE to "+peerID);
		try {
			if(isChoked == false)
			{
				startMeasuringDownloadTime();
				setChoke(true);
				peerMessageSender.sendMessage(chokeMessage);
			}
			else
			{
//				System.out.println(LOGGER_PREFIX+" Already CHOKED ");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendUnchokeMessage(Peer2PeerMessage unchokeMessage)
	{
//		System.out.println(LOGGER_PREFIX+" sending UNCHOKE_MESSAGE to "+peerID);
		try {
			if(isChoked == true)
			{
				startMeasuringDownloadTime();
				setChoke(false);
				peerMessageSender.sendMessage(unchokeMessage);
			}
			else
			{
//				System.out.println(LOGGER_PREFIX+" Already UNCHOKED");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void handleUnchokeMessage(Peer2PeerMessage unchokeMessage){
		try {
			//unchokeMessage.
			logger.info("Peer ["+peerID+"] is unchoked by ["+controller.getPeerID()+"]");
			peerMessageSender.sendMessage(unchokeMessage);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendHaveMessage(Peer2PeerMessage haveMessage) {
		try {
			peerMessageSender.sendMessage(haveMessage);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendShutdownMessage(Peer2PeerMessage shutdownMessage)
	{
		//Mohit
		try {
			peerMessageSender.sendMessage(shutdownMessage);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void handleShutdownMessage(Peer2PeerMessage message)
	{
		controller.markFileDownloadComplete(peerID);
	}

	private void setChoke(boolean message)
	{
		isChoked = message;
	}
	
	public boolean isPeerChoked()
	{
		return isChoked;
	}

	public String getPeerId()
	{
		return peerID;
	}
	
	synchronized public void setPeerID(String peerID) {
		this.peerID = peerID;
		
	}
	
	public boolean isPieceMessageForPreviousMessageReceived() {
		return isPieceMessageForPreviousMessageReceived;
	}

	public void setPieceMessageForPreviousMessageReceived(
			boolean isPieceMessageForPreviousMessageReceived) {
		this.isPieceMessageForPreviousMessageReceived = isPieceMessageForPreviousMessageReceived;
	}
	
	private void startMeasuringDownloadTime(){
		downloadStartTime = System.currentTimeMillis();
		downloadDataSize = 0;
	}
	
	public double getDownloadSpeed(){
		long timePeriod = System.currentTimeMillis() - downloadStartTime;
		if(timePeriod != 0){
			return ((downloadDataSize * 1.0) / (timePeriod * 1.0) );
		}else{
			return 0;
		} 
	}
	
	public boolean isHandshakeMessageReceived() {
		return isHandshakeMessageReceived;
	}

	public void setHandshakeMessageReceived(boolean isHandshakeMessageReceived) {
		this.isHandshakeMessageReceived = isHandshakeMessageReceived;
	}


	public synchronized boolean isChunkRequestedStarted() {
		return isChunkRequestedStarted;
	}


	public synchronized void setChunkRequestedStarted(boolean isChunkRequestedStarted) {
		this.isChunkRequestedStarted = isChunkRequestedStarted;
	}

	
	
}
