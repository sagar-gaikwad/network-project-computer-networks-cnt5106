package edu.ufl.cise.cn.peer2peer.messagehandler;

import edu.ufl.cise.cn.peer2peer.entities.HandshakeMessage;
import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;

public class MessageManager {

	private static MessageManager manager;
	
	private MessageManager(){
		
	}
	
	public static MessageManager getInstance(){
		if(manager == null){
			manager = new MessageManager();
			boolean isInitialized = manager.init();
			
			if(isInitialized == false){
				manager.close();
				manager = null;
			}
		}
		return manager;
	} 
	
	private boolean init(){
		return false;
	}
	
	public void close(){
		
	}
	
	public byte[] geHandshakeMessage(){
		return null;
	}
	
	public byte[] getRequestMessage(int pieceIndex){
		return null;
	}
	
	//getChokeMessage
	
	// getUnchokeMessage
	
	// getInterstedMessage
	
	// getNotInterstedMessage
	
	public byte[] getHaveMessage(int pieceIndex){
		return null;
	}
	
	//
	
	public HandshakeMessage parseHandShakeMessage(byte[] rawData){
		// create instance of HandShakeMessage
		// return it
		return null;
	}
	
	public Peer2PeerMessage parsePeer2PeerMessage(byte[] rawData){
		// create instance of Peer2PeerMessage
		// return it
		return null;
	}
}
