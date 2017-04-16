package assignment7;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Observer;

public class ClientObserver {
	public ClientObserver(){
		
	}
//	public ClientObserver(OutputStream out) {
//		super(out);
//	}

	
	public void update(String arg) {
		System.out.println(arg);
	}

}