package plant.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.github.javafx.charts.zooming.ZoomManager;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import plant.Main;

/**
 * Class-contoller for manage complex window
 * @author olegk
 * @since 01.04.2023
 */
public class ComplexController implements Initializable{
	/**
	 * Main stage
	 * @see Stage
	 */
	private Stage stage;
	
	/**
	 * Main scene
	 * @see Scene
	 */
	private Scene scene;
	
	/**
	 * Main parent
	 * @See Parent
	 */
	private Parent root;
	
	/**
	 * Special thread for update data from complex
	 * @see UpdateThread
	 */
	private static UpdateThread updateThread;
	
	/**
	 * Line for LineChart for basicity display
	 */
	private XYChart.Series<String, Number> realLine, realAverageLine, laboratoryLine, minLine, maxLine;	
	
	/**
	 * Date format for lineChart point output
	 * @see SimpleDateFormat
	 */
	private SimpleDateFormat dateFormat;
	
	/**
	 * Logical variable for stop/start complex modeling (update work)
	 */
	private Boolean stopModeling;
    
    /**
     * Scrolling for average lineChart
     * @see ScrollPane
     */
    final ScrollPane scrollAveragePane = new ScrollPane();
    
    /**
     * Container for scrolling and zooming for average lineChart
     * @see StackPane
     */
    final StackPane averageContainer = new StackPane();
    
    /**
     * Scrolling lineChart
     * @see ScrollPane
     */
    final ScrollPane scrollRealPane = new ScrollPane();
    
    /**
     * Container for scrolling and zooming for real lineChart
     */
    final StackPane realContainer = new StackPane();
	
	/**
	 * Auxiliary dateTime for lineChart point output
	 * @see LocalDateTime
	 */
    LocalDateTime date, startDate, finishDate;
    
    /**
     * Auxiliary double variable for definition real average value 
     */
    double oldValue, realOldValue, realAverage8, realAverage12, realAverage24;
    
    /**
     * Counter for definition real average value
     */
    int realAverageCounter8, realAverageCounter12, realAverageCounter24;
    
    SendingThread sendingSampleToComplex;
    
    /**
     * Special formatter for lineChart point output
     * @see DateTimeFormatter
     */
    DateTimeFormatter formatter;
    
    /**
     * Series line for lineChart
     */
    XYChart.Series sample, dataComplex;
	
	/**
	 * Displaying the ratio of correct basicity to error
	 * @see PieChart
	 */
	@FXML PieChart piechart;
	
	/**
	 * Control of infra-red lamp
	 * @see Button
	 */
	@FXML Button lampManageButton;
	
	/**
	 * Indicator of light from infra-red lamp
	 * @see Rectangle
	 */
	@FXML Rectangle infraRedLight;
	
	/**
	 * Text message of current state temperature and humidity from sensors
	 * @see Label
	 */
	@FXML Label temperature1, humidity1, temperature2, humidity2;
	
	/**
	 * Basicity value for manual enter
	 * @see Spinner
	 */
	@FXML Spinner chartValue;
	
	/**
	 * Layouts for complex window
	 * @see AnchorPane
	 */
	@FXML AnchorPane mainAnchor, footerPanel;
	
	/**
	 * @see BarChart
	 */
	@FXML BarChart barChart;
    
	/**
	 * Splitter/crusher control
	 * @see Button;
	 */
	@FXML Button drobManage;
	
	/**
	 * Bunker heating control
	 */
	@FXML Button bunkerHeatingManage;
	
	/**
	 * Sampling control
	 */
	@FXML Button otborManage; 
	
	/**
	 * Conveyor control
	 */
	@FXML Button conveyorManage;
	
	/**
	 * Control upper flap of first bunker from complex 
	 */
	@FXML Button upperFlapManage;
	
	/**
	 * Control down flap of first bunker from complex
	 */
	@FXML Button downFlapManage;
	
	/**
	 * Control of the reference drive
	 */
	@FXML Button standardManage;
	
	/**
	 * Status indicator of the sampling process
	 */
	@FXML Circle otborStatus;	
	
	/**
	 * Indicator of upper flap of first bunker
	 */
	@FXML Rectangle upperFlap;
	
	/**
	 * Indicator of down (bottom) flap of first bunker
	 */
	@FXML Rectangle bottomFlap;
	
	@FXML Rectangle bottomFlap1;
	
	@FXML Rectangle upperFlap1;
	
	@FXML Rectangle downFlap1;
	
	@FXML Label VB_Status, NB_Status;
	
	@FXML Button upperFlapManage1;
	
	@FXML Button downFlapManage1;
	
	@FXML Button preparationMode;
	
	/**
	 * Indicator of reference (standard) model
	 */
	@FXML Rectangle standardModel;
	
	@FXML AreaChart xraySpectrum;
	
	@FXML VBox elementsInfo;
	
	@FXML AnchorPane spectrumPane;
	
	XYChart.Series realStacked, labStacked;
	
	Data labStacked8, labStacked12, labStacked24, 
		 realStacked8, realStacked12, realStacked24;
	
	PieChart.Data pieNorm, pieError;
	
	LineChart<String, Number> averageChart;
	
	Stage averageStage;
	
	StackPane averagePane;
	
	PreparationAutoThread preparationThread;
	
	/**
	 * Initialization main window and charts
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		
		//Setting of spinner
		SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 3.5, 1.5);
		((DoubleSpinnerValueFactory) valueFactory).setAmountToStepBy(0.01);
		
		chartValue.setValueFactory(valueFactory);
		
		realAverageCounter8= 0;
		
		BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
		
		//Initialization of series for line chart
		chartLinesInit();
		
        //realLineChart.getData().addAll(minLine, maxLine, realLine);
        
		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			    
	    stopModeling = false;
	    
	    pieChartDataInit();
	    
	    oldValue = -1; realAverage8 = 0;
	    
	    stackedInit();
	    
	    date = LocalDateTime.of(2023, 3 , 1, 0, 0, 0);
	    
	    try {
	    	updateThread.stop();
	    }catch(Exception e) {
	    	Main.getLogger().info(e.getMessage());
	    }
	    
	    sample = new XYChart.Series();
	    sample.setName("Образец 1");	    		
	    xraySpectrum.getData().add(0, sample);
	    
	    dataComplex = new XYChart.Series();
	    dataComplex.setName("Результат");	    		
	    xraySpectrum.getData().add(1, dataComplex);
	    
		updateThread = new UpdateThread();
	    updateThread.start();
	    
	    sendingSampleToComplex = new SendingThread();
	    
	    preparationThread = new PreparationAutoThread();
	}
	
	/**
	 * Initialize stacked charts 
	 */
	private void stackedInit() {
		realStacked = new XYChart.Series<>();
	    realStacked.setName("Реальное");  
	    labStacked = new XYChart.Series<>();
	    labStacked.setName("Лаборатория");
	    
	    realStacked8 = new XYChart.Data<>("8 часов", 0.0);
	    realStacked12 = new XYChart.Data<>("12 часов", 0.0);
	    realStacked24 = new XYChart.Data<>("24 часов", 0.0);
	    realStacked.getData().add(realStacked8);
	    realStacked.getData().add(realStacked12);
	    realStacked.getData().add(realStacked24);
	    
	    labStacked8 = new XYChart.Data<>("8 часов", 0.0);
	    labStacked12 = new XYChart.Data<>("12 часов", 0.0);
	    labStacked24 = new XYChart.Data<>("24 часов", 0.0);
	    labStacked.getData().add(labStacked8);
	    labStacked.getData().add(labStacked12);
	    labStacked.getData().add(labStacked24);
	    
	    barChart.getData().addAll(realStacked, labStacked);
	}
		
	/**
	 * Initialization of lines for line chart
	 */
	private void chartLinesInit() {
		realLine = new XYChart.Series<>();
		realLine.setName("Реальное");
	    
		realAverageLine = new XYChart.Series<>();
		realAverageLine.setName("Реальное");
		
		laboratoryLine = new XYChart.Series<>();
		laboratoryLine.setName("Лабораторное");
		
		minLine = new XYChart.Series<>();
		minLine.setName("Минимальное значение");
		
		maxLine = new XYChart.Series<>();
		maxLine.setName("Максимальное значение");
	}
	
	/**
	 * Demonstration of the work of pieChart
	 */
	private void pieChartDataInit() {
		
		pieNorm = new PieChart.Data("Норма", 0);
		pieError = new PieChart.Data("Ошибка", 0);
		
		ObservableList<PieChart.Data> pieChartData = 
				FXCollections.observableArrayList(pieNorm, pieError);
		piechart.setData(pieChartData);
		     
		pieChartData.get(0).getNode().setStyle("-fx-pie-color: green");
		pieChartData.get(1).getNode().setStyle("-fx-pie-color: red");
	}
	
	/**
	 * Auto modeling of data get from device
	 */
	public void autoMode() {
		Main.getLogger().info("Start modeling");
		stopModeling = false;
		AutoThread auto = new AutoThread();
		auto.start();
	}
	
	/**
	 * Stop auto modeling of data get from device
	 * @see autoMode()
	 */
	public void stopLineChartModeling() {
		stopModeling = true;
		Main.getLogger().info("Stop modeling");
	}
	
	/**
	 * Manually simulate of data get
	 */
	public void sendToChart() {
        addRealPoint(date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), (Double)chartValue.getValue());
        date = date.plusMinutes(15);
	}
	
	/**
	 * Add point to real chart
	 * @param date String with date of receiving (X-axis)
	 * @param value Number with value of point (Y-ordinate)
	 */
	public void addRealPoint(String date, Number value) {
		realLine.getData().add(new XYChart.Data(date, value));
		
		Main.getLogger().info("Point was added to real line");
	}
	
	/**
	 * Add point to average chart
	 * @param date String with date of receiving (X-axis)
	 * @param value Number with value of point (Y-ordinate)
	 */
	public void addAveragePoint(String date, Number value) {
		laboratoryLine.getData().add(new XYChart.Data(date, value));
	}
	
	/**
	 * Add point with real average value to average chart
	 * @param date String with date of receiving (X-axis)
	 * @param value Number with value of point (Y-ordinate)
	 */
	public void addRealAveragePoint(String date, Number value) {
		realAverageLine.getData().add(new XYChart.Data(date, value));
	}
	
	/**
	 * Processing manage of IR-lamp
	 * @param event ActionEvent from IR-lamp manage button
	 */
	public void lampManage(ActionEvent event) {
		LedThread ledT = new LedThread();
		ledT.run(lampManageButton.getText().equals("Включить"));
	}
	
	/**
	 * Processing manage of standard
	 * @param event ActionEvent from standard manage button
	 */
	public void actionStandard(ActionEvent event) {
		SampleThread standardT = new SampleThread();
		standardT.run(standardManage.getText().equals("Установить эталон"));
	}
	
	/**
	 * Show charts with basicity
	 * @param event
	 */
	public void showCharts(ActionEvent event) {
		
		averageStage = new Stage();
		averageStage.setTitle("Среднее значение основности");
			
		// defining the axes
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis(0.5, 2.5, 0.1);
		yAxis.setAutoRanging(true);
		yAxis.setForceZeroInRange(false);
		xAxis.setLabel("Дата и время");
		yAxis.setLabel("Импульсы");
		// creating the chart
		averageChart = new LineChart<String, Number>(xAxis, yAxis);
		averagePane = new StackPane();
		averagePane.getChildren().add(averageChart);
		final Scene scene = new Scene(averagePane, 500, 400);
		averageStage.setScene(scene);
			
		new ZoomManager(averagePane, averageChart, realAverageLine, laboratoryLine);
			
		averageStage.show();
	}
	
	/**
	 * Setting temperature from first sensor on modeling window
	 * @param temperature String with temperature value
	 */
	private void setTemperature1(String temperature) {
		temperature1.setText(temperature+"°");
	}
	
	/**
	 * Setting humidity from first sensor on modeling window
	 * @param humidity String with humidity value
	 */
	private void setHumidity1(String humidity) {
		humidity1.setText(humidity+"%");
	}
	
	/**
	 * Setting temperature from second sensor on modeling window
	 * @param temperature String with temperature value
	 */
	private void setTemperature2(String temperature) {
		temperature2.setText(temperature+"°");
	}
	
	/**
	 * Setting humidity from second sensor on modeling window
	 * @param humidity String with humidity value
	 */
	private void setHumidity2(String humidity) {
		humidity2.setText(humidity.substring(0, humidity.length()-1)+"%");
	}
	
	/**
	 * Action for control of top flap driver 
	 */
	public void actionPVZ() {
		stopUpdateThread();
		if(upperFlapManage.getText().equals("Открыть")){
			Main.getSerial().turnOnPVZ();
			Main.getLogger().info("The upper flap is open");
			upperFlapManage.setText("Закрыть");
			upperFlap.setWidth(10);
			upperFlap.setHeight(91);
			upperFlap.setLayoutY(125);
		}else {
			Main.getSerial().turnOffPVZ();
			Main.getLogger().info("The upper flap is close");
			upperFlapManage.setText("Открыть");
			upperFlap.setWidth(98);
			upperFlap.setHeight(10);
			upperFlap.setLayoutY(206);
		}
	}
	
	/**
	 * Action for control of bottom flap driver 
	 */
	public void actionPNZ() {
		stopUpdateThread();
		if(downFlapManage.getText().equals("Открыть")){
			Main.getSerial().turnOnPNZ();
			Main.getLogger().info("The down flap is open");
			downFlapManage.setText("Закрыть");
			bottomFlap.setLayoutX(357);
		}else {
			Main.getSerial().turnOffPNZ();
			Main.getLogger().info("The down flap is close");
			downFlapManage.setText("Открыть");
			bottomFlap.setLayoutX(437);
		}
	}
	
	/**
	 * Action for control of sampler drive
	 */
	public void actionPPO() {
		stopUpdateThread();
		if(otborManage.getText().equals("Запустить")){
			Main.getSerial().turnOnPPO();
			Main.getLogger().info("Starting sampling");
			otborManage.setText("Остановить");
			otborStatus.setFill(Color.LIME);
		}else {
			Main.getSerial().turnOffPPO();
			Main.getLogger().info("Sampling stop");
			otborManage.setText("Запустить");
			otborStatus.setFill(Color.RED);
		}
	}
	
	/**
	 * Action for control of top bunker heating 
	 */
	public void actionNVB() {
		stopUpdateThread();
		if(bunkerHeatingManage.getText().equals("Включить нагрев")){
			Main.getSerial().turnOnNVB();
			bunkerHeatingManage.setText("Выключить нагрев");
			Main.getLogger().info("Starting the bunker heating");
		}else {
			Main.getSerial().turnOffNVB();
			bunkerHeatingManage.setText("Включить нагрев");
			Main.getLogger().info("The bunker heating stop");
		}
	}
	
	/**
	 * Action for control of crusher
	 */
	public void actionDrob() {
		stopUpdateThread();			
		if(drobManage.getText().equals("Пуск")){
			Main.getSerial().turnOnDrob();
			Main.getLogger().info("Starting the crusher");
			drobManage.setText("Стоп");
		}else {
			Main.getSerial().turnOffDrob();
			drobManage.setText("Пуск");
			Main.getLogger().info("Crusher stop");
		}
	}
	
	/**
	 * Sample conveyor belt drive
	 */
	public void actionPLP() {
		stopUpdateThread();
		if(conveyorManage.getText().equals("Пуск")){
			Main.getSerial().turnOnPLP();
			Main.getLogger().info("Starting the conveyor");
			conveyorManage.setText("Стоп");
		}else {
			Main.getSerial().turnOffPLP();
			conveyorManage.setText("Пуск");
			Main.getLogger().info("Conveyor stop");
		}
	}
	
	/**
	 * Using the first sample of the composition for analysis
	 */
	public void firstSample() {
		List<XYChart.Data> elements = new ArrayList<XYChart.Data>();
		
		elements.add(new XYChart.Data(1.2, 3200));		//Mg
		elements.add(new XYChart.Data(2.0, 2000));		//P
		elements.add(new XYChart.Data(5.9, 8000));		//Mn
		elements.add(new XYChart.Data(6.4, 3000));		//Fe
		
		sampleChart(elements);
	}
	
	/**
	 * Using the second sample of the composition for analysis
	 */
	public void secondSample() {
		List<XYChart.Data> elements = new ArrayList<XYChart.Data>();
		
		elements.add(new XYChart.Data(1.2, 5000));	//Mg
		elements.add(new XYChart.Data(2.0, 6500));	//P
		elements.add(new XYChart.Data(5.9, 4000));	//Mn
		elements.add(new XYChart.Data(6.4, 6000));	//Fe
		elements.add(new XYChart.Data(8.0, 2000));	//Cu
		
		sampleChart(elements);
	}
	
	/**
	 * Using the third sample of the composition for analysis
	 */
	public void thirdSample() {
		List<XYChart.Data> elements = new ArrayList<XYChart.Data>();
		
		elements.add(new XYChart.Data(1.2, 4000));	//Mg
		elements.add(new XYChart.Data(2.0, 2500));	//P
		elements.add(new XYChart.Data(5.9, 5500));	//Mn
		elements.add(new XYChart.Data(6.4, 8000));	//Fe
		
		sampleChart(elements);
	}
	
	/**
	 * Filling the chemical composition with random 
	 * values with changes at specified energy levels
	 * @param elements List with values of data 
	 * for line chart for manual changing line parameters
	 */
	private void sampleChart(List<XYChart.Data> elements) {
		sample.getData().clear();

		for(float f=1;f<7.0;f+=0.1) {
			Boolean found = false;
			String elementTitle = Main.getDB().getElementTitleByKa(f);
			for(XYChart.Data element: elements) {
				float energyLevel = ((Double)element.getXValue()).floatValue();
				if(energyLevel>f-0.05&&energyLevel<=f+0.05){
					element.setNode(new Label(elementTitle));
					sample.getData().add(element);
					found = true;
					break;
				}
			}	
			if(!found) {
				XYChart.Data data = new XYChart.Data(f, (int)Math.floor(Math.random() * (300 - 1)));
				if((int)data.getYValue()>=500 && !elementTitle.isEmpty()) {
					data.setNode(new Label(elementTitle));
				}
				sample.getData().add(data);
			}
			
			XYChart.Data dataTooltip = (Data) sample.getData().get(sample.getData().size()-1);
			String tooltip = "Импульсы: "+dataTooltip.getYValue().toString() + "\n" + dataTooltip.getXValue()+" кэВ";
			if(!elementTitle.equals("")) {
				tooltip+="\nЭлемент: "+elementTitle;
			}
	        Tooltip.install(dataTooltip.getNode(), new Tooltip(tooltip));
		}
	}

	/**
	 * Sending data of sample to system of complex
	 */
	public void sendSample() {
		
		if(sendingSampleToComplex != null && sendingSampleToComplex.isAlive()) {
			
			Alert sampleError = new Alert(AlertType.ERROR);
			
			sampleError.setTitle("Ошибка отправки данных.");
			sampleError.setHeaderText("Данные уже находятся в процессе отправки!");
			sampleError.show();
			
			Main.getLogger().error("Error sending data. Data is already in the process of being sent.");
			
			return;
		}
		
		Main.getLogger().info("Target data send button pressed");
		if(!xraySpectrum.getData().isEmpty()) {
			Main.getLogger().info("Sending data");
			
			if(sendingSampleToComplex == null) {
				sendingSampleToComplex = new SendingThread();
			}
			
			sendingSampleToComplex.start();
		}else {
			Alert sampleError = new Alert(AlertType.ERROR);
			
			sampleError.setTitle("Ошибка получения эталона");
			sampleError.setHeaderText("Вы не добавили эталон.");
			sampleError.setContentText("Нет данных для отправки на комплекс. Вы не выбрали эталонный график!");
			sampleError.show();
			
			Main.getLogger().error("Error sending data. There is no standard data.");
		}
	}
	
	/**
	 * Print chart with chemical composition and other info 
	 */
	public void printChart() {	   
		
		Printer printer = Printer.getDefaultPrinter();
		PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);
        
		PrinterJob job = PrinterJob.createPrinterJob();
		if (job != null) {
			boolean successPrintDialog = job.showPrintDialog(Main.getComplexStage().getOwner());
			if(successPrintDialog){
				boolean success = job.printPage(pageLayout, spectrumPane);
				if (success) {
            	  job.endJob();
				}
			}
		}
	}
	
	/**
	 * Save chart with chemical composition and other info as PNG image
	 */
	public void saveChart() {
		WritableImage image = spectrumPane.snapshot(null, null);
	    
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setTitle("Сохранение результата");     
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PNG", "*.png")
        );
	    
        File file = fileChooser.showOpenDialog(Main.getComplexStage());
        
	    try {
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "PNG", file);
		} catch (IOException e) {
			Main.getLogger().error(e.getMessage());
		}
	}
	
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void preparationModeManage() {
		if(preparationMode.getText().contains("Авто")) {
			preparationMode.setText("Ручной режим");
			preparationThread.start();
		}else {
			preparationThread.stop();
			preparationMode.setText("Авто режим");
		}
	}
	
	public void setUpperBunkerStatus(String status) {
		int statusNumber = 0;
		
		try {
			statusNumber = Integer.parseInt(status);
		}catch(Exception e) {
			Main.getLogger().error(e.getMessage());
		}
		
		if(status.contains("Полный")) {
			VB_Status.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
		}else if(statusNumber>0&&statusNumber<=80) {
			status+="%";
			VB_Status.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
		}else {
			VB_Status.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
		}
		VB_Status.setText(status);
	}
	
	public void setDownBunkerStatus(String status) {
		int statusNumber = 0;
		
		try {
			statusNumber = Integer.parseInt(status);
		}catch(Exception e) {
			Main.getLogger().error(e.getMessage());
		}
		
		if(status.contains("Полный")) {
			NB_Status.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
		}else if(statusNumber>0&&statusNumber<=80) {
			status+="%";
			NB_Status.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));
		}else {
			NB_Status.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
		}
		NB_Status.setText(status);
	}
	
	public void actionPVZ1() {
		stopUpdateThread();
		if(upperFlapManage1.getText().equals("Открыть")){
			Main.getLogger().info("The upper flap of down bunker is open");
			upperFlapManage1.setText("Закрыть");
			upperFlap1.setWidth(10);
		}else {
			Main.getLogger().info("The upper flap of down bunker is close");
			upperFlapManage1.setText("Открыть");
			upperFlap1.setWidth(85);
		}
	}
	
	public void actionPNZ1() {
		stopUpdateThread();
		if(downFlapManage1.getText().equals("Открыть")){
			Main.getLogger().info("The down flap of down bunker is open");
			downFlapManage1.setText("Закрыть");
			downFlap1.setWidth(10);
		}else {
			Main.getLogger().info("The down flap of down bunker is close");
			downFlapManage1.setText("Открыть");
			downFlap1.setWidth(60);
		}
	}
	
	private void stopUpdateThread() {
		updateThread.stop=true;
		try {
	    	updateThread.stop();
	    }catch(Exception e) {
	    	Main.getLogger().info(e.getMessage());
	    }
		
	}
	
	int volume;
	
	public class PreparationAutoThread extends Thread{
		public void run() {
			for(int counter = 0; counter<5; counter++) {
				initPreparationSystem();
				pause(5);
				
				Platform.runLater(new Runnable() {
	                @Override public void run() {
						//Open upper flap of upper bunker
						actionPVZ();
	                }
	            });
				
						pause(1);
						
				Platform.runLater(new Runnable() {
			        @Override public void run() {
						//Start sampling
						actionPPO();
					}
		        });		
						
				for(volume=1;volume<=4;volume++) {
					Platform.runLater(new Runnable() {
						@Override public void run() {
							setUpperBunkerStatus(String.valueOf(volume*20));
					    }
					});		
					pause(2);
				}
				
				Platform.runLater(new Runnable() {
					@Override public void run() {
						//Close upper flap of upper bunker
						actionPVZ();
						setUpperBunkerStatus("Полный");
					}
				});
						
				pause(1);
				
				Platform.runLater(new Runnable() {
					@Override public void run() {
						//Start heating of the upper bunker
						actionNVB();
					}
				});
				
				pause(2);
						
				Platform.runLater(new Runnable() {
					@Override public void run() {
						actionNVB();
						//Start crushing
						actionDrob();
					}
				});		
				
				pause(1);
						
				Platform.runLater(new Runnable() {
					@Override public void run() {
						//Open bottom flap of upper bunker
						actionPNZ();
					}
				});
	               
				pause(1);
			}
		}
		
		public void pause(int seconds) {
			try {
				sleep(seconds*1000);
			} catch (InterruptedException e) {
				Main.getLogger().error(e.getMessage());
			}
		}
		
		public void initPreparationSystem() {
			Platform.runLater(new Runnable() {
                @Override public void run() {
					setUpperBunkerStatus("Пусто");
					setDownBunkerStatus("Пусто");
					
					if(otborManage.getText().contains("Остановить")) {
						actionPPO();
					}
					
					if(upperFlapManage.getText().contains("Закрыть")) {
						actionPVZ();
					}
					
					if(downFlapManage.getText().contains("Закрыть")) {
						actionPNZ();
					}
					
					if(downFlapManage.getText().contains("Стоп")) {
						actionDrob();
					}
					
					if(upperFlapManage1.getText().contains("Закрыть")) {
						actionPVZ1();
					}
					
					if(downFlapManage1.getText().contains("Закрыть")) {
						actionPVZ1();
					}
					
					if(conveyorManage.getText().contains("Стоп")) {
						actionPLP();
					}
                }
            });
		}
	}
	
	/**
	 * Thread for update data on modeling window
	 * @author olegk
	 * @see Thread
	 */
	public class UpdateThread extends Thread  {
		String result;
		public Boolean stop = false;
		  public void run() {
			while(!stop) {
			    try {
			    	Main.getSerial().executeCommand("ALL");
					sleep(5000);
					result = Main.getSerial().getResult();
					Main.getLogger().info(result);
					if(result.charAt(0)!='%') {    
						String[] arr = result.split(":");
						    
						Platform.runLater(() -> {
						 	try {
						   		setHumidity1(arr[0].isEmpty()||arr[0].length()>5?"nan":arr[0]);
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
						   	try {
						   		if(arr[5].equals("StandardState+")) {
						   			standardModel.setVisible(true);
						   			standardManage.setText("Убрать эталон");
						   		}else {
						   			standardModel.setVisible(false);
						   			standardManage.setText("Установить эталон");
						   		}
						   	}catch(Exception e) {
						   		Main.getLogger().error("An error occurred while detecting the status of the IR lamp");
						   	}
						});
					}else {
						
					}
				} catch (Exception e) {
					Main.getLogger().error(e.getMessage());
				}
			  }
		  }
	}
		
	/**
	 * Thread for manage IR-lamp state
	 * @author olegk
	 * @see Thread
	 */
	public class LedThread extends Thread  {
		String result;
		  public void run(Boolean mode) {
			try {	
				updateThread.stop=true;
				try {
			    	updateThread.stop();
			    }catch(Exception e) {
			    	Main.getLogger().info(e.getMessage());
			    }
				
				int counter = 0;
				while(counter<5) {
					int timerCounter = 0;
					while(updateThread.isAlive()) {
						sleep(1000);
						if(++timerCounter==13) {
							break;
						}
					}
					
					if(mode) {  //Включить
						Main.getSerial().lampTurnON();
					}else {		//Выключить
						Main.getSerial().lampTurnOFF();
					}
					sleep(2000);
					result = Main.getSerial().getResult();
					
					Main.getLogger().info("Result: "+result);
					
					if(result.equals("LED+")||result.equals("LED-")) {
						Main.getLogger().info("IR-lamp manage");
						Platform.runLater(() -> {
							infraRedLight.setVisible(mode);
							lampManageButton.setText(mode?"Выключить":"Включить");
							Main.getLogger().info("Change IR-lamp state");
					        return;
						});
						break;
					}
					
					sleep(3000);
					counter++;
				}
			} catch (Exception e) {
				Main.getLogger().error(e.getMessage());
			}
			
			updateThread = new UpdateThread();
	        updateThread.start();
		  }
	}
	
	/**
	 * Thread for standard manage
	 * @author olegk
	 * @see Thread
	 */
	public class SampleThread extends Thread  {
		String result;
		  public void run(Boolean mode) {
			try {	
				updateThread.stop=true;
				try {
			    	updateThread.stop();
			    }catch(Exception e) {
			    	Main.getLogger().info(e.getMessage());
			    }
				
				int counter = 0;
				while(counter<5) {
					if(mode) {  //Установить эталон
						Main.getSerial().setStandard();;
					}else {		//Убрать эталон
						Main.getSerial().removeStandard();
					}
					sleep(2000);
					result = Main.getSerial().getResult();
					Main.getLogger().info("Result: "+result);
					if(result.equals("Standard+")||result.equals("Standard-")) {
						Platform.runLater(() -> {
							standardModel.setVisible(mode);
							standardManage.setText(mode?"Убрать эталон":"Установить эталон");
							
					        return;
						});
						break;
					}
					sleep(3000);
					counter++;
				}
			} catch (Exception e) {
				Main.getLogger().error(e.getMessage());
			}
			updateThread = new UpdateThread();
	        updateThread.start();
		  }
	}
	
	/**
	 * Thread for generate data for simulate getting data from device
	 * @author olegk
	 * @see Thread
	 */
	public class AutoThread extends Thread  {
		public void run() {
			if(!stopModeling) {
				//realLineChart.setAnimated(false);
				Platform.runLater(() -> {
					for(int i=0;i<200;i++) {
//					try {
//						this.sleep(5000);
//					} catch (InterruptedException e) {
//						Main.getLogger().error(e.getMessage());
//					}
					
					double x = (Math.random() * ((2.5 - 1.5) + 1)) + 1.5;
					double xrounded = Math.round(x * 100.0) / 100.0;
				        
					realAverage8 += xrounded;
					realAverageCounter8++;
					
					realAverage12 += xrounded;
					realAverageCounter12++;
					
					realAverage24 += xrounded;
					realAverageCounter24++;
					
					Main.getLogger().info("Point parameters: "+date.format(formatter)+" | "+xrounded);
					addRealPoint(date.format(formatter), xrounded);
					
					if((date.getHour()==0||date.getHour()==8||date.getHour()==16)&&date.getMinute()==0) {
						finishDate = date;
						if(startDate == null) {
							startDate = date;	
						}
						
						if(oldValue != -1) {
							addAveragePoint(date.format(formatter), oldValue);
							labStacked8.setYValue(oldValue);
						}
						addAveragePoint(date.format(formatter), xrounded);
						
						realAverage8/=realAverageCounter8;
						addRealAveragePoint(date.minusHours(8).format(formatter), realAverage8);
						addRealAveragePoint(date.format(formatter), realAverage8);
						realStacked8.setYValue(realAverage8);
						
						pieError.setPieValue((realAverage8-1.25)*100/realAverage8-5);
						pieNorm.setPieValue(100-pieError.getPieValue());
						
						Main.getLogger().info("Norm value: "+pieNorm.getPieValue());
						Main.getLogger().info("Error value: "+pieError.getPieValue());
						
						realAverage8 = 0;
						realAverageCounter8 = 0;
						startDate = finishDate;
						
						oldValue = xrounded;
					}
					
					if(date.getHour()==0||date.getHour()==12) {
						labStacked12.setYValue(oldValue);
						
						realAverage12/=realAverageCounter12;
						realStacked12.setYValue(realAverage12);
						
						realAverage12 = 0;
						realAverageCounter12 = 0;
					}
					
					if(date.getHour()==0) {
						labStacked24.setYValue(oldValue);
						
						realAverage24/=realAverageCounter24;
						realStacked24.setYValue(realAverage24);
						
						realAverage24 = 0;
						realAverageCounter24 = 0;
					}
					
					date = date.plusMinutes(15);
					}
				});
				//realLineChart.setAnimated(true);
			}
		}
	}
	
	/**
	 * Stream for sending data to the detector of the complex
	 * @author olegk
	 */
	public class SendingThread extends Thread  {
		
		int[] randomChannels;
		
		@Override
		public void run() {
			
			updateThread.stop();
			
			dataComplex.getData().clear();
			
			pause(2);
			
			Platform.runLater(() -> {
				for(float f=1;f<7.0;f+=0.1) {
					Data<Number, Number> data = new XYChart.Data(f, 0);
					dataComplex.getData().add(data);
				}
			});
			
			ProcessThread process = new ProcessThread();
			process.start();
			
			Main.getLogger().info("Sending thread is run!");
			
			initRandomChannels();
			
			for(int packet = 0;packet<randomChannels.length;packet+=20){
				String sendPacket = getPacket(packet);
				
				Main.getLogger().info("Packet being sent: "+sendPacket);
				
				Main.getSerial().executeCommand(sendPacket);
				String result = "";
				
				for(int loopCounter = 0;loopCounter<5;loopCounter++){
					
					pause(10);
					
					result = Main.getSerial().getResult();
					
					Main.getLogger().info("Message from complex: "+result);
					
					if(result.contains("ImpulsesProcessing")) {
						Main.getLogger().info("The complex started working with pulses");
						break;
					}
					
					Main.getSerial().executeCommand(sendPacket);
				}			
				
				readResultPackets();
				
				pause(1);
			}
			
			pause(1);
			
			updateThread = new UpdateThread();
	        updateThread.start();
	        
			Main.getLogger().info("All data has been sent.");
		}
		
		/**
		 * Read result package from serial port for detect current state of analyzer
		 */
		private void readResultPackets() {
			String result = "";
			
			while(!result.contains("END")) {
				result = Main.getSerial().getResult();
				if(result.length()>8&&result.charAt(0)=='&'&&result.charAt(4)=='$') {
					
					try {
						int getChannel = Integer.valueOf(result.substring(1, 4)),
							getImpulses = Integer.valueOf(result.substring(5, 9));
						
						Main.getLogger().info("Channel: "+getChannel);
						Main.getLogger().info("Impulses: "+getImpulses);
						
						Platform.runLater(() -> {
							((XYChart.Data)dataComplex.getData().get(getChannel)).setYValue((int)((XYChart.Data)dataComplex.getData().get(getChannel)).getYValue()+getImpulses);
						});
					}catch(Exception e) {
						Main.getLogger().info(e.getMessage());
					}
				}
				pause(2);
			}
		}
		
		/**
		 * Forming packet with channels and impulses for sending to complex
		 * @param packet Specifying the package number (the index from which the countdown will start)
		 * @return String with packet data
		 */
		private String getPacket(int packet) {
			String sendPacket = "1";
			for(int channel = 1;channel<=20;channel++) {
				try { 
					if(randomChannels[packet+channel]>0) {
						int impulses = (int)((XYChart.Data)sample.getData().get(randomChannels[packet+channel])).getYValue();
						if(impulses>500) {
							for(int i = 0;i<impulses/500;i++)
								sendPacket += Main.getSerial().generateInpulses(randomChannels[packet+channel], 500);
							if(impulses%500 != 0) {
								sendPacket += Main.getSerial().generateInpulses(randomChannels[packet+channel], impulses%500);
							}
						}else {
							sendPacket += Main.getSerial().generateInpulses(randomChannels[packet+channel], impulses);
						}
					}
				}catch(Exception e) {
					Main.getLogger().error(e.getMessage());
				}
			}
			return sendPacket;
		}
		
		private void initRandomChannels() {
			randomChannels = new int[sample.getData().size()];
			
			for(int i=1;i<sample.getData().size();i++) {
				int channel = randInt(1, sample.getData().size());
				if(i-1>0) {
					Boolean found = false;
					for(int j=0;j<i;j++) {
						if(randomChannels[j] == channel) {
							found = true;
							break;
						}
					}
					if(found) {
						i--;
						continue;
					}
				}
				try {
					randomChannels[i-1] = channel;
				}catch(Exception e) {
					Main.getLogger().error(e.getMessage());
				}
			}
		}
		
		private void pause(double sec) {
			try {
				sleep((int)(sec * 1000));
			} catch (InterruptedException e) {
				Main.getLogger().error(e.getMessage());
			}
		}
		
		/**
		 * Returns a pseudo-random number between min and max, inclusive.
		 * The difference between min and max can be at most
		 * <code>Integer.MAX_VALUE - 1</code>.
		 *
		 * @param min Minimum value
		 * @param max Maximum value.  Must be greater than min.
		 * @return Integer between min and max, inclusive.
		 * @see java.util.Random#nextInt(int)
		 */
		public static int randInt(int min, int max) {

		    // NOTE: This will (intentionally) not run as written so that folks
		    // copy-pasting have to think about how to initialize their
		    // Random instance.  Initialization of the Random instance is outside
		    // the main scope of the question, but some decent options are to have
		    // a field that is initialized once and then re-used as needed or to
		    // use ThreadLocalRandom (if using at least Java 1.7).
		    // 
		    // In particular, do NOT do 'Random rand = new Random()' here or you
		    // will get not very good / not very random results.
		    Random rand = new Random();

		    // nextInt is normally exclusive of the top value,
		    // so add 1 to make it inclusive
		    int randomNum = rand.nextInt((max - min) + 1) + min;

		    return randomNum;
		}
	}
	
	/**
	 * Stream for sending data to the detector of the complex
	 * @author olegk
	 */
	public class ProcessThread extends Thread  {
		public void run() {
			Main.getLogger().info("Process thread is run!");
			
			Platform.runLater(() -> {
				AnchorPane waitPane = new AnchorPane();
				
				ProgressIndicator PI=new ProgressIndicator();  
		        PI.setMinHeight(150);
		        PI.setMinWidth(150);
	            
	            AnchorPane.setTopAnchor(PI, 20.0);
	            AnchorPane.setLeftAnchor(PI, 0.0);
	            AnchorPane.setRightAnchor(PI, 0.0);
	            AnchorPane.setBottomAnchor(PI, 0.0);
	            
		        waitPane.getChildren().add(PI);
		        
				Label messageProcess = new Label("Процесс отравки данных");
				messageProcess.setWrapText(true);
				messageProcess.setAlignment(Pos.BASELINE_CENTER);
				
				AnchorPane.setTopAnchor(messageProcess, 5.0);
	            AnchorPane.setLeftAnchor(messageProcess, 0.0);
	            AnchorPane.setRightAnchor(messageProcess, 0.0);
	            
		        waitPane.getChildren().add(messageProcess);
				
		        Scene scene = new Scene(waitPane,200,200);  
		        Stage stage = new Stage();
		        stage.setScene(scene);  
		        stage.setTitle("Отправка данных на устройство");  
		        stage.show();  
		        stage.setMaxHeight(250);
		        stage.setMinHeight(250);
		        stage.setMinWidth(250);
		        stage.setMaxWidth(250);
			});
		}
	}
	
}