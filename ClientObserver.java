package assignment7;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
//import java.util.Observable;
//import java.util.Observer;
import java.net.Socket;

public class ClientObserver {
	private int clientNum;
	private Socket client;
	private ObjectOutputStream obj;
	
	public ClientObserver(int clientNum, Socket client, ObjectOutputStream obj) {
		//super(out);
		this.clientNum = clientNum;
		this.client = client;
		this.obj = obj;
	}

	public int getClientNum() {
		return clientNum;
	}

	public Socket getClient() {
		return client;
	}

	public void update(ServerObservable o, Object arg) {
		try {
			obj.writeObject(arg);
			obj.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString() {
		return "Client " + clientNum;
	}
}