package com.test;

import edu.ufl.cise.cn.peer2peer.Controller;
import edu.ufl.cise.cn.peer2peer.OptimisticUnchokeManager;

public class TestOpmisiticUnchoke {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		Controller control = Controller.getInstance("MohitPeer");	
		OptimisticUnchokeManager chodManager = OptimisticUnchokeManager.getInstance(control);
		System.out.println("Test Main : Starting optimistic unchoke manager");
		chodManager.start(0, 25);

	}

}
