package plant.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import plant.IFrame;
import plant.Main;
import plant.lib.CustomComboBox;

/**
 * Class for create frame for working with Echelons table
 * @author olegk
 * @version 0.1
 * @since 21.10.2022
 * @see IFrame 
 * @author olegk
 *
 */
public class Echelons extends IFrame{

	/**
	 * Titles of columns
	 */
	private static Object[] columnNames = {"Идентификатор эшелона", "Номер эшелона", "Рудник", "Дата начала разгрузки эшелона", "Дата конца разгрузки эшелона" , "Примечание"};
	
	/**
	 * Class constructor
	 * @param title Title of the frame
	 */
	public Echelons() {
		super("Эшелоны");

		//Create panel with table, manage buttons and edit area
		panel = new JPanel(new BorderLayout());
				
		//Add data to table
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
	 * Private method for processing the edit button
	 * @return ActionListener with actions
	 */
	private ActionListener getEditActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				int[] data = getSelectedID();
				if(data != null) {
					try {
						(new EchelonFrame(0)).setEditContent(data[0]);
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
				new EchelonFrame(1);
		    }
		};
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
						if(Main.getDB().deleteEchelon(id) == false) {
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
	public void tableChanged(TableModelEvent arg0) {
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
		
		if(result.length == 0) {
			return null;
		}
		
		return result;
	}

	/**
	 * Update table of Echelons</br>
	 * Get new data from database
	 */
	@Override
	protected void updateTable() {
		Object[][] data = Main.getDB().getEchelons(columnNames.length);
		DefaultTableModel model = new DefaultTableModel(data, columnNames);	
		
		table = new JTable(model);
		
		setMinesColumn(data);
		
		//setWorkersColumn(data);
        
		table.getModel().addTableModelListener(this);
		
		if(sp != null) {
			panel.remove(sp);
		}
		
		panel.add(sp = new JScrollPane(table), BorderLayout.CENTER);
		panel.updateUI();		
	}
	
	/**
	 * Fill in and add the mines column
	 * @param dataEchelons Data of table. Need for fill column.
	 */
	private void setMinesColumn(Object[][] dataEchelons) {
		CustomComboBox mines = new CustomComboBox();
		Object[][] minesArray = Main.getDB().getMines(Mines.getColumnLength());
		for(Object[] row: minesArray) {
			mines.addID(Integer.parseInt((String) row[0]));
			mines.addItem((String)row[1]);
		}
		table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(mines));
		
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Выберите рудник, откуда прибыл эшелон");
        table.getColumnModel().getColumn(2).setCellRenderer(renderer);
        
        for(int i=0;i<dataEchelons.length;i++) {
			table.getModel().setValueAt(mines.getItemByID(Integer.parseInt((String)dataEchelons[i][2])), i, 2);
		}
	}

	/**
	 * Get count of columns in Echelons table
	 * @return count of columns
	 */
	public static int getColumnLength() {
		return columnNames.length; 
	}
	
	/**
	 * Get row data from Positions table by row number
	 */
	protected Object[] getRow(int number) {
		Object[] data = new Object[getColumnLength()];
		
		for(int i=0;i<getColumnLength();i++) {
			data[i] = table.getValueAt(number, i);
		}
		
		return data;
	}

	
	/**
	 * SubFrame for adding new data to Echelons table
	 * @author olegk
	 * @version 0.1
	 * @since 19.20.2022
	 */
	private class EchelonFrame extends JFrame{
		
		private JPanel contentPanel;
		/**
		 * Field for enter full name of worker
		 */
		private JTextField echelonNumber;
		
		/**
		 * Modified JComboBox for storing mines id and title 
		 * @see WorkerComboBox
		 */
		private CustomComboBox mines;
		
		/**
		 * Modified JComboBox for storing workers id and title 
		 * @see WorkerComboBox
		 */
		//private CustomComboBox workers;
		
		/**
		 * Variables for choose date of start and finish arrival echelon
		 */
		private JDateChooser startArrivalDate, finishArrivalDate;
		
		/**
		 * Additional information about echelon
		 * @see JTextArea
		 */
		private JTextArea additionalArea;
		
		private int modeFrame;
		
		private int echelonID;
		
		/**
		 * Class constructor
		 */
		public EchelonFrame(int modeFrame) {
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
		
		public void setEditContent(int id) throws ParseException {
			
			Object[] echelonData = Main.getDB().getEchelonByID(id);

			this.echelonID = Integer.parseInt((String)echelonData[0]); // Идентификатор
			
			echelonNumber.setText((String)echelonData[1]);
			mines.setSelectedID(Integer.parseInt((String)echelonData[2]));
			//workers.setSelectedID(Integer.parseInt((String)echelonData[3]));
			
			if(echelonData[3]!=null) {
				startArrivalDate.setDate(new SimpleDateFormat("yyyy-MM-dd").parse((String)echelonData[3]));
			}
			if(echelonData[4]!=null) {
				finishArrivalDate.setDate(new SimpleDateFormat("yyyy-MM-dd").parse((String)echelonData[4]));
			}
			additionalArea.setText((String)echelonData[5]);
		}
		
		/**
		 * Configure the form for the correct display
		 */
		private void formSettings() {
			//Main.getManagementController().setEnable(false);
			
			this.setEnabled(true);
             
			//Settings of frame
			this.setSize(350, 600);
			this.setVisible(true);
			
			this.setFocusable(true);
		}
		
		/**
		 * Get customized panel with components for adding new worker
		 * @see JPanel
		 */
		private void setCustomizedPanel() {
			
			JPanel mainPanel = new JPanel(new BorderLayout());
			
			initContent();			
			
			//Add button section
			JButton mineAction = new JButton((modeFrame==0?"Редактировать ":"Добавить"));
			
			//Clicking event by button processing
			
			mineAction.addActionListener(modeFrame==0?getEditEchelonActionListener():getAddEchelonActionListener());
			
			mainPanel.add(contentPanel, BorderLayout.CENTER);
			mainPanel.add(mineAction, BorderLayout.SOUTH);
			
			setContentPane(mainPanel);
		}
		
		/**
		 * Build up interface of frame</br>
		 * Initialize all components
		 */
		private void initContent() {
			contentPanel = new JPanel(new GridBagLayout());
			
			initEchelonNumberSection();
			initMinesSection();
			//initWorkersSection();
			initArrivalDatesSection();
			initAdditionalSection();
		}
		
		/**
		 * Initialize echelon section
		 */
		private void initEchelonNumberSection() {
			//Echelon number section
			contentPanel.add(new JLabel("Номер эшелона"), Main.getManagementController().createGbc(0, 0));
			
			NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
			DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
			decimalFormat.setGroupingUsed(false);
			echelonNumber = new JFormattedTextField(decimalFormat);
			echelonNumber.setColumns(15); //whatever size you wish to set
			
			contentPanel.add(echelonNumber, Main.getManagementController().createGbc(0, 1));
		}
		
		/**
		 * Initialize mines section
		 */
		private void initMinesSection() {
			//Mines section
			contentPanel.add(new JLabel("Выберите рудник"), Main.getManagementController().createGbc(0, 2));
			mines = new CustomComboBox();
			
			for(Object[] row: Main.getDB().getMines()) {
				mines.addID(Integer.parseInt((String) row[0]));
				mines.addItem((String)row[1]);
			}
			
			contentPanel.add(mines, Main.getManagementController().createGbc(0, 3));
		}
		
		/**
		 * Initialize arrival dates section
		 */
		private void initArrivalDatesSection() {
			//Arrival dates section
			contentPanel.add(new JLabel("Выберите дату начала разгрузки эшелона"), Main.getManagementController().createGbc(0, 6));
			startArrivalDate = new JDateChooser();
			startArrivalDate.setDateFormatString("yyyy-MM-dd");
			contentPanel.add(startArrivalDate, Main.getManagementController().createGbc(0, 7));
			
			contentPanel.add(new JLabel("Выберите дату завершения разгрузки эшелона"), Main.getManagementController().createGbc(0, 8));
			finishArrivalDate = new JDateChooser();
			finishArrivalDate.setDateFormatString("yyyy-MM-dd");
			contentPanel.add(finishArrivalDate, Main.getManagementController().createGbc(0, 9));
		}
		
		/**
		 * Initialize arrival date section
		 */
		private void initAdditionalSection() {
			//Additional section
			contentPanel.add(new JLabel("Дополнительная информация"), Main.getManagementController().createGbc(0, 10));
			additionalArea = new JTextArea();
			additionalArea.setColumns(20);
			additionalArea.setRows(5);
			contentPanel.add(new JScrollPane(additionalArea), Main.getManagementController().createGbc(0, 11));
		}
		
		/**
		 * Private method for processing the add new worker button
		 * @return ActionListener with actions
		 */
		private ActionListener getAddEchelonActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите добавить запись?", "Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка добавления записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;						
						
						if(echelonNumber.getText().trim().isEmpty()) {
							JOptionPane.showMessageDialog(null, "Не указан номер эшелона!", "Ошибка!", messageCode);
							return;
						}
						
						if(getStartArrivalDate()==null) {
							JOptionPane.showMessageDialog(null, "Вы не указали дату прибытия эшелона!", "Ошибка!", messageCode);
							return;
						}
						
						if(Main.getDB().addEchelon(Integer.parseInt(echelonNumber.getText().trim()), 
								mines.getSelectedID(), 
								//workers.getSelectedID(), 
								getStartArrivalDate(), 
								getFinishArrivalDate(), 
								additionalArea.getText())){
							result = "Запись была добавлена в БД";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
							
							//TODO: Create operation for clear components
						}		
						
						JOptionPane.showMessageDialog(null, result, "Результат:", messageCode);
						updateTable();
					}
			    }
			};
		}
		
		private String getStartArrivalDate() {
			String startDate = null;
			if(startArrivalDate.getDate() != null) {
				startDate = (new SimpleDateFormat("yyyy-MM-dd")).format(startArrivalDate.getDate());
			}
			return startDate;
		}
		
		private String getFinishArrivalDate() {
			String finishDate = null;
			if(finishArrivalDate.getDate() != null) {
				finishDate = (new SimpleDateFormat("yyyy-MM-dd")).format(startArrivalDate.getDate());
			}
			return finishDate;
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
		private ActionListener getEditEchelonActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите изменить запись?", "Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка изменения записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;
						
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

						if(Main.getDB().updateEchelon(echelonID, 
								Integer.parseInt(echelonNumber.getText()), 
								mines.getSelectedID(), 
								//workers.getSelectedID(), 
								getStartArrivalDate(), 
								getFinishArrivalDate(), 
								additionalArea.getText())){
							result = "Запись была изменена  в БД";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
							
							//TODO: Create operation for clear components
						}		
						
						JOptionPane.showMessageDialog(null, result, "Результат:", messageCode);
						updateTable();
						
						close();
					}
			    }
			};
		}
	}
}
