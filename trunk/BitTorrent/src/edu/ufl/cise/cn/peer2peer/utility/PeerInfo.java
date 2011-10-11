package edu.ufl.cise.cn.peer2peer.utility;



// TODO: Auto-generated Javadoc
/**
 * The Class PeerInfo.
 *
 * @author sagarg
 */
public class PeerInfo {
	
	/** The peer id. */
	private String peerID;
	
	/** The host address. */
	private String hostAddress;
	
	/** The port number. */
	private int portNumber;
	
	/** The is file exists. */
	private boolean isFileExists;
	
	/**
	 * Gets the peer id.
	 *
	 * @return the peer id
	 */
	public String getPeerID() {
		return peerID;
	}
	
	/**
	 * Sets the peer id.
	 *
	 * @param peerID the new peer id
	 */
	public void setPeerID(String peerID) {
		this.peerID = peerID;
	}
	
	/**
	 * Gets the host address.
	 *
	 * @return the host address
	 */
	public String getHostAddress() {
		return hostAddress;
	}
	
	/**
	 * Sets the host address.
	 *
	 * @param hostAddress the new host address
	 */
	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}
	
	/**
	 * Gets the port number.
	 *
	 * @return the port number
	 */
	public int getPortNumber() {
		return portNumber;
	}
	
	/**
	 * Sets the port number.
	 *
	 * @param portNumber the new port number
	 */
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	
	/**
	 * Checks if is file exists.
	 *
	 * @return true, if is file exists
	 */
	public boolean isFileExists() {
		return isFileExists;
	}
	
	/**
	 * Sets the file exists.
	 *
	 * @param isFileExists the new file exists
	 */
	public void setFileExists(boolean isFileExists) {
		this.isFileExists = isFileExists;
	}
}
