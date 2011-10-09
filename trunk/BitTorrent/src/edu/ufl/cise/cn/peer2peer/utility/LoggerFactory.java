package edu.ufl.cise.cn.peer2peer.utility;

/**
 * A factory for creating Logger objects.
 */
public class LoggerFactory {
	
	/** The logger. */
	private static MessageLogger logger;
	
	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	public synchronized static MessageLogger getLogger(){
		if(logger == null){
			logger = new MessageLogger(PropsReader.getPropertyValue(Constants.LOG_FILE_NAME), Constants.LOGGER_NAME);
		}
		return logger;
	}
}
