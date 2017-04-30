package assignment7;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

//import com.mongodb.BasicDBObject;
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;



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
import javafx.scene.layout.Pane;
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
	public Pane loginPane;
	
	
//	public Object chatLock = new Object();
	public Object clientLock = new Object();
	public ClientLobbyController lobbyController;
//	public ClientChatroomController chatController;
//	public ClientChatroomController privateChatController;
	
	public HashMap<String, ClientChatroomController> groupControllerList = new HashMap<String, ClientChatroomController>();
	public HashMap<String, ClientChatroomController> privateControllerList = new HashMap<String, ClientChatroomController>();
	
	public Stage loginStage;
	public ChatLoginController loginController;
	
	public TabPane panes;
	
	public static MongoClient mongoClient;
	public MongoDatabase db;
	public MongoCollection<Document> col;
	public Document doc;
	
	public String username;
	public String password;
	public Thread loginWait;
	
	boolean isShown = false;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void initLobby() {
		FXMLLoader loaderPanes = null;
		FXMLLoader loaderLobby = null;
		
		lobbyStage = new Stage();
		chatStage = new Stage();
		
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
	
	public void joiningPrivateChat(String chatName, String otherClient) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				showChatroom(chatName, otherClient, true);
			}
			
		});	
	}
	
	public void exitPrivateChat(String clientName) {
		ObservableList<Tab> paneList= panes.getTabs();
		
		System.out.println(clientName);
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < paneList.size(); i++) {
					System.out.println(paneList.get(i).getText());
					if (paneList.get(i).getText().equals(clientName)) {
						System.out.println("CLOSED TAB");
						panes.getTabs().remove(paneList.get(i));
					}
				}
			}
		});		
	}
	
	public void joiningGroupChat(String chatName) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				showChatroom(chatName, null, false);
			}
			
		});	
	}
	
	public void showChatroom(String chatName, String otherClient, boolean isPrivate) {
		System.out.println("CALLED HERE");
		isShown = false; //temporary 'hacky' solution, not very thread-safe
		
		
		
//		try {
//       	 	 loaderChatroom = new FXMLLoader(getClass().getResource("ClientChatroom.fxml"));
//	         chatStage.setScene(new Scene((TabPane) loaderChatroom.load()));
//		} catch (Exception ex) {
//          Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
//		}
		
		Tab chatTab = new Tab();
        
        
        
        if (isPrivate) {
        	chatTab.setOnClosed(new EventHandler<Event>() {

    			@Override
    			public void handle(Event arg0) {
    				// TODO Auto-generated method stub
    				System.out.println("EXITING PRIVATE CHAT");
					client.exitPrivateChat(otherClient);
    			}
            	
            });
        } else {
        	chatTab.setOnClosed(new EventHandler<Event>() {

    			@Override
    			public void handle(Event arg0) {
    				// TODO Auto-generated method stub
					client.exitChat(chatName);
    			}
            	
            });
        }
        
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
			
			chatTab.setText(otherClient);
			
			privateControllerList.put(chatName, privateChatController);
		} else {
			ClientChatroomController chatController = null;
			chatController = loaderChat.<ClientChatroomController>getController();
			chatController.setName(chatName);
			System.out.println("group" + chatController.getClass().toString());
			chatController.setClient(client);
			
			chatTab.setText(chatName);
			
			groupControllerList.put(chatName,  chatController);
		}
		
//		chatStage.show();
		
		synchronized(clientLock) {
			clientLock.notify();
		}

		isShown = true;
	}
	
	public void exitLobby() {
		try {
			this.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.exit(0);
	}
	
	public void setCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	@SuppressWarnings("deprecation")
	public void createAccount(String username, String password) {
		loginWait.stop();
		setCredentials(username, password);
		
		doc = new Document("_username", username)
						.append("_password", password);
		col.insertOne(doc);
		System.out.println("CREATED NEW USER");
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				initLobby();
				loginStage.close();
			}
			
		});
		
		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void start(Stage primaryStage) {
		FXMLLoader loaderLogin = null;

		
		// IF USER/PASS IS VALID, LAUNCH, OTHERWISE BLOCK
		
		mongoClient = new MongoClient(new MongoClientURI("mongodb://admin:pass@ds033066.mlab.com:33066/chatfx"));
		
		MongoDatabase db = mongoClient.getDatabase("chatfx");
		
		if ((col = db.getCollection("userCredentials")) != null) {		
			col = db.getCollection("userCredentials");
		} else {
			System.out.println("NULL");
			db.createCollection("userCredentials");
			col = db.getCollection("userCredentials");
		}
		
		loginStage = new Stage();
		
		try {
       	 	loaderLogin = new FXMLLoader(getClass().getResource("ChatLogin.fxml"));
       	 	loginPane = loaderLogin.load();
       	 	System.out.println(loginPane);
	        loginStage.setScene(new Scene(loginPane));
	        loginStage.setTitle("ChatRoomFX");
	        loginStage.show();
		} catch (Exception ex) {
			Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		loginController = loaderLogin.<ChatLoginController>getController();
		loginController.setClient(this);
		
		final ChatLoginController finalLoginController = loginController;
		final Stage finalLoginStage = loginStage;
		final MongoCollection finalCol = col;
		final Document finalDoc = doc;
		
		loginWait = new Thread(new Runnable() {

			@Override
			public void run() {
				MongoCollection col = finalCol;
				Document doc = finalDoc;
				ChatLoginController loginController = finalLoginController;
				Stage loginStage = finalLoginStage;
				
				boolean loggedIn = false;
				boolean isShifted = false;
				
				// TODO Auto-generated method stub
				while (!loggedIn) {
					if (username != null && password != null) {
						if ((doc = (Document) col.find(eq("_username", username)).first()) != null) {
							System.out.println("FOUND " + doc.toString());
							
							if (doc.get("_password").equals(password)) {
								System.out.println("LAUNCHED");
								loginController.loginSucceeded(isShifted);
								loggedIn = true;
							} else {
								if (!isShifted) {
									loginController.loginFailed();
									isShifted = true;
								}
							}
						} else {
							if (!isShifted) {
								loginController.loginFailed();
								isShifted = true;
							}
						}
					}
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						initLobby();
						loginStage.close();
					}
					
				});
				
			}		
		});
		
		loginWait.start();
	}
}
