package assignment7;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.javafx.scene.control.skin.ListViewSkin;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class ClientMainGUI extends Application {
	
	public ObservableList<String> clientList = FXCollections.observableArrayList("Client A", "Client B", "Client C");
	public ObservableList<String> chatList = FXCollections.observableArrayList("Chat A", "Chat B", "Chat C");
	
	@FXML
    private ListView<String> lobbyChats;

    @FXML
    private ListView<String> lobbyClients;

    @FXML
    private Button startChat;

    @FXML
    private Button joinChat;
    
    public ClientMainGUI() {
    	
    }
    
    public class UpdateableListViewSkin<T> extends ListViewSkin<T> {

		public UpdateableListViewSkin(ListView<T> arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}
		
		public void refresh() {
			super.flow.rebuildCells();
		}
    }
    
    public void initialize() {
    	lobbyChats.setItems(chatList);
    	lobbyClients.setItems(clientList);
    	
    	UpdateableListViewSkin<String> listSkin = new UpdateableListViewSkin<>(this.lobbyChats);
    	this.lobbyChats.setSkin(listSkin);
    	
    	updateLobby();
    }
    
    public void updateLobby() {
    	lobbyChats.setItems(chatList);
    	lobbyClients.setItems(clientList);
    	
    	((UpdateableListViewSkin) lobbyChats.getSkin()).refresh();
    }
    
	public void start(Stage primaryStage) {
        try {
            BorderPane page = (BorderPane) FXMLLoader.load(ClientMainGUI.class.getResource("ClientMainGUI.fxml"));
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("FXML is Simple");
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(ClientMainGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	

    
    
}
