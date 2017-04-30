package assignment7;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
//import java.util.Observable;
//import java.util.Observer;
import java.net.Socket;
import java.util.ArrayList;

public class ClientObserver {
	private int clientNum;
	private String clientName;
	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	private Socket socket;
	private ObjectOutputStream obj;
	private ArrayList<ServerObservable> chat = new ArrayList<ServerObservable>();
	
	public ClientObserver(int clientNum, Socket socket, ObjectOutputStream obj, ServerObservable chat) {
		//super(out);
		this.clientNum = clientNum;
		this.socket = socket;
		this.obj = obj;
//		this.chat.add(chat);
	}
	
	public ArrayList<ServerObservable> getChatList(){
		return chat;
	}

	public ServerObservable getChat(String ChatName) {
		for(int i = 0; i <chat.size(); i++){
			if((chat.get(i).toString()).equals(ChatName)){
				return chat.get(i);
			}
		}
		return null;
	}

	public void setChat(ServerObservable chat) {
		this.chat.add(chat);
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getClientNum() {
		return clientNum;
	}

	public Socket getSocket() {
		return socket;
	}

	public void update(Object arg) {
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