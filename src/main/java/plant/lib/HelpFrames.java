package plant.lib;

import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import plant.Main;

public class HelpFrames{
	public void docWindow() {
	    System.out.println("Web content");
	    Stage stage = new Stage();
	    stage.setTitle("Документация");             

	    WebView webView = new WebView();
	    WebEngine webEngine = webView.getEngine();
	    webEngine.load(getClass().getResource("/doc/index.html").toString());
	    Scene scene = new Scene(webView,600,600);
	    
	    stage.setScene(scene);
	    stage.setMaximized(true);
	    stage.show();
	}
}
