package edu.ufl.cise.cn.peer2peer.filehandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import edu.ufl.cise.cn.peer2peer.entities.Piece;
import edu.ufl.cise.cn.peer2peer.utility.PropsReader;

// TODO: Auto-generated Javadoc
/**
 * The Class PieceManager.
 *
 * @author Mohit
 */
public class PieceManager {
	
	/** The piece manager instance. */
	private static PieceManager pieceManagerInstance;	
	int numOfPieces ;
	int pieceSize;
	private static BitFieldHandler bitField ;
	FileOutputStream outStream;
	FileInputStream inStream;
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
	synchronized public static PieceManager getPieceManagerInstance(){
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
	//to make this private
	public boolean init(){
		
		//initialize bit vector according to pieces and set all bits to 0
		//doubt. how will this peer know that it has file or not
		//if input config file exists then get filename from sagar
		//to get from sagar. file name, number of pieces		
		//create file name as output stream
		
		pieceSize = Integer.parseInt(PropsReader.getPropertyValue("PieceSize"));
		numOfPieces = (int) Math.ceil(Integer.parseInt(PropsReader.getPropertyValue("FileSize")) / pieceSize) ;
		System.out.println("PieceManager : number of pieces : "+numOfPieces);
		try
		{
			bitField = new BitFieldHandler(numOfPieces);
			//input file connection??? why???
			String outputFileName = new String();			
			outputFileName = PropsReader.getPropertyValue("FileName");
			File outFile = new File(outputFileName);
			if(!outFile.exists()){
				outFile.createNewFile();
				System.out.println("PieceManager : new outputfile "+outputFileName+" created");
			}
			else
			{
				System.out.println("PieceManager : outputfile "+outputFileName+" already exists");
			}
			outStream = new FileOutputStream(outputFileName);
			inStream = new FileInputStream(outputFileName);
			return true;
			
		}
		catch(Exception e)
		{
		  e.printStackTrace();	
		}
		
		return false;
		
	}
	
	// Close open file pointer
	/**
	 * Close all open file connections. Handle all exceptions. Handles case when multiple thread calls this method. 
	 */
	synchronized public void close(){
		//close outputfilestream
		try {
			outStream.close();
			inStream.close();
			System.out.println("PieceManager : input and output streams closed");
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		//assuming there is not connection of input file needed				
	}
	
	/**
	 * Gets the piece of file.
	 *
	 * @param number the number
	 * @return the piece
	 */
	synchronized public Piece getPiece(int number){
		
		Piece readPiece = new Piece();
		//have to read this piece from my own output file.
		try{
			byte[] readBytes = new byte[pieceSize];
			inStream.read(readBytes, number*pieceSize, pieceSize);
			readPiece.setData(readBytes);
		//	inStream.close();
			return readPiece;
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}		
	}
	
	/**
	 * Write piece.
	 *
	 * @param number of piece in file
	 * @param piece to be written
	 * @return if operation is successful
	 */
	synchronized public boolean writePiece(int number,Piece piece){
		try {
			//have to write this piece in Piece object array
			outStream.write(piece.getData(), number*pieceSize, pieceSize);
			outStream.flush();
			bitField.setBitFieldOn(number, true);
			bitField.printvector();
			System.out.println("PieceManager : piece "+number+" written succesfully");
			//outStream.close();
			return true;
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		
	}
	
	/**
	 * Gets the missing piece number.
	 *
	 * @return the missing piece number
	 */
	synchronized public int[] getMissingPieceNumberArray(){
		//to return all missing piece index????
		int i = 0; 
		int j = 0;
				
		//Finding number of missing indexes count
		while( i < bitField.getLength())
		{
			if(bitField.getBitFieldOn(i) == false)
			{				
				j++;
			}
			i++;											
		}
		//Now j as number of missing indexes count so creating an array of count size
		int[] missing = new int[j];
		j = 0;
		i = 0;
		while( i < bitField.getLength())
		{
			if(bitField.getBitFieldOn(i) == false)
			{				
				missing[j] = i;
				j++;
			}
			i++;											
		}		
		bitField.printvector();
		return missing;
		//return null;
	}
	
	/**
	 * Gets the available piece number array.
	 *
	 * @return the available piece number array
	 */
	synchronized public int[] getAvailablePieceNumberArray(){
		//to return all available piece number array?????
		int i = 0; 
		int j = 0;
		
		int[] available = new int[numOfPieces];
		while( i < bitField.getLength())
		{
			if(bitField.getBitFieldOn(i) == true)
			{
				available[j] = i;
				j++;
			}
			else
				i++;								
		}
		return available;
	}
	
	/**
	 * Checks if file download is complete or not.
	 *
	 * @return true, if is file download complete
	 */
	synchronized public boolean isFileDownloadComplete(){
		//to traverse whole bitfield vector for value 1
		int i = 0;
		while(i < bitField.getLength())
		{
			if(bitField.getBitFieldOn(i)!=true)
			{
				return false;
			}
			else
			{
				i++;
			}
		}
		return true;
	}
}
