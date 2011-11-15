package com.test;

import java.io.ObjectInputStream.GetField;

import edu.ufl.cise.cn.peer2peer.ChokeUnchokeManager;
import edu.ufl.cise.cn.peer2peer.Controller;

public class TestChokeUnchoke {

	public static void main(String args[])
	{
		Controller control = Controller.getInstance("MohitPeer");		
		ChokeUnchokeManager chodManager = ChokeUnchokeManager.getInstance(control);
		System.out.println("Test Main : Starting choke manager");
		chodManager.start(0, 25);
	}
}
