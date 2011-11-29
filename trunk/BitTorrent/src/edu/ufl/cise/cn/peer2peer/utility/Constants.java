package edu.ufl.cise.cn.peer2peer.utility;

/**
 * The Class Constants.
 *
 * @author sagarg
 */
public class Constants {
	
	/** The Constant MAX_MESSAGE_SIZE. */
	public static final int MAX_MESSAGE_SIZE = 40000;
	
	/** The Constant RAW_DATA_SIZE. */
	public static final int RAW_DATA_SIZE = 1000;
	
	/** The Constant XYZ. */
	public static final String XYZ = "xyz";
	
	/** The Constant HANDSHAKE_MESSAGE. */
	public static final byte HANDSHAKE_MESSAGE = 9;
	
	/** The Constant BITFIELD_MESSAGE. */
	public static final byte BITFIELD_MESSAGE = 5;
	
	/** The Constant REQUEST_MESSAGE. */
	public static final byte REQUEST_MESSAGE = 6;
	
	/** The Constant PIECE_MESSAGE. */
	public static final byte PIECE_MESSAGE = 7;
	
	/** The Constant INTERESTED_MESSAGE. */
	public static final byte INTERESTED_MESSAGE = 2;
	
	/** The Constant NOT_INTERESTED_MESSAGE. */
	public static final byte NOT_INTERESTED_MESSAGE = 3;
	
	/** The Constant HAVE_MESSAGE. */
	public static final byte HAVE_MESSAGE = 4;
	
	/** The Constant CHOKE_MESSAGE. */
	public static final byte CHOKE_MESSAGE = 0;
	
	public static final byte SHUTDOWN_MESSAGE = 100;
	
	public static final byte SHUTDOWN_DONE_MESSAGE = 101;
	
	/** The Constant UNCHOKE_MESSAGE. */
	public static final byte UNCHOKE_MESSAGE = 1;

	/** The Constant LOG_FILE_DIRECTORY. */
	public static final String LOG_FILE_DIRECTORY = ".";
	
	//changes by rhishikesh 
	/** The Constant HANDSHAKE_HEADER_STRING. */
	public static final String HANDSHAKE_HEADER_STRING = "CEN5501C2008SPRING";
	
	/** The Constant SIZE_OF_EMPTY_MESSAGE. */
	public static final int SIZE_OF_EMPTY_MESSAGE = 1;
	
	/** The Constant LOG_FILE_NAME. */
	public static final String LOG_FILE_NAME = "logger.filename";
	
	/** The Constant LOGGER_NAME. */
	public static final String LOGGER_NAME = "logger.name";
	
	/** The Constant CONFIGURATION_FILE. */
	public static final String CONFIGURATION_FILE = "common.cfg";
	

	/** The Constant PEER_INFO_FILE. */
	public static final String PEER_INFO_FILE = "PeerInfo.cfg";
	
	public static final int SENDER_QUEUE_SIZE = 100;
	
	public static final String CHOKE_UNCHOKE_INTERVAL = "UnchokingInterval";
	
	public static final String OPTIMISTIC_UNCHOKE_INTERVAL = "OptimisticUnchokingInterval";
	
	public static final String FILE_SIZE = "FileSize";
	
	public static String getMessageName(int i){
		if(i == Constants.BITFIELD_MESSAGE){
			return "BITFIELD_MESSAGE";
		}
		
		if(i == Constants.REQUEST_MESSAGE){
			return "REQUEST_MESSAGE";
		}
		
		if(i == Constants.HANDSHAKE_MESSAGE){
			return "HANDSHAKE_MESSAGE";
		}
		
		if(i == Constants.CHOKE_MESSAGE){
			return "CHOKE_MESSAGE";
		}
		
		if(i == Constants.UNCHOKE_MESSAGE){
			return "UNCHOKE_MESSAGE";
		}
		
		if(i == Constants.HAVE_MESSAGE){
			return "HAVE_MESSAGE";
		}
		
		if(i == Constants.INTERESTED_MESSAGE){
			return "INTERESTED_MESSAGE";
		}
		
		if(i == Constants.NOT_INTERESTED_MESSAGE){
			return "NOT_INTERESTED_MESSAGE";
		}
		
		if(i == Constants.PIECE_MESSAGE){
			return "PIECE_MESSAGE";
		}
		
		
		return null;
	}
}
