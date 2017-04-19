package assignment7;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChatClient {
//	private JTextArea incoming;
//	private JTextField outgoing;
	private BufferedReader reader;
	public PrintWriter writer;
	public Thread clientReaderThread;
	public Thread chatReaderThread;
	public Object clientLock = new Object();
	public Object chatLock = new Object();
	public ObservableList<String> clientList = FXCollections.observableArrayList();
	public ObservableList<String> chatList = FXCollections.observableArrayList();
	
	public ObjectInputStream objectInput;
	
	public boolean isChatFinished = false;
	public boolean isClientFinished = false;
	public ClientMain client;
	
	public ChatClient(ClientMain client) {
		this.client = client;
		try {
			this.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() throws Exception {
		setUpNetworking();
	}

	@SuppressWarnings("resource")
	private void setUpNetworking() throws Exception {		
		// establish connection with server
		Socket sock = new Socket("127.0.0.1", 4242);
		
		// reads input from socket from server -> client
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());	
		reader = new BufferedReader(streamReader);
		
		objectInput = new ObjectInputStream(sock.getInputStream());
		
		// writes output to socket from client -> server
		writer = new PrintWriter(sock.getOutputStream());	
		
		clientReaderThread = new Thread(new ListUpdater(clientList));
		clientReaderThread.start();
		
		System.out.println("networking established");
	}
	
	public void addChat() {
		writer.println("startChat");
		writer.flush();
	}
	
	public void joinChat(String chat) {
		client.showChatroom(client.chatStage);
		client.exitLobby();
		writer.println("joinChat " + chat);
		writer.flush();
	}
	
	public void sendMessage(String message) {
		writer.println(message);
		writer.flush();
	}
	
	class ListUpdater implements Runnable {
		ObservableList<String> list;
		
		public ListUpdater(ObservableList<String> list) {
			this.list = list;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			ArrayList<String> message;
			
			// TODO Auto-generated method stub
			while (true) {
				synchronized(clientList) {
					try {
						while ((message = (ArrayList<String>) objectInput.readObject()) != null) {
							System.out.println("from cc" + message);
							if (message.get(0).contains("cClient") || message.get(0).contains("cEmpty")) {
								client.updateChatClientList(message);
							} else if (message.get(0).contains("Chat")) {
								client.updateChatList(message);
							} else if (message.get(0).contains("Client") || message.get(0).contains("Empty")) {
								client.updateClientList(message);
							}
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/*
	 * Run task for client thread: "constantly checks for input from server and updates client"
	 */
	class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					client.updateChatMessage(message);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
