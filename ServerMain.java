package assignment7;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

//TODO: change name
public class ServerMain  {
	
	//keeps track of which clients are involved in which chat
	//TODO: migrate client lists to ServerObservable class
	HashMap<ServerObservable, ArrayList<ClientObserver>> events = new HashMap<ServerObservable, ArrayList<ClientObserver>>();
	ArrayList<ClientObserver> clientList = new ArrayList<ClientObserver>();
	ServerObservable chatLobby;
	Object lock = new Object();
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
			client.setChat(chat);
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
			
			// if there are still clients left in the chat
			if (!events.get(chat).isEmpty()) {
				events.get(chat).remove(index);
			}
		}	
	}
	
	/**
	 * Notify when something changes in the specific chat
	 * @param chat: the chat that changed
	 */
	public void notifyObservers(ServerObservable chat, ChatPacket data){
		ArrayList<ClientObserver> clients = events.get(chat);
		
		for(int i = 0; i < clients.size(); i++){
			clients.get(i).update(chat, data);
		}
	}

	@SuppressWarnings("resource")
	private void setUpNetworking() throws Exception {
		// initial port for clients to connect
		serverNum = 0;
//		String addr = "169.254.61.84"; // router: 192.168.184.1
		String addr = "localhost"; //name of the ip address
		
		InetAddress ip = InetAddress.getByName(addr);
		System.out.println(ip.getHostName());
		ServerSocket serverSock = new ServerSocket(4242, 10, ip); //60784
		
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
			ClientObserver writer = new ClientObserver(++clientNum, clientSocket, obj, chatLobby);
			
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
			} else if (chat.isPrivate) {
				clients.add("p" + events.get(chat).get(i).toString()); // if not lobby, add "cClient"
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
		
		ChatPacket cmd = new ChatPacket(null, null, clients);
		
		System.out.println("at update cc " + clients);
		System.out.println(events);
		
		//if chatlobby, then update with all the clients
		if (chat == chatLobby) {
			for (ClientObserver w : clientList) {
				w.update(chat, cmd);
			}
		//else update it with clients in the chat
		} else {
			for (ClientObserver w : events.get(chat)) {
				w.update(chat, cmd);
			}
		}	
	}
	
	public void updateServerChats(ServerObservable chat) {
		ArrayList<String> chats = new ArrayList<String>();
		
		//look through the list of the chats and update the list in the lobby
		for (ServerObservable s : events.keySet()) {
			if (!s.isPrivate) {
				chats.add(s.toString());
			}
		}
		
		chats.remove(chats.indexOf("Chat 0")); // don't display lobby in lobby chat list
		
		ChatPacket cmd = new ChatPacket(null, null, chats);
		
		if (!chats.isEmpty()) {
			for (ClientObserver w : events.get(chatLobby)) {
				w.update(chat, cmd);
			}
		}
	}
	
	// reads data from client
	//TODO: rename class and add more for reading chat, lists, buttons
	class ChatListHandler implements Runnable {
		private ObjectInputStream objectInput;
		private ClientObserver client;

		public ChatListHandler(Socket clientSocket, ClientObserver client) {
			Socket sock = clientSocket;
			this.client = client;
			
			try {
				objectInput = new ObjectInputStream(sock.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			ChatPacket message;
			
			try {
				while ((message = (ChatPacket)objectInput.readObject()) != null) {
					//if the data is a message being transmitted
					if(message.getCommand() == null){
						//determine the nature of the chat
						if (!client.getChat().isPrivate) {
							message.setCommand("groupChat");
						} else {
							message.setCommand("privateChat");
						}
						
						System.out.println("Group message sent: " + message.getMessage());
						notifyObservers(this.client.getChat(), message);

					//starting a chat
					} else if (message.getCommand().equals("startChat") && events.get(chatLobby).contains(client)) {					
						ServerObservable chat = new ServerObservable(++serverNum);
						ArrayList<ClientObserver> clients = new ArrayList<ClientObserver>();
						
						events.put(chat, clients);
						updateServerChats(chat);
						System.out.println("added chat");

					//joining a group chat
					} else if (message.getCommand().contains("joinChat ") && events.get(chatLobby).contains(client)) {
						ServerObservable chat = null;
						
						//look for the chat in the events HashMap 
						for (ServerObservable s : events.keySet()) {
							if (s.toString().equals(message.getCommand().substring(9, message.getCommand().length()))) {
								chat = s;
								break;
							}
						}
						
						//TODO: take out this line to allow multiple chats for one client 
						unregisterObserver(chatLobby, client);
						registerObserver(chat, client);
						updateServerClients(chatLobby);
						System.out.println(events);
						
					//joining a private messaging 
					} else if(message.getCommand().contains("joinPrivateChat ")){
						boolean exist = false;
						int index = 0;

						//look for the cliient that they wnat to talk to
						for(int i = 0; i < clientList.size(); i++){
							if(clientList.get(i).toString().contains(message.getCommand().substring(16, message.getCommand().length()))){
								exist = true;
								index = i;
								break;
							}
						}
						

						//create a private chat for them if they exist
						if(exist){
							ChatPacket cm = new ChatPacket("joiningPrivateChat", null, null);
							clientList.get(index).update(null, cm);
							ServerObservable chat = new ServerObservable(++serverNum);
							chat.isPrivate = true;
							ArrayList<ClientObserver> clients = new ArrayList<ClientObserver>();
							
							clients.add(this.client);
							clients.add(clientList.get(index));
							clientList.get(index).setChat(chat);
							this.client.setChat(chat);
							
							System.out.println(chat.toString() + this.client.toString());
							events.put(chat, clients);
							updateServerClients(chat);
//							registerObserver(chat, this.client);
//							registerObserver(chat, clientList.get(index));
						}
					}	
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
