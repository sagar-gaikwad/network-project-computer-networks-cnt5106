package edu.ufl.cise.cn.peer2peer.utility;

public class Constants {
	public static final int REQUEST_MESSAGE_TYPE = 1;
	public static final int MAX_MESSAGE_SIZE = 40000;
	public static final int RAW_DATA_SIZE = 1000;
	public static final String XYZ = "xyz";
	
	public static final int HANDSHAKE_MESSAGE = 1;
	public static final int BITFIEELD_MESSAGE = 2;
	public static final int REQUEST_MESSAGE = 3;
	public static final int PIECE_MESSAGE = 4;
	public static final int INTERESTED_MESSAGE = 5;
	public static final int NOT_INTERESTED_MESSAGE = 6;
	public static final int HAVE_MESSAGE = 7;

	//changes by rhishikesh 
	public static final int CHOKE_MESSAGE_CON = 0;
	public static final int SIZE_OF_MESSAGE = 1;
	
	/** The Constant LOG_FILE_NAME. */
	public static final String LOG_FILE_NAME = "logger.filename";
	
	/** The Constant LOGGER_NAME. */
	public static final String LOGGER_NAME = "logger.name";
	
	public static final String CONFIGURATION_FILE = "common.cfg";
}
