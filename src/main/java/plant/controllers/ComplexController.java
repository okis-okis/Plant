package plant.controllers;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import plant.Main;

public class ComplexController implements Initializable{
	private Stage stage;
	private Scene scene;
	private Parent root;
	private UpdateThread updateThread;
	private XYChart.Series<String, Number> realLine, laboratoryLine, minLine, maxLine;	
	private SimpleDateFormat dateFormat;
	private Date date;
	
	private double mouseX = 0, mouseY = 0;
	
	@FXML LineChart lineChart;
	@FXML PieChart piechart;
	@FXML Button lampManageButton;
	@FXML Rectangle infraRedLight;
	@FXML Label temperature1, humidity1, temperature2, humidity2;
	@FXML Spinner chartValue;
	@FXML AnchorPane mainAnchor, footerPanel;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 3.5, 1.5);
		((DoubleSpinnerValueFactory) valueFactory).setAmountToStepBy(0.01);
		
		chartValue.setValueFactory(valueFactory);
		
		BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());

		realLine = new XYChart.Series<>();
		realLine.setName("Реальное");
	    
		laboratoryLine = new XYChart.Series<>();
		laboratoryLine.setName("Лабораторное");
		
		minLine = new XYChart.Series<>();
		minLine.setName("Минимальное значение");
		
		maxLine = new XYChart.Series<>();
		maxLine.setName("Максимальное значение");
		
		lineChart.getData().addAll(realLine, laboratoryLine, minLine, maxLine);
		
		Line verticleLine = new Line();
        verticleLine.setStrokeWidth(3);
        Line horizontalLine = new Line();
        horizontalLine.setStrokeWidth(3);

        Pane pane = new Pane(verticleLine, horizontalLine);

        StackPane stackPane = new StackPane(lineChart, pane);
        
        stackPane.setPrefHeight(300);
        
//        AnimationTimer loop = new AnimationTimer()
//        {
//            @Override
//            public void handle(long now)
//            {
//                verticleLine.setStartY(0);
//                verticleLine.setEndY(pane.getHeight());
//                verticleLine.setEndX(mouseX);
//                verticleLine.setStartX(mouseX);
//
//                horizontalLine.setStartX(0);
//                horizontalLine.setEndX(pane.getWidth());
//                horizontalLine.setEndY(mouseY);
//                horizontalLine.setStartY(mouseY);
//            }
//        };
//        
//        pane.addEventFilter(MouseEvent.ANY, event -> {
//            mouseX = event.getSceneX();
//            mouseY = stackPane.getPrefHeight()-(mainAnchor.getHeight()-event.getSceneY());
//            if(mouseY>0) {
//            	mouseY=0;
//            }
//            System.out.println(mouseY);
//
//            loop.start();
//        });
        
        AnchorPane.setLeftAnchor(stackPane, 0.0);
        AnchorPane.setBottomAnchor(stackPane, 0.0);
        AnchorPane.setRightAnchor(stackPane, 300.0);
        
        footerPanel.getChildren().add(stackPane);
		
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		
	    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList( 
     		   new PieChart.Data("Норма", 83), 
     		   new PieChart.Data("Ошибка", 17));
	    piechart.setData(pieChartData);
	     
	    pieChartData.get(0).getNode().setStyle("-fx-pie-color: green");
	    pieChartData.get(1).getNode().setStyle("-fx-pie-color: red");
     
		updateThread = new UpdateThread();
        updateThread.start();
	}
	
	public void sendToChart() {
		date = new Date();
        addChartPoint(dateFormat.format(date), (Double)chartValue.getValue());
	}
	
	public void addChartPoint(String date, Number value) {
		realLine.getData().add(new XYChart.Data(date, value));
		
		minLine.getData().add(new XYChart.Data(date, 1.5));
		maxLine.getData().add(new XYChart.Data(date, 2.5));
		
		if(realLine.getData().size()%3==0)
			laboratoryLine.getData().add(new XYChart.Data(date, value));
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
					Main.getLogger().debug(result);
					    
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
					   		Main.getLogger().error("An error occurred while detecting the status of the IR lamp");
					   	}
					});
					if(!stop) {
						sleep(10000);
					}
				} catch (Exception e) {
					Main.getLogger().error(e.getMessage());
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
				Main.getLogger().error(e.getMessage());
			}
			updateThread = new UpdateThread();
	        updateThread.start();
		  }
	}
}
