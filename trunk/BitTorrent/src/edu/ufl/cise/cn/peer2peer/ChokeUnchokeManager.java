package edu.ufl.cise.cn.peer2peer;

import edu.ufl.cise.cn.peer2peer.utility.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.ufl.cise.cn.peer2peer.utility.PropsReader;

public class ChokeUnchokeManager implements Runnable {
	
	private ScheduledFuture<?> task = null;
	
    private ScheduledExecutorService scheduler = null;
	
    private static ChokeUnchokeManager chokeUnchokeManager = null;
    
    private Controller controller = null;
    
    public static synchronized ChokeUnchokeManager getInstance(Controller controller){
    	if(chokeUnchokeManager == null){
    		if(controller == null){
    			return null;
    		}
    		
    		
    		chokeUnchokeManager = new ChokeUnchokeManager();
    		boolean isInitialized = chokeUnchokeManager.init();
    		
    		if(isInitialized == false){
    			chokeUnchokeManager.deinit();
    			chokeUnchokeManager = null;
    			return null;
    		}
    		
    		chokeUnchokeManager.controller = controller;
    		
    	}

    	return chokeUnchokeManager;
    	
    }
    
    private boolean init(){
    	scheduler = Executors.newScheduledThreadPool(1);    	
    	return true;
    }
    
    public void deinit(){
    	task.cancel(true);
    }
    
	public void run() {
		
	
		// TODO Auto-generated method stub
		Integer preferredNeighbors = 0;
		HashMap<String, Double> speedMap = controller.getSpeedForAllPeers();
		// -- test code logging
		/*System.out.println("ChokeUnchokeManager : run function called...");
		System.out.println("printing hashmap of peerrs and download speed");
		for(Entry<String, Double> entry : speedMap.entrySet()) 
		{
			  String key = entry.getKey();
			  Double value = entry.getValue();	
			  System.out.println(" PeerID--> "+key+" Download Speed"+value);
			  
		}*/
		// --- test code ends
		
		if(PropsReader.getPropertyValue("NumberOfPreferredNeighbors") != null)
			preferredNeighbors = Integer.parseInt(PropsReader.getPropertyValue("NumberOfPreferredNeighbors"));
		else
			System.err.println("NumberOfPreferredNeighbors variable not in properties file. Invalid Properties File!!!");
		preferredNeighbors = 3;
		ArrayList<String> unchokePeers = new ArrayList();
		//Find top k preferred neighbours
		
		//creating a treemap sorted on values for selecting top k preferred neighbors
		System.out.println("SpeedMap Size"+speedMap.size());
		ValueComparator bvc = new ValueComparator(speedMap);
		TreeMap<String, Double> sortedSpeedMap = new TreeMap(bvc);		
		sortedSpeedMap.putAll(speedMap);		
		System.out.println("sorted map made");
		
		int count = 0;
		//adding preferredNeighbors string to ArrayList
		System.out.println("ChokeUnchokeManager : Sorting by download speed done");
		for(Entry<String, Double> entry : sortedSpeedMap.entrySet()) 
		{
			  String key = entry.getKey();
			  Double value = entry.getValue();
			  System.out.println("SortedPeer -->"+key+ " dload speed"+value);
			  unchokePeers.add(key);
			  count++; // maintaining count to break out of map iterator
			  if(count == preferredNeighbors)
				  break;			  
			}							
		//unchoking those peers.
		controller.chokePeers(unchokePeers);
	}

	//delay iin seconds
	public void start(int startDelay, int intervalDelay){
		System.out.println("ChokeUnchokeManager : start called now wait for "+intervalDelay+" seconds....");
		task = scheduler.scheduleAtFixedRate(this, 10, intervalDelay, TimeUnit.SECONDS);
	}
	
}
