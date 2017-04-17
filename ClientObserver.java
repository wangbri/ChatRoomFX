package assignment7;

import java.io.OutputStream;
import java.io.PrintWriter;
//import java.util.Observable;
//import java.util.Observer;
import java.net.Socket;

public class ClientObserver extends PrintWriter{
	private int clientNum = 0;
	private Socket client;
	
	public ClientObserver(int clientNum, Socket client, OutputStream out) {
		super(out);
		this.clientNum = clientNum;
		this.client = client;
	}

	public int getClientNum() {
		return clientNum;
	}

	public Socket getClient() {
		return client;
	}

	public void update(ServerObservable o, Object arg) {
		this.println(arg); //writer.println(arg);
		this.flush(); //writer.flush();
	}
	
	public String toString() {
		return "Client " + clientNum;
	}
}