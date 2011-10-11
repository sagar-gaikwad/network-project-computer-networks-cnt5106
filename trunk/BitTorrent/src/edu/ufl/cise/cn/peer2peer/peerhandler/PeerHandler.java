package edu.ufl.cise.cn.peer2peer.peerhandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import edu.ufl.cise.cn.peer2peer.Controller;
import edu.ufl.cise.cn.peer2peer.entities.HandshakeMessage;
import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;
import edu.ufl.cise.cn.peer2peer.entities.PeerMessage;
import edu.ufl.cise.cn.peer2peer.messagehandler.MessageManager;
import edu.ufl.cise.cn.peer2peer.utility.Constants;

public class PeerHandler implements Runnable{
	
	private String peerID;
	private Socket neighbourPeerSocket;
	private InputStream neighbourPeerInputStream;
	private OutputStream neighbourPeerOutputStream;
	private MessageManager messageManager;
	private Controller controller;
	
	private PeerHandler(){
		
	}
	
	synchronized public static PeerHandler getInstance(Socket socket, Controller controller){
		PeerHandler peerHandler = new PeerHandler();
		
		peerHandler.neighbourPeerSocket = socket;
		peerHandler.controller = controller;
		
		boolean isInitialized = peerHandler.init();
		
		if(isInitialized == false){
			peerHandler.close();
			peerHandler = null;
		}
		return peerHandler;
	}
	
	synchronized private boolean init(){
		if(neighbourPeerSocket == null){
			return false;
		}
		
		try {
			neighbourPeerOutputStream = neighbourPeerSocket.getOutputStream();
			neighbourPeerInputStream = neighbourPeerSocket.getInputStream();			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		messageManager = MessageManager.getInstance();
		
		if(messageManager == null){
			close();
			return false;
		}
		
		if(controller == null){
			close();
			return false;
		}
		
		return true;
	}
	
	synchronized public void close(){
		try {
			if(neighbourPeerInputStream != null){
				neighbourPeerInputStream.close();
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			if(neighbourPeerOutputStream != null){
				neighbourPeerOutputStream.close();
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void run(){
		byte[] rawData = new byte[Constants.RAW_DATA_SIZE];
		ByteBuffer buffer = ByteBuffer.allocate(Constants.MAX_MESSAGE_SIZE);
		try {
			
			while(controller.isOperationCompelete() == false){
				int numbeOfBytesRead = -1;
				System.out.println("Reading data from peer");
				numbeOfBytesRead = neighbourPeerInputStream.read(rawData,0,Constants.RAW_DATA_SIZE);
//				neighbourPeerInputStream.reset();
				while(numbeOfBytesRead > 0){
					System.out.println("rawData: "+new String(rawData,0,numbeOfBytesRead));
					buffer.put(rawData);
					numbeOfBytesRead = neighbourPeerInputStream.read(rawData,0,Constants.RAW_DATA_SIZE);
				}
				
				System.out.println("Data received: "+buffer.toString());
				
				PeerMessage message = messageManager.parseMessage(buffer.array());
				
				if(message.getType() == Constants.HANDSHAKE_MESSAGE){					
					if(message instanceof Peer2PeerMessage){
						System.out.println("Received handshake message");
						HandshakeMessage handshakeMessage = (HandshakeMessage)message;
						handleHandshakeMessage(handshakeMessage);
					}else{
						// send some invalid data
					}
				}else if(message.getType() == Constants.REQUEST_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message; 
					handleRequestMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.BITFIELD_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handleBitFieldMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.CHOKE_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handleChokeMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.HAVE_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handleHaveMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.INTERESTED_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handleInterestedMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.NOT_INTERESTED_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handleNotInterestedMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.PIECE_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handlePieceMessage(peer2PeerMessage);
				}else if(message.getType() == Constants.UNCHOKE_MESSAGE){
					Peer2PeerMessage peer2PeerMessage = (Peer2PeerMessage)message;
					handleUnchockMessage(peer2PeerMessage);
				}
				break;
			}
			close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void handleUnchockMessage(Peer2PeerMessage peer2PeerMessage) {
		// TODO Auto-generated method stub
		
	}

	private void handlePieceMessage(Peer2PeerMessage peer2PeerMessage) {
		// TODO Auto-generated method stub
		
	}

	private void handleChokeMessage(Peer2PeerMessage peer2PeerMessage) {
		// TODO Auto-generated method stub
		
	}

	private void handleBitFieldMessage(Peer2PeerMessage peer2PeerMessage) {
		// TODO Auto-generated method stub
		
	}

	synchronized private void handleHandshakeMessage(HandshakeMessage handshakeMessage){
		
	}
	
	synchronized private void handleRequestMessage(Peer2PeerMessage message){
		
	}
	
	synchronized private void handleHaveMessage(Peer2PeerMessage message){
		
	}
	
	synchronized private void handleInterestedMessage(Peer2PeerMessage message){
		
	}
	
	synchronized private void handleNotInterestedMessage(Peer2PeerMessage message){
		
	}
	
	public synchronized boolean sendHandshakeMessage(String peerID){
		try {
			
			byte[] data = controller.getHandshakeMessage(peerID);
			
			if(data != null){
				
				System.out.println("Sending handshake message to "+peerID);
				
				neighbourPeerOutputStream.write(data,0,data.length);
				neighbourPeerOutputStream.flush();
				
				neighbourPeerOutputStream.write(data,0,data.length);
				neighbourPeerOutputStream.flush();
				neighbourPeerOutputStream.close();
				System.out.println("sent data to peer: "+peerID);
			}
			
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
