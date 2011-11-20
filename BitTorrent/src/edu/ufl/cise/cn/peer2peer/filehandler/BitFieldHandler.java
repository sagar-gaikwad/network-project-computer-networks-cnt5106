package edu.ufl.cise.cn.peer2peer.filehandler;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class BitFieldHandler.
 */
public class BitFieldHandler implements Serializable{
	
	/** The bitfield vector. */
	private boolean bitfieldVector[];
	
	/** The size. */
	private int size;
	
	/**
	 * Instantiates a new bit field handler.
	 *
	 * @param numOfPieces the num of pieces
	 */
	public BitFieldHandler(int numOfPieces) {
		bitfieldVector = new boolean[numOfPieces];
		size = numOfPieces;
		// TODO Auto-generated constructor stub
		for(int i = 0; i < size; i++)
		{
			bitfieldVector[i] = false;
		}
		//printvector();
		
	}

	/**
	 * Gets the length.
	 *
	 * @return the length
	 */
	public int getLength()
	{
		return size;
	}
	
	/**
	 * Gets the bitfield vector.
	 *
	 * @return the bitfield vector
	 */
	public boolean[] getBitfieldVector() {
		return bitfieldVector;
	}

	/**
	 * Sets the bitfield vector.
	 *
	 * @param bitfieldVector the new bitfield vector
	 */
	public void setBitfieldVector(boolean[] bitfieldVector) {
		this.bitfieldVector = bitfieldVector;
	}
	
	/**
	 * Gets the bit field on.
	 *
	 * @param number the number
	 * @return the bit field on
	 */
	public boolean getBitFieldOn(int number)
	{
		return bitfieldVector[number];
	}
	
	/**
	 * Sets the bit field on.
	 *
	 * @param number the number
	 * @param value the value
	 */
	public void setBitFieldOn(int number, boolean value)
	{
		bitfieldVector[number] = value;
	}
	
	/**
	 * Printvector.
	 */
	public void printvector()
	{
		int i =0;
		System.out.println(" printing bitvector");
		while (i < size)
		{
			System.out.print(" "+i+" : "+bitfieldVector[i++]);
		}
		System.out.println("");
	}
	
	public void setBitFieldOnForAllIndexes(){
		for(int i=0 ; i<bitfieldVector.length ; i++){
			bitfieldVector[i] = true;
		}
	}
}
