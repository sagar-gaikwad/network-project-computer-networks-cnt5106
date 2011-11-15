package edu.ufl.cise.cn.peer2peer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
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
	
	public static String LOGGER_PREFIX = Controller.class.getCanonicalName();
	
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
		
		pieceManager = PieceManager.getPieceManagerInstance();
		
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

	/*public byte[] getBitFieldMessage() {
		int[] missingPieceArray = pieceManager.getMissingPieceNumberArray();
		
		// create bitFieldArray according to number of Missing Piece 
		byte[] bitFieldArray = new byte[10];
		
		// return the bitfield array
		return bitFieldArray;
	}*/
	
	public PeerMessage getBitFieldMessage() {
		
		Peer2PeerMessage message = Peer2PeerMessage.getInstance(); 
		
		message.setMessgageType(Constants.BITFIELD_MESSAGE);
		message.setMessageLength(5);
		
		Piece piece = new Piece(5);
		piece.setData(new byte[]{1,2,3,4,5});
		
		message.setData(piece);
		
		// return the bitfield array
		return message;
	}
	
	public HashMap<String,Calendar> getSpeedForAllPeers(){
		return null;
	}
	
	public void chokePeers(ArrayList<String> peerList){
		
	}
	
	public void unChokePeers(ArrayList<String> peerList){
		
	}
	
	public void optimisticallyUnChokePeers(String peer){
		
	}
	
	public ArrayList<String> getChokedPeerList(){
		return null;
	}
}
