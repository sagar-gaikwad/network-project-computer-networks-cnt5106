	

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.test.ProcessStarter;

import edu.ufl.cise.cn.peer2peer.Controller;
import edu.ufl.cise.cn.peer2peer.utility.PropsReader;

public class EntryPoint {
	public static void main(String args[]){
		String peerID = args[0];
		String hostAddress = args[1];
		String portNumber = args[2];
		String isFileExists = args[3];
		
		
		Controller controller = Controller.getInstance(peerID);
		controller.startProcess();
		
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
