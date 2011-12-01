package edu.ufl.cise.cn.peer2peer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;
import edu.ufl.cise.cn.peer2peer.entities.Piece;
import edu.ufl.cise.cn.peer2peer.filehandler.PieceManager;
import edu.ufl.cise.cn.peer2peer.messagehandler.MessageManager;
import edu.ufl.cise.cn.peer2peer.peerhandler.PeerHandler;
import edu.ufl.cise.cn.peer2peer.utility.Constants;
import edu.ufl.cise.cn.peer2peer.utility.LogFactory;
import edu.ufl.cise.cn.peer2peer.utility.MessageLogger;
import edu.ufl.cise.cn.peer2peer.utility.PeerConfigFileReader;
import edu.ufl.cise.cn.peer2peer.utility.PeerInfo;
import edu.ufl.cise.cn.peer2peer.utility.PropsReader;

// TODO: Auto-generated Javadoc
/**
 * The Class Controller.
 * 
 * @author sagar
 */
public class Controller {

	public static String LOGGER_PREFIX = Controller.class.getSimpleName();

	private HashMap<String, String> peerCompleteMap = new HashMap<String, String>();

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

	private boolean isAllPeersConnectionEstablished = false;

	public String getPeerID() {
		return peerID;
	}

	/** The peer id. */
	private String peerID;

	private ArrayList<String> chokedPeerList = new ArrayList<String>();

	private ChokeUnchokeManager chokeUnchokeManager = null;

	private OptimisticUnchokeManager optimisticUnchokeManager = null;

	private PeerServer peerServer;

	private MessageLogger logger = null;

	/**
	 * Gets the single instance of Controller.
	 * 
	 * @param peerID
	 *            the peer id
	 * @return single instance of Controller
	 */
	public static synchronized Controller getInstance(String peerID) {
		if (controller == null) {
			controller = new Controller();
			controller.peerID = peerID;
			boolean isInitialized = controller.init();

			if (isInitialized == false) {
				controller.close();
				controller = null;
			}
		}
		return controller;
	}

	/**
	 * Start P2P process.
	 */
	public void startProcess() {
		System.out.println(LOGGER_PREFIX + ": Starting Server process");
		startServerThread(peerID);
		System.out.println(LOGGER_PREFIX + ": Server process started.");
		System.out.println(LOGGER_PREFIX + ": Connecting to client mentioned above the list.");
		connectToPreviousPeerneighbors();

		chokeUnchokeManager = ChokeUnchokeManager.getInstance(this);
		int chokeUnchokeInterval = Integer.parseInt(PropsReader.getPropertyValue(Constants.CHOKE_UNCHOKE_INTERVAL));
		chokeUnchokeManager.start(0, chokeUnchokeInterval);

		optimisticUnchokeManager = OptimisticUnchokeManager.getInstance(this);
		int optimisticUnchokeInterval = Integer.parseInt(PropsReader.getPropertyValue(Constants.OPTIMISTIC_UNCHOKE_INTERVAL));
		optimisticUnchokeManager.start(0, optimisticUnchokeInterval);
	}

	/**
	 * Start peer server thread which will accept connection from other peers
	 * and initiates P2P process with them.
	 * 
	 * @param peerID
	 *            the peer id
	 */
	private void startServerThread(String peerID) {
		new Thread(peerServer).start();
	}

	/**
	 * Connect to previous peer neighbors as per the project requirement.
	 */
	private void connectToPreviousPeerneighbors() {
		HashMap<String, PeerInfo> neighborPeerMap = peerConfigurationReader.getPeerInfoMap();
		Set<String> peerIDList = neighborPeerMap.keySet();

		System.out.println("Current Peer Name : " + peerID);

		for (String neighborPeerID : peerIDList) {
			System.out.println("Checking neighbor client : " + neighborPeerID);
			// if peer ID is less than the ID of this peer then it ocured
			// previously in file.
			if (Integer.parseInt(neighborPeerID) < Integer.parseInt(peerID)) {
				logger.info("Peer " + peerID + " makes a connection  to Peer [" + neighborPeerID + "]");
				System.out.println("Connecting neighbor client : " + neighborPeerID);
				makeConnectionToneighborPeer(neighborPeerMap.get(neighborPeerID));
			}
		}
		setAllPeersConnection(true);
	}

	/**
	 * Make connection to neighbor peer.
	 * 
	 * @param peerInfo
	 *            the peer info
	 */
	private void makeConnectionToneighborPeer(PeerInfo peerInfo) {
		String neighborPeerHost = peerInfo.getHostAddress();
		int neighborPortNumber = peerInfo.getPortNumber();

		try {

//			System.out.println(LOGGER_PREFIX + " Connection peer " + peerInfo.getPeerID() + " on " + neighborPeerHost + " port: " + neighborPortNumber);

			Socket neighborPeerSocket = new Socket(neighborPeerHost, neighborPortNumber);

			System.out.println(LOGGER_PREFIX + " Connected to peer " + peerInfo.getPeerID() + " on " + neighborPeerHost + " port: " + neighborPortNumber);

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
	private boolean init() {

		peerConfigurationReader = PeerConfigFileReader.getInstance();

		if (peerConfigurationReader == null) {
			return false;
		}

		messageManager = MessageManager.getInstance();
		if (messageManager == null) {
			return false;
		}

		if (PeerConfigFileReader.getInstance().getPeerInfoMap().get(peerID).isFileExists() == false) {
			pieceManager = PieceManager.getPieceManagerInstance(false, peerID);
		} else {
			pieceManager = PieceManager.getPieceManagerInstance(true, peerID);
		}

		if (pieceManager == null) {
			return false;
		}

		neighborPeerHandlerList = new ArrayList<PeerHandler>();

		logger = LogFactory.getLogger(peerID);
		if (logger == null) {
			System.out.println("Unable to Initialize logger object");
			close();
			return false;
		}

		peerServer = PeerServer.getInstance(peerID, this);

		logger = LogFactory.getLogger(peerID);

		return true;
	}

	/**
	 * Close.
	 */
	public void close() {

	}

	public boolean checkAllPeersFileDownloadComplete() {

		if (isAllPeersConnection() == false || peerServer.isPeerServerCompleted() == false) {

			return false;
		}

		if (peerConfigurationReader.getPeerInfoMap().size() == peerCompleteMap.size()) {
			shutdown();
		}

		return false;
	}

	public void shutdown() {
		chokeUnchokeManager.deinit();
		optimisticUnchokeManager.deinit();
		logger.close();
		pieceManager.close();
		System.exit(0);
	}

	/**
	 * Checks if is operation compelete.
	 * 
	 * @return true, if is operation compelete
	 */
	public boolean isOperationCompelete() {
		return false;
	}

	/**
	 * Register peer.
	 * 
	 * @param neighborPeerHandler
	 *            the neighbor peer handler
	 */
	public synchronized void registerPeer(PeerHandler neighborPeerHandler) {
		// TODO Auto-generated method stub
		neighborPeerHandlerList.add(neighborPeerHandler);
	}

	/**
	 * Gets the handshake message.
	 * 
	 * @param peerID
	 *            the peer id
	 * @return the handshake message
	 */
	public synchronized byte[] getHandshakeMessage(String peerID) {
		// messageManager.get
		// TODO Auto-generated method stub
		return null;
	}

	public synchronized Peer2PeerMessage getBitFieldMessage() {

		Peer2PeerMessage message = Peer2PeerMessage.getInstance();

		message.setHandler(pieceManager.getBitFieldHandler());
		if (message.getHandler() == null) {
			System.out.println(LOGGER_PREFIX + " BITFIELD HANDLER NULL.");
		}
		message.setMessgageType(Constants.BITFIELD_MESSAGE);

		return message;
	}

	public HashMap<String, Double> getSpeedForAllPeers() {

		/*
		 * HashMap<String, Double> peerSpeeds = new HashMap();
		 * peerSpeeds.put("1010", 100.98d); peerSpeeds.put("1014", 120.9d);
		 * peerSpeeds.put("1015", 98.2d); peerSpeeds.put("1016", 78.3d);
		 * peerSpeeds.put("1017", 108.4d); peerSpeeds.put("1019", 101.7d);
		 * return peerSpeeds;
		 */

		HashMap<String, Double> peerSpeedList = new HashMap<String, Double>();

		for (PeerHandler peerHandler : neighborPeerHandlerList) {
			peerSpeedList.put(peerHandler.getPeerId(), peerHandler.getDownloadSpeed());
		}
		return peerSpeedList;
	}

	public void chokePeers(ArrayList<String> peerList) {
		chokedPeerList = peerList;
		Peer2PeerMessage chokeMessage = Peer2PeerMessage.getInstance();
		chokeMessage.setMessgageType(Constants.CHOKE_MESSAGE);

		// System.out.println(LOGGER_PREFIX+" : Sending CHOKE message to peers...");

		for (String peerToBeChoked : peerList) {
			for (PeerHandler peerHandler : neighborPeerHandlerList) {
				if (peerHandler.getPeerId().equals(peerToBeChoked)) {
					if (peerHandler.isHandshakeMessageReceived() == true) {
						// System.out.println(LOGGER_PREFIX+" : Sending CHOKE message to peers : "+peerToBeChoked);
						peerHandler.sendChokeMessage(chokeMessage);
					}
					break;
				}
			}
		}
	}

	public void unChokePeers(ArrayList<String> peerList) {
		Peer2PeerMessage unChokeMessage = Peer2PeerMessage.getInstance();
		unChokeMessage.setMessgageType(Constants.UNCHOKE_MESSAGE);
		// System.out.println(LOGGER_PREFIX+" : Sending UNCHOKE message to peers...");
		for (String peerToBeUnChoked : peerList) {
			for (PeerHandler peerHandler : neighborPeerHandlerList) {
				if (peerHandler.getPeerId().equals(peerToBeUnChoked)) {
					if (peerHandler.isHandshakeMessageReceived() == true) {
						// System.out.println(LOGGER_PREFIX+" : Sending UNCHOKE message to peers..."+peerToBeUnChoked);
						peerHandler.sendUnchokeMessage(unChokeMessage);
					}
					break;
				}
			}
		}
	}

	public void optimisticallyUnChokePeers(String peerToBeUnChoked) {
		Peer2PeerMessage unChokeMessage = Peer2PeerMessage.getInstance();
		unChokeMessage.setMessgageType(Constants.UNCHOKE_MESSAGE);

		System.out.println(LOGGER_PREFIX + ": Sending OPTIMISTIC UNCHOKE message to " + peerToBeUnChoked);
		logger.info("Peer [" + peerID + "] has the optimistically unchoked neighbor [" + peerToBeUnChoked + "]");
		for (PeerHandler peerHandler : neighborPeerHandlerList) {
			if (peerHandler.getPeerId().equals(peerToBeUnChoked)) {
				if (peerHandler.isHandshakeMessageReceived() == true) {
					peerHandler.sendUnchokeMessage(unChokeMessage);
				}
				break;
			}
		}
	}

	public ArrayList<String> getChokedPeerList() {
		return chokedPeerList;
	}

	public synchronized void insertPiece(Peer2PeerMessage pieceMessage, String sourcePeerID) {		
		pieceManager.writePiece(pieceMessage.getPieceIndex(), pieceMessage.getData());
		logger.info("Peer [" + controller.getPeerID() + "] has downloaded the piece [" + pieceMessage.getPieceIndex() + "] from [" + sourcePeerID + "]. Now the number of pieces it has is " + (pieceManager.getBitFieldHandler().getNoOfPieces()));
	}

	public int[] getMissingPieceIndexArray() {
		return pieceManager.getMissingPieceNumberArray();
	}

	public Peer2PeerMessage getPieceMessage(int pieceIndex) {
		Piece piece = pieceManager.getPiece(pieceIndex);
		if (piece == null) {
			return null;
		} else {
			Peer2PeerMessage message = Peer2PeerMessage.getInstance();
			message.setMessgageType(Constants.PIECE_MESSAGE);
			message.setPieceIndex(pieceIndex);
			message.setData(piece);
			return message;
		}
	}

	public boolean isFileDownloadComplete() {
		return pieceManager.isFileDownloadComplete();
	}

	public void sendHaveMessage(int pieceIndex, String fromPeerID) {
		Peer2PeerMessage haveMessage = Peer2PeerMessage.getInstance();
		haveMessage.setPieceIndex(pieceIndex);
		haveMessage.setMessgageType(Constants.HAVE_MESSAGE);

		for (PeerHandler peerHandler : neighborPeerHandlerList) {
			// System.out.println(LOGGER_PREFIX+": Sending have message from "+peerID+
			// " to : "+peerHandler.getPeerId());
			if (fromPeerID.equals(peerHandler.getPeerId()) == false) {
				peerHandler.sendHaveMessage(haveMessage);
			}
		}
	}

	public void broadcastShutdownMessage() {
		if (isAllPeersConnection() == false || peerServer.isPeerServerCompleted() == false) {

			return;
		}

		Peer2PeerMessage shutdownMessage = Peer2PeerMessage.getInstance();

		shutdownMessage.setMessgageType(Constants.SHUTDOWN_MESSAGE);

		markFileDownloadComplete(peerID);
		for (PeerHandler peerHandler : neighborPeerHandlerList) {
			System.out.println(LOGGER_PREFIX + ": Sending SHUTDOWN message from " + peerID + " to : " + peerHandler.getPeerId());
			peerHandler.sendShutdownMessage(shutdownMessage);
		}
	}

	public int getNumberOfPeersSupposedToBeConnected() {
		HashMap<String, PeerInfo> neighborPeerMap = peerConfigurationReader.getPeerInfoMap();
		Set<String> peerIDList = neighborPeerMap.keySet();

		System.out.println("Current Peer Name : " + peerID);

		int numberOfPeersSupposedToBeEstablishingConnection = 0;

		for (String neighborPeerID : peerIDList) {
			System.out.println("Checking neighbor client : " + neighborPeerID);
			// if peer ID is less than the ID of this peer then it ocured
			// previously in file.
			if (Integer.parseInt(neighborPeerID) > Integer.parseInt(peerID)) {
				numberOfPeersSupposedToBeEstablishingConnection++;
			}
		}

		return numberOfPeersSupposedToBeEstablishingConnection;
	}

	public boolean isAllPeersConnection() {
		return isAllPeersConnectionEstablished;
	}

	public void setAllPeersConnection(boolean isAllPeersConnection) {
		this.isAllPeersConnectionEstablished = isAllPeersConnection;
	}

	public synchronized void markFileDownloadComplete(String peer) {
		peerCompleteMap.put(peer, "");
	}

	public synchronized MessageLogger getLogger() {
		return logger;
	}
}
