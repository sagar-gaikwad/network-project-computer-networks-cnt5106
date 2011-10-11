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
	
	public byte[] geHandshakeMessage(byte[] rawData){
		String headerString = Constants.HANDSHAKE_HEADER_STRING;
		char array[] = headerString.toCharArray();
		byte[] rawHeaderMessage = new byte[32];
		for(int i = 0; i< 18; i++){
			rawHeaderMessage[i] = (byte)array[i];
			
		}
		
		for(int i = 18; i<31;i++){
			rawHeaderMessage[i] = (byte)0;
			
		}
		
		rawHeaderMessage[31] = rawData[3];
		
		return rawHeaderMessage;
	}
	
	public byte[] getRequestMessage(int pieceIndex){
		return null;
	}
	
	
	
	
	
		
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
	
	// getInterstedMessage
	public byte[] getInterestedMessage(){
		ByteBuffer byteBuffer = ByteBuffer.allocate(5);
		byteBuffer.putInt(Constants.SIZE_OF_EMPTY_MESSAGE);
		byteBuffer.put(Constants.INTERESTED_MESSAGE_CON);
		byte[] rawInterestedMessage = byteBuffer.array();
		return rawInterestedMessage;
	}
	
	// getNotInterstedMessage
	public byte[] getNotInterestedMessage(){
		ByteBuffer byteBuffer = ByteBuffer.allocate(5);
		byteBuffer.putInt(Constants.SIZE_OF_EMPTY_MESSAGE);
		byteBuffer.put(Constants.NOT_INTERESTED_MESSAGE_CON);
		byte[] rawNotInterestedMessage = byteBuffer.array();
		return rawNotInterestedMessage;

	}
	
	//getHaveMessage
	
	public byte[] getHaveMessage(byte[] payLoad){
		ByteBuffer byteBuffer = ByteBuffer.allocate(9);
		byteBuffer.putInt(5);
		byteBuffer.put(Constants.HAVE_MESSAGE_CON);
		byteBuffer.put(payLoad);
		byte[] rawHaveMessage = byteBuffer.array();
		return rawHaveMessage;
		
	}
	
	//getbitFieldMessage
	
	public byte[] getBitFieldMessage(byte[] payload){
		int payloadSize = payload.length;
		ByteBuffer byteBuffer = ByteBuffer.allocate(payloadSize+5);
		byteBuffer.putInt(payloadSize+1);
		byteBuffer.put(Constants.BITFIELD_MESSAGE_CON);
		byteBuffer.put(payload);
		byte[] rawBitFieldMessage = byteBuffer.array();
		return rawBitFieldMessage;
	}

	//getRequestMessage
	
	public byte[] getRequestMessage(byte[] payLoad){
		ByteBuffer byteBuffer = ByteBuffer.allocate(9);
		byteBuffer.putInt(5);
		byteBuffer.put(Constants.REQUEST_MESSAGE_CON);
		byteBuffer.put(payLoad);
		byte[] rawRequestMessage = byteBuffer.array();
		return rawRequestMessage;
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
			message.setMessageLength(1);
			message.setData(null);
			return message;			
		}
		
		if(messageType == Constants.UNCHOKE_MESSAGE_CON){
			System.out.println("This is unchoke message");
			Peer2PeerMessage message = Peer2PeerMessage.getInstance();
			message.setMessgageType(Constants.UNCHOKE_MESSAGE_CON);
			message.setMessageLength(1);
			message.setData(null);
			return message;			
		}
		
		if(messageType == Constants.INTERESTED_MESSAGE_CON){
			System.out.println("This is interested message");
			Peer2PeerMessage message = Peer2PeerMessage.getInstance();
			message.setMessgageType(Constants.INTERESTED_MESSAGE_CON);
			message.setMessageLength(1);
			message.setData(null);
			return message;			
		}
		
		if(messageType == Constants.NOT_INTERESTED_MESSAGE_CON){
			System.out.println("This is not interested message");
			Peer2PeerMessage message = Peer2PeerMessage.getInstance();
			message.setMessgageType(Constants.NOT_INTERESTED_MESSAGE_CON);
			message.setMessageLength(1);
			message.setData(null);
			return message;			
		}
		
		if(messageType == Constants.HAVE_MESSAGE_CON){
			System.out.println("This is Have message");
			Peer2PeerMessage message = Peer2PeerMessage.getInstance();
			message.setMessageLength(5);
			message.setMessageLength(Constants.HAVE_MESSAGE_CON);
			message.setPieceIndex((int)rawData[8]);
		}
		
		if(messageType == Constants.REQUEST_MESSAGE_CON){
			System.out.println("This is Request message");
			Peer2PeerMessage message = Peer2PeerMessage.getInstance();
			message.setMessageLength(5);
			message.setMessageLength(Constants.REQUEST_MESSAGE_CON);
			message.setPieceIndex((int)rawData[8]);
		}
		return null;
	}
}
