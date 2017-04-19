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
	public ObjectOutputStream writer;
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
		
		objectInput = new ObjectInputStream(sock.getInputStream());
		
		// writes output to socket from client -> server
//		writer = new PrintWriter(sock.getOutputStream());	
		writer = new ObjectOutputStream(sock.getOutputStream());
		
		// reads input from socket from server -> client
		clientReaderThread = new Thread(new ListUpdater(clientList));
		clientReaderThread.start();
		
		System.out.println("networking established");
	}
	
	public void addChat() {
		ClientCommand cm = new ClientCommand("startChat", null, null);
		writeCommand(cm);
	}
	
	public void joinChat(String chat) {
		client.showChatroom(client.chatStage);
		client.exitLobby();
		ClientCommand cm = new ClientCommand("joinChat " + chat, null, null);
		writeCommand(cm);
//		chatReaderThread.start();
	}
	
	public void sendMessage(String message) {
		client.showChatroom(client.chatStage);
		client.exitLobby();
		ClientCommand cm = new ClientCommand(null, message, null);
		writeCommand(cm);
	}
	
	public void joinPrivateMessage(String client) {
		ClientCommand cm = new ClientCommand("joinPrivateChat " + client, null, null);
		writeCommand(cm);
	}
	
	public void writeCommand(ClientCommand cm) {
		try {
			writer.writeObject(cm);
			writer.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class ListUpdater implements Runnable {
		ObservableList<String> list;
		
		public ListUpdater(ObservableList<String> list) {
			this.list = list;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			ClientCommand message = null;
			
			// TODO Auto-generated method stub
			while (true) {
				synchronized(clientList) {
					try {
						while ((message = (ClientCommand) objectInput.readObject()) != null) {
							System.out.println("from cc" + message);
							if (message.getList().get(0).contains("cClient") || message.getList().get(0).contains("cEmpty")) {
								client.updateChatClientList(message.getList());
							} else if (message.getList().get(0).contains("Chat")) {
								client.updateChatList(message.getList());
							} else if (message.getList().get(0).contains("Client") || message.getList().get(0).equals("Empty")) {
								client.updateClientList(message.getList());
							} else if (!message.getList().get(1).equals("")) {
								client.updateChatMessage(message.getMessage());
							}
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassCastException e) {
						System.out.println(message);
					}
				}
			}
		}
	}
}
