package assignment7;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
//import assignment7.ClientMain.UpdateableListViewSkin;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class ClientLobbyController implements Initializable {

    @FXML
    private ListView<String> lobbyChats;

    @FXML
    private ListView<String> lobbyClients;

    @FXML
    private Button startChat;

    @FXML
    private Button joinChat;
    
    private ChatClient client;
    private String selectedChat;
    
    public void setClient(ChatClient client) {
    	this.client = client;
    }
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
    	
    	lobbyChats.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				// TODO Auto-generated method stub
				selectedChat = lobbyChats.getSelectionModel().getSelectedItem();
			}
    	
    	});
    	
		// TODO Auto-generated method stub
		startChat.setOnAction(new EventHandler<ActionEvent>() {

	        @Override
	        public void handle(ActionEvent event) {
	            client.addChat();
	        }
	    });
		
		joinChat.setOnAction(new EventHandler<ActionEvent>() {

	        @Override
	        public void handle(ActionEvent event) {
	            client.joinChat(selectedChat);
	        }
	    });
	}
    
    public void updateLobbyChats(ObservableList<String> list) {
    	
    	Platform.runLater(new Runnable() {
			@Override
			public void run() {
				lobbyChats.getItems().clear();
				lobbyChats.setItems(list);
			}
    	});
    }
    
    
    public void updateLobbyClients(ObservableList<String> list) {
    	
    	Platform.runLater(new Runnable() {
    		
			@Override
			public void run() {
				lobbyClients.getItems().clear();
				lobbyClients.setItems(list);
			}
    	});
    }

	
}