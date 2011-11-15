package com.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import edu.ufl.cise.cn.peer2peer.entities.Peer2PeerMessage;
import edu.ufl.cise.cn.peer2peer.entities.Piece;
import edu.ufl.cise.cn.peer2peer.utility.Constants;

public class Server {
	public static void main(String args[]) throws Exception{
		ServerSocket serverSocket = new ServerSocket(4545);
//		serverSocket.getLocalPort();
		Socket socket = serverSocket.accept();
		
		OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		
//		OutputStream os = null;
//		InputStream is = null;
		
		Server server = new Server();
		server.sendFile(os, is);
	}
	
	public void testCommunicationWithClient(OutputStream os, InputStream is) throws Exception{
		
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
	
	public void sendFile(OutputStream os, InputStream is) throws Exception{

		String path = "C:\\Users\\sagar\\MyStudy\\ComputerNetworks\\Project\\test.txt";
//		String path = "C:\\Users\\sagar\\Pictures\\poem.jpg";
		
		BufferedInputStream bufferedIS = new BufferedInputStream(new FileInputStream(path));
		ByteBuffer buffer = ByteBuffer.allocate(646464646);
		
		int size = 0;
		
		byte b = (byte)bufferedIS.read();
		size++;
		while(b != -1){
			buffer.put(b);
			b = (byte)bufferedIS.read();
			size++;
		}
		
		Peer2PeerMessage message = Peer2PeerMessage.getInstance();
		message.setMessgageType(Constants.PIECE_MESSAGE);
		message.setMessageLength(1000);
		
		byte[] dst = new byte[size];
		
		buffer.get(dst);
		
		Piece piece = new Piece(size);
		piece.setData(dst);
		message.setData(piece);
		
		System.out.println("Read file successfulle: "+size);
		
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
		objectOutputStream.writeObject(message);
		objectOutputStream.flush();
		objectOutputStream.close();
	}
}
