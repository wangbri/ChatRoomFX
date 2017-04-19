package assignment7;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ClientChatroomController {

    @FXML
    private ListView<String> chatText;

    @FXML
    private ListView<String> chatClients;
    
    private ChatClient client;
    
    public void setClient(ChatClient client) {
    	this.client = client;
    }
    
    public void updateClientList(ObservableList<String> list) {
    	
    	Platform.runLater(new Runnable() {
			@Override
			public void run() {
				chatClients.getItems().clear();
				chatClients.setItems(list);
			}
    	});
    }

}
