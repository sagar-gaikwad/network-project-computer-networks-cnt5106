package edu.ufl.cise.cn.peer2peer.entities;

import java.io.Serializable;

public interface PeerMessage extends Serializable{
	public int getType();	
	public int getMessageLength();
}
