package plant.controllers;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import plant.Main;

public class ComplexController implements Initializable{
	private Stage stage;
	private Scene scene;
	private Parent root;
	private UpdateThread updateThread;
	
	@FXML AreaChart areachart;
	@FXML PieChart piechart;
	@FXML Button lampManageButton;
	@FXML Rectangle infraRedLight;
	@FXML Label temperature1, humidity1, temperature2, humidity2;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		    XYChart.Series laboratoryValue= new XYChart.Series();
	        laboratoryValue.setName("Лаборатория");
	        Random rand = new Random();
	        for(int i=0;i<3600;i+=500) {
	        	laboratoryValue.getData().add(new XYChart.Data(i, rand.nextInt(10)+10));
	        }
	        
	        XYChart.Series realValue = new XYChart.Series();
	        realValue.setName("Реальное значение");
	        for(int i=0;i<3600;i+=100) {
	        	realValue.getData().add(new XYChart.Data(i, rand.nextInt(20)+5));
	        }
	        
			areachart.setTitle("Основность аглошихты");
	        areachart.getData().addAll(laboratoryValue, realValue);
	        
	        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList( 
	        		   new PieChart.Data("Норма", 83), 
	        		   new PieChart.Data("Ошибка", 17));
	        piechart.setData(pieChartData);
	        
	        pieChartData.get(0).getNode().setStyle("-fx-pie-color: green");
	        pieChartData.get(1).getNode().setStyle("-fx-pie-color: red");
	        
	        updateThread = new UpdateThread();
	        updateThread.start();
	}
	
	public void lampManage(ActionEvent event) {
		LedThread ledT = new LedThread();
		ledT.run(lampManageButton.getText().equals("Включить"));
	}
	
	private void setTemperature1(String temperature) {
		temperature1.setText(temperature+"°");
	}
	
	private void setHumidity1(String humidity) {
		humidity1.setText(humidity+"%");
	}
	
	private void setTemperature2(String temperature) {
		temperature2.setText(temperature+"°");
	}
	
	private void setHumidity2(String humidity) {
		humidity2.setText(humidity.substring(0, humidity.length()-1)+"%");
	}
	
	public class UpdateThread extends Thread  {
		String result;
		public Boolean stop = false;
		  public void run() {
			while(!stop) {
			    try {
			    	Main.getSerial().executeCommand("ALL");
					sleep(5000);
					result = Main.getSerial().getResult();
					System.out.println(result);
					    
					String[] arr = result.split(":");
					    
					Platform.runLater(() -> {
					 	try {
					   		setHumidity1(arr[0].isEmpty()?"nan":arr[0]);
					   	}catch(Exception e) {
					   		setHumidity1("nan");
					   	}
					   	try {
					   		setTemperature1(arr[1].isEmpty()?"nan":arr[1]);
					   	}catch(Exception e) {
					   		setTemperature1("nan");
					   	}
					   	try {
					   		setHumidity2(arr[2].isEmpty()?"nan":arr[2]);
					   	}catch(Exception e) {
					   		setHumidity2("nan");
					   	}
					   	try {
					   		setTemperature2(arr[3].isEmpty()?"nan":arr[3]);
					   	}catch(Exception e) {
					   		setTemperature2("nan");
					   	}
					   	try {
					   		arr[4] = arr[4].substring(0, arr[4].length()-1);
					   		if(arr[4].equals("LED+")) {
					   			infraRedLight.setVisible(true);
								lampManageButton.setText("Выключить");
					   		}else {
					   			infraRedLight.setVisible(false);
								lampManageButton.setText("Включить");
					   		}
					   	}catch(Exception e) {
					   		System.out.println("Возникла ошибка с определением состояния ИК лампы");
					   	}
					});
					if(!stop) {
						sleep(10000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			  }
		  }
	}
	
	public class LedThread extends Thread  {
		String result;
		  public void run(Boolean mode) {
			try {	
				updateThread.stop = true;
				updateThread.join();
				
				if(mode) {  //Включить
					Main.getSerial().lampTurnON();
				}else {		//Выключить
					Main.getSerial().lampTurnOFF();
				}
				sleep(2000);
				result = Main.getSerial().getResult();
				result = result.substring(0, result.length()-1);
				System.out.println("Result: "+result);
				if(result.equals("LED+")||result.equals("LED-")) {
					Platform.runLater(() -> {
						infraRedLight.setVisible(mode);
						lampManageButton.setText(mode?"Выключить":"Включить");
						
				        return;
					});
				}
			} catch (Exception e) {
				System.out.println(e);
			}
			updateThread = new UpdateThread();
	        updateThread.start();
		  }
	}
}
