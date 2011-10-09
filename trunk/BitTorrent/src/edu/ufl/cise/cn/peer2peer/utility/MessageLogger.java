package edu.ufl.cise.cn.peer2peer.utility;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * This class extends Logger class to log custom messages
 * */
public class MessageLogger extends Logger{
	private String logFileName;
	FileHandler fileHandler;
	
	/*
	 * Parameter
	 * logFileName: This parameter contains name of the log file.
	 * name: This is name of the Logger class.
	 * */
	public MessageLogger(String logFileName, String name) {
		super(name, null);
		this.logFileName = logFileName+".log";
	}

	/*
	 * Add File Handler to logger class so that all log messages will be written in the file 
	 * */
	public void initialize() throws SecurityException, IOException{
		fileHandler = new FileHandler(logFileName);
		fileHandler.setFormatter(new LogMessageFormatter());
		this.addHandler(fileHandler);
	}

	/*
	 * Log messages into file. This method is synchronized as multiple threads access this method. 
	 * */
	@Override
	public synchronized void log(Level level, String msg) {
		// TODO Auto-generated method stub
		super.log(level, msg);
		super.log(level, "\n");
//		System.out.println(msg);
	}
	
	public synchronized void log(Level level, String prefix, String msg) {
		// TODO Auto-generated method stub
		super.log(level, "["+prefix+"]: "+ msg);
		super.log(level, "\n");
//		System.out.println(msg);
	}
	
	public synchronized void logStackTrace(Level level, String prefix, StackTraceElement[] element){
		
	}
	/*
	 * Close the file in which log messages are written.
	 * */
	public void close(){
		fileHandler.close();
	}
	
	public void debug(String prefix, String msg){
		super.log(Level.FINEST,"["+prefix+"]: "+msg);
		System.out.println("["+prefix+"]: "+msg);
	}
	
	public void error(String prefix, String msg, Exception ex){
		super.log(Level.SEVERE,"["+prefix+"]: "+msg+" : "+ex.getMessage());
		System.out.println("["+prefix+"]: "+msg+" : "+ex.getMessage());
		logStackTrace(Level.FINEST, prefix , ex.getStackTrace());
	}
}
