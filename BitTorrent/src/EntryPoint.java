	

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import com.test.ProcessStarter;

import edu.ufl.cise.cn.peer2peer.Controller;
import edu.ufl.cise.cn.peer2peer.peerhandler.ChunkRequester;
import edu.ufl.cise.cn.peer2peer.utility.PropsReader;

public class EntryPoint {
	public static void main(String args[]){
		/*String peerID = args[0];
		String hostAddress = args[1];
		String portNumber = args[2];
		String isFileExists = args[3];
		
		
		Controller controller = Controller.getInstance(peerID);
		controller.startProcess();*/
		
//		test3();
//		test4();
		test5();
	}
	
	public static void test5(){
		try {
			long time1 = System.currentTimeMillis();
			System.out.println("Time 1: "+time1);
			Thread.sleep(1230);
			long time2 = System.currentTimeMillis();
			System.out.println("Time 2: "+time2);
			System.out.println("Time Difference: "+ (time2-time1));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	public static void test4(){
		System.out.println(" : "+new Random().nextInt(1000));
		System.out.println(" : "+new Random().nextInt(1000));
		System.out.println(" : "+new Random().nextInt(1000));
		System.out.println(" : "+new Random().nextInt(1000));
		System.out.println(" : "+new Random().nextInt(1000));
	}
	
	public static void test3(){
		System.out.println("numOfPieces: "+(int)Math.ceil( 139/ (70*1.0)) );
	}
	
	public static void test2(){
		System.out.println(": "+ChunkRequester.class.getCanonicalName());
		System.out.println(": "+ChunkRequester.class.getName());
		System.out.println(": "+ChunkRequester.class.getSimpleName());
	}
	
	public void test(){
int arr[] = {1,2,3,4};
		
		ByteBuffer buffer = ByteBuffer.allocate(1000);
		
		
		
		buffer.order(ByteOrder.BIG_ENDIAN);
		int count = 0;
		
		for(int i=0 ; i<arr.length ; i++){
			buffer.putInt(arr[i]);
			count++;
		}
		
		byte byteArr[]  = new byte[count];
		
		byteArr = buffer.array();
		
		System.out.println("length : "+byteArr.length + " : "+byteArr[3]);
		
		buffer = ByteBuffer.wrap(byteArr);
		buffer.order(ByteOrder.BIG_ENDIAN);
		
		for(int i=0 ; i<arr.length ; i++){
			System.out.println(": "+buffer.getInt());
		}
	}
}
