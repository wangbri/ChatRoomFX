package assignment7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.javafx.scene.control.skin.ListViewSkin;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class ClientMain extends Application {
	
	public ObservableList<String> clientList = FXCollections.observableArrayList();
	public ObservableList<String> chatList;
	public Object lock = new Object();
   
	public void start(Stage primaryStage) {
		FXMLLoader loader = null;
		
        try {
        	 loader = new FXMLLoader(getClass().getResource("ClientMainGUI.fxml"));
	         primaryStage.setScene(new Scene((BorderPane) loader.load()));
	         primaryStage.setTitle("ChatroomFX");
	         primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        ClientController controller = loader.<ClientController>getController();
        
        ChatClient client = new ChatClient(this);   
        
        
//        // initial update to client
//        System.out.println("initial update");
//        clientList = FXCollections.observableArrayList(client.clientList);
//        controller.updateLobby(clientList);
//		synchronized(client.lock) {
//			client.lock.notify();
//		}
        
        // handles subsequent updates to client
        client.clientList.addListener(new ListChangeListener<String>() {
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends String> c) {
				System.out.println("from cm: " + client.clientList);
				
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
//						synchronized(lock) {
//							try {
//								lock.wait();
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
						
						synchronized(clientList) {
							System.out.println("here");
							if (!client.clientList.isEmpty() && client.isFinished) {
								clientList = FXCollections.observableArrayList(client.clientList);
								clientList.remove(clientList.size() - 1);
								controller.updateLobby(clientList);
								System.out.println("listen: " + clientList);
								
								synchronized(client.lock) {
									client.lock.notify();
								}
							}
						}
					}
				});
			}
		});
        
        // wait until listener is implemented
        client.startThread(client.readerThread);
         
        System.out.println("start: " + clientList);
    }
}
