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
		this.data = data;
	}
	
	public int getSize(){
		if(data == null){
			return -1;
		}else{
			return data.length;
		}		
	}
}
