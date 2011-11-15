package com.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String args[]) throws Exception{
		ServerSocket serverSocket = new ServerSocket(4545);
//		serverSocket.getLocalPort();
		Socket socket = serverSocket.accept();
		
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		
		System.out.println("Connected to client");
		Thread.sleep(10000);
		System.out.println("Writing to client");		
		os.write("TestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest".getBytes());
		os.flush();
		os.close();
		
		System.out.println("waiting for client to write");
		byte[] b = new byte[10];
		is.read(b);
		Thread.sleep(4000);
		System.out.println("read from client: "+new String(b));
	}
}
