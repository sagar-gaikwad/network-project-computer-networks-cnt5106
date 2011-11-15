package com.test;

import java.net.ServerSocket;
import java.net.Socket;

import edu.ufl.cise.cn.peer2peer.Controller;
import edu.ufl.cise.cn.peer2peer.peerhandler.PeerHandler;

public class Peer1 {
	public static void main(String args[]) throws Exception{
		Controller controller = Controller.getInstance("1001");
		controller.startProcess();
	}
}
