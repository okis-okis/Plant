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

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import plant.lib.CompositionButtonEditor;
import plant.lib.CompositionChooser;
import plant.lib.TableButton;

/**
 * Class for create frame for working with stacks table
 * @author olegk
 * @version 0.1
 * @see IFrame
 */
public class Stacks extends IFrame{

	/**
	 * Titles of columns
	 */
	private static Object[] columnNames = {"Идентификатор", "Номер штабеля", "Состав", "Время фиксации"};
	
	/**
	 * Constructor of Stacks class
	 * @param title Title of frame
	 * @see IFrame
	 */
	public Stacks() {
		super("Штабеля");
		
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
	 * Update table of Workers</br>
	 * Get new data from database
	 */
	@Override
	protected void updateTable() {
		Object[][] data = Main.getDB().getStacks();
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
				new StacksFrame(1);
			}
		};
	}
	
	private ActionListener getEditActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				int[] data = getSelectedID();
				if(data != null) {
					try {
						(new StacksFrame(0)).setEditContent();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
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
						if(Main.getDB().deleteStack(id) == false) {
							JOptionPane.showMessageDialog(null, "Произошла ошибка при удалении записи с идентификатором "+id, "Результат добавления", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				
				updateTable();
			}
		};
	}
	
	/**
	 * Get count of columns in stacks table
	 * @return int value with result 
	 */
	public static int getColumnLength() {
		return columnNames.length;
	}
	
	public static Object[] getColumns() {
		return columnNames;
	}
	
	private class StacksFrame extends JFrame{
		
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
		
		private JSpinner stackNumber, compositionIDField, timeAnalyses;
		private JDateChooser fixedDate;
		
		private CompositionNumberThread thread;
		
		private CompositionChooser chooser;
		
		private Object[] editData;
		
		/**
		 * Row number for content build
		 */
		private int row;
		
		public StacksFrame(int modeFrame) {
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
			this.setSize(350, 300);
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
			
			initStackNumber();
			initCompositionSection();
			initDateTimeSection();
		}
		
		private void initStackNumber() {
			contentPanel.add(new JLabel("Выберите номер штабеля"), Main.getManagementController().createGbc(0, row++, 2, 1));
			
			stackNumber = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
			
			contentPanel.add(stackNumber, Main.getManagementController().createGbc(0, row++, 2, 1));
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
		
		private void initDateTimeSection() {
			contentPanel.add(new JLabel("Выберите дату проверки штабеля"), Main.getManagementController().createGbc(0, row++, 2, 1));
			
			fixedDate = new JDateChooser();
			
			fixedDate.setDateFormatString("yyyy-MM-dd");
			contentPanel.add(fixedDate, Main.getManagementController().createGbc(0, row++, 2, 1));
			
			contentPanel.add(new JLabel("Выберите время проверки штабеля"), Main.getManagementController().createGbc(0, row++, 2, 1));
			
			timeAnalyses = new JSpinner( new SpinnerDateModel() );
			JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeAnalyses, "HH:mm:ss");
			timeAnalyses.setEditor(timeEditor);
			timeAnalyses.setValue(new Date()); // will only show the current time
			
			contentPanel.add(timeAnalyses, Main.getManagementController().createGbc(0, row++, 2, 1));
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
			
			stackNumber.setValue(Integer.parseInt((String)editData[1]));
			compositionIDField.setValue(Integer.parseInt((String)editData[2]));
			
			String date, time; 
			try {
				date = ((String)editData[3]).split(" ")[0];
				time = ((String)editData[3]).split(" ")[1];
				
				time = time.substring(0, time.length() - 2);
				
				fixedDate.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date));
				 
	            // Getting the Date from String by
	            // creating object of Instant class
				timeAnalyses.setValue(new SimpleDateFormat("HH:mm:ss").parse(time));
	        }
	 
	        // Catch block to handle exceptions
	        catch (DateTimeParseException e) {
	 
	            // Throws DateTimeParseException
	            // if the string cannot be parsed
	            System.out.println("Exception: " + e);
	        }
		}
		
		private String getDateTimeAnalyse() {
			String result = null;
			if(fixedDate.getDate() != null) {
				result = (new SimpleDateFormat("yyyy-MM-dd")).format(fixedDate.getDate());
				result+= " "+(timeAnalyses.getValue()+"").split(" ")[3];
			}
			return result;
		}
		
		private ActionListener getAddActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что точно хотите добавить запись?\n",
							"Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка добавления записи! Возможно, вы не указали дату или время.";
						int messageCode = JOptionPane.ERROR_MESSAGE;						
						
						int composition = 0;
						try {
							if(compositionIDField.getValue().getClass().getSimpleName() == "Double") {
								composition = ((Double)compositionIDField.getValue()).intValue();
							} else {
								composition = (int)compositionIDField.getValue();
							}
						}catch(Exception exp) {
							System.out.println("Composition chooser error: "+exp);
						}
						
						if(Main.getDB().addStack(
								(int)stackNumber.getValue(),
								composition,
								getDateTimeAnalyse()
							)){
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
		 * Close sub frame (this frame)
		 */
		protected void close() {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		
		private ActionListener getEditActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					
					int composition = 0;
					
					try {
						if(compositionIDField.getValue().getClass().getSimpleName().equals("Double")) {
							composition = ((Double)compositionIDField.getValue()).intValue();
						} else {
							composition = (int)compositionIDField.getValue();
						}
					}catch(Exception exp) {
						System.out.println("Composition chooser error: "+exp);
					}
					
									
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что точно хотите изменить запись?\n",
									"Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка изменения записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;						
						
						if(Main.getDB().updateStack(
								Integer.parseInt((String) editData[0]),
								(int)stackNumber.getValue(),
								composition,
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
