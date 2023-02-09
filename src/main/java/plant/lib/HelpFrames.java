package plant.lib;

import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import plant.controllers.AuthorizationController;

public class HelpFrames{
	public void docWindow() {
		JFXPanel panel = new JFXPanel();
		 
		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
	            	System.out.println("Web content");
	            	Stage stage = new Stage();
			    	stage.setTitle("Документация");             

			    	WebView webView = new WebView();
			        WebEngine webEngine = webView.getEngine();
			        webEngine.load(getClass().getResource("/doc/index.html").toString());
			        Scene scene = new Scene(webView,600,600);
			        panel.setScene(scene);
			}
	    );
		 
	   JFrame frame = new JFrame();
	   frame.setContentPane(panel);
	   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(300, 300);
       frame.setVisible(true);
	}
}
