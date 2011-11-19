	

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
}
