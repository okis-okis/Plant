package plant.controllers;

import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class ComplexController {
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML AreaChart areachart;
	
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
		    
//	        final NumberAxis xAxis = new NumberAxis(256, 16384, 2016);
//	        final NumberAxis yAxis = new NumberAxis();
//	        final AreaChart<Number,Number> areachart = new AreaChart<Number,Number>(xAxis,yAxis);
	        
	        stage.setTitle("Управление комплексом");
		    stage.setScene(scene);
		    stage.setMaximized(true);
		    stage.show();
		    
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(ActionEvent event) {
		System.out.println("Click");
		
		XYChart.Series seriesApril= new XYChart.Series();
        seriesApril.setName("April");
        seriesApril.getData().add(new XYChart.Data(1, 4));
        seriesApril.getData().add(new XYChart.Data(3, 10));
        seriesApril.getData().add(new XYChart.Data(6, 15));
        seriesApril.getData().add(new XYChart.Data(9, 8));
        seriesApril.getData().add(new XYChart.Data(12, 5));
        seriesApril.getData().add(new XYChart.Data(15, 18));
        seriesApril.getData().add(new XYChart.Data(18, 15));
        seriesApril.getData().add(new XYChart.Data(21, 13));
        seriesApril.getData().add(new XYChart.Data(24, 19));
        seriesApril.getData().add(new XYChart.Data(27, 21));
        seriesApril.getData().add(new XYChart.Data(30, 21));
        
        XYChart.Series seriesMay = new XYChart.Series();
        seriesMay.setName("May");
        seriesMay.getData().add(new XYChart.Data(1, 20));
        seriesMay.getData().add(new XYChart.Data(3, 15));
        seriesMay.getData().add(new XYChart.Data(6, 13));
        seriesMay.getData().add(new XYChart.Data(9, 12));
        seriesMay.getData().add(new XYChart.Data(12, 14));
        seriesMay.getData().add(new XYChart.Data(15, 18));
        seriesMay.getData().add(new XYChart.Data(18, 25));
        seriesMay.getData().add(new XYChart.Data(21, 25));
        seriesMay.getData().add(new XYChart.Data(24, 23));
        seriesMay.getData().add(new XYChart.Data(27, 26));
        seriesMay.getData().add(new XYChart.Data(31, 26));
        
		areachart.setTitle("Распределение веществ по энергетическим уровням");
        areachart.getData().addAll(seriesApril, seriesMay);
	}
}
