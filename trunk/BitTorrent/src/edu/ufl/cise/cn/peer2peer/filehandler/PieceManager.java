package edu.ufl.cise.cn.peer2peer.filehandler;

import edu.ufl.cise.cn.peer2peer.entities.Piece;

// TODO: Auto-generated Javadoc
/**
 * The Class PieceManager.
 *
 * @author Mohit
 */
public class PieceManager {
	
	/** The piece manager instance. */
	private static PieceManager pieceManagerInstance;
	
	/**
	 * Instantiates a new piece manager.
	 */
	private PieceManager(){
		
	}
	
	/**
	 * Gets the piece manager instance.
	 *
	 * @return the piece manager instance
	 */
	synchronized public PieceManager getPieceManagerInstance(){
		if(pieceManagerInstance == null){
			pieceManagerInstance = new PieceManager();
			boolean isSuccessfullyInitialized = pieceManagerInstance.init();
			if(isSuccessfullyInitialized == false){
				pieceManagerInstance = null;
			}
		}
		return pieceManagerInstance;
	}
	
	  
	/**
	 * Inits the.
	 * If Input file exists then connection of it. It also creates the output stream for output file where pieces will be written.
	 * It initializes bit vector
	 * @return true, if successful
	 */
	private boolean init(){
		return false;
	}
	
	// Close open file pointer
	/**
	 * Close all open file connections. Handle all exceptions. Handles case when multiple thread calls this method. 
	 */
	synchronized public void close(){
		
	}
	
	/**
	 * Gets the piece of file.
	 *
	 * @param number the number
	 * @return the piece
	 */
	synchronized public Piece getPiece(int number){
		return null;
	}
	
	/**
	 * Write piece.
	 *
	 * @param number of piece in file
	 * @param piece to be written
	 * @return if operation is successful
	 */
	synchronized public boolean writePiece(int number,Piece piece){
		return false;
	}
	
	/**
	 * Gets the missing piece number.
	 *
	 * @return the missing piece number
	 */
	synchronized public int[] getMissingPieceNumberArray(){
		return null;
	}
	
	/**
	 * Gets the available piece number array.
	 *
	 * @return the available piece number array
	 */
	synchronized public int[] getAvailablePieceNumberArray(){
		return null;
	}
	
	/**
	 * Checks if file download is complete or not.
	 *
	 * @return true, if is file download complete
	 */
	synchronized public boolean isFileDownloadComplete(){
		return false;
	}
}
