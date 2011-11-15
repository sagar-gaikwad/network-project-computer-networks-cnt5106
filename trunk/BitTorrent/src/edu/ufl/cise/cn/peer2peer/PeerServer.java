package edu.ufl.cise.cn.peer2peer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import edu.ufl.cise.cn.peer2peer.peerhandler.PeerHandler;
import edu.ufl.cise.cn.peer2peer.utility.PeerConfigFileReader;
import edu.ufl.cise.cn.peer2peer.utility.PeerInfo;

/**
 * The Class PeerServer.
 * This server will create ServerSocket and listen to incoming socket connection.
 * After receiving socket it creates PeerHandler instance and instantiates P2P protocol with them.
 *
 * @author sagarg
 */
public class PeerServer implements Runnable{
	
	public static String LOGGER_PREFIX = PeerServer.class.getName();
	
	/** The peer configuration reader. */
	private PeerConfigFileReader peerConfigurationReader = null;
	
	/** The peer server. */
	private static PeerServer peerServer = null;
	
	/** The server socket. */
	private ServerSocket serverSocket = null;

	/** The peer server id. */
	private String peerServerID;
	
	/** The controller. */
	private Controller controller;
	
	/**
	 * Gets the single instance of PeerServer.
	 *
	 * @param peerServerID the peer server id
	 * @param controller the controller
	 * @return single instance of PeerServer
	 */
	public static PeerServer getInstance(String peerServerID, Controller controller){
		if(peerServer == null){
			
			peerServer = new PeerServer();
			peerServer.peerServerID = peerServerID;
			peerServer.controller = controller;
			
			boolean isInitialized = peerServer.init();
			if(isInitialized == false){
				peerServer.close();
				peerServer = null;
			}
		}
		return peerServer;
	}
	
	/**
	 * Initialize. Get PeerInfo of all peers from configuration file.
	 *
	 * @return true, if successful
	 */
	public boolean init(){
		
		peerConfigurationReader = PeerConfigFileReader.getInstance();
		
		if(peerConfigurationReader == null){
			return false;
		}
		
		return true;
	}
	
	/**
	 * Close.
	 */
	private void close(){
		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		HashMap<String,PeerInfo> peerInfoMap = peerConfigurationReader.getPeerInfoMap();
		PeerInfo serverPeerInfo = peerInfoMap.get(peerServerID);
		
//		String peerServerHostAddress = serverPeerInfo.getHostAddress();
		int peerServerPortNumber = serverPeerInfo.getPortNumber();
		
		try {
			// accpet incoming connection
			serverSocket = new ServerSocket(peerServerPortNumber);
			
			System.out.println(LOGGER_PREFIX+ " : Waiting for client connection");
			
			Socket neighborPeerSocket = serverSocket.accept();
			
			System.out.println(LOGGER_PREFIX+ " : Connection established");
			
			//create and initialize peer handler class. 
			PeerHandler neighborPeerHandler = PeerHandler.getInstance(	neighborPeerSocket, controller);
			
			neighborPeerHandler.exchangeHandshakeAndBitFieldMessages();
			
			// keep track of encountered peer by registering it to controller
			controller.registerPeer(neighborPeerHandler);
			
			// initiates protocol
			new Thread(neighborPeerHandler).start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
}
