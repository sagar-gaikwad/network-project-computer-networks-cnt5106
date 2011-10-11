package com.test;

import java.net.ServerSocket;
import java.net.Socket;

import edu.ufl.cise.cn.peer2peer.peerhandler.PeerHandler;

public class Peer1 {
	public static void main(String args[]) throws Exception{
		ServerSocket serverSocket = new ServerSocket(4545);
		Socket socket = serverSocket.accept();
//		PeerHandler handler = PeerHandler.getInstance("2", socket);
//		new Thread(handler).start();
	}
}
