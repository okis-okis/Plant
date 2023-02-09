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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.TableModelEvent;

import plant.IFrame;
import plant.Main;
import plant.lib.CompositionButtonEditor;
import plant.lib.CompositionChooser;
import plant.lib.TableButton;

/**
 * Class for create frame for working with sinter types table
 * @author olegk
 * @version 0.1
 * @see IFrame
 */
public class SinterTypes extends IFrame{

	private SinterFrame sinterFrame;
	/**
	 * Titles of columns
	 */
	private static Object[] columnNames = {"Идентификатор", "Наименование", "Состав", "Примечание"};
	
	/**
	 * Constructor of SinterTypes class
	 * @param title Title of frame
	 * @see IFrame
	 */
	public SinterTypes() {
		super("Типы аглошихты");

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
	 * Update table of SinterTypes</br>
	 * Get new data from database
	 */
	@Override
	protected void updateTable() {
		Object[][] data = Main.getDB().getSinterTypes();
		
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
		Object[] data = new Object[columnNames.length];
		for(int i=0;i<columnNames.length;i++) {
			data[i] = table.getValueAt(number, i);
		}
		return data;
	}
	
	/**
	 * Get length of title columns array
	 * @return length of title column array
	 * @see columnNames
	 */
	public static int getColumnLength() {
		return columnNames.length;
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
						if(Main.getDB().deleteSinterType(id) == false) {
							JOptionPane.showMessageDialog(null, "Произошла ошибка при удалении записи с идентификатором "+id, "Результат добавления", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
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
						sinterFrame = new SinterFrame(0);
						sinterFrame.setEditContent(data[0]);
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "Пожалуйста, выберите строку для редактирования!", "Ошибка!", JOptionPane.ERROR_MESSAGE);
				}
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
				sinterFrame = new SinterFrame(1);
			}
		};
	}
	
	public SinterFrame getSinterFrame() {
		return sinterFrame;
	}
	
	public class SinterFrame extends JFrame{
		
		/**
		 * Mode of frame
		 * 0 - editing
		 * 1 and others - adding
		 */
		private int modeFrame;
		
		private JTextField sinterTitle;
				
		/**
		 * Main content panel for components
		 */
		private JPanel contentPanel;
		
		private JSpinner compositionIDField;
		
		private Object[] editData;
		
		private JTextArea additionalArea;
		
		private CompositionChooser chooser;
		
		private CompositionNumberThread thread;
		
		/**
		 * Row number for content build
		 */
		private int row;
		
		public SinterFrame(int modeFrame) {
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
			
			sensorAction.addActionListener(modeFrame==0?getEditSinterTypeActionListener():getAddSinterTypeActionListener());
			
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
			
			row = 0;
			
			initTitleSection();			
			initCompositionSection();
			initAdditionalSection();
		}

		private void initAdditionalSection() {
			contentPanel.add(new JLabel("Дополнительная информация"), Main.getManagementController().createGbc(0, row++, 2, 1));
			additionalArea = new JTextArea();
			additionalArea.setColumns(20);
			additionalArea.setRows(5);
			contentPanel.add(new JScrollPane(additionalArea), Main.getManagementController().createGbc(0, row++, 2, 1));
		}
		
		private void initCompositionSection() {
			contentPanel.add(new JLabel("Выберите состав агломерата"), Main.getManagementController().createGbc(0, row++, 2, 1));
			compositionIDField = new JSpinner(new SpinnerNumberModel(0.0, 0.0, null, 1.0));
			contentPanel.add(compositionIDField, Main.getManagementController().createGbc(0, row));
			
			JButton openCompositionButton = new JButton("Выбрать");
			openCompositionButton.addActionListener(getCompositionIDActionListener());
			contentPanel.add(openCompositionButton, Main.getManagementController().createGbc(1, row++));
		}

		private void initTitleSection() {
			contentPanel.add(new JLabel("Наименование агломерата"), Main.getManagementController().createGbc(0, row++));
			sinterTitle = new JTextField();
			sinterTitle.setColumns(20);
			contentPanel.add(sinterTitle, Main.getManagementController().createGbc(0, row++, 2, 1));			
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
			
		}
		
		/**
		 * Set choosed id in composition field
		 * @param id of compositions
		 */
		public void setChoosedID(int id) {
			compositionIDField.setValue(id);
		}
		
		/**
		 * Private method for processing the add new composition
		 * @return ActionListener with actions
		 */
		private ActionListener getCompositionIDActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					chooser = new CompositionChooser();
					thread = new CompositionNumberThread();
					thread.start();
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
		 * Private method for processing the add new composition
		 * @return ActionListener with actions
		 */
		private ActionListener getAddSinterTypeActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					int composition;
					
					try {
						composition = (int)compositionIDField.getValue();
					} catch(Exception exp) {
						composition = ((Double)compositionIDField.getValue()).intValue();
					}
					
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите изменить запись?\nВводимые параментры:"
							+"\nНаименование агломерата: "+sinterTitle.getText()
							+"\nИдентификатор состава: "+ composition
							+"\nДополнительная информация: "+additionalArea.getText(),
							"Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка добавления записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;						
						
						if(Main.getDB().addSinterType(
								sinterTitle.getText(),
								composition,
								additionalArea.getText()
								)
							){
							result = "Запись была добавлена в БД";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
							additionalArea.setText("");
							sinterTitle.setText("");
							compositionIDField.setValue(0);
						}
						
						JOptionPane.showMessageDialog(null, result, "Результат:", messageCode);
						
						updateTable();
					}
			    }
			};
		}
		
		private ActionListener getEditSinterTypeActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите изменить запись?", "Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка изменения записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;
						
						int composition;
						
						try {
							composition = (int)compositionIDField.getValue();
						} catch(Exception exp) {
							composition = ((Double)compositionIDField.getValue()).intValue();
						}
						
						if(Main.getDB().updateSinterType(
								Integer.valueOf((String)editData[0]),
								sinterTitle.getText(),
								composition,
								additionalArea.getText()
								)){
							result = "Запись была изменена  в БД";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
							
						}		
						
						JOptionPane.showMessageDialog(null, result, "Результат:", messageCode);
						updateTable();
						
						close();
					}
			    }
			};
		}
		
		public void setEditContent(int id) throws ParseException {
			editData = Main.getDB().getSinterTypeById(id);
			sinterTitle.setText((String)editData[1]);
			compositionIDField.setValue(Integer.valueOf((String)editData[2]));
			additionalArea.setText((String)editData[3]);
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
