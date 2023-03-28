package plant;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.stage.Stage;
import plant.controllers.AuthorizationController;
import plant.controllers.ComplexController;
import plant.controllers.ManagementController;
import plant.lib.HelpFrames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static Logger logger;
	
	private static Serial serial;
	
	@Override
	public void start(Stage primaryStage) {	
		logger = LoggerFactory.getLogger(App.class);
		
		//Init variable for working with database
		db = new DB();
		
		//JFrame f = db.createUI();
		
		//Try to open connection with database
		if(!db.openConnection()) {
			JOptionPane.showMessageDialog(null,"Ошибка соединения с БД","Ошибка!",1);
			logger.error("DB connection error!");
			System.exit(1);
		}
		logger.info("Connect to DB");
		//f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));		
				
		new AuthorizationController().init();
		help = new HelpFrames();
		
		serial = new Serial();
		try {
			serial.initSerialPort("COM3", 9600);
			serial.openPort();
	    	if(!serial.isOpen()) {
	        	logger.error("Serial port closed");
	        	return;
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public static Serial getSerial() {
		return serial;
	}

	public static Logger getLogger() {
		return logger;
	}
}
