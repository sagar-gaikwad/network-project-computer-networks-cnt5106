package com.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.ufl.cise.cn.peer2peer.entities.HandshakeMessage;
import edu.ufl.cise.cn.peer2peer.entities.PeerMessage;
import edu.ufl.cise.cn.peer2peer.utility.Constants;

public class Tester implements Runnable {
	
	private static final String LOGGER_PREFIX = Tester.class.getCanonicalName();
	
	private BlockingQueue<PeerMessage> messageQueue;
	
	private boolean isShutDown = false;
	
	private Tester(){
		
	}
	
	public static Tester getInstance(){
		
		Tester messageSender = new Tester();
		boolean isInitialized = messageSender.init();		
		if(isInitialized == false){
			messageSender.deinit();
			messageSender = null;
			return null;
		}
		
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
				System.out.println(LOGGER_PREFIX+": Waiting for queue to get some message: ");
				PeerMessage message = messageQueue.take();
				System.out.println(LOGGER_PREFIX+": Sending message: "+message.getType());
				Thread.sleep(4000);
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
	
	public static void main(String args[]) throws Exception{
		PeerMessage peerMessage = HandshakeMessage.getInstance();
		Tester tester = Tester.getInstance();
		System.out.println("Starting Tester thread");
		new Thread(tester).start();
		Thread.sleep(1000);
		tester.sendMessage(peerMessage);
		tester.sendMessage(peerMessage);
		tester.sendMessage(peerMessage);
		tester.sendMessage(peerMessage);
		tester.sendMessage(peerMessage);
		tester.sendMessage(peerMessage);
		tester.sendMessage(peerMessage);
		tester.sendMessage(peerMessage);
		tester.sendMessage(peerMessage);
		tester.sendMessage(peerMessage);
	}
	
}
