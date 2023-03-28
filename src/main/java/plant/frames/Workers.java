package plant.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import plant.IFrame;
import plant.Main;
import plant.lib.CustomComboBox;

/**
 * Class for create frame for working with Workers table
 * @author olegk
 * @version 0.1
 * @since 20.10.2022
 * @see IFrame
 */
public class Workers extends IFrame{

	/**
	 * Titles of columns
	 */
	private static Object[] columnNames = {"Идентификатор работника", "ФИО работника", "Должность работника"};
	
	/**
	 * Variable contains positions for worker
	 */
	private CustomComboBox positions;
	
	/**
	 * Constructor of WorkersIFrame class
	 * @param title Title of frame
	 * @see IFrame
	 */
	public Workers() {
		super("Работники");

		//Create panel with table, manage buttons and edit area
		panel = new JPanel(new BorderLayout());
				
		updateTable();
				
		panel.add(createManagePanel(), BorderLayout.SOUTH);
				
		setContentPane(panel);
				
		setVisible(true);
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
	
	private ActionListener getEditActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				int[] data = getSelectedID();
				if(data != null) {
					try {
						(new WorkerFrame(0)).setEditContent();
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
	 * Private method for processing the add button
	 * @return ActionListener with actions
	 */
	private ActionListener getAddActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){  
				new WorkerFrame(1);
		    }
		};
	}
	
	/**
	 * Private method for processing the delete button
	 * @return ActionListener with actions
	 */
	private ActionListener getDeleteActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){  
				int answer = JOptionPane.showConfirmDialog(null,
						"Вы уверены, что хотите удалить выбранные записи?",
						"Информационное окно", 
				        JOptionPane.YES_NO_OPTION);

				if(answer == JOptionPane.YES_OPTION) {
					for(int id: getSelectedID()) {
						if(Main.getDB().deleteWorker(id) == false) {
							JOptionPane.showMessageDialog(null, "Произошла ошибка при удалении записи с идентификатором "+id, "Результат добавления", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				updateTable();
			}
		};
	}
	
	/**
	 * Process for table change event </br>
	 * This event need for table edit
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		updateTable();		
	}

	/**
	 * Get IDs of selected row
	 * @return int[] array with selected id 
	 */
	@Override
	protected int[] getSelectedID() {
		int[] result = new int[table.getSelectedRows().length];
		int counter = 0;
		for(int rowNumber: table.getSelectedRows()){
			result[counter] = Integer.parseInt((String) getRow(rowNumber)[0]);			
			counter++;
		}
		if(counter == 0) {
			return null;
		}
		return result;
	}

	/**
	 * Get count of columns in Workers table
	 * @return count of columns
	 */
	public static int getColumnLength() {
		return columnNames.length; 
	}
	
	/**
	 * Update table of Workers</br>
	 * Get new data from database
	 */
	@Override
	protected void updateTable() {		
		Object[][] data = Main.getDB().getWorkers(columnNames.length);
		DefaultTableModel model = new DefaultTableModel(data, columnNames);	
		
		table = new JTable(model);
		
		int[] PosID = new int[data.length];
		
		for(int i=0;i<PosID.length;i++) {
			PosID[i] = Integer.parseInt((String)data[i][2]);
		}
				
		updateID(table.getColumnModel().getColumn(2), PosID);
		
		table.getModel().addTableModelListener(this);
		
		if(sp != null) {
			panel.remove(sp);
		}
		
		panel.add(sp = new JScrollPane(table), BorderLayout.CENTER);
		panel.updateUI();
	}
	
	/**
	 * Set column with WorkerComboBox (Upgraded ComboBox) for this table (consist Positions)
	 * @param positionColumn Column of Workers table for changing for WorkerComboBox
	 * @param PosID Array of positions id
	 * @see JComboBox
	 */
	protected void updateID(TableColumn positionColumn, int[] PosID) {
		positions = new CustomComboBox();
		Object[][] posArray = Main.getDB().getPositions();
		
		for(Object[] row: posArray) {
			positions.addID(Integer.parseInt((String) row[0]));
			positions.addItem((String)row[1]);
		}
		positionColumn.setCellEditor(new DefaultCellEditor(positions));
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Выберите должность для работника");
        positionColumn.setCellRenderer(renderer);
        
        for(int i=0;i<PosID.length;i++) {
        	table.getModel().setValueAt(positions.getItemByID(PosID[i]), i, 2);
        }
	}

	/**
	 * Get row by number
	 * @param number int of number of need row
	 * @return Object[] Array of data of need row by number
	 */
	@Override
	protected Object[] getRow(int number) {
		Object[] data = new Object[getColumnLength()];
		
		for(int i=0;i<getColumnLength();i++) {
			data[i] = table.getValueAt(number, i);
		}
		
		return data;
	}

	/**
	 * SubFrame for adding new data to Worker table
	 * @author olegk
	 * @version 0.1
	 * @since 19.20.2022
	 */
	private class WorkerFrame extends JFrame{
		
		/**
		 * Field for enter full name of worker
		 */
		private JTextField fullNameField;
		
		/**
		 * Mode of frame
		 * 0 - editing
		 * 1 and others - adding
		 */
		private int modeFrame;
		
		private Object[] editData;
		
		/**
		 * Modified JComboBox for storing positions id and title 
		 * @see WorkerComboBox
		 */
		private CustomComboBox wcb;
		
		/**
		 * Class constructor
		 */
		public WorkerFrame(int modeFrame) {
			super((modeFrame==0?"Редактирование ":"Добавление ")+"записи");
			
			this.modeFrame = modeFrame;
			
			//Build interface
			JPanel mainPanel = new JPanel(new BorderLayout());
			
			//Add button section
			JButton WorkerBtn;
			
			if(modeFrame == 0) {
				WorkerBtn = new JButton("Редактировать запись");
				
				//Clicking event by button processing
				WorkerBtn.addActionListener(getEditWorkerActionListener());
			}else {
				WorkerBtn = new JButton("Добавить пользователя");
				
				//Clicking event by button processing
				WorkerBtn.addActionListener(getAddWorkerActionListener());
			}
			
			mainPanel.add(getCustomizedPanel(), BorderLayout.CENTER);
			mainPanel.add(WorkerBtn, BorderLayout.SOUTH);
			setContentPane(mainPanel);
			
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
		 * Configure the form for the correct display
		 */
		private void formSettings() {
			//Main.getManagementController().setEnable(false);
			this.setEnabled(true);
             
			//Settings of frame
			this.setSize(250, 300);
			this.setVisible(true);
			
			this.setFocusable(true);
		}
		
		/**
		 * Get customized panel with components for adding new worker
		 * @return Customized JPanel
		 * @see JPanel
		 */
		private JPanel getCustomizedPanel() {
			JPanel panel = new JPanel(new GridBagLayout());
			
			//FullName section
			panel.add(new JLabel("ФИО работника"), Main.getManagementController().createGbc(0, 0));
			fullNameField = new JTextField();
			fullNameField.setColumns(20);
			fullNameField.addKeyListener(new java.awt.event.KeyAdapter() {
		        public void keyTyped(java.awt.event.KeyEvent evt) {
		        	
		            if(!(Character.isLetter(evt.getKeyChar())) && !Character.isSpaceChar(evt.getKeyChar()) && Character.compare(evt.getKeyChar(), '.') != 0){
		                   evt.consume();
		               }
		           }
		       });
			panel.add(fullNameField, Main.getManagementController().createGbc(0, 1));
			
			//Position section
			panel.add(new JLabel("Выберите должность работника"), Main.getManagementController().createGbc(0, 2));
			wcb = new CustomComboBox();
			
			for(Object[] row: Main.getDB().getPositions()) {
				wcb.addID(Integer.parseInt((String) row[0]));
				wcb.addItem((String)row[1]);
			}
			
			panel.add(wcb, Main.getManagementController().createGbc(0, 3));
			
			return panel;
		}
		
		/**
		 * Private method for processing the add new worker button
		 * @return ActionListener with actions
		 */
		private ActionListener getAddWorkerActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите добавить запись?", "Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка добавления записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;
						
						if(Main.getDB().addWorker(fullNameField.getText(), wcb.getSelectedID())) {
							result = "Запись была добавлена в БД";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
						}		
						JOptionPane.showMessageDialog(null, result, "Результат добавления", messageCode);
						updateTable();
						fullNameField.setText(null);
						wcb.setSelectedIndex(0);
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
		
		/**
		 * Private method for processing the add new worker button
		 * @return ActionListener with actions
		 */
		private ActionListener getEditWorkerActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					//Checking editing
			        int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите отредактировать запись?", "Подтверждение действий", 
					        JOptionPane.YES_NO_OPTION);
			        
			        if(answer == JOptionPane.YES_OPTION) {
			        	//Edit table row
			        	int idWorker = Integer.parseInt((String)editData[0]);
			        	String fullName = (String) fullNameField.getText();
			        	int posID = (Integer) wcb.getSelectedID();
			        	
			        	if(Main.getDB().updateWorker(idWorker, fullName, posID)) {
			        		JOptionPane.showMessageDialog(null, "Запись была успешно изменена!", "Результат обновления", JOptionPane.INFORMATION_MESSAGE);
			        	}else {
			        		JOptionPane.showMessageDialog(null, "Произошла ошибка при обновлении записи", "Результат обновления", JOptionPane.ERROR_MESSAGE);
			        	}
			        }
			        
			        //Updating table for get visual changing
			        updateTable();
			        
			        close();
			    }
			};
		}
		
		/**
		 * Function using for updating table.</br> 
		 * Set to fields values of table row by id.
		 * @throws ParseException Error handling
		 * @see ParseException
		 */
		public void setEditContent() throws ParseException {
			editData = getRow(table.getSelectedRow());
			
			fullNameField.setText((String)editData[1]);
			wcb.setSelectedItem((String)editData[2]);
		}
	}
}
