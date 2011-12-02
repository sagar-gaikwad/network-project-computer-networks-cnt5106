package edu.ufl.cise.cn.peer2peer.peerhandler;

import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.ufl.cise.cn.peer2peer.entities.HandshakeMessage;
import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;
import edu.ufl.cise.cn.peer2peer.entities.PeerMessage;
import edu.ufl.cise.cn.peer2peer.utility.Constants;

public class PeerMessageSender implements Runnable {
	
	private static final String LOGGER_PREFIX = PeerMessageSender.class.getSimpleName();
	
	private BlockingQueue<PeerMessage> messageQueue;
	
	private ObjectOutputStream outputStream = null;
	
	private boolean isShutDown = false;
	
	private PeerHandler handler;
	
	private PeerMessageSender(){
		
	}
	
	public static PeerMessageSender getInstance(ObjectOutputStream outputStream, PeerHandler handler){
		
		PeerMessageSender messageSender = new PeerMessageSender();
		boolean isInitialized = messageSender.init();		
		if(isInitialized == false){
			messageSender.deinit();
			messageSender = null;
			return null;
		}
		
		messageSender.outputStream = outputStream;
		messageSender.handler = handler;
		
		return messageSender;
	}
	
	public void deinit(){
		if(messageQueue !=null && messageQueue.size()!=0){
			messageQueue.clear();
		}
		messageQueue = null;
	}
	
	private boolean init(){
		messageQueue = new ArrayBlockingQueue<PeerMessage>(Constants.SENDER_QUEUE_SIZE);
		return true;
	}
	
	public void printMessageDetails(PeerMessage message){
		if(message.getType() != Constants.HAVE_MESSAGE && message.getType() != Constants.NOT_INTERESTED_MESSAGE && message.getType() != Constants.INTERESTED_MESSAGE){
			if(message.getType() == Constants.PIECE_MESSAGE || message.getType() == Constants.REQUEST_MESSAGE){
//				System.out.println(LOGGER_PREFIX+": Sent message:["+message.getMessageNumber()+"]:"+Constants.getMessageName(message.getType()) +" Piece Number : "+((Peer2PeerMessage)message).getPieceIndex());
//				System.out.println(LOGGER_PREFIX+":["+handler.getPeerId()+"]"+": Sent message:["+message.getMessageNumber()+"]:"+Constants.getMessageName(message.getType()) +" Piece Number : "+((Peer2PeerMessage)message).getPieceIndex());
			}else{
//				System.out.println(LOGGER_PREFIX+": Sent message:["+message.getMessageNumber()+"]:"+Constants.getMessageName(message.getType()) );
//				System.out.println(LOGGER_PREFIX+":["+handler.getPeerId()+"]"+": Sent message:["+message.getMessageNumber()+"]:"+Constants.getMessageName(message.getType()));
			}			
		}		
	}
	
	public void run() {
		
		if(messageQueue == null){
			throw new IllegalStateException(LOGGER_PREFIX+": This object is not initialized properly. This might be result of calling deinit() method");
		}
		
		while(isShutDown == false){
			try {				
				PeerMessage message = messageQueue.take();
				
//				System.out.println(LOGGER_PREFIX+": Sending message: "+Constants.getMessageName(message.getType()));
				outputStream.writeUnshared(message);
				outputStream.flush();
				printMessageDetails(message);				
//				outputStream.reset();				
				message = null;
			} catch (Exception e) {				
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void sendMessage(PeerMessage message) throws InterruptedException{
		if(messageQueue == null){
			throw new IllegalStateException("");
		}else{
			messageQueue.put(message);
		}
	}
	
	public void shutdown(){
		//System.out.println(LOGGER_PREFIX+" Shutting down PeerMessageSender");
		isShutDown = true;
	}
}
