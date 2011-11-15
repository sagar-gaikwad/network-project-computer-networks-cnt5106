package com.test;

import edu.ufl.cise.cn.peer2peer.Controller;

public class Peer4 {
	public static void main(String args[]) throws Exception{
		Controller controller = Controller.getInstance("1004");
		controller.startProcess();
	}
}
