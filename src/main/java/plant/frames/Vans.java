package plant.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;

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
import javax.swing.event.TableModelEvent;

import plant.IFrame;
import plant.Main;
import plant.lib.CompositionButtonEditor;
import plant.lib.CompositionChooser;
import plant.lib.CustomComboBox;
import plant.lib.TableButton;

/**
 * Class for create frame for working with van table
 * @author olegk
 * @version 0.1
 * @see IFrame
 */
public class Vans extends IFrame{

	/**
	 * Titles of columns
	 */
	private static Object[] columnNames = {"Идентификатор", "Номер вагона", "Эшелон", "Вес", "Состав", "Штабель"};
	
	/**
	 * Constructor of Vans class
	 * @param title Title of frame
	 * @see IFrame
	 */
	public Vans() {
		super("Вагоны");

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
		
		panel = new JPanel(new BorderLayout());
		
		//Add data to table
		updateTable();
								
		panel.add(createManagePanel(), BorderLayout.SOUTH);
								
		setContentPane(panel);
								
		setVisible(true);
	}

	/**
	 * Update table of Workers</br>
	 * Get new data from database
	 */
	@Override
	protected void updateTable() {
		Object[][] data = Main.getDB().getVans();
		table = new JTable(data, columnNames);
		
		table.getModel().addTableModelListener(this);
		
		table.getColumn("Состав").setCellRenderer(new TableButton());
	    table.getColumn("Состав").setCellEditor(new CompositionButtonEditor(new JCheckBox()));
	    
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
	
	/**
	 * Get count of columns in vans table
	 * @return int value with result 
	 */
	public static int getColumnLength() {
		return columnNames.length;
	}
	
	/**
	 * Private method for processing the update button
	 * @return ActionListener with actions
	 */
	private ActionListener getUpdateActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				updateTable();
			}
		};
	}
	
	/**
	 * Private method for processing the add button
	 * @return ActionListener with actions
	 */
	private ActionListener getAddActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				new VanFrame(1);
			}
		};
	}
	
	private ActionListener getEditActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				int[] data = getSelectedID();
				if(data != null) {
					try {
						(new VanFrame(0)).setEditContent();
					} catch (ParseException e1) {
						Main.getLogger().error(e1.getMessage());
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "Пожалуйста, выберите строку для редактирования!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
				}
		    }
		};
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
						if(Main.getDB().deleteVan(id) == false) {
							JOptionPane.showMessageDialog(null, "Произошла ошибка при удалении записи с идентификатором "+id, "Результат добавления", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				
				updateTable();
			}
		};
	}
	
	private class VanFrame extends JFrame{
		
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
		
		private JSpinner vanNumber, weight, compositionIDField, stackNumber;
		private CompositionChooser chooser;
		
		private CustomComboBox echelon;
		
		private CompositionNumberThread thread;
		
		private Object[] editData;
		
		/**
		 * Row number for content build
		 */
		private int row;
		
		public VanFrame(int modeFrame) {
			super((modeFrame==0?"Редактирование ":"Добавление ")+"записи");
			
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
			
			sensorAction.addActionListener(modeFrame==0?getEditActionListener():getAddActionListener());
			
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
			this.setSize(400, 450);
			this.setVisible(true);
			
			this.setFocusable(true);
		}
		
		/**
		 * Build up interface of frame</br>
		 * Initialize all components
		 */
		private void initContent() {
			contentPanel = new JPanel(new GridBagLayout());
			
			row = 0;
			
			initVanNumber();
			initEchelonNumber();
			initWeightSection();
			initCompositionSection();
			initStackNumber();
		}
		
		private void initVanNumber() {
			contentPanel.add(new JLabel("Выберите номер вагона"), Main.getManagementController().createGbc(0, row++, 2, 1));
			
			vanNumber = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
			
			contentPanel.add(vanNumber, Main.getManagementController().createGbc(0, row++, 2, 1));
		}
		
		private void initEchelonNumber() {
			contentPanel.add(new JLabel("Укажите номер эшелона"), Main.getManagementController().createGbc(0, row++, 2, 1));
			
			echelon = new CustomComboBox();
			
			for(Object[] row: Main.getDB().getEchelonNumbers()) {
				echelon.addID(Integer.parseInt((String) row[0]));
				echelon.addItem((String)row[1]);
			}
			
			contentPanel.add(echelon, Main.getManagementController().createGbc(0, row++));
		}
		
		private void initWeightSection() {
			contentPanel.add(new JLabel("Укажите вес содержимого вагона (тонны)"), Main.getManagementController().createGbc(0, row++, 2, 1));
			
			weight = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 0.1));
			
			contentPanel.add(weight, Main.getManagementController().createGbc(0, row++, 2, 1));
		}
		
		private void initCompositionSection() {
			contentPanel.add(new JLabel("Выберите состав агломерата"), Main.getManagementController().createGbc(0, row++, 2, 1));
			compositionIDField = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 1.0));
			contentPanel.add(compositionIDField, Main.getManagementController().createGbc(0, row));
			
			JButton openCompositionButton = new JButton("Выбрать");
			openCompositionButton.addActionListener(getCompositionIDActionListener());			
			contentPanel.add(openCompositionButton, Main.getManagementController().createGbc(1, row++));
		}
		
		private ActionListener getCompositionIDActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					chooser = new CompositionChooser();
					thread = new CompositionNumberThread();
					thread.start();
			    }
			};
		}
		
		private void initStackNumber() {
			contentPanel.add(new JLabel("Выберите номер штабеля"), Main.getManagementController().createGbc(0, row++, 2, 1));
			
			stackNumber = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
			
			contentPanel.add(stackNumber, Main.getManagementController().createGbc(0, row++, 2, 1));
		}
		
		/**
		 * Function using for updating table.</br> 
		 * Set to fields values of table row by id.
		 * @throws ParseException Error handling
		 * @see ParseException
		 */
		public void setEditContent() throws ParseException {
			editData = getRow(table.getSelectedRow());
			
			vanNumber.setValue(Integer.parseInt((String) editData[1]));
			echelon.setSelectedItem((String) editData[2]);
			weight.setValue(Float.parseFloat((String)editData[3]));
			compositionIDField.setValue(Integer.parseInt((String)editData[4]));
			stackNumber.setValue(Integer.parseInt((String)editData[5]));
		}
		
		private ActionListener getAddActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что точно хотите добавить запись?\n",
							"Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка добавления записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;						

						float weigthVal;
						
						int composition;
						
						try {
							composition = (int)compositionIDField.getValue();
						} catch(Exception exp) {
							composition = ((Double)compositionIDField.getValue()).intValue();
						}
						
						if(weight.getValue().getClass().getSimpleName().equals("Float")) {
							weigthVal = (Float)weight.getValue();
						}else {
							weigthVal = ((Double)weight.getValue()).floatValue();
						}

						
						try {
						if(Main.getDB().addVan(
									(int)vanNumber.getValue(),
									(int)echelon.getSelectedID(),
									weigthVal,
									composition,
									(int)stackNumber.getValue()
								)
							){
							result = "Запись была добавлена в БД";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
						}}catch(Exception exp) {
							Main.getLogger().error(exp.getMessage());
						}
						
						JOptionPane.showMessageDialog(null, result, "Результат:", messageCode);
						
						updateTable();
					}
			    }
			};
		}
		/**
		 * Close sub frame (this frame)
		 */
		protected void close() {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		
		private ActionListener getEditActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					
					if(table.getSelectedRow() == -1) {
						JOptionPane.showMessageDialog(null, "Выберите редактируемый элемент!\nТаблица была обновлена", "Ошибка", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
									
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что точно хотите изменить запись?\n",
									"Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка изменения записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;						
						
						float weigthVal;
						
						int composition;
						
						try {
							composition = (int)compositionIDField.getValue();
						} catch(Exception exp) {
							composition = ((Double)compositionIDField.getValue()).intValue();
						}
						
						if(weight.getValue().getClass().getSimpleName().equals("Float")) {
							weigthVal = (Float)weight.getValue();
						}else {
							weigthVal = ((Double)weight.getValue()).floatValue();
						}
						
						if(Main.getDB().updateVan(
								Integer.parseInt((String)editData[0]),
								(int)vanNumber.getValue(),
								(int)echelon.getSelectedID(),
								weigthVal,
								composition,
								(int)stackNumber.getValue()
								)
							){
							result = "Запись была обновлена";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
						}
						
						JOptionPane.showMessageDialog(null, result, "Результат:", messageCode);
						
						updateTable();
						
						close();
					}
			    }
			};
		}
		
		private class CompositionNumberThread extends Thread{
			public void run() {
				try {
					while(chooser.isSetChoosedID() == false) {
						Thread.sleep(1000);
					}
					
					compositionIDField.setValue(chooser.getChoosedID());
					chooser.destroy();
				} catch(Exception exp) {
					System.out.println("Thread error: "+exp);
				}
			}
		}
		
	}
}
