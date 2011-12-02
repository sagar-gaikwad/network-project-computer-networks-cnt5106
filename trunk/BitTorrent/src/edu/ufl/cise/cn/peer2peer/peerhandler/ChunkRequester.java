package edu.ufl.cise.cn.peer2peer.peerhandler;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import edu.ufl.cise.cn.peer2peer.Controller;
import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;
import edu.ufl.cise.cn.peer2peer.filehandler.BitFieldHandler;
import edu.ufl.cise.cn.peer2peer.utility.Constants;
import edu.ufl.cise.cn.peer2peer.utility.PropsReader;

public class ChunkRequester implements Runnable {
	
	private static String LOGGER_PREFIX = ChunkRequester.class.getSimpleName();
	
	private BlockingQueue<Peer2PeerMessage> messageQueue;
	
	private boolean isShutDown = false;
	
	private Controller controller;
	private PeerHandler peerHandler;
	
	private BitFieldHandler neighborPeerBitFieldhandler = null;
	
	private ChunkRequester(){
		
	}
	
	public static ChunkRequester getInstance(Controller controller, PeerHandler peerHandler){
//		System.out.println(LOGGER_PREFIX+" Initializing ChunkRequester");
		
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
		
//		System.out.println(LOGGER_PREFIX+" Initialized ChunkRequester successfully");
		
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

		int pieceSize = Integer.parseInt(PropsReader.getPropertyValue("PieceSize"));
		int numOfPieces = (int) Math.ceil(Integer.parseInt(PropsReader.getPropertyValue("FileSize")) / (pieceSize*1.0)) ;
	
		neighborPeerBitFieldhandler = new BitFieldHandler(numOfPieces);
		
		
		return true;
	}
	
	int [] pieceIndexArray = new int[1000];	
	
	public int getPieceNumberToBeRequested(){
		BitFieldHandler thisPeerBitFieldhandler = controller.getBitFieldMessage().getHandler(); 			
		int count = 0;
		for(int i=0 ; i<neighborPeerBitFieldhandler.getLength() && count<pieceIndexArray.length ; i++){
			if(thisPeerBitFieldhandler.getBitFieldOn(i) == false && neighborPeerBitFieldhandler.getBitFieldOn(i) == true){
				pieceIndexArray[count] = i;
				count++;
			}
		}
		
		if(count != 0){
			Random random = new Random();
			int index = random.nextInt(count);
			return pieceIndexArray[index];
		}else{
			return -1;
		}	
	} 
	
	public void run() {
		
		if(messageQueue == null){
			throw new IllegalStateException(LOGGER_PREFIX+": This object is not initialized properly. This might be result of calling deinit() method");
		}
		
		while(isShutDown == false){
			try {				
				Peer2PeerMessage message = messageQueue.take();
//				System.out.println(LOGGER_PREFIX+": Received Message: "+Constants.getMessageName(message.getType()));
				
				Peer2PeerMessage requestMessage = Peer2PeerMessage.getInstance();
				requestMessage.setMessgageType(Constants.REQUEST_MESSAGE);
				
				Peer2PeerMessage interestedMessage = Peer2PeerMessage.getInstance();
				interestedMessage.setMessgageType(Constants.INTERESTED_MESSAGE);
				
				if(message.getType() == Constants.BITFIELD_MESSAGE){
					
					neighborPeerBitFieldhandler = message.getHandler();
					
					/*BitFieldHandler receivedBitFieldhandler = message.getHandler();
					
					for(int i=0 ; i<receivedBitFieldhandler.getLength() ; i++){
						if(receivedBitFieldhandler.getBitFieldOn(i) == true){
							neighborPeerBitFieldhandler.setBitFieldOn(i, true);
						}
					}*/
					
					int missingPieceIndex = getPieceNumberToBeRequested();
					
					if(missingPieceIndex == -1){
						Peer2PeerMessage notInterestedMessage = Peer2PeerMessage.getInstance();
						notInterestedMessage.setMessgageType(Constants.NOT_INTERESTED_MESSAGE);
						peerHandler.sendNotInterestedMessage(notInterestedMessage);
					}else{
						interestedMessage.setPieceIndex(missingPieceIndex);
						peerHandler.sendInterestedMessage(interestedMessage);
						
						requestMessage.setPieceIndex(missingPieceIndex);
						peerHandler.sendRequestMessage(requestMessage);
					}									
				}
				
				if(message.getType() == Constants.HAVE_MESSAGE){
					int pieceIndex = message.getPieceIndex();
					
					try {
						neighborPeerBitFieldhandler.setBitFieldOn(pieceIndex, true);
					} catch (Exception e) {
						System.out.println(LOGGER_PREFIX+"["+peerHandler.getPeerId()+"]: NULL POINTER EXCEPTION for piece Index"+pieceIndex +" ... "+neighborPeerBitFieldhandler);
						e.printStackTrace();
					}
					
					int missingPieceIndex = getPieceNumberToBeRequested();

					if(missingPieceIndex == -1){
						Peer2PeerMessage notInterestedMessage = Peer2PeerMessage.getInstance();
						notInterestedMessage.setMessgageType(Constants.NOT_INTERESTED_MESSAGE);
						peerHandler.sendNotInterestedMessage(notInterestedMessage);
					}else{
						if(peerHandler.isPieceMessageForPreviousMessageReceived() == true){
							peerHandler.setPieceMessageForPreviousMessageReceived(false);
							interestedMessage.setPieceIndex(missingPieceIndex);
							peerHandler.sendInterestedMessage(interestedMessage);
							
							requestMessage.setPieceIndex(missingPieceIndex);
							peerHandler.sendRequestMessage(requestMessage);
						}	
					}									
				}

				/*
				 * We are supposed to send request message only after piece for previous request message.
				 * */
				if(message.getType() == Constants.PIECE_MESSAGE){
					
					int missingPieceIndex = getPieceNumberToBeRequested();

					if(missingPieceIndex == -1){
						// do nothing 
					}else{
						if(peerHandler.isPieceMessageForPreviousMessageReceived() == true){
							peerHandler.setPieceMessageForPreviousMessageReceived(false);
							interestedMessage.setPieceIndex(missingPieceIndex);
							peerHandler.sendInterestedMessage(interestedMessage);
							
							requestMessage.setPieceIndex(missingPieceIndex);
							peerHandler.sendRequestMessage(requestMessage);
						}						
					}									
				}
				
				/*
				 * We are supposed to send request message after receiving unchoke message
				 * */
				if(message.getType() == Constants.UNCHOKE_MESSAGE){
					
					int missingPieceIndex = getPieceNumberToBeRequested();

					peerHandler.setPieceMessageForPreviousMessageReceived(false);
					
					if(missingPieceIndex == -1){
						// do nothing 
					}else{
						interestedMessage.setPieceIndex(missingPieceIndex);
						peerHandler.sendInterestedMessage(interestedMessage);
						
						requestMessage.setPieceIndex(missingPieceIndex);
						peerHandler.sendRequestMessage(requestMessage);
					}									
				}
				
				// compare bit field message
				
				// send interested and request message to peers
				
				
			} catch (Exception e) {				
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void addMessage(Peer2PeerMessage message) throws InterruptedException{
		if(messageQueue == null){
			throw new IllegalStateException("");
		}else{
			messageQueue.put(message);
		}
	}
	
	public void shutdown(){
		//System.out.println(LOGGER_PREFIX+" Shutting down ChunkRequester......");
		isShutDown = true;
	}
	
	
	public boolean isNeighborPeerDownloadedFile(){
		if(neighborPeerBitFieldhandler != null && neighborPeerBitFieldhandler.isFileDownloadComplete() == true){
			return true;
		}else{
			return false;
		}
	}
	
}
