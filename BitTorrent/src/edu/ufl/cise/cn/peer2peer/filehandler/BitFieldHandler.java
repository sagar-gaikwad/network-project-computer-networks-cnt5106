package edu.ufl.cise.cn.peer2peer.filehandler;

public class BitFieldHandler {
	
	//no size declaration of this array????
	private boolean bitfieldVector[];
	private int size;
	public BitFieldHandler(int numOfPieces) {
		bitfieldVector = new boolean[numOfPieces];
		size = numOfPieces;
		// TODO Auto-generated constructor stub
		for(int i = 0; i < size; i++)
		{
			bitfieldVector[i] = false;
		}
		printvector();
		
	}

	public int getLength()
	{
		return size;
	}
	public boolean[] getBitfieldVector() {
		return bitfieldVector;
	}

	public void setBitfieldVector(boolean[] bitfieldVector) {
		this.bitfieldVector = bitfieldVector;
	}
	
	public boolean getBitFieldOn(int number)
	{
		return bitfieldVector[number];
	}
	public void setBitFieldOn(int number, boolean value)
	{
		bitfieldVector[number] = value;
	}
	
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
}
