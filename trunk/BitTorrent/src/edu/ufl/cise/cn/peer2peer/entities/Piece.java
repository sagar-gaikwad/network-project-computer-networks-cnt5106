package edu.ufl.cise.cn.peer2peer.entities;

import java.io.Serializable;

public class Piece implements Serializable{
	private byte[] data;
	int size;
	
	public Piece(int size)
	{
		this.size = size;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		if(this.size == data.length)
			this.data = data;
		else
			System.err.println("Piece Size and Data Size MisMatch");
	}
	
	public int getSize(){
		if(data == null){
			return -1;
		}else{
			return data.length;
		}		
	}
}
