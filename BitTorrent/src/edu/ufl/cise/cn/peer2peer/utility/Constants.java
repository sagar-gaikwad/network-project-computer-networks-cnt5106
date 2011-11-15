package edu.ufl.cise.cn.peer2peer.utility;

/**
 * The Class Constants.
 *
 * @author sagarg
 */
public class Constants {
	
	/** The Constant REQUEST_MESSAGE_TYPE. */
	public static final int REQUEST_MESSAGE_TYPE = 1;
	
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
	public static final String PEER_INFO_FILE = "peerInfo.cfg";
	
	public static final int SENDER_QUEUE_SIZE = 100;
}
