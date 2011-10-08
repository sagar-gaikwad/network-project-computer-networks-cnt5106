package com.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	public static void main(String args[]) throws Exception{
		Socket socket = new Socket(InetAddress.getLocalHost(),4545);
		
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
		
		byte[] b = new byte[10];
		is.read(b);
		
		System.out.println("Read from server: "+new String(b));
		
		os.write(b);
		System.out.println("sent to server...");
	}
}
