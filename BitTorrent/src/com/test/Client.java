package com.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	public static void main(String args[]) throws Exception{
		Socket socket = new Socket(InetAddress.getLocalHost(),4545);
		
		
		OutputStream os = socket.getOutputStream();		
		InputStream is = socket.getInputStream();
		
		
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
