package edu.ufl.cise.cn.peer2peer.peerhandler;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.ufl.cise.cn.peer2peer.entities.PeerMessage;
import edu.ufl.cise.cn.peer2peer.utility.Constants;

public class PeerMessageSender implements Runnable {
	
	private static final String LOGGER_PREFIX = PeerMessageSender.class.getCanonicalName();
	
	private BlockingQueue<PeerMessage> messageQueue;
	
	private static PeerMessageSender messageSender;
	
	private Socket neighborPeerSocket = null;
	
	private ObjectOutputStream outputStream = null;
	
	private boolean isShutDown = false;
	
	private PeerMessageSender(){
		
	}
	
	public static PeerMessageSender getInstance(ObjectOutputStream outputStream){
		
		System.out.println(LOGGER_PREFIX+" Initializing PeerMessageSender");
				
		messageSender = new PeerMessageSender();
		boolean isInitialized = messageSender.init();		
		if(isInitialized == false){
			messageSender.deinit();
			messageSender = null;
			return null;
		}
		
		messageSender.outputStream = outputStream;
		
		System.out.println(LOGGER_PREFIX+" Initialized PeerMessageSender successfully");
		
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
	
	public void run() {
		
		if(messageQueue == null){
			throw new IllegalStateException(LOGGER_PREFIX+": This object is not initialized properly. This might be result of calling deinit() method");
		}
		
		while(isShutDown == false){
			try {				
				PeerMessage message = messageQueue.take();
				System.out.println(LOGGER_PREFIX+": Sending message: "+message.getType());
				outputStream.writeObject(message);
				outputStream.flush();
				System.out.println(LOGGER_PREFIX+": Message sent");
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
		isShutDown = true;
	}
}
