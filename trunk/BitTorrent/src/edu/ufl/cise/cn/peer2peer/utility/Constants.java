package edu.ufl.cise.cn.peer2peer.utility;

public class Constants {
	public static final int REQUEST_MESSAGE_TYPE = 1;
	public static final int MAX_MESSAGE_SIZE = 40000;
	public static final int RAW_DATA_SIZE = 1000;
	public static final String XYZ = "xyz";
	
	public static final byte HANDSHAKE_MESSAGE = 9;
	public static final byte BITFIELD_MESSAGE = 5;
	public static final byte REQUEST_MESSAGE = 6;
	public static final byte PIECE_MESSAGE = 7;
	public static final byte INTERESTED_MESSAGE = 2;
	public static final byte NOT_INTERESTED_MESSAGE = 3;
	public static final byte HAVE_MESSAGE = 4;
	public static final byte CHOKE_MESSAGE = 0;
	public static final byte UNCHOKE_MESSAGE = 1;

	//changes by rhishikesh 
	public static final String HANDSHAKE_HEADER_STRING = "CEN5501C2008SPRING";
	
	public static final int SIZE_OF_EMPTY_MESSAGE = 1;
	
	/** The Constant LOG_FILE_NAME. */
	public static final String LOG_FILE_NAME = "logger.filename";
	
	/** The Constant LOGGER_NAME. */
	public static final String LOGGER_NAME = "logger.name";
	
	public static final String CONFIGURATION_FILE = "common.cfg";
	

	public static final String PEER_INFO_FILE = "peerInfo.cfg";
}
