package com.test;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.sound.midi.Receiver;

import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;

public class Client {
	public static void main(String args[]) throws Exception{
		Socket socket = new Socket(InetAddress.getLocalHost(),4545);
		
		
		OutputStream os = socket.getOutputStream();		
		InputStream is = socket.getInputStream();
		
		Client client = new Client();
		client.receiveFile(os, is);
		
	}
	
	public void receiveFile(OutputStream os, InputStream is) throws Exception{
		ObjectInputStream ois = new ObjectInputStream(is);
		Peer2PeerMessage message = (Peer2PeerMessage)ois.readObject();
		
		System.out.println(": "+message.getMessageLength());
		System.out.println(": "+message.getMessgageType());
		System.out.println(": "+message.getData().getData().length);
	}
	
	public void testCommunicationWithClient(OutputStream os, InputStream is) throws Exception{
		byte[] b = new byte[10];
		System.out.println("Waiting for server to write");
		int numberOfBytesRead = is.read(b,0,10);
		
		while(numberOfBytesRead!=-1){
			System.out.println("chunk: "+new String(b));
			numberOfBytesRead = is.read(b,0,10);
		}
		
		System.out.println("Read from server: "+new String(b));
		
		os.write(b);
		System.out.println("sent to server...");
	}
}
