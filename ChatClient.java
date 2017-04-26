package assignment7;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ChatClient {
	public ObjectOutputStream writer;
	public ObjectInputStream objectInput;
	
	public Thread clientReaderThread;
	public Thread chatReaderThread;
	
	public ObservableList<String> clientList = FXCollections.observableArrayList();
	public ObservableList<String> chatList = FXCollections.observableArrayList();
	
	public boolean isChatFinished = false;
	public boolean isClientFinished = false;
	public boolean threadStopped = false;
	
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
//		Socket sock = new Socket("169.254.61.84", 4242);
		Socket sock = new Socket("localhost", 4242);
		
		objectInput = new ObjectInputStream(sock.getInputStream());
		
		// writes output to socket from client -> server
		writer = new ObjectOutputStream(sock.getOutputStream());
		
		// reads input from socket from server -> client
		clientReaderThread = new Thread(new ListUpdater(clientList));
		clientReaderThread.start();
		
		System.out.println("networking established");
	}
	
	public void addChat() {
		ChatPacket cm = new ChatPacket("startChat");
		writeCommand(cm);
	}
	
	public void joinChat(String chat) {
		client.showChatroom(client.chatStage, false);
		//client.exitLobby();
		ChatPacket cm = new ChatPacket("joinChat");
		cm.setMessage(chat);
		writeCommand(cm);
	}
	
	public void sendMessage(String message) {
		ChatPacket cm = new ChatPacket();
		cm.setMessage(message);
		writeCommand(cm);
	}
	
	public void joinPrivateChat(String pClient) {
		client.showChatroom(client.pChatStage, true);
		ChatPacket cm = new ChatPacket("joinPrivateChat");
		cm.setMessage(pClient);
		writeCommand(cm);
	}
	
	public void exitChat(String chat) {
		ChatPacket cm = new ChatPacket();
		cm.setCommand("exitChat");
		cm.setMessage(chat);
		writeCommand(cm);
		threadStopped = true;
	}

	public void writeCommand(ChatPacket cm) {
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

		@Override
		public void run() {
			ChatPacket message = null;
			
			// TODO Auto-generated method stub
			while (true) {
				synchronized(clientList) {
					try {
						while ((!threadStopped && (message = (ChatPacket) objectInput.readObject()) != null)) {
							System.out.println("from cc " + message.getMessage() + " " + message.getCommand() + " " +  message.getList());

							switch (message.getCommand()) {
								case "groupChat":
									client.updateChatMessage(message.getMessage());
									break;
								case "privateChat":
									client.updatePChatMessage(message.getMessage());
									break;
								case "joiningPrivateChat":
									client.joiningPrivateChat();
									break;
								case "updateGroupClients":
									client.updateChatClientList(message.getList());
									break;
								case "updatePrivateClients":
									client.updatePChatClientList(message.getList());
									break;
								case "updateLobbyChats":
									client.updateChatList(message.getList());
									break;
								case "updateLobbyClients":
									client.updateClientList(message.getList());
									break;
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
