package plant;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.stage.Stage;
import plant.controllers.AuthorizationController;
import plant.controllers.ComplexController;
import plant.controllers.ManagementController;
import plant.lib.HelpFrames;

public class Main extends Application {
	/**
	 * @see DB
	 */
	private static DB db;
	
	private static User user;
	
	private static ManagementController managementController;
	
	private static Stage authorizationStage, complexStage;
	
	private static HelpFrames help;
	
	private static ComplexController complex;
	
	@Override
	public void start(Stage primaryStage) {	
		//Init variable for working with database
		db = new DB();
		
		//JFrame f = db.createUI();
		
		//Try to open connection with database
		if(!db.openConnection()) {
			JOptionPane.showMessageDialog(null,"Ошибка соединения с БД","Ошибка!",1);
			System.exit(1);
		}
		//f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));		
				
		new AuthorizationController().init();
		help = new HelpFrames();
	}
	
	public static void main(String[] args) {		
		launch(args);
	} 
	
	/**
	 * Function for get connection with database
	 * @return static db variable for working with database
	 * @see DB
	 */
	public static DB getDB() {
		return db;
	}
	
	public static void setUser(User u) {
		user = u;
	}
	
	public static User getUser() {
		return user;
	}
	
	public static void setManagementController(ManagementController mc) {
		managementController = mc;
	}
	
	public static ManagementController getManagementController() {
		return managementController;
	}

	public static Stage getAuthorizationStage() {
		return authorizationStage;
	}

	public static void setAuthorizationStage(Stage authorizationStage) {
		Main.authorizationStage = authorizationStage;
	}

	public static ComplexController getComplexController() {
		return complex;
	}

	public static void setComplexController(ComplexController complex) {
		Main.complex = complex;
	}

	public static Stage getComplexStage() {
		return complexStage;
	}

	public static void setComplexStage(Stage complexStage) {
		Main.complexStage = complexStage;
	}

	public static HelpFrames getHelp() {
		return help;
	}

	public static void setHelp(HelpFrames help) {
		Main.help = help;
	}
}
