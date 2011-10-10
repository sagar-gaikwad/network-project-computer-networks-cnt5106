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
		return true;
	}
	
	public void close(){
		
	}
	
	public byte[] geHandshakeMessage(){
		return null;
	}
	
	public byte[] getRequestMessage(int pieceIndex){
		return null;
	}
	
	
	
	
	// getInterstedMessage
	
	// getNotInterstedMessage
	
	//getChokeMessage
	public byte[] getChokeMessage(){

		
		ByteBuffer byteBuffer = ByteBuffer.allocate(5);
		byteBuffer.putInt(Constants.SIZE_OF_EMPTY_MESSAGE);
		byteBuffer.put(Constants.CHOKE_MESSAGE_CON);
		byte[] rawChokeMessage = byteBuffer.array();
		return rawChokeMessage;
		
	}
	
	// getUnchokeMessage
	public byte[] getUnchokeMessage(){
		ByteBuffer byteBuffer = ByteBuffer.allocate(5);
		byteBuffer.putInt(Constants.SIZE_OF_EMPTY_MESSAGE);
		byteBuffer.put(Constants.UNCHOKE_MESSAGE_CON);
		byte[] rawUnChokeMessage = byteBuffer.array();
		return rawUnChokeMessage;
		
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
		if( rawData== null || rawData.length < 5){
			return null;
		}
		
		byte messageType = rawData[4];
		
		if(messageType == Constants.CHOKE_MESSAGE_CON){
			System.out.println("This is choke message");
			Peer2PeerMessage message = Peer2PeerMessage.getInstance();
			message.setMessgageType(Constants.CHOKE_MESSAGE_CON);
			message.setMessageLength(0);
			message.setData(null);
			return message;			
		}
		return null;
	}
}
