package assignment7;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ClientChatroomController implements Initializable {

    @FXML
    private ListView<String> chatText;

    @FXML
    private ListView<String> chatClients;
    
    @FXML
    private TextField messageField;
    
    private ChatClient client;
    
    public void setClient(ChatClient client) {
    	this.client = client;
    }
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
    	messageField.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("HERE2");
				if (arg0.getCode() == KeyCode.ENTER) {
    				client.sendMessage(messageField.getText());
    				messageField.clear();
    			}
			}
    		
    	});
		
	}
    
    public void updateChat(String message) {
    	
    	Platform.runLater(new Runnable() {
			@Override
			public void run() {
				System.out.println("here" + chatClients.getItems().toString());
				ObservableList<String> messages = FXCollections.observableArrayList(Arrays.asList(message));
				ObservableList<String> history = chatText.getItems();
				System.out.println(history.toString());
				messages.addAll(history);
				chatText.setItems(messages);
			}
    	});
    }
    
    public void updateClientList(ObservableList<String> list) {
    	
    	Platform.runLater(new Runnable() {
			@Override
			public void run() {
				System.out.println("AT CC:" + list.toString());
				chatClients.getItems().clear();
				chatClients.setItems(list);
			}
    	});
    }

	
    

}
