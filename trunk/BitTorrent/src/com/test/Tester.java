package com.test;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import edu.ufl.cise.cn.peer2peer.messagehandler.MessageManager;
import edu.ufl.cise.cn.peer2peer.utility.PeerConfigFileReader;
import edu.ufl.cise.cn.peer2peer.utility.PeerInfo;
import edu.ufl.cise.cn.peer2peer.utility.PropsReader;

public class Tester {
	public static void main(String args[]){
		String value = PropsReader.getPropertyValue("PieceSize");
//		System.out.println("Value: "+value);
		
		/*String str = "CEN5501C2008SPRING";
		char arr[] = str.toCharArray();
		
		byte[] b = new byte[arr.length];
		for(int i=0 ; i<arr.length ; i++){
			b[i] = (byte)arr[i];
		}
		
		int peerID = Integer.MIN_VALUE; 
		
		System.out.println("________________________________: "+b.length);
		
		for(int i=0 ; i<b.length; i++){
			System.out.println("byte value: "+b[i]);
			System.out.println("char value: "+(char)b[i]);
		}
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(4);
		byteBuffer.putInt(peerID);
		
		System.out.println("______________________________");
		for(int i=0; i < byteBuffer.capacity() ; i++){
			byte temp = byteBuffer.get(i);
			System.out.println("temp: "+temp);
		}
		System.out.println("______________________________");
		
		byte[] dst = byteBuffer.array(); 
		
		byteBuffer = ByteBuffer.allocate(dst.length);
		byteBuffer.put(dst);
		
		byteBuffer.rewind();
		System.out.println("Integer: "+byteBuffer.getInt());*/
		
		/*MessageManager manager = MessageManager.getInstance();
		byte[] data = manager.getChokeMessage();
		
		for (byte b : data) {
			System.out.println("b: "+b);
		}
		
		manager.parseMessage(data);*/
		
		/*Calendar c = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss a");
		String dateInStringFormat = formatter.format(c.getTime());
		
		System.out.println("dateInStringFormat : "+dateInStringFormat);*/
		
//		System.out.println(""+PropsReader.getPropertyValue("PieceSize"));
		
		PeerConfigFileReader reader = PeerConfigFileReader.getInstance();
		
		HashMap<String,PeerInfo> peerInfoMap = reader.getPeerInfoMap();
		
		Set<String> peerIDList = peerInfoMap.keySet();
		
		for (String peer : peerIDList) {
			PeerInfo peerInfo = peerInfoMap.get(peer);
			System.out.println("ID: "+peerInfo.getPeerID());
			System.out.println("host: "+peerInfo.getHostAddress());
			System.out.println("port: "+peerInfo.getPortNumber());
			System.out.println("file exists: "+peerInfo.isFileExists());
			System.out.println("_________________________________________");
		}
		
	}
}
