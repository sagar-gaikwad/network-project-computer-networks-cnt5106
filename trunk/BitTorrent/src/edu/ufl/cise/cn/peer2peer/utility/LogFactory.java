package edu.ufl.cise.cn.peer2peer.utility;

import java.io.File;


/**
 * A factory for creating Log objects.
 * @author sagarg
 *
 */
public class LogFactory {
	
	/** The logger. */
	private static MessageLogger logger;
	
	/**
	 * Gets the log.
	 *
	 * @param peerID the peer id
	 * @return the log
	 */
	public static MessageLogger getLogger(String peerID){
		if(logger == null){
			
			String directory = ""+Constants.LOG_FILE_DIRECTORY_NAME;
			File file = new File(directory);
			file.mkdir();
			
			
			logger = new MessageLogger(peerID,directory+"/"+Constants.LOG_FILE_NAME_PREFIX+peerID+".log", Constants.LOGGER_NAME);
			try {
				logger.initialize();
			} catch (Exception e) {
				logger.close();
				logger = null;
				System.out.println("Unable to create or initialize logger");
				e.printStackTrace();
			}
		}
		return logger;
	}
}
