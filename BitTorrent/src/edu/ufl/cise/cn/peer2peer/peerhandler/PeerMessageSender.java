package edu.ufl.cise.cn.peer2peer.peerhandler;

import java.io.ObjectOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.ufl.cise.cn.peer2peer.entities.HandshakeMessage;
import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;
import edu.ufl.cise.cn.peer2peer.entities.PeerMessage;
import edu.ufl.cise.cn.peer2peer.utility.Constants;

public class PeerMessageSender implements Runnable {
	
	private static final String LOGGER_PREFIX = PeerMessageSender.class.getCanonicalName();
	
	private BlockingQueue<PeerMessage> messageQueue;
	
	private ObjectOutputStream outputStream = null;
	
	private boolean isShutDown = false;
	
	private PeerMessageSender(){
		
	}
	
	public static PeerMessageSender getInstance(ObjectOutputStream outputStream){
		
		PeerMessageSender messageSender = new PeerMessageSender();
		boolean isInitialized = messageSender.init();		
		if(isInitialized == false){
			messageSender.deinit();
			messageSender = null;
			return null;
		}
		
		messageSender.outputStream = outputStream;
		
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
				
				
				/*PeerMessage newMessage = null;
				
				if(message instanceof HandshakeMessage){
					System.out.println(LOGGER_PREFIX+": Creating new handshake message");
					HandshakeMessage newHandShakeMessage = HandshakeMessage.getInstance();
					newMessage = newHandShakeMessage;
				}
				
				if(message instanceof Peer2PeerMessage){
					System.out.println(LOGGER_PREFIX+": Creating new peer2peer message");
					Peer2PeerMessage newPeer2PeerMessage = Peer2PeerMessage.getInstance();
					
					Peer2PeerMessage peerMessage = (Peer2PeerMessage) message;
					
					newPeer2PeerMessage.setData(peerMessage.getData());
					newPeer2PeerMessage.setMessageLength(peerMessage.getMessageLength());
					newPeer2PeerMessage.setMessgageType(peerMessage.getMessgageType());
					newMessage = newPeer2PeerMessage;
				}*/
											
				
				System.out.println(LOGGER_PREFIX+": Sending message: "+message.getType());
				outputStream.writeUnshared(message);
				outputStream.flush();
				System.out.println(LOGGER_PREFIX+": Sent message: "+message.getType());
				
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
		isShutDown = true;
	}
}
