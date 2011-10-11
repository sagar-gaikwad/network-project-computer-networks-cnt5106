package com.test;

import edu.ufl.cise.cn.peer2peer.Controller;

public class ProcessStarter {
	public static void main(String args[]){
		ProcessStarter starter = new  ProcessStarter();
		
		// start the process on local machine
		starter.startProcessesOnLocalMachines();
		
		// start the process on Remote machine
		starter.startProcessesOnRemoteMachines();
	}
	
	public void startProcessesOnLocalMachines(){
		Controller controller = Controller.getInstance("1");
		controller.startProcess();
	}
	
	public void startProcessesOnRemoteMachines(){
		Controller controller = Controller.getInstance("1");
		controller.startProcess();
	}
}
