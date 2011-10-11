package com.test;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import edu.ufl.cise.cn.peer2peer.peerhandler.PeerHandler;

public class Peer2 {
	public static void main(String args[]) throws Exception{
		Socket socket = new Socket(InetAddress.getLocalHost(),4545);
//		PeerHandler handler = PeerHandler.getInstance("2", socket);		
//		new Thread(handler).start();
//		handler.sendHandshakeMessage();
		Thread.sleep(4000);
	}
}
