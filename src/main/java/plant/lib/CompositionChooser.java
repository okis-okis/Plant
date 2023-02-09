package plant.lib;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import com.toedter.calendar.JDateChooser;

import plant.Main;
import plant.frames.MaterialComposition;
import plant.lib.CollapsiblePanel;

public class CompositionChooser{

	private JFrame frame;
	
	private JPanel contentPanel;
	
	private JTable compositionTable;
	
	private JSpinner idField;
	
	private JDateChooser startDate, finishDate;
	
	private JButton filterClearButton, updateButton, searchButton, chooseButton; 
	
	private JScrollPane sp;
	
	private int gridRow;
	
	private JPanel managePanel;
	
	private int choosedID;
	private Boolean isChoosedID;
	
	public CompositionChooser() {
		choosedID = -1;
		isChoosedID = false;
		
		frame = new JFrame("Выберите состав материала");
		
		initContent();
		
		frame.setContentPane(contentPanel);
		
		frame.setSize(800, 700);
		frame.setVisible(true);
	}
	
	public Boolean isSetChoosedID() {
		return isChoosedID;
	}
	
	public int getChoosedID() {
		return choosedID;
	}
	
	private void initContent() {
		
		contentPanel = new JPanel(new GridBagLayout());
		
		gridRow = 0;
		
		updateTable(Main.getDB().getCompositionsWithWorker());
		initManagePanel();
	}
	
	private void updateTable(Object[][] data) {
		compositionTable = new JTable(data, MaterialComposition.getColumns());
		
		if(sp != null) {
			contentPanel.remove(sp);
		}
		
		GridBagConstraints gridBag = Main.getManagementController().createGbc(0, 0, 9, 10);
		gridBag.fill = GridBagConstraints.BOTH;
		
		contentPanel.add(sp = new JScrollPane(compositionTable), gridBag);
		contentPanel.updateUI();	
	}
	
	private void initManagePanel() {
		managePanel = new JPanel(new GridBagLayout());
		
		initIDSearch();
		initDateSearch();
		initManageButtons();
		
		GridBagConstraints gridBag = Main.getManagementController().createGbc(10, 0, 1, 5);
		gridBag.fill = GridBagConstraints.BOTH;
		contentPanel.add(managePanel, gridBag);
	}
	
	private void initManageButtons() {
		JPanel manageButtonsPane = new JPanel(new GridBagLayout());
		
		filterClearButton = new JButton("Очистить фильтр");
		filterClearButton.addActionListener(getFilterClearActionListener());
		manageButtonsPane.add(filterClearButton, Main.getManagementController().createGbc(0, 0));
		
		updateButton = new JButton("Обновить");
		updateButton.addActionListener(getUpdateActionListener());
		updateButton.setToolTipText("Обновление без учёта фильтра");
		manageButtonsPane.add(updateButton, Main.getManagementController().createGbc(0, 1));
		
		searchButton = new JButton("Поиск");
		searchButton.addActionListener(getSearchActionListener());
		manageButtonsPane.add(searchButton, Main.getManagementController().createGbc(0, 2));
		
		chooseButton = new JButton("Выбрать");
		chooseButton.addActionListener(getChooseActionListener());
		manageButtonsPane.add(chooseButton, Main.getManagementController().createGbc(0, 3));
		
		managePanel.add(manageButtonsPane, Main.getManagementController().createGbc(0, gridRow++));
	}

	private void initDateSearch() {
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
		
		managePanel.add(dateSearch, Main.getManagementController().createGbc(0, gridRow++));
	}

	private void initIDSearch() {
		CollapsiblePanel searchID = new CollapsiblePanel("Поиск по id", Color.black);
		searchID.setLayout(new GridBagLayout());
		
		JCheckBox enableID = new JCheckBox("Использовать поиск по id?");
		
		enableID.addItemListener(new ItemListener() {  
			public void itemStateChanged(ItemEvent e) {         
				idField.setEnabled(e.getStateChange()==1);
			}  
	    });
		
		searchID.add(enableID, Main.getManagementController().createGbc(0, 0)); 
		idField = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 1.0));
		idField.setEnabled(false);
		
		searchID.add(idField, Main.getManagementController().createGbc(0, 1));
		
		managePanel.add(searchID, Main.getManagementController().createGbc(0, gridRow++));
	}
	
	private ActionListener getFilterClearActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){ 
				idField.setValue(0);
				startDate.setDate(null);
				finishDate.setDate(null);
		  }
		};
	}
	
	private ActionListener getUpdateActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){ 
				updateTable(Main.getDB().getCompositionsWithWorker());
		  }
		};
	}
	
	private ActionListener getSearchActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){ 
				Object id = null;
				if(idField.isEnabled()) {
					
					try {
						if(idField.getValue().getClass().getSimpleName()=="Integer") {
							id = idField.getValue();
						} else {
							id = ((Double)idField.getValue()).intValue();
						}
					}catch(Exception mess) {
						JOptionPane.showConfirmDialog(null, "Возможно, произошла какая-то ошибка. Перезапустите приложение!", "Сообщение об ошибке", 
								JOptionPane.ERROR_MESSAGE);
					}
				}
				
				updateTable(Main.getDB().getCompositionWithFilter(id, getStartDate(), getFinishDate()));
		  }
		};
	}
	
	private ActionListener getChooseActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){ 
				if(compositionTable.getSelectedRow() != -1) {
					int id = Integer.parseInt((String)compositionTable.getModel().getValueAt(compositionTable.getSelectedRow(), 0));

					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите выбрать запись с идентификатором "+id+"?",
							"Информационное окно", 
					    JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						choosedID = id;
						isChoosedID = true;
					}else {
						JOptionPane.showConfirmDialog(null, "Отправка состава была отменена!", "Сообщение об ошибке", 
								JOptionPane.ERROR_MESSAGE);
					}
					
					return;
				}
				JOptionPane.showConfirmDialog(null, "Вы не выбрали строку!", "Сообщение об ошибке", 
						JOptionPane.ERROR_MESSAGE);
		  }
		};
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
	
	public void destroy() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
}
