package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

//TODO: change name
public class ServerMain extends Observable{
	//keeps track of which clients are involved in which chat
	HashMap<String, ArrayList<ClientMain>> events = new HashMap<String, ArrayList<ClientMain>>();
	
	public static void main(String[] args) {
		try {
			//TODO: change name 
			new ServerMain().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds the chat to the HashMap
	 * @param event: the type of chat initiated
	 * @param clients: an ArrayList of all the clients involved in this chat
	 */
	public void registerObserver(String chat, ClientMain clients){
		//if chat already exists, just add the people to it
		if(events.containsKey(chat)){
				events.get(chat).add(clients);
		}
		else{
			ArrayList<ClientMain> newClients = new ArrayList<ClientMain>();
			newClients.add(clients);
			events.put(chat, newClients);
		}
	}
	
	/**
	 * Remove client from list if they want to leave the chat
	 * @param chat: the chat they are in
	 * @param clients: the client that wants to leave
	 */
	public void unregisterObserver(String chat, String clients){
		if(events.containsKey(chat)){	
			 //when there are no more clients left in the chat
			if(events.get(chat).size() == 2){
				events.remove(chat);
				//TODO: maybe close the port?
			}
			else{
				int index = events.get(chat).indexOf(clients);
				events.get(chat).remove(index);
			}
		}
	}
	
	/**
	 * Notify when something changes in the specific chat
	 * @param chat: the chat that changed
	 */
	public void notifyObservers(String chat, String message){
		ArrayList<ClientMain> clients = events.get(chat);
		for(int i = 0; i < clients.size(); i++){
			(clients.get(i)).update(message);
		}
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4242);
		while (true) {
			Socket clientSocket = serverSock.accept();
//			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
//			this.addObserver(writer);
			//System.out.println("got a connection");
		}
	}
	class ClientHandler implements Runnable {
		private BufferedReader reader;

		public ClientHandler(Socket clientSocket) {
			Socket sock = clientSocket;
			try {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					//System.out.println("server read "+message);
					setChanged();
					notifyObservers(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
