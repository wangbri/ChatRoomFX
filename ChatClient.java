package assignment7;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

public class ChatClient {
//	private JTextArea incoming;
//	private JTextField outgoing;
	private BufferedReader reader;
	private PrintWriter writer;
	public Thread readerThread;
	public Object lock = new Object();
	public Object lock1 = new Object();
	public ObservableList<String> clientList = FXCollections.observableArrayList();
	
	
	public boolean isFinished = false;
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
		
		// writes output to socket from client -> server
		writer = new PrintWriter(sock.getOutputStream());	
		
		// one thread per client but is used to prevent blocking when waiting for server input
		readerThread = new Thread(new IncomingReader()); 
		//readerThread.start();
		
		System.out.println("networking established");
	}
	
	public void startThread(Thread t) {
		t.start();
	}

//	class SendButtonListener implements ActionListener {
//		public void actionPerformed(ActionEvent ev) {
//			writer.println(outgoing.getText());
//			writer.flush();
//			outgoing.setText("");
//			outgoing.requestFocus();
//		}
//	}
	
	/*
	 * Run task for client thread: "constantly checks for input from server and updates client"
	 */
	class IncomingReader implements Runnable {
		public void run() {
			Object message;
			
			try {
				while (true) {
					synchronized(clientList) {
						while ((message = reader.readLine()) != null) {
							if (message.equals("null")){
								
								System.out.println("finished: " + clientList);
								
//								synchronized(client.lock) {
//									client.lock.notify();
//								}
								isFinished = true;
								clientList.add((String) message);
								
								// wait until next client update
								synchronized(lock) {
									System.out.println("waiting in cc..");
									lock.wait();
								}
								
								isFinished = false;
								clientList.clear(); // clear existing list for new list update from server
								break;
							} else {
								System.out.println(message);
								clientList.add((String) message);
							}	
						}
					}
				}
			} catch (IOException | InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
}
