package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.Observable;
//import java.util.Observer;

//TODO: change name
public class ServerMain  {
	
	//keeps track of which clients are involved in which chat
	//TODO: migrate client lists to ServerObservable class
	HashMap<ServerObservable, ArrayList<ClientObserver>> events = new HashMap<ServerObservable, ArrayList<ClientObserver>>();
	ArrayList<ClientObserver> clientList = new ArrayList<ClientObserver>();
	ServerObservable chatLobby;
	static int clientNum;
	int serverNum;
	
	
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
			
			// when there are no more clients left in the chat
			if (!events.get(chat).isEmpty()) {
				events.get(chat).remove(index);
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
		//TODO: change
//		chat.clearChange();
	}

	@SuppressWarnings("resource")
	private void setUpNetworking() throws Exception {
		// initial port for clients to connect
		serverNum = 0;
		ServerSocket serverSock = new ServerSocket(4242);
		
		// create lobby for chat
		chatLobby = new ServerObservable(serverNum);
		ArrayList<ClientObserver> clientsLobby = new ArrayList<ClientObserver>();
		events.put(chatLobby, clientsLobby);
	
		System.out.println(events);
		
		// constantly accepts clients and adds to observers list
		while (true) {
			// assign client to their own socket
			Socket clientSocket = serverSock.accept();

			ObjectOutputStream obj = new ObjectOutputStream(clientSocket.getOutputStream());
			
			// writes output to socket from server -> client
			ClientObserver writer = new ClientObserver(++clientNum, clientSocket, obj);
			
			clientList.add(writer);
			registerObserver(chatLobby, writer);		
			updateServerChats(chatLobby);
			
			// create thread to handle (read) each client's "startChat button press"
			Thread t = new Thread(new ChatListHandler(clientSocket, writer));
			t.start();
						
			System.out.println("got a connection");
		}
	}
	
	
	
	public void updateServerClients(ServerObservable chat) {
		ArrayList<String> clients = new ArrayList<String>();
		
		for (int i = 0; i < events.get(chat).size(); i++) {
			if (chat == chatLobby) {
				clients.add(events.get(chat).get(i).toString());
			} else {
				clients.add("c" + events.get(chat).get(i).toString()); // if not lobby, add "cClient"
			}
		}
		
		if (clients.isEmpty()) {
			if (chat == chatLobby) {
				clients.add("Empty");
			} else { // temporary solution, need to consider cases when client enters more than one chat at the same time
				clients.add("cEmpty");
			}
		}
		
		System.out.println("at update cc " + clients);
		System.out.println(events);
		
		if (chat == chatLobby) {
			for (ClientObserver w : clientList) {
				w.update(chat, clients);
			}
		} else {
			for (ClientObserver w : events.get(chat)) {
				w.update(chat, clients);
			}
		}	
	}
	
	public void updateServerChats(ServerObservable chat) {
		ArrayList<String> chats = new ArrayList<String>();
		
		for (ServerObservable s : events.keySet()) {
			chats.add(s.toString());
		}
		
		chats.remove(chats.indexOf("Chat 0")); // don't display lobby in lobby chat list
		System.out.println(chats);
		
		if (!chats.isEmpty()) {
			for (ClientObserver w : events.get(chatLobby)) {
				w.update(chat, chats);
			}
		}
	}
	
	// reads data from client
	//TODO: rename class and add more for reading chat, lists, buttons
	class ChatListHandler implements Runnable {
		private BufferedReader reader;
		private ClientObserver client;

		public ChatListHandler(Socket clientSocket, ClientObserver client) {
			Socket sock = clientSocket;
			this.client = client;
			
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
					if (message.equals("startChat") && events.get(chatLobby).contains(client)) {					
						ServerObservable chat = new ServerObservable(++serverNum);
						ArrayList<ClientObserver> clients = new ArrayList<ClientObserver>();
						
//						clients.add(client);
						events.put(chat, clients);
						updateServerChats(chat);
						System.out.println("added chat");
					}
					
					if (message.substring(0, 9).equals("joinChat ") && events.get(chatLobby).contains(client)) {
						ServerObservable chat = null;
						
						for (ServerObservable s : events.keySet()) {
							if (s.toString().equals(message.substring(9, message.length()))) {
								chat = s;
								break;
							}
						}
						
						unregisterObserver(chatLobby, client);
						System.out.println("after unregister " + events);
						registerObserver(chat, client);
						updateServerClients(chatLobby);
						System.out.println(events);
						
						Thread t = new Thread(new ChatHandler(chat, client));
						System.out.println("added client to chat");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//TODO: changed
	//TODO: find a way to find out who sent the message
	class ChatHandler implements Runnable{
		private ArrayList<ClientObserver> clients;
		private ServerObservable chat;
		private BufferedReader reader;
		
		ChatHandler(ServerObservable chat, ClientObserver messageClient){
			clients = events.get(chat);
			Socket sock = messageClient.getClient();
			try {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String message;
			try{
				while((message = reader.readLine()) != null){
					System.out.println("Group message sent: " + message);
//					chat.setChange();
					notifyObservers(chat, message);
				}
			}catch(IOException e){}
			
		}
		
	}
}
