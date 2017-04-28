package assignment7;

import java.io.IOException;
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
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


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
	public ClientChatroomController privateChatController;
	
	public TabPane panes;
	
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
		if (list.get(0).equals("")) {
			list.clear();
		}
		
		System.out.println(list);
		clientList = FXCollections.observableArrayList(list);
		lobbyController.updateLobbyClients(clientList);
	}

	public void updateChatList(ArrayList<String> list) {	
		chatList = FXCollections.observableArrayList(list);
		lobbyController.updateLobbyChats(chatList);
	}
	
	public void updateChatClients(ArrayList<String> list) {		
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
		
		
		System.out.println(list);
		chatClientList = FXCollections.observableArrayList(list);
		System.out.println("Asdfadsf" + chatController);
		chatController.updateClientList(chatClientList);
	}
	
	public void updateChat(String message) {
		chatController.updateChat(message);
	}
	
	
	// called by ChatClient (private)
	public void updatePrivateChatClients(ArrayList<String> list) {		
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
		privateChatController.updateClientList(pChatClientList);
	}
	
	public void updatePrivateChat(String message) {
		privateChatController.updateChat(message);
	}
	
	public void joiningPrivateChat(String chatName) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				showChatroom(chatName, true);
			}
			
		});	
	}
	
	public void joiningGroupChat(String chatName) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				showChatroom(chatName, false);
			}
			
		});	
	}
	
	public void showChatroom(String chatName, boolean isPrivate) {
		System.out.println("CALLED HERE");
//		try {
//       	 	 loaderChatroom = new FXMLLoader(getClass().getResource("ClientChatroom.fxml"));
//	         chatStage.setScene(new Scene((TabPane) loaderChatroom.load()));
//		} catch (Exception ex) {
//          Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
//		}
		
		Tab chatTab = new Tab();
        chatTab.setText("Chat");
        
        FXMLLoader loaderChat = null;
        
        try {
        	loaderChat = new FXMLLoader(getClass().getResource("ClientChatroom.fxml"));
			chatTab.setContent((BorderPane)loaderChat.load());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        panes.getTabs().add(chatTab);
	
		if (isPrivate) {
			privateChatController = loaderChat.<ClientChatroomController>getController();
			privateChatController.setName(chatName);
			privateChatController.setClient(client);
			System.out.println("private" + privateChatController.getClass().toString());
		} else {

			chatController = loaderChat.<ClientChatroomController>getController();
			chatController.setName(chatName);
			System.out.println("group" + chatController.getClass().toString());
			chatController.setClient(client);
		}
		
//		chatStage.show();
		
		synchronized(clientLock) {
			clientLock.notify();
		}

		isShown = true;
	}
	
	public void exitLobby() {
		lobbyStage.close();
	}
	
	public void start(Stage primaryStage) {
		FXMLLoader loaderPanes = null;
		FXMLLoader loaderLobby = null;
		
		lobbyStage = new Stage();
		chatStage = new Stage();
		pChatStage = new Stage();
		
		lobbyStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				// TODO Auto-generated method stub
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						System.out.println("CLOSING CHAT");
						client.exitChat("Chat 0");
					}	
				});
			}
		});
		
        try {
        	 loaderPanes = new FXMLLoader(getClass().getResource("ChatPanes.fxml"));
        	 panes = loaderPanes.load();
	         lobbyStage.setScene(new Scene(panes));
	         lobbyStage.setTitle("ChatRoomFX");
	         lobbyStage.show();
        } catch (Exception ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Tab lobbyTab = new Tab();
        lobbyTab.setText("Lobby");
        
        try {
        	loaderLobby = new FXMLLoader(getClass().getResource("ClientLobby.fxml"));
			lobbyTab.setContent((BorderPane)loaderLobby.load());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        panes.getTabs().add(lobbyTab);
        
        client = new ChatClient(this);  
        
        lobbyController = loaderLobby.<ClientLobbyController>getController();
        lobbyController.setClient(client);
	}
}
