package plant.controllers;

import java.net.URL;

import javafx.application.Platform;
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
		//Platform.setImplicitExit(false);
//		Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
			try {
				URL complexFXML = getClass().getResource("/fxml/complex.fxml");
				
				root = FXMLLoader.load(complexFXML);
				
			    scene = new Scene(root);
			    stage = new Stage();
			    stage.setScene(scene);
			    stage.setMaximized(true);
			    //Main.setComplexStage(stage);
			} catch(Exception e) {
				e.printStackTrace();
			}
//		}});
	}
	
	public void show() {
		if(stage!=null) {
			stage.show();
		}
	}
	
	public void hide() {
		if(stage!=null) {
			stage.hide();
		}
	}
}
