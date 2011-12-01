package edu.ufl.cise.cn.peer2peer.utility;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class MessageLogger.
 *
 * @author sagarg
 */
public class MessageLogger extends Logger{
	
	/** The log file name. */
	private String logFileName;
	
	/** The file handler. */
	private FileHandler fileHandler;
	
	/** The peer id. */
	private String peerID;
	
	/** The formatter. */
	private SimpleDateFormat formatter = null;
		
	/**
	 * Instantiates a new message logger.
	 *
	 * @param peerID the peer id
	 * @param logFileName This parameter contains name of the log file.
	 * @param name This is name of the Logger class.
	 */
	public MessageLogger(String peerID, String logFileName, String name) {
		super(name, null);
		this.logFileName = logFileName;
		this.setLevel(Level.FINEST);
		this.peerID = peerID;
	}

	
	/**
	 * Initialize. Add File Handler to logger class so that all log messages will be written in the file
	 *
	 * @throws SecurityException the security exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void initialize() throws SecurityException, IOException{
		fileHandler = new FileHandler(logFileName);
		fileHandler.setFormatter(new LogMessageFormatter());
		formatter = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss a");
		this.addHandler(fileHandler);
	}

	
	/* Log messages into file. This method is synchronized as multiple threads access this method.
	 * (non-Javadoc)
	 * @see java.util.logging.Logger#log(java.util.logging.Level, java.lang.String)
	 */
	@Override
	public synchronized void log(Level level, String msg) {
		// TODO Auto-generated method stub
		super.log(level, msg);
		super.log(level, "\n");
		System.out.println(msg);
//		System.out.println(msg);
	}
	
	/**
	 * Log.
	 *
	 * @param level the level
	 * @param msg the msg
	 */
	public synchronized void log(Level level, StackTraceElement[] msg) {

	}
	
	
	/**
	 * Close the file in which log messages are written.
	 */
	public void close(){
		try {
			if(fileHandler!=null){
				fileHandler.close();
			}			
		} catch (Exception e) {			
			System.out.println("Unable to close logger.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Error.
	 *
	 * @param prefix the prefix
	 * @param errorMsg the error msg
	 * @param ex the ex
	 */
	public void error(String prefix, String errorMsg, Exception ex){
		this.log(Level.SEVERE, "["+prefix+"]: "+errorMsg);				
		if(ex!=null){
			this.log(Level.FINEST, "["+prefix+"]: "+ex.getMessage());
			StackTraceElement[] stackTrace = ex.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {	
				this.log(Level.FINEST, stackTraceElement.toString());
			}
		}
		
	}
	
	/**
	 * Error.
	 *
	 * @param errorMsg the error msg
	 */
	public void error(String errorMsg){
		Calendar c = Calendar.getInstance();		
		String dateInStringFormat = formatter.format(c.getTime());
		this.log(Level.SEVERE, "["+dateInStringFormat+"]: Peer [peer_ID "+peerID+"] "+errorMsg);
	}
	
	/**
	 * Debug.
	 *
	 * @param msg the msg
	 */
	public void debug(String msg){
		Calendar c = Calendar.getInstance();		
		String dateInStringFormat = formatter.format(c.getTime());
		this.log(Level.INFO, "["+dateInStringFormat+"]: Peer [peer_ID "+peerID+"] "+msg);
	}
	
	/* (non-Javadoc)
	 * @see java.util.logging.Logger#warning(java.lang.String)
	 */
	public void warning(String msg){
		Calendar c = Calendar.getInstance();		
		String dateInStringFormat = formatter.format(c.getTime());
		this.log(Level.WARNING, "["+dateInStringFormat+"]: Peer [peer_ID "+peerID+"] "+msg);
	}
	
	/* (non-Javadoc)
	 * @see java.util.logging.Logger#info(java.lang.String)
	 */
	public void info(String msg){
		Calendar c = Calendar.getInstance();		
		String dateInStringFormat = formatter.format(c.getTime());
		this.log(Level.INFO, "OFFICIAL LOGGING ["+getCurrentTime()+"] : "+msg);		
	}
	public String getCurrentTime(){
        DateFormat date = new SimpleDateFormat("HH:mm:ss");
        Date date1 = new Date();
        return(date.format(date1));

    }
	
	/**
	 * Error.
	 *
	 * @param prefix the prefix
	 * @param errorMsg the error msg
	 */
	public void error(String prefix, String errorMsg){
		this.log(Level.SEVERE, "["+prefix+"]: "+errorMsg);
	}
	
	/**
	 * Debug.
	 *
	 * @param prefix the prefix
	 * @param msg the msg
	 */
	public void debug(String prefix, String msg){
		this.log(Level.INFO, "["+prefix+"]: "+msg);
	}
	
	/**
	 * Warning.
	 *
	 * @param prefix the prefix
	 * @param msg the msg
	 */
	public void warning(String prefix, String msg){
		this.log(Level.WARNING, "["+prefix+"]: "+msg);
	}
	
	/**
	 * Info.
	 *
	 * @param prefix the prefix
	 * @param msg the msg
	 */
	public void info(String prefix, String msg){
		this.log(Level.INFO, "["+prefix+"]: "+msg);
	}
}
