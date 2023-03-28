package plant.reports;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.PrinterException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;

import plant.Main;
import plant.lib.CollapsiblePanel;

/**
 * Generate vans report
 * @author olegk
 */
public class ReportVans{

	private JFrame frame; 
	private JPanel panel, filterPanel;
	private JScrollPane sp;
	private JTextPane jtp;
	private JCheckBox enableVan, enableEchelon, enableMines, enableWorkers;
	private JSpinner vanNumber, echelonNumber;
	private JComboBox mines, workers;
	
	/**
	 * Class constructor
	 * @param title (String) - Frame title
	 */
	public ReportVans(){
		frame = new JFrame("Паспорта хим. состава на вагоны");
		panel = new JPanel(new BorderLayout());
		
		initFilterPanel();
	    panel.add(filterPanel, BorderLayout.LINE_END);
	    
	    updateResult();
		manageButtons();
		
		frame.add(panel);
		frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	/**
	 * Create manage panel with buttons
	 */
	private void manageButtons() {
		JPanel managePanel = new JPanel(new FlowLayout());
		
		JButton updateButton = new JButton("Обновить");
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateResult();
			}
		});
		managePanel.add(updateButton);
		
		JButton printButton = new JButton("Печать");
		printButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					JOptionPane.showConfirmDialog(null, "Рекомендуется печатать страницу с горизонтальной ориентацией!", "Внимание!", 
							JOptionPane.OK_OPTION);
					PrintRequestAttributeSet attr_set = new HashPrintRequestAttributeSet();
                    attr_set.add(MediaSizeName.ISO_A4);
                    attr_set.add(new MediaPrintableArea(0.5f, 0.5f, 7.268f, 10.693f, MediaPrintableArea.INCH));
                    attr_set.add(OrientationRequested.LANDSCAPE);                    
                    jtp.print(null, null, true, null, attr_set, true);
				} catch (PrinterException e) {
					Main.getLogger().error(e.getMessage());
				}
			}
		});
		managePanel.add(printButton);
		panel.add(managePanel, BorderLayout.PAGE_END);
	}
	
	/**
	 * Function for print selected in table row(-s)
	 */
	private void updateResult() {
		jtp = new JTextPane();
	    jtp.setContentType("text/html");
	    String printHtml = "<html><style>"
	    		+ "table {"
	    		+ "  font-family: Arial, Helvetica, sans-serif;"
	    		+ "  border-collapse: collapse;"
	    		+ "  width: 100%;"
	    		+ "}"
	    		+ "table td, table th {"
	    		+ "  border: 1px solid #ddd;"
	    		+ "}"
	    		+ "table tr:nth-child(even){background-color: #f2f2f2;}"
	    		+ "table th, .composition {"
	    		+ "  text-align: left;"
	    		+ "  background-color: black;"
	    		+ "  color: white;"
	    		+ "}"
	    		+ "</style>"
	    		+ "<center><h2>ПАСПОРТА НА СОДЕРЖИМОЕ ВАГОНОВ С ХИМИЧЕСКИМ СОСТАВОМ ПРЕДПРИЯТИЯ ЮГМК</h2></center>"
	    		+ "<p>по состоянию на: "+(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).format((Calendar.getInstance()).getTime())+"</p>"
	    		+ "<table><tr>"
	    		+ "<th rowspan=\"2\">ID</th>"
	    		+ "<th rowspan=\"2\">Номер \nвагона</th>"
	    		+ "<th rowspan=\"2\">Номер \nэшелона</th>"
	    		+ "<th rowspan=\"2\">Рудник</th>"
	    		+ "<th rowspan=\"2\">Вес \nсодержимого \nвагона\n(т)</th>"
	    		+ "<th colspan=\"4\">Состав (в-ва указаны в %)</th>"
	    		+ "<th rowspan=\"2\">Отправлен на штабель</th>"
	    		+ "</tr>"
	    		+ "<tr>"
	    		+ "<td class=\"composition\">id</td>"
	    		+ "<td class=\"composition\">Содержание</td>"
	    		+ "<td class=\"composition\">Проверил</td>"
	    		+ "<td class=\"composition\">Дата и время проверки</td>"
	    		+ "</tr>";
	    
	    Object vanNumberFilter = null, echelonNumberFilter = null, minesFilter = null, workerFilter = null;
	    
	    if(enableVan.isSelected()) {
	    	vanNumberFilter = vanNumber.getValue();
	    }
	    
	    if(enableEchelon.isSelected()) {
	    	echelonNumberFilter = echelonNumber.getValue();
	    }
	    
	    if(enableMines.isSelected()) {
	    	minesFilter = mines.getSelectedItem();
	    }
	    
	    if(enableWorkers.isSelected()) {
	    	workerFilter = workers.getSelectedItem();
	    }
	    
	    Object[][] data = Main.getDB().getVanPassportWithFilter(vanNumberFilter, echelonNumberFilter, minesFilter, workerFilter);
	    
	    if(data == null) {
	    	return;
	    }
	    
	    for(Object[] row: data) {
	    	printHtml+="<tr>";
	    	int counter = 0;
	    	for(Object item: row) {
	    		if(counter>5&&counter<13) {   			
	    			switch(counter) {
	    			case 6:
	    				printHtml+="<td>MgO: ";
	    				break;
	    			case 7:
	    				printHtml+="CaO: ";
	    				break;
	    			case 8:
	    				printHtml+="Al<sub>2</sub>O<sub>3</sub>: ";
	    				break;
	    			case 9:
	    				printHtml+="SiO<sub>2</sub>: ";
	    				break;
	    			case 10:
	    				printHtml+="P: ";
	    				break;
	    			case 11:
	    				printHtml+="S: ";
	    				break;
	    			case 12:
	    				printHtml+="Fe &sum;: ";
	    				break;
	    			}
	    			printHtml+=item+"%<br>";
	    			if(counter==12) {
	    				printHtml+="</td>";
	    			}
	    		}
	    		else {
	    			printHtml+="<td>"+item+"</td>";
	    		}
	    		counter++;
	    	}
	    }
	    
	    printHtml+="</table><p><p></html>";
	    jtp.setText(printHtml);
	    
	    if(sp != null) {
	    	panel.remove(sp);
	    }
	    jtp.setEditable(false);
	    sp = new JScrollPane(jtp);
	    javax.swing.SwingUtilities.invokeLater(new Runnable() {
	    	   public void run() { 
	    	       sp.getVerticalScrollBar().setValue(0);
	    	   }
	    	});
		
	    panel.add(sp, BorderLayout.CENTER);
	    panel.updateUI();
	}
	
	private void initFilterPanel() {
		filterPanel = new JPanel(new GridBagLayout());
		
		CollapsiblePanel searchVan = new CollapsiblePanel("Поиск по номеру вагона", Color.black);
		searchVan.setLayout(new GridBagLayout());
		
		enableVan = new JCheckBox("Использовать поиск по номеру вагона?");
		
		enableVan.addItemListener(new ItemListener() {  
			public void itemStateChanged(ItemEvent e) {         
				vanNumber.setEnabled(e.getStateChange()==1);
			}  
	    });
		
		searchVan.add(enableVan, Main.getManagementController().createGbc(0, 0)); 
		vanNumber = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
		vanNumber.setEnabled(false);
		
		searchVan.add(vanNumber, Main.getManagementController().createGbc(0, 1));
		
		filterPanel.add(searchVan, Main.getManagementController().createGbc(0, 0));
		
		CollapsiblePanel searchEchelon = new CollapsiblePanel("Поиск по номеру эшелона", Color.black);
		searchEchelon.setLayout(new GridBagLayout());
		
		enableEchelon = new JCheckBox("Использовать поиск по номеру эшелона?");
		
		enableEchelon.addItemListener(new ItemListener() {  
			public void itemStateChanged(ItemEvent e) {         
				echelonNumber.setEnabled(e.getStateChange()==1);
			}  
	    });
		
		searchEchelon.add(enableEchelon, Main.getManagementController().createGbc(0, 0)); 
		echelonNumber = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
		echelonNumber.setEnabled(false);
		
		searchEchelon.add(echelonNumber, Main.getManagementController().createGbc(0, 1));
		
		filterPanel.add(searchEchelon, Main.getManagementController().createGbc(0, 1));
		
		CollapsiblePanel searchMines = new CollapsiblePanel("Поиск по руднику", Color.black);
		searchMines.setLayout(new GridBagLayout());
		mines = new JComboBox();
		
		for(Object[] row: Main.getDB().getMines()) {
			mines.addItem((String)row[1]);
		}
		
		try {
			mines.setSelectedIndex(0);
		}catch(Exception e) {
			JOptionPane.showConfirmDialog(null, "Нет доступных рудников", "Ошибка!", 
					JOptionPane.ERROR_MESSAGE);
		}
		
		enableMines = new JCheckBox("Использовать поиск по рудникам?");
		
		enableMines.addItemListener(new ItemListener() {  
			public void itemStateChanged(ItemEvent e) {         
				mines.setEnabled(e.getStateChange()==1);
			}  
	    });
		
		mines.setEnabled(false);
		
		searchMines.add(enableMines, Main.getManagementController().createGbc(0, 0));
		searchMines.add(mines, Main.getManagementController().createGbc(0, 1));
		
		filterPanel.add(searchMines, Main.getManagementController().createGbc(0, 2));
		
		CollapsiblePanel searchWorkers = new CollapsiblePanel("Поиск по проверяющему", Color.black);
		searchWorkers.setLayout(new GridBagLayout());
		workers = new JComboBox();
		
		for(Object[] row: Main.getDB().getWorkers()) {
			workers.addItem((String)row[1]);
		}
		
		try {
			workers.setSelectedIndex(0);
		}catch(Exception e) {
			JOptionPane.showConfirmDialog(null, "Вы не зарегистрировали ни одного работника!", "Ошибка!", 
					JOptionPane.ERROR_MESSAGE);
		}
		
		enableWorkers = new JCheckBox("Использовать поиск по работнику?");
		
		enableWorkers.addItemListener(new ItemListener() {  
			public void itemStateChanged(ItemEvent e) {         
				workers.setEnabled(e.getStateChange()==1);
			}  
	    });
		
		workers.setEnabled(false);
		
		searchWorkers.add(enableWorkers, Main.getManagementController().createGbc(0, 0));
		searchWorkers.add(workers, Main.getManagementController().createGbc(0, 1));
		
		filterPanel.add(searchWorkers, Main.getManagementController().createGbc(0, 3));
		
		JButton searchButton = new JButton("Поиск");
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateResult();
			}
		});
		
		filterPanel.add(searchButton, Main.getManagementController().createGbc(0, 4));
	}
}
