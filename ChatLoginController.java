package assignment7;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ChatLoginController implements Initializable{

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private TextField loginUsername;

    @FXML
    private Hyperlink createAccountLink;
    
    @FXML
    private Text failedText;
    
    private ClientMain client;
    public boolean createAccount = false;
    
    public void setClient(ClientMain client) {
    	this.client = client;
    }
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	
    	failedText.setOpacity(0);
    	System.out.println("afsadf");
    	loginButton.setOnAction(new EventHandler <ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (!createAccount) {
					System.out.println("LOGIN ATTEMPT");
					client.setCredentials(loginUsername.getText(), loginPassword.getText());
					loginUsername.clear();
					loginPassword.clear();
				} else {
					client.createAccount(loginUsername.getText(), loginPassword.getText());
				}
			}   		
    	});
    	
    	createAccountLink.setOnAction(new EventHandler <ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				loginButton.setText("Create account");
				createAccountLink.setDisable(true);
				
				if (loginUsername.getLayoutY() != 65) { // if layout is shifted
		    		loginPassword.setLayoutY(loginPassword.getLayoutY() + 20);
					loginUsername.setLayoutY(loginUsername.getLayoutY() + 20);
					failedText.setOpacity(0);
    			}
				
				createAccount = true;
			}  		
    	});
    }
    
    public void loginFailed() {
    	Platform.runLater(new Runnable() {
    		
			@Override
			public void run() {
				loginPassword.setLayoutY(loginPassword.getLayoutY() - 20);
				loginUsername.setLayoutY(loginUsername.getLayoutY() - 20);
				failedText.setOpacity(100);
			}
    	});
    }
    
    public void loginSucceeded(boolean isShifted) {
    	Platform.runLater(new Runnable() {
    		
			@Override
			public void run() {
				if (isShifted) {
					loginPassword.setLayoutY(loginPassword.getLayoutY() + 20);
					loginUsername.setLayoutY(loginUsername.getLayoutY() + 20);
				}
				
				failedText.setOpacity(0);
			}
    	});
    }
}
