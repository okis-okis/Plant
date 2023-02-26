package plant.controllers;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import plant.Main;
import plant.User;

public class AuthorizationController {
	private Stage stage;
	private Scene scene;
	private Parent root;

	@FXML
	TextField loginField;
	
	@FXML
	PasswordField passwordField;
	
	@FXML
	Label errorMessage;
	
	public void init(){		
		URL authorizationFXML 	= getClass().getResource("/fxml/authorization.fxml");
		try {
			root = FXMLLoader.load(authorizationFXML);
			scene = new Scene(root);
				
			stage = new Stage();
			
			stage.setScene(scene);
			stage.setMaximized(false);
			stage.setMaxHeight(375);
			stage.setMaxWidth(370);
			stage.setMinHeight(375);
			stage.setMinWidth(370);
			stage.show();
			Main.setAuthorizationStage(stage);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void login(ActionEvent event) {
		//Login to system
		
		//ManagementController.startProgram();
		
		Boolean res = Main.getDB().login(loginField.getText(), passwordField.getText());
		
		if(res == false) {
			errorMessage.setVisible(true);
		}else {
			User user = new User(loginField.getText());
			
			System.out.println("Privileges:");
			
			if(user.getPrivileges()!=null) {
				for(Object[] privilege: user.getPrivileges()) {
					System.out.println(privilege[0]+": "+(Boolean.parseBoolean(String.valueOf(privilege[1]))==true?"true":"false"));
				}
			}
			
			Main.setUser(user);
			Main.setManagementController(new ManagementController());
			Main.getAuthorizationStage().hide();
		}
    }
}
