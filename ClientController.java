package assignment7;

import com.sun.javafx.scene.control.skin.ListViewSkin;

import javafx.application.Platform;
//import assignment7.ClientMain.UpdateableListViewSkin;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

@SuppressWarnings("restriction")
public class ClientController {

    @FXML
    private ListView<String> lobbyChats;

    @FXML
    private ListView<String> lobbyClients;

    @FXML
    private Button startChat;

    @FXML
    private Button joinChat;
    
    public ClientController() {
    	
    }
    
//    public class UpdateableListViewSkin<T> extends ListViewSkin<T> {
//		
//		public UpdateableListViewSkin(ListView<T> arg0) {
//			super(arg0);
//		}
//		
//		public void refresh() {
//			super.flow.rebuildCells();
//		}
//    }
    
    @SuppressWarnings("rawtypes")
	public void updateLobby(ObservableList<String> clientList) {
    	//lobbyClients.setItems(chatList);
    	
    	Platform.runLater(new Runnable() {

			@Override
			public void run() {
				lobbyClients.getItems().clear();
//				UpdateableListViewSkin<String> listSkin = new UpdateableListViewSkin<String>(lobbyClients);
//		    	lobbyClients.setSkin(listSkin);
				System.out.println("from controller: " + clientList + "\n");
				lobbyClients.setItems(clientList);	
//				((UpdateableListViewSkin) lobbyClients.getSkin()).refresh();
			}
    		
    	});
    	
    	
    }

}