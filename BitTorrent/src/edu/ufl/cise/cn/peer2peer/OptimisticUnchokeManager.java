package edu.ufl.cise.cn.peer2peer;


import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class OptimisticUnchokeManager implements Runnable{
	
	private ScheduledFuture<?> task = null;
	
    private ScheduledExecutorService scheduler = null;
	
    private static OptimisticUnchokeManager optimisticUnchokeManager = null;
    
    private Controller controller = null;
    
    public static synchronized OptimisticUnchokeManager getInstance(Controller controller){
    	if(optimisticUnchokeManager == null){
    		if(controller == null){
    			return null;
    		}
    		
    		
    		optimisticUnchokeManager = new OptimisticUnchokeManager();
    		boolean isInitialized = optimisticUnchokeManager.init();
    		
    		if(isInitialized == false){
    			optimisticUnchokeManager.deinit();
    			optimisticUnchokeManager = null;
    			return null;
    		}
    		
    		optimisticUnchokeManager.controller = controller;
    		
    	}

    	return optimisticUnchokeManager;
    	
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
		ArrayList<String> chokedPeerList = controller.getChokedPeerList();
		Integer random = ((int)(Math.random()*1000))%chokedPeerList.size();
		System.out.println("OptimisticUnchokeManager : random peer to unchoke is "+chokedPeerList.get(random));
		controller.optimisticallyUnChokePeers(chokedPeerList.get(random));	
	}
	
	public void start(int startDelay, int intervalDelay){
		task = scheduler.scheduleAtFixedRate(this, 10, intervalDelay, TimeUnit.SECONDS);
	}
	
}
