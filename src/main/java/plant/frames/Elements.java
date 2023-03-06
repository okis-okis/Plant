package plant.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.TableModelEvent;

import plant.IFrame;
import plant.Main;

/**
 * Class for create frame for working with elements table
 * @author olegk
 * @version 0.1
 * @see IFrame
 */
public class Elements extends IFrame{
	
	/**
	 * Titles of columns
	 */
	private static Object[] columnNames = {"Номер элемента", "Наименование", "Ka", "Kb"};
	
	/**
	 * Constructor of Elements class
	 * @param title Title of frame
	 * @see IFrame
	 */
	public Elements() {
		super("Химические элементы");
		
		//Create panel with table, manage buttons and edit area
		panel = new JPanel(new BorderLayout());
						
		//Add data to table
		updateTable();
								
		panel.add(createManagePanel(), BorderLayout.SOUTH);
								
		setContentPane(panel);
								
		setVisible(true);
	}

	/**
	 * Process for table change event </br>
	 * This event need for table edit
	 */
	@Override
	public void tableChanged(TableModelEvent arg0) {
		updateTable();		
	}

	/**
	 * Update table of Elements</br>
	 * Get new data from database
	 */
	@Override
	protected void updateTable() {		
		Object[][] data = Main.getDB().getElements();
		table = new JTable(data, columnNames);
		
		table.getModel().addTableModelListener(this);
		
		if(sp != null) {
			panel.remove(sp);
		}
		
		panel.add(sp = new JScrollPane(table), BorderLayout.CENTER);
		panel.updateUI();		
	}

	@Override
	protected Object[] getRow(int number) {
		Object[] data = new Object[getColumnLength()];
		
		for(int i=0;i<getColumnLength();i++) {
			data[i] = table.getValueAt(number, i);
		}
		
		return data;
	}

	/**
	 * Function for create and filling in the control panel
	 * @return JPanel A filled panel containing manage buttons
	 * @see getUpdateActionListener(), getAddActionListener(), getDeleteActionListener()
	 */
	private JPanel createManagePanel() {
		//Manage panel
		JPanel managePanel = new JPanel(new FlowLayout());
		
		//Update button
		JButton updateTableBtn = new JButton("Обновить");
		updateTableBtn.addActionListener(getUpdateActionListener());
		managePanel.add(updateTableBtn);
						
		//Add button
		JButton addRow = new JButton("Добавить");
		addRow.addActionListener(getAddActionListener());
		managePanel.add(addRow);
					
		JButton editRow = new JButton("Изменить");
		editRow.addActionListener(getEditActionListener());
		managePanel.add(editRow);
		
		//Delete button
		JButton deleteRow = new JButton("Удалить");
		deleteRow.addActionListener(getDeleteActionListener());
		managePanel.add(deleteRow);
		
		return managePanel;
	}
	
	private ActionListener getDeleteActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){  
				int answer = JOptionPane.showConfirmDialog(null,
						"Вы уверены, что хотите удалить выбранные записи?",
						"Информационное окно", 
				        JOptionPane.YES_NO_OPTION);

				if(answer == JOptionPane.YES_OPTION) {
					for(int id: getSelectedID()) {
						if(Main.getDB().deleteElement(id) == false) {
							JOptionPane.showMessageDialog(null, "Произошла ошибка при удалении записи с идентификатором "+id, "Результат добавления", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				
				updateTable();
			}
		};
	}
	
	/**
	 * Action for update button
	 * @return ActionListener
	 * @see ActionListener
	 */
	private ActionListener getUpdateActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				updateTable();
			}
		};
	}
	
	/**
	 * Action for add button
	 * @return ActionListener
	 * @see ActionListener
	 */
	private ActionListener getAddActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				new ElementFrame(1);
			}
		};
	}
	
	/**
	 * Action for edit button
	 * @return ActionListener
	 * @see ActionListener
	 */
	private ActionListener getEditActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				int[] data = getSelectedID();
				if(data != null) {
					try {
						(new ElementFrame(0)).setEditContent();
					} catch (ParseException e1) {
						Main.getLogger().error(e1.getMessage());
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "Пожалуйста, выберите строку для редактирования!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
				}
		    }
		};
	}
	
	/**
	 * Get length of title columns array
	 * @return length of title column array
	 * @see columnNames
	 */
	public static int getColumnLength() {
		return columnNames.length;
	}
	
	private class ElementFrame extends JFrame{
		
		/**
		 * Mode of frame
		 * 0 - editing
		 * 1 and others - adding
		 */
		private int modeFrame;
		
		/**
		 * Main content panel for components
		 */
		private JPanel contentPanel;
		
		private JLabel errorLabel;
		
		private JSpinner elementNumber, Ka, Kb;
		
		private JTextField elementTitle;
		
		private int editElementNumber;
		
		private Object[] editData;
		
		/**
		 * Row number for content build
		 */
		private int row;
		
		public ElementFrame(int modeFrame) {
			super((modeFrame==0?"Редактирование ":"Добавление ")+"записи");
			
			errorLabel = new JLabel("Максимум 2 символа");
			errorLabel.setForeground(Color.red);
			errorLabel.setVisible(false);
			
			this.modeFrame = modeFrame;
			
			//Build up interface of frame
			setCustomizedPanel();
			
			//Add to close program event operation for close database connection
			this.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					//Main.getManagementController().setEnable(true);
				}
			});
			
			formSettings();
		}
		
		/**
		 * Get customized panel with components for adding or updating table
		 */
		private void setCustomizedPanel() {
			
			JPanel mainPanel = new JPanel(new BorderLayout());
			
			initContent();			
			
			//Add button section
			JButton sensorAction = new JButton((modeFrame==0?"Редактировать ":"Добавить"));
			
			//Clicking event by button processing
			
			sensorAction.addActionListener(modeFrame==0?getEditElementActionListener():getAddElementActionListener());
			
			mainPanel.add(contentPanel, BorderLayout.CENTER);
			mainPanel.add(sensorAction, BorderLayout.SOUTH);
			
			setContentPane(mainPanel);
		}
		
		/**
		 * Configure the form for the correct display
		 */
		private void formSettings() {
			//Main.getManagementController().setEnable(false);
			
			this.setEnabled(true);
             
			//Settings of frame
			this.setSize(350, 500);
			this.setVisible(true);
			
			this.setFocusable(true);
		}
		
		/**
		 * Build up interface of frame</br>
		 * Initialize all components
		 */
		private void initContent() {
			contentPanel = new JPanel(new GridBagLayout());
			
			initNumberSection();	
			initTitleSection();
			initEnergyLevelSection();
		}

		private void initNumberSection() {
			contentPanel.add(new JLabel("Выберите номер элемента"), Main.getManagementController().createGbc(0, row++));
			
			elementNumber = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0));
			
			contentPanel.add(elementNumber, Main.getManagementController().createGbc(0, row++));
		}
		
		private void initTitleSection() {
			contentPanel.add(new JLabel("Введите наименование элемента"), Main.getManagementController().createGbc(0, row++));
			contentPanel.add(new JLabel("(не более 2-х символов)"), Main.getManagementController().createGbc(0, row++));
			
			elementTitle = new JTextField();
			
			elementTitle.addKeyListener(new KeyAdapter() {
		        @Override
		        public void keyTyped(KeyEvent e) {
		            if (elementTitle.getText().length() >= 2 ) { // limit to 3 characters
		            	errorLabel.setVisible(true);
		            }else {
		            	errorLabel.setVisible(false);
		            }
		        }
		    });
			
			contentPanel.add(elementTitle, Main.getManagementController().createGbc(0, row++));
			contentPanel.add(errorLabel, Main.getManagementController().createGbc(0, row++));
		}

		private void initEnergyLevelSection() {
			contentPanel.add(new JLabel("Энергетический уровень Ka"), Main.getManagementController().createGbc(0, row++));
			
			Ka = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.001));
			
			contentPanel.add(Ka, Main.getManagementController().createGbc(0, row++));
			
			contentPanel.add(new JLabel("Энергетический уровень Kb"), Main.getManagementController().createGbc(0, row++));
			
			Kb = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.001));
			
			contentPanel.add(Kb, Main.getManagementController().createGbc(0, row++));
		}
		
		/**
		 * Function using for updating table.</br> 
		 * Set to fields values of table row by id.
		 * @param id int ID of table row for set to fields
		 * @throws ParseException Error handling
		 * @see ParseException
		 */
		public void setEditContent() throws ParseException {
			editData = getRow(table.getSelectedRow());
			
			this.editElementNumber = Integer.parseInt((String)editData[0]);
			
			elementNumber.setValue(Integer.parseInt((String)editData[0]));
			elementTitle.setText((String)editData[1]);
			Ka.setValue(Float.parseFloat((String)editData[2]));
			Kb.setValue(Float.parseFloat((String)editData[3]));
		}
		
		/**
		 * Close sub frame (this frame)
		 */
		protected void close() {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		
		/**
		 * Private method for processing the add new composition
		 * @return ActionListener with actions
		 */
		private ActionListener getEditElementActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					
					if(table.getSelectedRow() == -1) {
						JOptionPane.showMessageDialog(null, "Выберите редактируемый элемент!\nТаблица была обновлена", "Ошибка", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					float KaValue, KbValue; 
					
					if(Ka.getValue().getClass().getSimpleName().equals("Float")) {
						KaValue = (Float)Ka.getValue();
					}else {
						KaValue = ((Double)Ka.getValue()).floatValue();
					}
					
					if(Kb.getValue().getClass().getSimpleName().equals("Float")) {
						KbValue = (Float)Kb.getValue();
					}else {
						KbValue = ((Double)Kb.getValue()).floatValue();
					}
					
					int elemNum;
					
					if(elementNumber.getValue().getClass().getSimpleName().equals("Integer")) {
						elemNum = (Integer)elementNumber.getValue();
					}else {
						elemNum = ((Double)elementNumber.getValue()).intValue();
					}
					
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите изменить запись?\nВводимые параментры:"
							+"\nНомер элемента: "+editElementNumber+"->"+elemNum
							+"\nНаименование элемента: "+getRow(table.getSelectedRow())[1]+"->"+elementTitle.getText()
							+"\nЭнергетический уровень Ka: "+getRow(table.getSelectedRow())[2]+"->"+KaValue
							+"\nЭнергетический уровень Kb: "+getRow(table.getSelectedRow())[3]+"->"+KbValue,
							"Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка добавления записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;						
						
						if(Main.getDB().updateElement(
								editElementNumber,
								elemNum,
								elementTitle.getText(),
								KaValue,
								KbValue
								)
							){
							result = "Запись была обновлена";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
						}
						
						JOptionPane.showMessageDialog(null, result, "Результат:", messageCode);
						
						updateTable();
						
						//editElementNumber = ((Double)elementNumber.getValue()).intValue();
						
						close();
					}
			    }
			};
		}
		
		private ActionListener getAddElementActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					
					float KaValue, KbValue; 
					
					if(Ka.getValue().getClass().getSimpleName().equals("Float")) {
						KaValue = (Float)Ka.getValue();
					}else {
						KaValue = ((Double)Ka.getValue()).floatValue();
					}
					
					if(Kb.getValue().getClass().getSimpleName().equals("Float")) {
						KbValue = (Float)Kb.getValue();
					}else {
						KbValue = ((Double)Kb.getValue()).floatValue();
					}
					
					int elemNum;
					
					if(elementNumber.getValue().getClass().getSimpleName().equals("Integer")) {
						elemNum = (Integer)elementNumber.getValue();
					}else {
						elemNum = ((Double)elementNumber.getValue()).intValue();
					}
					
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что точно хотите добавить запись?\nВводимые параментры:"
							+"\nНомер элемента: "+((Double)elementNumber.getValue()).intValue()
							+"\nНаименование элемента: "+elementTitle.getText()
							+"\nЭнергетический уровень Ka: "+KaValue
							+"\nЭнергетический уровень Kb: "+KbValue,
							"Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка добавления записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;						
						
						if(Main.getDB().addElement(
								elemNum,
								elementTitle.getText(),
								KaValue,
								KbValue
								)
							){
							result = "Запись была добавлена в БД";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
						}
						
						JOptionPane.showMessageDialog(null, result, "Результат:", messageCode);
						
						updateTable();
						
						editElementNumber = ((Double)elementNumber.getValue()).intValue();
					}
			    }
			};
		}
	}
}
