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
    
    private OptimisticUnchokeManager(){
		
	}
    
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
    	System.out.println("OptimisticUnchokeManager : Shutting down OptimisticUnchokeManager......");
    	task.cancel(true);
    }
    
	public void run() {
		// TODO Auto-generated method stub		
		System.out.println("OptimisiticUnchokeManager : Calling opmtimistic unchoke");
		ArrayList<String> chokedPeerList = controller.getChokedPeerList();
		if(chokedPeerList.size() > 0){
			Random random = new Random();
			controller.optimisticallyUnChokePeers(chokedPeerList.get(random.nextInt(chokedPeerList.size())));
		}
		
		if(controller.isFileDownloadComplete() == true)
    	{    		
			System.out.println("File DOWNLOAD COMPLETE FOR PEER : "+controller.getPeerID());
    	}
		
		if(controller.isAllPeersFileDownloadComplete() == true)
		{
			controller.BroadcastShutdownMessage();
		
		}
		
		
	}
	
	public void start(int startDelay, int intervalDelay){
		task = scheduler.scheduleAtFixedRate(this, 10, intervalDelay, TimeUnit.SECONDS);
	}
	
}
