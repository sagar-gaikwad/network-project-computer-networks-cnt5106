package edu.ufl.cise.cn.peer2peer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import com.test.Peer2;

import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;
import edu.ufl.cise.cn.peer2peer.entities.PeerMessage;
import edu.ufl.cise.cn.peer2peer.entities.Piece;
import edu.ufl.cise.cn.peer2peer.filehandler.PieceManager;
import edu.ufl.cise.cn.peer2peer.messagehandler.MessageManager;
import edu.ufl.cise.cn.peer2peer.peerhandler.PeerHandler;
import edu.ufl.cise.cn.peer2peer.utility.Constants;
import edu.ufl.cise.cn.peer2peer.utility.LogFactory;
import edu.ufl.cise.cn.peer2peer.utility.MessageLogger;
import edu.ufl.cise.cn.peer2peer.utility.PeerConfigFileReader;
import edu.ufl.cise.cn.peer2peer.utility.PeerInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class Controller.
 *
 * @author sagar
 */
public class Controller {
	
	public static String LOGGER_PREFIX = Controller.class.getSimpleName();
	
	MessageLogger logger = null;
	/** The controller. */
	private static Controller controller = null;
	
	/** The neighbor peer handler list. */
	private ArrayList<PeerHandler> neighborPeerHandlerList = null;
	
	/** The message manager. */
	private MessageManager messageManager = null;
	
	/** The piece manager. */
	private PieceManager pieceManager = null;
	
	/** The peer configuration reader. */
	private PeerConfigFileReader peerConfigurationReader = null;
	
	public String getPeerID() {
		return peerID;
	}

	/** The peer id. */
	private String peerID;
	
	/**
	 * Gets the single instance of Controller.
	 *
	 * @param peerID the peer id
	 * @return single instance of Controller
	 */
	public static synchronized Controller getInstance(String peerID){
		if(controller == null){
			controller = new  Controller();
			controller.peerID = peerID;
			boolean isInitialized = controller.init();
			
			if(isInitialized == false){
				controller.close();
				controller = null;
			}
		}
		return controller;
	}
	
	/**
	 * Start P2P process.
	 */
	public void startProcess(){
		System.out.println(LOGGER_PREFIX+": Starting Server process");
		startServerThread(peerID);
		System.out.println(LOGGER_PREFIX+": Server process started.");
		System.out.println(LOGGER_PREFIX+": Connecting to client mentioned above the list.");
		connectToPreviousPeerneighbors();
	}
	
	/**
	 * Start peer server thread which will accept connection from other peers and initiates P2P process with them.
	 *
	 * @param peerID the peer id
	 */
	private void startServerThread(String peerID){
		PeerServer peerServer = PeerServer.getInstance(peerID, this);
		new Thread(peerServer).start();
	}
	
	/**
	 * Connect to previous peer neighbors as per the project requirement.
	 */
	private void connectToPreviousPeerneighbors(){
		HashMap<String, PeerInfo> neighborPeerMap = peerConfigurationReader.getPeerInfoMap();
		Set<String> peerIDList = neighborPeerMap.keySet();
		
		
		System.out.println("Current Peer Name : "+peerID);
		
		for (String neighborPeerID : peerIDList) {
			System.out.println("Checking neighbor client : "+neighborPeerID);
			// if peer ID is less than the ID of this peer then it ocured previously in file. 
			if(Integer.parseInt(neighborPeerID) < Integer.parseInt(peerID)){
				System.out.println("Connecting neighbor client : "+neighborPeerID);
				makeConnectionToneighborPeer(neighborPeerMap.get(neighborPeerID));
			}
		}
	}
	
	/**
	 * Make connection to neighbor peer.
	 *
	 * @param peerInfo the peer info
	 */
	private void makeConnectionToneighborPeer(PeerInfo peerInfo){
		String neighborPeerHost = peerInfo.getHostAddress();
		int neighborPortNumber = peerInfo.getPortNumber();
		
		try {
			
			System.out.println(LOGGER_PREFIX+" Connection peer "+peerInfo.getPeerID() + " on "+neighborPeerHost + " port: "+neighborPortNumber);
			
			Socket neighborPeerSocket = new Socket(neighborPeerHost, neighborPortNumber);
			
			System.out.println(LOGGER_PREFIX+" Connected to peer "+peerInfo.getPeerID() + " on "+neighborPeerHost + " port: "+neighborPortNumber);
			
			PeerHandler neighborPeerHandler = PeerHandler.getInstance(neighborPeerSocket, this);
			
			neighborPeerHandler.setPeerID(peerInfo.getPeerID());

			
			neighborPeerHandlerList.add(neighborPeerHandler);
			
			new Thread(neighborPeerHandler).start();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Inits the.
	 *
	 * @return true, if successful
	 */
	private boolean init(){
		
		peerConfigurationReader = PeerConfigFileReader.getInstance();
		
		if(peerConfigurationReader == null){
			return false;
		}
		
		messageManager = MessageManager.getInstance();
		if(messageManager == null){
			return false;
		}
		
		if(PeerConfigFileReader.getInstance().getPeerInfoMap().get(peerID).isFileExists() == false){
			pieceManager = PieceManager.getPieceManagerInstance(false,peerID);
		}else{
			pieceManager = PieceManager.getPieceManagerInstance(true,peerID);
		}
		

		
		if(pieceManager == null){
			return false;
		}
		
		neighborPeerHandlerList = new ArrayList<PeerHandler>();
		
		logger = LogFactory.getLogger(peerID);
		if(logger == null){
			System.out.println("Unable to Initialize logger object");
			close();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Close.
	 */
	public void close(){
		
	}
	
	/**
	 * Checks if is operation compelete.
	 *
	 * @return true, if is operation compelete
	 */
	public boolean isOperationCompelete(){
		return false;
	}	

	/**
	 * Register peer.
	 *
	 * @param neighborPeerHandler the neighbor peer handler
	 */
	public synchronized void registerPeer(PeerHandler neighborPeerHandler) {
		// TODO Auto-generated method stub
		neighborPeerHandlerList.add(neighborPeerHandler);
	}

	/**
	 * Gets the handshake message.
	 *
	 * @param peerID the peer id
	 * @return the handshake message
	 */
	public synchronized byte[] getHandshakeMessage(String peerID) {
//		messageManager.get
		// TODO Auto-generated method stub
		return null;
	}
	
	public synchronized Peer2PeerMessage getBitFieldMessage() {
		
		Peer2PeerMessage message = Peer2PeerMessage.getInstance(); 
		
		message.setHandler(pieceManager.getBitFieldHandler());
		message.setMessgageType(Constants.BITFIELD_MESSAGE);
		

		return message;
	}
	
	public HashMap<String,Double> getSpeedForAllPeers(){

		//------------ Test code
		HashMap<String, Double> peerSpeeds = new HashMap();
		peerSpeeds.put("1010", 100.98d);
		peerSpeeds.put("1014", 120.9d);
		peerSpeeds.put("1015", 98.2d);
		peerSpeeds.put("1016", 78.3d);
		peerSpeeds.put("1017", 108.4d);
		peerSpeeds.put("1019", 101.7d);			
		//System.out.println("Ghanta...tumhare pappa ne bhi li thi speed measure kabhi??");		
		return peerSpeeds;
		//------------ Test code ends
		//return null;
	}
	
	public void chokePeers(ArrayList<String> peerList){
		System.out.println("Sagar beta...choke karo");
	}
	
	public void unChokePeers(ArrayList<String> peerList){
		System.out.println("Sagar beta...unchoke karo");
	}
	
	public void optimisticallyUnChokePeers(String peer){
		System.out.println("Sagar beta...optimistically choke karo :P");
	}
	
	public ArrayList<String> getChokedPeerList(){

		//----------Test code
		ArrayList<String> chokedPeers = new ArrayList();
		chokedPeers.add("1002");
		chokedPeers.add("3002");
		chokedPeers.add("4002");
		chokedPeers.add("5002");
		chokedPeers.add("6002");
		chokedPeers.add("7002");
		return chokedPeers;
		//---Test code ends
		//return null;
	}
	
	public void insertPiece(Peer2PeerMessage pieceMessage){
		pieceManager.writePiece(pieceMessage.getPieceIndex(), pieceMessage.getData());
	}
	
	public int[] getMissingPieceIndexArray(){
		return pieceManager.getMissingPieceNumberArray();
	}
	
	public Peer2PeerMessage getPieceMessage(int pieceIndex){
		Piece piece = pieceManager.getPiece(pieceIndex);
		if(piece == null){
			return null;
		}else{
			Peer2PeerMessage message = Peer2PeerMessage.getInstance();
			message.setMessgageType(Constants.PIECE_MESSAGE);
			message.setPieceIndex(pieceIndex);
			message.setData(piece);
			return message;
		}		
	}
	
	public boolean isFileDownloadComplete(){
		return pieceManager.isFileDownloadComplete();
	}

	public void sendHaveMessage(int pieceIndex, String fromPeerID) {
		Peer2PeerMessage haveMessage = Peer2PeerMessage.getInstance();
		haveMessage.setPieceIndex(pieceIndex);
		haveMessage.setMessgageType(Constants.HAVE_MESSAGE);
		
		for (PeerHandler peerHandler : neighborPeerHandlerList) {
//			System.out.println(LOGGER_PREFIX+": Sending have message from "+peerID+ " to : "+peerHandler.getPeerId());
			if(fromPeerID.equals(peerHandler.getPeerId()) == false){
				peerHandler.sendHaveMessage(haveMessage);
			}
		}
		
	}

	public int getNumberOfPeersSupposedToBeConnected() {
		HashMap<String, PeerInfo> neighborPeerMap = peerConfigurationReader.getPeerInfoMap();
		Set<String> peerIDList = neighborPeerMap.keySet();
		
		
		System.out.println("Current Peer Name : "+peerID);
		
		int numberOfPeersSupposedToBeEstablishingConnection = 0;
		
		for (String neighborPeerID : peerIDList) {
			System.out.println("Checking neighbor client : "+neighborPeerID);
			// if peer ID is less than the ID of this peer then it ocured previously in file. 
			if(Integer.parseInt(neighborPeerID) > Integer.parseInt(peerID)){
				numberOfPeersSupposedToBeEstablishingConnection++;
			}
		}
		
		return numberOfPeersSupposedToBeEstablishingConnection;
	}
}
