package edu.ufl.cise.cn.peer2peer.utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.test.ProcessStarter;


/**
 * The Class PeerConfigFileReader.
 *
 * @author sagar
 */
public class PeerConfigFileReader {
	
	/** The peer info list. */
	private HashMap<String,PeerInfo> peerInfoMap = null;
	
	/** The peer config file reader. */
	private static PeerConfigFileReader peerConfigFileReader = null;
	
	/**
	 * Instantiates a new peer config file reader.
	 */
	private PeerConfigFileReader(){
		
	}
	
	/**
	 * Gets the single instance of PeerConfigFileReader.
	 *
	 * @return single instance of PeerConfigFileReader
	 */
	public static PeerConfigFileReader getInstance(){
		if(peerConfigFileReader == null){
			peerConfigFileReader = new  PeerConfigFileReader();
			peerConfigFileReader.init();
		}
		return peerConfigFileReader;
	}
	
	/**
	 * Parse Peer configuration file and store information in HashMap with PeerID as Key. 
	 *
	 * @return true, if successful
	 */
	public boolean init(){
		
		
		try {
			BufferedReader configFileReader =  new BufferedReader(new InputStreamReader(PeerConfigFileReader.class.getResourceAsStream(Constants.PEER_INFO_FILE)));
			
			peerInfoMap = new HashMap<String,PeerInfo>();
			
			String line = configFileReader.readLine();
			
			// File is separated by space character
			PeerInfo peerInfoInstance = null;
			while(line != null){
				peerInfoInstance = new PeerInfo();
				String tokens[] = line.trim().split(" ");
				peerInfoInstance.setPeerID(tokens[0]);
				peerInfoInstance.setHostAddress(tokens[1]);
				peerInfoInstance.setPortNumber(Integer.parseInt(tokens[2]));
				
				if(tokens[3].equals("1")){
					peerInfoInstance.setFileExists(true);
				}else{
					peerInfoInstance.setFileExists(false);
				}
				
				peerInfoMap.put(tokens[0],peerInfoInstance);
				
				line = configFileReader.readLine();
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Gets the peer info map.
	 *
	 * @return the peer info map
	 */
	public HashMap<String, PeerInfo> getPeerInfoMap() {
		return peerInfoMap;
	}
}
