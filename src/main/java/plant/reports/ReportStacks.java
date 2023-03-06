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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;

import com.toedter.calendar.JDateChooser;

import plant.Main;
import plant.lib.CollapsiblePanel;

/**
 * Generate stacks report
 * @author olegk
 */
public class ReportStacks{
	private JFrame frame; 
	private JPanel panel, filterPanel;
	private JScrollPane sp;
	private JTextPane jtp;
	private JCheckBox enableStack, enableWorkers;
	private JSpinner stackNumber;
	private JComboBox workers;
	private JDateChooser startDate, finishDate;
	
	/**
	 * Class constructor
	 * @param title (String) - Frame title
	 */
	public ReportStacks(){
		frame = new JFrame("Паспорта хим. состава на штабели");
		panel = new JPanel(new BorderLayout());
		
		initFilterPanel();
		panel.add(filterPanel, BorderLayout.LINE_END);
		updateResult();
		manageButtons();
		
		frame.add(panel);
		frame.setSize(500, 400);
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
	    		+ "tr:nth-child(even){background-color: #f2f2f2;}"		
	    		+ "</style>"
	    		+ "<center><h2>ПАСПОРТА НА ШТАБЕЛИ С ХИМИЧЕСКИМ СОСТАВОМ (ПРЕДПРИЯТИЕ ЮГМК)</h2></center>"
	    		+ "<p>по состоянию на: "+(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).format((Calendar.getInstance()).getTime())+"</p>"
	    		+ "<table><tr>"
	    		+ "<th rowspan=\"2\">ID</th>"
	    		+ "<th rowspan=\"2\">Номер \nштабеля</th>"
	    		+ "<th colspan=\"4\">Состав (в-ва указаны в %)</th>"
	    		+ "<th rowspan=\"2\">Дата и время поступления на склад</th>"
	    		+ "</tr>"
	    		+ "<tr>"
	    		+ "<td class=\"composition\">id</td>"
	    		+ "<td class=\"composition\">Содержание</td>"
	    		+ "<td class=\"composition\">Проверил</td>"
	    		+ "<td class=\"composition\">Дата и время проверки</td>"
	    		+ "</tr>";
	    
	    Object numberStackFilter = null, workerFilter = null, startDateFilter = null, finishDateFilter = null;  
	    
	    if(enableStack.isSelected()) {
	    	numberStackFilter = stackNumber.getValue();
	    }
	    
	    if(enableWorkers.isSelected()) {
	    	workerFilter = workers.getSelectedItem();
	    }
	    
	    Object[][] data = Main.getDB().getStacksWithFilter(numberStackFilter, workerFilter, getStartDate(), getFinishDate());
	    
	    if(data == null) {
	    	return;
	    }
	    
	    for(Object[] row: data) {
	    	printHtml+="<tr>";
	    	int counter = 0;
	    	for(Object item: row) {
	    		if(counter>2&&counter<10) {   			
	    			switch(counter) {
	    			case 3:
	    				printHtml+="<td>MgO: ";
	    				break;
	    			case 4:
	    				printHtml+="CaO: ";
	    				break;
	    			case 5:
	    				printHtml+="Al<sub>2</sub>O<sub>3</sub>: ";
	    				break;
	    			case 6:
	    				printHtml+="SiO<sub>2</sub>: ";
	    				break;
	    			case 7:
	    				printHtml+="P: ";
	    				break;
	    			case 8:
	    				printHtml+="S: ";
	    				break;
	    			case 9:
	    				printHtml+="Fe &sum;: ";
	    				break;
	    			}
	    			printHtml+=item+"%<br>";
	    			if(counter==9) {
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
	
	private String getStartDate() {
		String result = null;
		if(startDate.getDate() != null) {
			result = (new SimpleDateFormat("yyyy-MM-dd")).format(startDate.getDate());
			result+= " 00:00:00";
		}
		return result;
	}
	
	private String getFinishDate() {
		String result = null;
		if(finishDate.getDate() != null) {
			result = (new SimpleDateFormat("yyyy-MM-dd")).format(finishDate.getDate());
			result+= " 23:59:59";
		}
		return result;
	}
	
	private void initFilterPanel() {
		filterPanel = new JPanel(new GridBagLayout());
		
		CollapsiblePanel searchStack = new CollapsiblePanel("Поиск по номеру штабеля", Color.black);
		searchStack.setLayout(new GridBagLayout());
		
		enableStack = new JCheckBox("Использовать поиск по номеру штабеля?");
		
		enableStack.addItemListener(new ItemListener() {  
			public void itemStateChanged(ItemEvent e) {         
				stackNumber.setEnabled(e.getStateChange()==1);
			}  
	    });
		
		searchStack.add(enableStack, Main.getManagementController().createGbc(0, 0)); 
		stackNumber = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
		stackNumber.setEnabled(false);
		
		searchStack.add(stackNumber, Main.getManagementController().createGbc(0, 1));
		
		filterPanel.add(searchStack, Main.getManagementController().createGbc(0, 0));
		
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
		
		filterPanel.add(searchWorkers, Main.getManagementController().createGbc(0, 1));
		
		CollapsiblePanel dateSearch = new CollapsiblePanel("Поиск по дате", Color.black);
		dateSearch.setLayout(new GridBagLayout());
		
		dateSearch.add(new JLabel("Начиная с"), Main.getManagementController().createGbc(0, 0));
		startDate = new JDateChooser();
		startDate.setDateFormatString("yyyy-MM-dd");
		dateSearch.add(startDate, Main.getManagementController().createGbc(0, 1));
		
		
		dateSearch.add(new JLabel("Заканчивая"), Main.getManagementController().createGbc(0, 2));
		finishDate = new JDateChooser();
		finishDate.setDateFormatString("yyyy-MM-dd");
		dateSearch.add(finishDate, Main.getManagementController().createGbc(0, 3));
		
		filterPanel.add(dateSearch, Main.getManagementController().createGbc(0, 2));
		
		JButton searchButton = new JButton("Поиск");
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateResult();
			}
		});
		
		filterPanel.add(searchButton, Main.getManagementController().createGbc(0, 3));
	}
}
