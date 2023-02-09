package plant.controllers;

import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import plant.Main;

public class ComplexController {
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	public void init() {
		try {
			URL complexFXML 	= getClass().getResource("/fxml/complex.fxml");
			
			root = FXMLLoader.load(complexFXML);
			
		    scene = new Scene(root);
		    
		    Main.setStage(new Stage());
		    Main.getStage().setScene(scene);
		    Main.getStage().setMaximized(true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void show() {
		Main.getStage().show();
	}
	
	public void hide() {
		Main.getStage().hide();
	}
}
