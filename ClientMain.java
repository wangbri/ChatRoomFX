package assignment7;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
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
//	public ClientChatroomController chatController;
//	public ClientChatroomController privateChatController;
	
	public HashMap<String, ClientChatroomController> groupControllerList = new HashMap<String, ClientChatroomController>();
	public HashMap<String, ClientChatroomController> privateControllerList = new HashMap<String, ClientChatroomController>();
	
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
	
	// FOR LOBBY CONTROLLER
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
	
	// FOR GROUP CHATS
	public void updateChatClients(String chat, ArrayList<String> list) {		
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
		
		
		System.out.println("ADSFASDFA" + groupControllerList.get(chat) + chat);
		groupControllerList.get(chat).updateClientList(FXCollections.observableArrayList(list));
	}
	
	public void updateChat(String chat, String message) {
		groupControllerList.get(chat).updateChat(message);
	}
	
	
	// FOR PRIVATE CHATS
	public void updatePrivateChatClients(String chat, ArrayList<String> list) {		
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
		privateControllerList.get(chat).updateClientList(pChatClientList);
	}
	
	public void updatePrivateChat(String chat, String message) {
		privateControllerList.get(chat).updateChat(message);
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
		isShown = false; //temporary 'hacky' solution, not very thread-safe
		
		
		
//		try {
//       	 	 loaderChatroom = new FXMLLoader(getClass().getResource("ClientChatroom.fxml"));
//	         chatStage.setScene(new Scene((TabPane) loaderChatroom.load()));
//		} catch (Exception ex) {
//          Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
//		}
		
		Tab chatTab = new Tab();
        chatTab.setText(chatName);
        
        chatTab.setOnClosed(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				// TODO Auto-generated method stub
				System.out.println("asfasdf");
				client.exitChat(chatName);
			}
        	
        });
        
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
			ClientChatroomController privateChatController = null;
			privateChatController = loaderChat.<ClientChatroomController>getController();
			privateChatController.setName(chatName);
			privateChatController.setClient(client);
			System.out.println("private" + privateChatController.getClass().toString());
			
			privateControllerList.put(chatName, privateChatController);
		} else {
			ClientChatroomController chatController = null;
			chatController = loaderChat.<ClientChatroomController>getController();
			chatController.setName(chatName);
			System.out.println("group" + chatController.getClass().toString());
			chatController.setClient(client);
			
			groupControllerList.put(chatName,  chatController);
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
        
        panes.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
        
        Tab lobbyTab = new Tab();
        lobbyTab.setClosable(false);
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
