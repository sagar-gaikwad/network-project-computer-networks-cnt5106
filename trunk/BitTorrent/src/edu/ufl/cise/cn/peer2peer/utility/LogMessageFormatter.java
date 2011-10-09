package edu.ufl.cise.cn.peer2peer.utility;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/*
 * This class formats the log records as per requirement. 
 * */
public class LogMessageFormatter extends SimpleFormatter{
	
	/*
	 * This method has to be synchronized as multiple threads access it.
	 * record: This object contains log messages to be formatted 
	 * */
	@Override
	public synchronized String format(LogRecord record) {
		if(record != null){
			return record.getMessage();
		}else{
			return null;
		}
	}
	
	/*
	 * This method has to be synchronized as multiple threads access it.
	 * record: This object contains log messages to be formatted 
	 * */
	@Override
	public synchronized String formatMessage(LogRecord record) {
		// TODO Auto-generated method stub
		return this.format(record);
	}
}
