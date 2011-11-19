package edu.ufl.cise.cn.peer2peer.peerhandler;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.ufl.cise.cn.peer2peer.Controller;
import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;
import edu.ufl.cise.cn.peer2peer.entities.Piece;
import edu.ufl.cise.cn.peer2peer.utility.Constants;

public class ChunkRequester implements Runnable {
	
	private static final String LOGGER_PREFIX = ChunkRequester.class.getCanonicalName();
	
	private BlockingQueue<Peer2PeerMessage> messageQueue;
	
	private boolean isShutDown = false;
	
	private Controller controller;
	private PeerHandler peerHandler;
	
	private ChunkRequester(){
		
	}
	
	public static ChunkRequester getInstance(Controller controller, PeerHandler peerHandler){
		
		System.out.println(LOGGER_PREFIX+" Initializing PeerMessageSender");
		
		if(controller == null || peerHandler == null){
			return null;
		}
		
		ChunkRequester requestSender = new ChunkRequester();
		boolean isInitialized = requestSender.init();		
		if(isInitialized == false){
			requestSender.deinit();
			requestSender = null;
			return null;
		}
		
		requestSender.controller = controller;
		requestSender.peerHandler = peerHandler;
		
		System.out.println(LOGGER_PREFIX+" Initialized PeerMessageSender successfully");
		
		return requestSender;
	}
	
	public void deinit(){
		if(messageQueue !=null && messageQueue.size()!=0){
			messageQueue.clear();
		}
		messageQueue = null;
	}
	
	private boolean init(){
		messageQueue = new ArrayBlockingQueue<Peer2PeerMessage>(Constants.SENDER_QUEUE_SIZE);
		return true;
	}
	
	public int getPieceNumberToBeRequested(Peer2PeerMessage bitFieldMessage ){
		int[] thisPeerMissingPieceArray = controller.getMissingPieceIndexArray(); 			

		ByteBuffer byteBuffer = ByteBuffer.wrap(bitFieldMessage.getData().getData());
		
		int recievedBitFieldSize = bitFieldMessage.getData().getSize()/4;
		
		int[] receivedBitFieldData = new int[recievedBitFieldSize]; 
		
		for(int i=0 ; i<recievedBitFieldSize ; i++){
			receivedBitFieldData[i] = byteBuffer.getInt();
		}
		
		for(int i=0 ; i<thisPeerMissingPieceArray.length ; i++){
			for(int j=0 ; j<recievedBitFieldSize ; j++){
				if(thisPeerMissingPieceArray[i] == receivedBitFieldData[j]){
					return thisPeerMissingPieceArray[i];
				}
			}
		}
		
		return -1;
	} 
	
	public void run() {
		
		if(messageQueue == null){
			throw new IllegalStateException(LOGGER_PREFIX+": This object is not initialized properly. This might be result of calling deinit() method");
		}
		
		while(isShutDown == false){
			try {				
				Peer2PeerMessage message = messageQueue.take();
				System.out.println(LOGGER_PREFIX+": Received Message: "+message.getType());
				
				Peer2PeerMessage requestMessage = Peer2PeerMessage.getInstance();
				
				if(message.getType() == Constants.BITFIELD_MESSAGE){
//					int missingPieceIndex = 
//					System.out.println(LOGGER_PREFIX+"bitfield message");
				}
				
				if(message.getType() == Constants.HAVE_MESSAGE){
					System.out.println(LOGGER_PREFIX+"have message");
				}							
				// compare bit field message
				
				// send interested and request message to peers
				
				peerHandler.sendInterestedMessage(requestMessage);
				peerHandler.sendRequestMessage(requestMessage);
				
			} catch (Exception e) {				
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void setBitField(Peer2PeerMessage message) throws InterruptedException{
		if(messageQueue == null){
			throw new IllegalStateException("");
		}else{
			messageQueue.put(message);
		}
	}
	
	public void shutdown(){
		isShutDown = true;
	}
	
}
