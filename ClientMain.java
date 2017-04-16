package assignment7;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javafx.application.Application;

public class ClientMain {
	private JTextArea incoming;
	private JTextField outgoing;
	private BufferedReader reader;
	private PrintWriter writer;
	
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
		Thread readerThread = new Thread(new IncomingReader()); 
		readerThread.start();
		
		System.out.println("networking established");
	}

//	class SendButtonListener implements ActionListener {
//		public void actionPerformed(ActionEvent ev) {
//			writer.println(outgoing.getText());
//			writer.flush();
//			outgoing.setText("");
//			outgoing.requestFocus();
//		}
//	}

	public static void main(String[] args) {
		Application.launch(ClientMainGUI.class, args);
		try {
			new ClientMain().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Run task for client thread: constantly checks for input from server and updates client
	 */
	class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					incoming.append(message + "\n");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
