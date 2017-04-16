package assignment7;

import java.io.OutputStream;
import java.io.PrintWriter;
//import java.util.Observable;
//import java.util.Observer;

public class ClientObserver extends PrintWriter{
	public ClientObserver(OutputStream out) {
		super(out);
	}

	public void update(ServerObservable o, Object arg) {
		this.println(arg); //writer.println(arg);
		this.flush(); //writer.flush();
	}

}