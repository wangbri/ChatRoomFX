package assignment7;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class ClientMain extends Application {
	
	// for lobby
	public Stage lobbyStage;
	public ObservableList<String> clientList = FXCollections.observableArrayList();
	public ObservableList<String> chatList = FXCollections.observableArrayList();
	
	// for chat
	public Stage chatStage;
	public ObservableList<String> chatClientList = FXCollections.observableArrayList();
	
	public FXMLLoader loaderChatroom;
	public ChatClient client;
	
	
//	public Object chatLock = new Object();
//	public Object clientLock = new Object();
	public ClientLobbyController lobbyController;
	public ClientChatroomController chatController;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	// called by ChatClient
	public void updateClientList(ArrayList<String> list) {
		if (list.get(0).contains("Empty")) {
			list.clear();
		}
		
		clientList = FXCollections.observableArrayList(list);
		lobbyController.updateLobbyClients(clientList);
	}

	public void updateChatList(ArrayList<String> list) {	
		chatList = FXCollections.observableArrayList(list);
		lobbyController.updateLobbyChats(chatList);
	}
	
	public void updateChatClientList(ArrayList<String> list) {		
		if (list.get(0).contains("cEmpty")) {
			list.clear();
		}
		
		for (int i = 0; i < list.size(); i++) {
			list.set(i, list.get(i).substring(1, list.get(i).length()));
		}
		
		chatClientList = FXCollections.observableArrayList(list);
		chatController.updateClientList(chatClientList);
	}

	public void showChatroom(Stage chatStage) {
		try {
       	 	 loaderChatroom = new FXMLLoader(getClass().getResource("ClientChatroom.fxml"));
	         chatStage.setScene(new Scene((BorderPane) loaderChatroom.load()));
	         chatStage.setTitle("Chatroom");
	         chatStage.show();
       } catch (Exception ex) {
          Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
       }
		
		chatController = loaderChatroom.<ClientChatroomController>getController();
        chatController.setClient(client);
	}
	
	public void exitLobby() {
		lobbyStage.close();
	}
	
	public void start(Stage primaryStage) {
		FXMLLoader loaderLobby = null;
		
		lobbyStage = new Stage();
		chatStage = new Stage();
		
        try {
        	 loaderLobby = new FXMLLoader(getClass().getResource("ClientLobby.fxml"));
	         lobbyStage.setScene(new Scene((BorderPane) loaderLobby.load()));
	         lobbyStage.setTitle("Lobby");
	         lobbyStage.show();
        } catch (Exception ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
//        this.showChatroom(chatStage);
        
        client = new ChatClient(this);  
        
        lobbyController = loaderLobby.<ClientLobbyController>getController();
        lobbyController.setClient(client);
//        client.clientReaderThread.start();
        
        
         
        System.out.println("end");
    }
	

}
