package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

//TODO: change name
public class ServerMain extends Observable {
	
	//keeps track of which clients are involved in which chat
	//TODO: migrate client lists to ServerObservable class
	HashMap<ServerObservable, ArrayList<ClientObserver>> events = new HashMap<ServerObservable, ArrayList<ClientObserver>>();
	
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
	 * @param client: the client that wants to chat
	 */
	public void registerObserver(ServerObservable chat, ClientObserver client){
		//if chat already exists, just add the people to it
		if(events.containsKey(chat)){
			events.get(chat).add(client);
		} else{
			ArrayList<ClientObserver> newClients = new ArrayList<ClientObserver>();
			newClients.add(client);
			
			ServerObservable newChat = new ServerObservable();
			events.put(newChat, newClients);
		}
		
		updateServerClients(chat);
	}
	
	/**
	 * Remove client from list if they want to leave the chat
	 * @param chat: the chat they are in
	 * @param client: the client that wants to leave
	 */
	public void unregisterObserver(ServerObservable chat, ClientObserver client){
		if(events.containsKey(chat)){	
			int index = events.get(chat).indexOf(client);
			
			//when there are no more clients left in the chat
			events.get(chat).remove(index);
			
			try {
				events.get(chat).get(index).getClient().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(events.get(chat).size() == 0){
				events.remove(chat);
				//TODO: maybe close the port?
			}
		}
		
	}
	
	/**
	 * Notify when something changes in the specific chat
	 * @param chat: the chat that changed
	 */
	public void notifyObservers(ServerObservable chat, Object data){
		ArrayList<ClientObserver> clients = events.get(chat);
		
		for(int i = 0; i < clients.size(); i++){
			clients.get(i).update(chat, data);
		}
	}

	@SuppressWarnings("resource")
	private void setUpNetworking() throws Exception {
		// initial port for clients to connect
		ServerSocket serverSock = new ServerSocket(4242);
		ServerObservable chatLobby = new ServerObservable();
		ArrayList<ClientObserver> clientsLobby = new ArrayList<ClientObserver>();
		
		events.put(chatLobby, clientsLobby);
		
		// constantly accepts clients and adds to observers list
		while (true) {
			// assign client to their own socket
			Socket clientSocket = serverSock.accept();
			
			// writes output to socket from server -> client
			ClientObserver writer = new ClientObserver(events.get(chatLobby).size(), clientSocket, clientSocket.getOutputStream());
			
			// create thread to handle (read) each client
//			Thread t = new Thread(new ClientHandler(clientSocket));
//			t.start();
			
			registerObserver(chatLobby, writer);
			
//			if (events.get(chatLobby).size() == 2) {
//				unregisterObserver(chatLobby, writer);
//			}
			
			System.out.println("got a connection");
		}
	}
	
	public void updateServerClients(ServerObservable chat) {
		for (ClientObserver w : events.get(chat)) {
			for (int i = 0; i < events.get(chat).size(); i++) {
				w.update(chat, events.get(chat).get(i).toString());
			}
			w.update(chat, null);
		}
	}
	
	// reads data from client
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
					System.out.println("server read " + message);
					
					setChanged();
					notifyObservers(message);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
