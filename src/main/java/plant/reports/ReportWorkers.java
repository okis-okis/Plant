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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;

import plant.Main;
import plant.lib.CollapsiblePanel;

/**
 * Generate workers report
 * @author olegk
 */
public class ReportWorkers{
	private JFrame frame; 
	private JPanel panel, filterPanel;
	private JSpinner idField;
	private JTextField name;
	private JComboBox positions;
	private JScrollPane sp;
	private JTextPane jtp;
	private JCheckBox enableID, enablePosition;
	
	/**
	 * Class constructor
	 * @param title (String) - Frame title
	 */
	public ReportWorkers(){
		frame = new JFrame("Отчёт по работникам");
		panel = new JPanel(new BorderLayout());
		
		initFilterPanel();
	    panel.add(filterPanel, BorderLayout.LINE_END);
	    
		updateResult();
		manageButtons();
		
		frame.add(panel);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
	
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
	    		+ "  padding: 8px; "
	    		+ "}"
	    		+ "table tr:nth-child(even){background-color: #f2f2f2;}"
	    		+ "table th {"
	    		+ "  padding-top: 12px;"
	    		+ "  padding-bottom: 12px;"
	    		+ "  text-align: left;"
	    		+ "  background-color: black;"
	    		+ "  color: white;"
	    		+ "}"
	    		+ "</style>"
	    		+ "<center><h2>ДАННЫЕ О РАБОТНИКАХ ПРЕДПРИЯТИЯ ЮГМК</h2></center>"
	    		+ "<p>по состоянию на: "+(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).format((Calendar.getInstance()).getTime())+"</p>"
	    		+ "<table><tr>\r\n"
	    		+ "<th>Идентификатор</th>\r\n"
	    		+ "<th>Работник</th>\r\n"
	    		+ "<th>Должность</th>\r\n"
	    		+ "</tr>";
	    
	    Object id = null, fullName = null, position = null;
	    
	    if(enableID.isSelected()) {
	    	id = idField.getValue();
	    }
	    
	    if(!name.getText().equals("")) {
	    	fullName = name.getText();
	    }
	    
	    if(enablePosition.isSelected()) {
	    	position = positions.getSelectedItem(); 
	    }
	    
	    Object[][] data = Main.getDB().getWorkersWithFilter(id, fullName, position);
	    
	    if(data == null) {
	    	return;
	    }
	    
	    for(Object[] row: data) {
	    	printHtml+="<tr>\r\n"
	    			+ "<td>"+row[0]+"</td>\r\n"
	    			+ "<td>"+row[1]+"</td>\r\n"
	    			+ "<td>"+row[2]+"</td>\r\n"
	    			+ "</tr>";
	    }
	    
	    printHtml+="</table><p><p></html>";
	    jtp.setText(printHtml);
	    jtp.setEditable(false);
	    
	    if(sp != null) {
	    	panel.remove(sp);
	    }
	    
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
	    
	    CollapsiblePanel searchID = new CollapsiblePanel("Поиск по id", Color.black);
		searchID.setLayout(new GridBagLayout());
		
		enableID = new JCheckBox("Использовать поиск по id?");
		
		enableID.addItemListener(new ItemListener() {  
			public void itemStateChanged(ItemEvent e) {         
				idField.setEnabled(e.getStateChange()==1);
			}  
	    });
		
		searchID.add(enableID, Main.getManagementController().createGbc(0, 0)); 
		idField = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 1.0));
		idField.setEnabled(false);
		
		searchID.add(idField, Main.getManagementController().createGbc(0, 1));
		
		filterPanel.add(searchID, Main.getManagementController().createGbc(0, 0));
		
		CollapsiblePanel searchName = new CollapsiblePanel("Поиск по имени", Color.black);
		
		name = new JTextField(20);
		
		name.addKeyListener(new java.awt.event.KeyAdapter() {
	        public void keyTyped(java.awt.event.KeyEvent evt) {
	        	
	            if(!(Character.isLetter(evt.getKeyChar())) && !Character.isSpaceChar(evt.getKeyChar()) && Character.compare(evt.getKeyChar(), '.') != 0){
	                   evt.consume();
	               }
	           }
	       });
		
		searchName.add(name, BorderLayout.CENTER);
	    
		filterPanel.add(searchName, Main.getManagementController().createGbc(0, 1));
		
		CollapsiblePanel searchPositions = new CollapsiblePanel("Поиск по должности", Color.black);
		searchPositions.setLayout(new GridBagLayout());
		positions = new JComboBox();
		
		for(Object[] row: Main.getDB().getPositions()) {
			positions.addItem((String)row[1]);
		}
		
		try {
			positions.setSelectedIndex(0);
		}catch(Exception e) {
			JOptionPane.showConfirmDialog(null, "Нет доступных должностей", "Ошибка!", 
					JOptionPane.ERROR_MESSAGE);
		}
		
		enablePosition = new JCheckBox("Использовать поиск по должности?");
		
		enablePosition.addItemListener(new ItemListener() {  
			public void itemStateChanged(ItemEvent e) {         
				positions.setEnabled(e.getStateChange()==1);
			}  
	    });
		
		positions.setEnabled(false);
		
		searchPositions.add(enablePosition, Main.getManagementController().createGbc(0, 0));
		searchPositions.add(positions, Main.getManagementController().createGbc(0, 1));
		
		filterPanel.add(searchPositions, Main.getManagementController().createGbc(0, 2));
		
		JButton searchButton = new JButton("Поиск");
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateResult();
			}
		});
		
		filterPanel.add(searchButton, Main.getManagementController().createGbc(0, 3));
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
					jtp.print();
				} catch (PrinterException e) {
					Main.getLogger().error(e.getMessage());
					JOptionPane.showConfirmDialog(null, e.getMessage(), "Возникла ошибка печати", 
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		managePanel.add(printButton);
		
		panel.add(managePanel, BorderLayout.PAGE_END);
	}
}
