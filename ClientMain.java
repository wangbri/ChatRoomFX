package assignment7;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//import com.mongodb.BasicDBObject;
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.DBObject;
//import com.mongodb.MongoClient;
//import com.mongodb.MongoClientURI;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//
//import org.bson.Document;
//import org.junit.Assert;

import javafx.application.Application;
import javafx.application.Platform;
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
	
	// for private chats
	public Stage pChatStage;
	public ObservableList<String> pChatClientList = FXCollections.observableArrayList();
	
	public FXMLLoader loaderChatroom;
	public ChatClient client;
	
	
//	public Object chatLock = new Object();
	public Object clientLock = new Object();
	public ClientLobbyController lobbyController;
	public ClientChatroomController chatController;
	public ClientChatroomController pChatController;
	
	public int clientNum = 0;
	
	
	boolean isShown = false;
	
	public static void main(String[] args) {
		launch(args);
		
//		MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://admin:pass@ds033066.mlab.com:33066/chatfx"));
////		MongoClient mongoClient = new MongoClient();
//		
//		MongoDatabase database = mongoClient.getDatabase("chatfx");
//		if (database.getCollection("mynewcollection") != null) {
//			database.getCollection("mynewcollection").drop();
//		}
//		
////		database.createCollection("testCollection");
//		MongoCollection collection = (MongoCollection) database.getCollection("mynewcollection");
//		
//		List<Integer> books = Arrays.asList(27464, 747854);
//		Document person = new Document("_id", "jo")
//		                            .append("name", "Jo Bloggs")
//		                            .append("address", new BasicDBObject("street", "123 Fake St")
//		                                                         .append("city", "Faketon")
//		                                                         .append("state", "MA")
//		                                                         .append("zip", 12345))
//		                            .append("books", books);
//		
//		
//		collection.insertOne(person);
//		mongoClient.close();
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
		if (list.get(0).equals("")) {
			list.clear();
		}

		chatClientList = FXCollections.observableArrayList(list);
		System.out.println(chatController);
		chatController.updateClientList(chatClientList);
	}
	
	public void updateChatMessage(String message) {
		chatController.updateChat(message);
	}
	
	
	// called by ChatClient (private)
	public void updatePChatClientList(ArrayList<String> list) {		
		if (list.get(0).equals("")) {
			list.clear();
		}
		
		if (!isShown) {
			synchronized(clientLock) {
				try {
					clientLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		pChatClientList = FXCollections.observableArrayList(list);
		pChatController.updateClientList(pChatClientList);
	}
	
	public void updatePChatMessage(String message) {
		pChatController.updateChat(message);
	}
	
	public void joiningPrivateChat() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				showChatroom(pChatStage, true);
			}
			
		});	
	}
	
	public void showChatroom(Stage chatStage, boolean isPrivate) {
		try {
       	 	 loaderChatroom = new FXMLLoader(getClass().getResource("ClientChatroom.fxml"));
	         chatStage.setScene(new Scene((BorderPane) loaderChatroom.load()));
		} catch (Exception ex) {
          Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
		}
	
		if (isPrivate) {
			pChatController = loaderChatroom.<ClientChatroomController>getController();
			pChatController.setClient(client);
			chatStage.setTitle("Private Chatroom");
		} else {
			chatController = loaderChatroom.<ClientChatroomController>getController();
			chatController.setClient(client);
			chatStage.setTitle("Group Chatroom");
		}
		
		chatStage.show();
		
		synchronized(clientLock) {
			clientLock.notify();
		}

		isShown = true;
	}
	
	public void exitLobby() {
		lobbyStage.close();
	}
	
	public void start(Stage primaryStage) {
		FXMLLoader loaderLobby = null;
		
		lobbyStage = new Stage();
		chatStage = new Stage();
		pChatStage = new Stage();
		
        try {
        	 loaderLobby = new FXMLLoader(getClass().getResource("ClientLobby.fxml"));
	         lobbyStage.setScene(new Scene((BorderPane) loaderLobby.load()));
	         lobbyStage.setTitle("Lobby");
	         lobbyStage.show();
        } catch (Exception ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        client = new ChatClient(this);  
        
        lobbyController = loaderLobby.<ClientLobbyController>getController();
        lobbyController.setClient(client);
        
        
        
        
         
        System.out.println("end");
    }
	

}
