package edu.ufl.cise.cn.peer2peer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
		
	}
	
	public void start(int startDelay, int intervalDelay){
		task = scheduler.scheduleAtFixedRate(this, 10, intervalDelay, TimeUnit.SECONDS);
	}
	
}
