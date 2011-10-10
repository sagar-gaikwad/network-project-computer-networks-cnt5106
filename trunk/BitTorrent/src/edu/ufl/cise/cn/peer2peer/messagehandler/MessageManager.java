package edu.ufl.cise.cn.peer2peer.messagehandler;

import java.nio.ByteBuffer;

import edu.ufl.cise.cn.peer2peer.entities.HandshakeMessage;
import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;
import edu.ufl.cise.cn.peer2peer.entities.PeerMessage;
import edu.ufl.cise.cn.peer2peer.utility.Constants;

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
	
	public byte[] getChokeMessage(){
		int temp = 0;
		int size = Constants.SIZE_OF_MESSAGE;
		temp = Constants.CHOKE_MESSAGE_CON;
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(5);
		byteBuffer.putInt(size);
		byte[] rawChokeMessage = byteBuffer.array();
		rawChokeMessage[4] = (byte)temp;
		return rawChokeMessage;
	}
	
	public byte[] getHaveMessage(byte[] payLoad){
		return null;
	}
	
	public byte[] getRequestMessage(byte[] payLoad){
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
	
	public PeerMessage parseMessage(byte[] rawData){
		return null;
	}
}
