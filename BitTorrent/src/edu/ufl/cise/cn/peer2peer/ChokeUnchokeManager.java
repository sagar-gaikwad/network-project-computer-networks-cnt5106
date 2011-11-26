package edu.ufl.cise.cn.peer2peer;

import edu.ufl.cise.cn.peer2peer.utility.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.ufl.cise.cn.peer2peer.utility.PropsReader;

public class ChokeUnchokeManager implements Runnable {
	
	private static final String LOGGER_PREFIX = ChokeUnchokeManager.class.getSimpleName();

	private ScheduledFuture<?> task = null;

	private ScheduledExecutorService scheduler = null;

	private static ChokeUnchokeManager chokeUnchokeManager = null;

	private Controller controller = null;
	
	private ChokeUnchokeManager(){
		
	}

	public static synchronized ChokeUnchokeManager getInstance(
			Controller controller) {
		if (chokeUnchokeManager == null) {
			if (controller == null) {
				return null;
			}

			chokeUnchokeManager = new ChokeUnchokeManager();
			boolean isInitialized = chokeUnchokeManager.init();

			if (isInitialized == false) {
				chokeUnchokeManager.deinit();
				chokeUnchokeManager = null;
				return null;
			}

			chokeUnchokeManager.controller = controller;

		}

		return chokeUnchokeManager;

	}

	private boolean init() {
		scheduler = Executors.newScheduledThreadPool(5);
		System.out.println("scheduler: "+scheduler);
		return true;
	}

	public void deinit() {
		task.cancel(true);
	}

	public void run() {

		// TODO Auto-generated method stub
		Integer preferredNeighbors = 0;
		HashMap<String, Double> speedMap = controller.getSpeedForAllPeers();
		// -- test code logging
		/*
		 * System.out.println("ChokeUnchokeManager : run function called...");
		 * System.out.println("printing hashmap of peerrs and download speed");
		 * for(Entry<String, Double> entry : speedMap.entrySet()) { String key =
		 * entry.getKey(); Double value = entry.getValue();
		 * System.out.println(" PeerID--> "+key+" Download Speed"+value);
		 * 
		 * }
		 */
		// --- test code ends

		if (PropsReader.getPropertyValue("NumberOfPreferredNeighbors") != null)
			preferredNeighbors = Integer.parseInt(PropsReader
					.getPropertyValue("NumberOfPreferredNeighbors"));
		else
			System.err
					.println("NumberOfPreferredNeighbors variable not in properties file. Invalid Properties File!!!");

		if (preferredNeighbors > speedMap.size()) {

			System.err
					.println("ChokeUnchokeManager : Number of preferred neighbors is less than total peers. Might be problem. ");

		} else {
			ArrayList<String> unchokePeers = new ArrayList<String>();
			// Find top k preferred neighbours

			// creating a LinkedHashMap sorted on values for selecting top k
			// preferred neighbors
			Set<Entry<String, Double>> entrySet = speedMap.entrySet();

			Entry<String, Double>[] tempArr = new Entry[speedMap.size()];
			tempArr = entrySet.toArray(tempArr);

			for (int i = 0; i < tempArr.length; i++) {
				for (int j = i + 1; j < tempArr.length; j++) {
					if (tempArr[i].getValue().compareTo(tempArr[j].getValue()) == -1) {
						Entry<String, Double> tempEntry = tempArr[i];
						tempArr[i] = tempArr[j];
						tempArr[j] = tempEntry;
					}
				}
			}

			// To make valuecomparator object working.
			LinkedHashMap<String, Double> sortedSpeedMap = new LinkedHashMap<String, Double>();

			for (int i = 0; i < tempArr.length; i++) {
				sortedSpeedMap.put(tempArr[i].getKey(), tempArr[i].getValue());
				System.out.print(tempArr[i].getKey() + ":["+tempArr[i].getValue()+"] "+" , " );
			}

			int count = 0;

			// adding preferredNeighbors string to ArrayList

			for (Entry<String, Double> entry : sortedSpeedMap.entrySet()) {
				String key = entry.getKey();
				unchokePeers.add(key);
				count++; // maintaining count to break out of map iterator
				if (count == preferredNeighbors)
					break;
			}
			
			ArrayList<String> chokedPeerList = new ArrayList<String>();
			
			for (String peerID : unchokePeers) {
				sortedSpeedMap.remove(peerID);				
			}			
			chokedPeerList.addAll(sortedSpeedMap.keySet());

			System.out.print(LOGGER_PREFIX+": Choking these peers: ");
			
			for (String peerID : chokedPeerList) {
				System.out.print(peerID +" , " );
			}
			
			System.out.println(" ");
			System.out.print(LOGGER_PREFIX+": Unchoking these peers: ");
			
			for (String peerID : unchokePeers) {
				System.out.print(peerID + " , " );
			}
			System.out.println(" ");
			
			controller.unChokePeers(unchokePeers);
			controller.chokePeers(chokedPeerList);
		}
	}

	// delay iin seconds
	public void start(int startDelay, int intervalDelay) {
		System.out.println("scheduler : "+scheduler);
		task = scheduler.scheduleAtFixedRate(this, startDelay, intervalDelay,TimeUnit.SECONDS);
	}

}
