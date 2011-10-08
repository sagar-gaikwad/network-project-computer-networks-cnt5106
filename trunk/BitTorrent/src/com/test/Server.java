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
		
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
				
		os.write("Test".getBytes());
		System.out.println("waiting for client to write");
		byte[] b = new byte[10];
		is.read(b);
		Thread.sleep(4000);
		System.out.println("read from client: "+new String(b));
	}
}
