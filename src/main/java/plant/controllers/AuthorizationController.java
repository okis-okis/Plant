package plant.controllers;

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
	
	public AuthorizationController() {
		try {
			URL authorizationFXML 	= getClass().getResource("/fxml/authorization.fxml");
			
			root = FXMLLoader.load(authorizationFXML);
			
		    scene = new Scene(root);
		    
		    Main.setStage(new Stage());
		    Main.getStage().setScene(scene);
		    Main.getStage().setMaximized(false);
		    Main.getStage().setMaxHeight(375);
		    Main.getStage().setMaxWidth(370);
		    Main.getStage().setMinHeight(375);
		    Main.getStage().setMinWidth(370);
			Main.getStage().show();
		} catch(Exception e) {
			e.printStackTrace();
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
			
			for(Object[] privilege: user.getPrivileges()) {
				System.out.println(privilege[0]+": "+(Boolean.parseBoolean(String.valueOf(privilege[1]))==true?"true":"false"));
			}
			
			Main.setUser(user);
			Main.setManagementController(new ManagementController());
			Main.getStage().close();
		}
    }
}
