package plant.controllers;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

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
import javafx.stage.Stage;

public class ComplexController implements Initializable{
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML AreaChart areachart;
	@FXML PieChart piechart;
	
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
}
