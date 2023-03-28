package plant.frames;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.TableModelEvent;

import com.toedter.calendar.JDateChooser;

import plant.IFrame;
import plant.Main;
import plant.lib.StackChooser;

/**
 * Class for create frame for working with bunker filling table
 * @author olegk
 * @version 0.1
 * @see IFrame
 */
public class BunkersFilling extends IFrame{

	/**
	 * Titles of columns
	 */
	private static Object[] columnNames = {"Идентификатор", "Бункер", "Штабель", "Время операции"};
	
	/**
	 * Constructor of BunkersFilling class
	 * @param title Title of frame
	 * @see IFrame
	 */
	public BunkersFilling() {
		super("Операции с бункерами");

		panel = new JPanel(new BorderLayout());
		
		//Add data to table
		updateTable();
								
		panel.add(createManagePanel(), BorderLayout.SOUTH);
								
		setContentPane(panel);
								
		setVisible(true);
	}
	
	/**
	 * Create manage panel for working with table (data)
	 * @return
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
	
	/**
	 * Private method for processing the add button
	 * @return ActionListener with actions
	 */
	private ActionListener getAddActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				new FillingFrame(1);
			}
		};
	}
	
	/**
	 * Edit action for edit button
	 * @return ActionListener with edit action
	 */
	private ActionListener getEditActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				int[] data = getSelectedID();
				if(data != null) {
					try {
						(new FillingFrame(0)).setEditContent();
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
	 * Delete action for delete button
	 * @return ActionListener with delete action
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
						if(Main.getDB().deleteBunkerFilling(id) == false) {
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
	 * Get data from DB and update table
	 */
	@Override
	protected void updateTable() {
		Object[][] data = Main.getDB().getBunkersFilling();
		table = new JTable(data, columnNames);
		
		table.getModel().addTableModelListener(this);
	    
		if(sp != null) {
			panel.remove(sp);
		}
		
		panel.add(sp = new JScrollPane(table), BorderLayout.CENTER);
		panel.updateUI();	
	}

	/**
	 * Get row data by row number
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
	 * Get count of columns in Bunkers Filling table
	 * @return int value with result 
	 */
	public static int getColumnLength() {
		return columnNames.length;
	}
	
	/**
	 * Sub frame for add/edit content
	 * @author olegk 
	 */
	private class FillingFrame extends JFrame{
		
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
		
		private JComboBox bunkers;
		
		private JDateChooser fixedDate;
		
		private JSpinner stackNumber, fixedTime;
		
		StackChooser chooser;
		
		StackNumberThread thread;
		
		private Object[] editData;
		
		/**
		 * Row number for content build
		 */
		private int row;
		
		public FillingFrame(int modeFrame) {
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
			this.setSize(350, 400);
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
			
			initBunkerNumber();
			initStacks();
			initDateTimeSection();
		}
		/**
		 * Close sub frame (this frame)
		 */
		protected void close() {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		
		/**
		 * Init section for working with bunker numbers
		 */
		private void initBunkerNumber() {
			contentPanel.add(new JLabel("Укажите номер бункера"), Main.getManagementController().createGbc(0, row++, 2, 1));
			
			bunkers = new JComboBox();
			
			for(Object[] row: Main.getDB().getBunkers()) {
				bunkers.addItem((String)row[0]);
			}
			
			contentPanel.add(bunkers, Main.getManagementController().createGbc(0, row++, 2, 1));
		}
		
		/**
		 * Init section for working with stacks 
		 */
		private void initStacks() {
			contentPanel.add(new JLabel("Выберите операцию со штабелем"), Main.getManagementController().createGbc(0, row++, 2, 1));
			stackNumber = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 1.0));
			contentPanel.add(stackNumber, Main.getManagementController().createGbc(0, row));
			
			JButton openCompositionButton = new JButton("Выбрать");
			openCompositionButton.addActionListener(getChooseActionListener());			
			contentPanel.add(openCompositionButton, Main.getManagementController().createGbc(1, row++));
		}
		
		/**
		 * choose action for get composition id
		 * @return ActionListener with choose action
		 */
		private ActionListener getChooseActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					chooser = new StackChooser();
					//System.out.println("Created new chooser: "+chooser);
					thread = new StackNumberThread();
					thread.start();
			    }
			};
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
			
			bunkers.setSelectedItem((String)editData[1]);
			stackNumber.setValue(Integer.parseInt((String)editData[2]));
			
			String date = ((String)editData[3]).split(" ")[0], 
				   time = ((String)editData[3]).split(" ")[1];
				
				   time = time.substring(0, time.length() - 2);
				
				if(editData[3]!=null) {
					fixedDate.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date));
				}
				
				try {
					 
		            // Getting the Date from String by
		            // creating object of Instant class
					fixedTime.setValue(new SimpleDateFormat("HH:mm:ss").parse(time));
		        }
		 
		        // Catch block to handle exceptions
		        catch (DateTimeParseException e) {
		 
		            // Throws DateTimeParseException
		            // if the string cannot be parsed
		            Main.getLogger().error("Exception: " + e);
		        }
		}
		
		/**
		 * Init date and time section
		 */
		private void initDateTimeSection() {
			contentPanel.add(new JLabel("Выберите дату осуществления проверки"), Main.getManagementController().createGbc(0, row++, 2, 1));
			
			fixedDate = new JDateChooser();
			
			fixedDate.setDateFormatString("yyyy-MM-dd");
			contentPanel.add(fixedDate, Main.getManagementController().createGbc(0, row++, 2, 1));
			
			contentPanel.add(new JLabel("Выберите время осуществления проверки"), Main.getManagementController().createGbc(0, row++, 2, 1));
			
			fixedTime = new JSpinner( new SpinnerDateModel() );
			JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(fixedTime, "HH:mm:ss");
			fixedTime.setEditor(timeEditor);
			fixedTime.setValue(new Date()); // will only show the current time
			
			contentPanel.add(fixedTime, Main.getManagementController().createGbc(0, row++, 2, 1));
		}
		
		/**
		 * Get format string with date and time
		 * @return String with format date and time
		 */
		private String getDateTimeAnalyse() {
			String result = null;
			if(fixedDate.getDate() != null) {
				result = (new SimpleDateFormat("yyyy-MM-dd")).format(fixedDate.getDate());
				result+= " "+(fixedTime.getValue()+"").split(" ")[3];
			}
			return result;
		}
		
		/**
		 * Add action for Add button
		 * @return ActionListener with add action
		 */
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
						
						int stackVal;
						
						try {
							stackVal = (int)stackNumber.getValue();
						} catch(Exception exp) {
							stackVal = ((Double)stackNumber.getValue()).intValue();
						}
						
						if(Main.getDB().addBunkersFilling(
								Integer.parseInt((String)bunkers.getSelectedItem()),
								stackVal,
								getDateTimeAnalyse()
							)
						){
						result = "Запись была добавлена в БД";
						messageCode = JOptionPane.INFORMATION_MESSAGE;
					}
						
						JOptionPane.showMessageDialog(null, result, "Результат:", messageCode);
						
						updateTable();
					}
			    }
			};
		}
		
		/**
		 * Edit action for edit button
		 * @return ActionListener with edit action
		 */
		private ActionListener getEditActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  					
									
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что точно хотите изменить запись?\n",
									"Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка изменения записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;						
							
						int stackVal;
						
						try {
							stackVal = (int)stackNumber.getValue();
						} catch(Exception exp) {
							stackVal = ((Double)stackNumber.getValue()).intValue();
						}
						
						if(Main.getDB().updateBunkerFilling(
								Integer.parseInt((String)editData[0]),
								Integer.parseInt((String)bunkers.getSelectedItem()),
								stackVal,
								getDateTimeAnalyse()
							)){
							result = "Запись была изменена!";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
						}
						
						JOptionPane.showMessageDialog(null, result, "Результат:", messageCode);
						
						updateTable();
						
						close();
					}
			    }
			};
		}
		
		/**
		 * Sub class for choose composition ID
		 */
		private class StackNumberThread extends Thread{
			public void run() {
				try {
					while(chooser.isSetChoosedID() == false) {
						TimeUnit.SECONDS.sleep(1);
					}
					stackNumber.setValue(chooser.getChoosedID());
					chooser.destroy();
				} catch(Exception exp) {
					System.out.println("Thread error: "+exp);
				}
			}
		}
	}
}
