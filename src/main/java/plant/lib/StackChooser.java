package plant.lib;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import plant.Main;
import plant.frames.Stacks;

public class StackChooser {
	private JFrame frame;
	
	private JPanel contentPanel;
	
	private JTable stackTable;
	
	private JScrollPane sp;
	
	private int gridRow;
	
	private JPanel managePanel;
	
	private JSpinner stackNumber;
	
	private JButton searchButton, chooseButton; 
	
	private int choosedID;
	private Boolean isChoosedID;
	
	public StackChooser() {
		choosedID = -1;
		isChoosedID = false;
		
		frame = new JFrame("Выберите операцию со штабелем");
		
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
		
		updateTable(Main.getDB().getStacks());
		initManagePanel();
	}
	
	private void initManagePanel() {
		managePanel = new JPanel(new GridBagLayout());
		
		initStackNumber();
		initManageButtons();
		
		GridBagConstraints gridBag = Main.getManagementController().createGbc(10, 0, 1, 5);
		gridBag.fill = GridBagConstraints.BOTH;
		contentPanel.add(managePanel, gridBag);
	}
	
	private void updateTable(Object[][] data) {
		stackTable = new JTable(data, Stacks.getColumns());
		
		stackTable.getColumn("Состав").setCellRenderer(new TableButton());
		stackTable.getColumn("Состав").setCellEditor(new CompositionButtonEditor(new JCheckBox()));
		
		if(sp != null) {
			contentPanel.remove(sp);
		}
		
		GridBagConstraints gridBag = Main.getManagementController().createGbc(0, 0, 9, 10);
		gridBag.fill = GridBagConstraints.BOTH;
		
		contentPanel.add(sp = new JScrollPane(stackTable), gridBag);
		contentPanel.updateUI();	
	}
	
	private void initStackNumber() {
		CollapsiblePanel searchNumber = new CollapsiblePanel("Поиск по номеру штабеля", Color.black);
		searchNumber.setLayout(new GridBagLayout());
		
		JCheckBox enableID = new JCheckBox("Использовать поиск по номеру?");
		
		enableID.addItemListener(new ItemListener() {  
			public void itemStateChanged(ItemEvent e) {         
				stackNumber.setEnabled(e.getStateChange()==1);
			}  
		});
		
		searchNumber.add(enableID, Main.getManagementController().createGbc(0, 0)); 
		stackNumber = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
		stackNumber.setEnabled(false);
		
		searchNumber.add(stackNumber, Main.getManagementController().createGbc(0, 1));
		
		managePanel.add(searchNumber, Main.getManagementController().createGbc(0, gridRow++));
	}
	
	private void initManageButtons() {
		JPanel manageButtonsPane = new JPanel(new GridBagLayout());
		
		searchButton = new JButton("Поиск");
		searchButton.addActionListener(getSearchActionListener());
		manageButtonsPane.add(searchButton, Main.getManagementController().createGbc(0, 2));
		
		chooseButton = new JButton("Выбрать");
		chooseButton.addActionListener(getChooseActionListener());
		manageButtonsPane.add(chooseButton, Main.getManagementController().createGbc(0, 3));
		
		managePanel.add(manageButtonsPane, Main.getManagementController().createGbc(0, gridRow++));
	}
	
	private ActionListener getSearchActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){ 
				Object number = null;
				if(stackNumber.isEnabled()) {
					try {
						number = stackNumber.getValue();
					}catch(Exception mess) {
						JOptionPane.showConfirmDialog(null, "Возможно, произошла какая-то ошибка. Перезапустите приложение!", "Сообщение об ошибке", 
								JOptionPane.ERROR_MESSAGE);
					}
				}
				
				updateTable(Main.getDB().getStacksWithFilter(number));
		  }
		};
	}
	
	private ActionListener getChooseActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){ 
				if(stackTable.getSelectedRow() != -1) {
					int id = Integer.parseInt((String)stackTable.getModel().getValueAt(stackTable.getSelectedRow(), 0));
					System.out.println("Choosed id: "+id);
					
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите выбрать запись с идентификатором "+id+"?",
							"Информационное окно", 
					    JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						choosedID = id;
						isChoosedID = true;
						System.out.println("Chooser mode: "+isChoosedID);
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
	
	public void destroy() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
}
