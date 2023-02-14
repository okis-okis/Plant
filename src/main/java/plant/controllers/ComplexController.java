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

public class ComplexController implements Initializable{
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML AreaChart areachart;
	@FXML PieChart piechart;
	@FXML Circle bunkerDry, bunkerDryM, splitter3, splitter3M, splitter05, splitter05M, bunkerCompile, bunkerCompileM, conveyor, conveyorM, ukat1, ukat1M, ukat2, ukat2M, IKLamp, IKLampM, xrayLED, xrayLEDM;
	@FXML Button bunkerDryB, splitter3B, splitter05B, bunkerCompileB, conveyorB, ukat1B, ukat2B, IKLampB, xrayLEDB, samplerB;
	@FXML Circle samplerM;
	@FXML Rectangle sampler;
	
	public void update(ActionEvent event) {
		System.out.println("Click");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		    XYChart.Series seriesApril= new XYChart.Series();
	        seriesApril.setName("Лаборатория");
	        seriesApril.getData().add(new XYChart.Data(1, 7000));
	        seriesApril.getData().add(new XYChart.Data(1500, 7500));
	        seriesApril.getData().add(new XYChart.Data(3000, 3700));
	        seriesApril.getData().add(new XYChart.Data(4500, 4000));
	        seriesApril.getData().add(new XYChart.Data(6000, 4156));
	        seriesApril.getData().add(new XYChart.Data(7500, 1245));
	        seriesApril.getData().add(new XYChart.Data(9000, 1256));
	        seriesApril.getData().add(new XYChart.Data(10500, 6500));
	        
	        XYChart.Series seriesMay = new XYChart.Series();
	        seriesMay.setName("Реальное значение");
	        Random rand = new Random();
	        for(int i=0;i<11000;i+=200) {
	        	seriesMay.getData().add(new XYChart.Data(i, rand.nextInt(16000)));
	        }
	        
			areachart.setTitle("Распределение веществ по энергетическим уровням");
	        areachart.getData().addAll(seriesApril, seriesMay);
	        
	        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList( 
	        		   new PieChart.Data("Норма", 83), 
	        		   new PieChart.Data("Ошибка", 17));
	        piechart.setData(pieChartData);
	        
	        pieChartData.get(0).getNode().setStyle("-fx-pie-color: green");
	        pieChartData.get(1).getNode().setStyle("-fx-pie-color: red");
	}
	
	public void bunkerDrying(ActionEvent event) {
		if(bunkerDry.getFill() == Color.LIME) {
			bunkerDry.setFill(Color.RED);
			bunkerDryM.setFill(Color.RED);
			bunkerDryB.setText("Включить");
		}else {
			bunkerDry.setFill(Color.LIME);
			bunkerDryM.setFill(Color.LIME);
			bunkerDryB.setText("Выключить");
		}
	}
	
	public void splitter3Manage(ActionEvent event) {
		if(splitter3.getFill() == Color.LIME) {
			splitter3.setFill(Color.RED);
			splitter3M.setFill(Color.RED);
			splitter3B.setText("Включить");
		}else {
			splitter3.setFill(Color.LIME);
			splitter3M.setFill(Color.LIME);
			splitter3B.setText("Выключить");
		}
	}
	
	public void splitter05Manage(ActionEvent event) {
		if(splitter05.getFill() == Color.LIME) {
			splitter05.setFill(Color.RED);
			splitter05M.setFill(Color.RED);
			splitter05B.setText("Включить");
		}else {
			splitter05.setFill(Color.LIME);
			splitter05M.setFill(Color.LIME);
			splitter05B.setText("Выключить");
		}
	}
	
	public void bunkerCompile(ActionEvent event) {
		if(bunkerCompile.getFill() == Color.LIME) {
			bunkerCompile.setFill(Color.RED);
			bunkerCompileM.setFill(Color.RED);
			bunkerCompileB.setText("Включить");
		}else {
			bunkerCompile.setFill(Color.LIME);
			bunkerCompileM.setFill(Color.LIME);
			bunkerCompileB.setText("Выключить");
		}
	}
	
	public void conveyor(ActionEvent event) {
		if(conveyor.getFill() == Color.LIME) {
			conveyor.setFill(Color.RED);
			conveyorM.setFill(Color.RED);
			conveyorB.setText("Включить");
		}else {
			conveyor.setFill(Color.LIME);
			conveyorM.setFill(Color.LIME);
			conveyorB.setText("Выключить");
		}
	}
	
	public void ukat1(ActionEvent event) {
		if(ukat1.getFill() == Color.LIME) {
			ukat1.setFill(Color.RED);
			ukat1M.setFill(Color.RED);
			ukat1B.setText("Включить");
		}else {
			ukat1.setFill(Color.LIME);
			ukat1M.setFill(Color.LIME);
			ukat1B.setText("Выключить");
		}
	}
	
	public void ukat2(ActionEvent event) {
		if(ukat2.getFill() == Color.LIME) {
			ukat2.setFill(Color.RED);
			ukat2M.setFill(Color.RED);
			ukat2B.setText("Включить");
		}else {
			ukat2.setFill(Color.LIME);
			ukat2M.setFill(Color.LIME);
			ukat2B.setText("Выключить");
		}
	}
	
	public void iklamp(ActionEvent event) {
		if(IKLamp.getFill() == Color.LIME) {
			IKLamp.setFill(Color.RED);
			IKLampM.setFill(Color.RED);
			IKLampB.setText("Включить");
		}else {
			IKLamp.setFill(Color.LIME);
			IKLampM.setFill(Color.LIME);
			IKLampB.setText("Выключить");
		}
	}
	
	public void xray(ActionEvent event) {
		if(xrayLED.getFill() == Color.LIME) {
			xrayLED.setFill(Color.RED);
			xrayLEDM.setFill(Color.RED);
			xrayLEDB.setText("Включить");
		}else {
			xrayLED.setFill(Color.LIME);
			xrayLEDM.setFill(Color.LIME);
			xrayLEDB.setText("Выключить");
		}
	}
	
	public void sampler(ActionEvent event) {
		SampleProcess sample = new SampleProcess();
		sample.run();
	}
	
	public class SampleProcess extends Thread{
		Boolean mode;
		public void run() {
			mode = !sampler.isVisible();
			
			samplerM.setFill(Color.BLUE);
			
			Stage s = new Stage();
			
			// set title for the stage
	        s.setTitle("В процессе...");
			  
	        // create a progressbar
	        ProgressBar pb = new ProgressBar();
	  
	        // create a tile pane
	        AnchorPane r = new AnchorPane();
	  	  	
	        Label proccesLabel = new Label("Процесс "+(mode?"установки":"задвигания")+" пробы");
	        
	        AnchorPane.setTopAnchor(proccesLabel, 5.0);
	        AnchorPane.setLeftAnchor(proccesLabel, 10.0);
	        
	        r.getChildren().add(proccesLabel);
	        
	        AnchorPane.setTopAnchor(pb, 25.0);
	        AnchorPane.setLeftAnchor(pb, 5.0);
	        AnchorPane.setRightAnchor(pb, 5.0);
	        
	        // add button
	        r.getChildren().add(pb);
	  
	        // create a scene
	        Scene sc = new Scene(r, 200, 50);
	  
	        // set the scene
	        s.setScene(sc);
	  
	        s.show();
	        
	        TimerTask task = new TimerTask() {
	            public void run() {
	                Platform.runLater(() -> {
		            	Alert alert = new Alert(AlertType.INFORMATION);
		    	    	alert.setTitle("Информация");
		    	    	alert.setHeaderText("Проба была "+(mode?"установлена":"убрата")+"!");
		    	    	alert.show();
		    	    	
		    	    	samplerB.setText(mode?"Убрать пробу":"Установить пробу");
		    	    	
		    	    	sampler.setVisible(mode);
		    	    	samplerM.setFill(mode?Color.LIME:Color.WHITE);
		    	    	s.close();
	                });
	            }
	        };
	        Timer timer = new Timer("Timer");
	        long delay = 3000L;
	        timer.schedule(task, delay);
		}
	}
}
