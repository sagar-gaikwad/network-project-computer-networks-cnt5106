package edu.ufl.cise.cn.peer2peer.utility;


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
			logger = new MessageLogger(peerID,Constants.LOG_FILE_DIRECTORY+""+peerID+".log", Constants.LOGGER_NAME);
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
