package com.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import edu.ufl.cise.cn.peer2peer.Controller;
import edu.ufl.cise.cn.peer2peer.utility.OutputDisplayer;
import edu.ufl.cise.cn.peer2peer.utility.PeerConfigFileReader;
import edu.ufl.cise.cn.peer2peer.utility.PeerInfo;

public class ProcessStarter {
	public static void main(String args[]) throws Exception{
		ProcessStarter starter = new  ProcessStarter();
		
		// start the process on local machine
//		starter.startProcessesOnLocalMachines();
		
		// start the process on Remote machine
		starter.startProcessesOnRemoteMachines();
	}
	
	public void startProcessesOnLocalMachines(){
		Controller controller = Controller.getInstance("1");
		controller.startProcess();
	}
	
	public void startProcessesOnRemoteMachines() throws Exception{
		
		PeerConfigFileReader fileReader = PeerConfigFileReader.getInstance();
		
		HashMap<String, PeerInfo> peerMap = fileReader.getPeerInfoMap();
		
		Set<String> peerID = peerMap.keySet();

		String path = System.getProperty("user.dir");
		
		for (String peer : peerID) {
			PeerInfo peerInfo = peerMap.get(peer);
			String runCommand = "java peerProcess";
			
			String commandLineArgument = peerInfo.getPeerID();
			
			Process serverProcess = Runtime.getRuntime().exec("ssh " + peerInfo.getHostAddress() + " cd " + path + " ;" +runCommand+" "+commandLineArgument);
			OutputDisplayer outputDisplayer = new OutputDisplayer(peer, new BufferedReader(new InputStreamReader(serverProcess.getInputStream()))  );
			new Thread(outputDisplayer).start();
			OutputDisplayer errorDisplayer = new OutputDisplayer(peer, new BufferedReader(new InputStreamReader(serverProcess.getErrorStream()))  );
			new Thread(errorDisplayer ).start();
			Thread.sleep(5000);
			System.out.println("Started Process "+peerInfo.getPeerID() +" on "+peerInfo.getHostAddress()+" and port number : "+peerInfo.getPortNumber());
		}
	}
}
