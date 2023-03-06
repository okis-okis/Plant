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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

import plant.IFrame;
import plant.Main;
import plant.lib.CustomComboBox;

/**
 * Class for create frame for working with analyzed elements table
 * @author olegk
 * @version 0.1
 * @see IFrame
 */
public class AnalyzedElements extends IFrame{

	/**
	 * Titles of columns
	 */
	private static Object[] columnNames = {"Идентификатор", "Элемент", "Тип аглошихты"};
	
	/**
	 * Constructor of AnalyzedElements class
	 * @param title Title of frame
	 * @see IFrame
	 */
	public AnalyzedElements() {
		super("Таблица анализируемых элементов");

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
	 * Update table of Analyzed Elements</br>
	 * Get new data from database
	 */
	@Override
	protected void updateTable() {
		Object[][] data = Main.getDB().getAnalyzedElementsWithNames();
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
	
	/**
	 * Get count of columns in Analyzed Elements table
	 * @return int value with result 
	 */
	public static int getColumnLength() {
		return columnNames.length;
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
						if(Main.getDB().deleteAnalyzedElement(id) == false) {
							JOptionPane.showMessageDialog(null, "Произошла ошибка при удалении записи с идентификатором "+id, "Результат добавления", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				
				updateTable();
			}
		};
	}
	
	/**
	 * Edit action for editing
	 * @return ActionListener with edit action
	 */
	private ActionListener getEditActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				int[] data = getSelectedID();
				if(data != null) {
					try {
						(new AnalyzedElementFrame(0)).setEditContent();
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
				new AnalyzedElementFrame(1);
			}
		};
	}
	
	/**
	 * Special frame for add/edit content of analyzed elements table
	 * @author olegk
	 */
	private class AnalyzedElementFrame extends JFrame{
		
		/**
		 * Mode of frame
		 * 0 - editing
		 * 1 and others - adding
		 */
		private int modeFrame;
		
		/**
		 * ManagementController content panel for components
		 */
		private JPanel contentPanel;
		
		/**
		 * @see CustomComboBox
		 */
		private CustomComboBox elements, sinterTypes;
		
		/**
		 * Data for editing
		 */
		private Object[] editData;
		
		/**
		 * Row number for content build
		 */
		private int row;
		
		/**
		 * Class constructor
		 * @param modeFrame (int) - Mode of frame</br>
		 * 							0 - editing</br>
		 * 							1 and others - adding
		 */
		public AnalyzedElementFrame(int modeFrame) {
			super((modeFrame==0?"Редактирование ":"Добавление ")+"записи");
			
			this.modeFrame = modeFrame;
			
			//Build up interface of frame
			setCustomizedPanel();
			
			//Add to close program event operation for close database connection
			this.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					//ManagementController.setEnable(true);
				}
			});
			
			formSettings();
		}
		
		/**
		 * Get customized panel with components for adding or updating table
		 */
		private void setCustomizedPanel() {
			
			JPanel ManagementControllerPanel = new JPanel(new BorderLayout());
			
			initContent();			
			
			//Add button section
			JButton sensorAction = new JButton((modeFrame==0?"Редактировать ":"Добавить"));
			
			//Clicking event by button processing
			
			sensorAction.addActionListener(modeFrame==0?getEditActionListener():getAddActionListener());
			
			ManagementControllerPanel.add(contentPanel, BorderLayout.CENTER);
			ManagementControllerPanel.add(sensorAction, BorderLayout.SOUTH);
			
			setContentPane(ManagementControllerPanel);
		}
		
		/**
		 * Configure the form for the correct display
		 */
		private void formSettings() {
			//ManagementController.setEnable(false);
			
			this.setEnabled(true);
             
			//Settings of frame
			this.setSize(350, 300);
			this.setVisible(true);
			
			this.setFocusable(true);
		}
		
		/**
		 * Close sub frame (this frame)
		 */
		protected void close() {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		
		/**
		 * Build up interface of frame</br>
		 * Initialize all components
		 */
		private void initContent() {
			contentPanel = new JPanel(new GridBagLayout());
			
			row = 0;
			
			initElementSection();	
			initSinterSection();
		}

		/**
		 * Init section for working with elements
		 */
		private void initElementSection() {
			contentPanel.add(new JLabel("Выберите анализируемый элемент"), Main.getManagementController().createGbc(0, row++));
			
			elements = new CustomComboBox();
			
			for(Object[] row: Main.getDB().getElementsTitle()) {
				elements.addID(Integer.parseInt((String) row[0]));
				elements.addItem((String)row[1]);
			}
			
			contentPanel.add(elements, Main.getManagementController().createGbc(0, row++));
		}
		
		/**
		 * Init section for working with sinter types
		 */
		private void initSinterSection() {
			contentPanel.add(new JLabel("Выберите тип аглошихты"), Main.getManagementController().createGbc(0, row++));
			
			sinterTypes = new CustomComboBox();
			
			for(Object[] row: Main.getDB().getSinterTypesTitle()) {
				sinterTypes.addID(Integer.parseInt((String) row[0]));
				sinterTypes.addItem((String)row[1]);
			}
			
			contentPanel.add(sinterTypes, Main.getManagementController().createGbc(0, row++));
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
			
			elements.setSelectedItem((String) editData[1]);
			sinterTypes.setSelectedItem((String) editData[2]);
		}
		
		/**
		 * Add new analyzed element to table
		 * @return ActionListener with add action 
		 */
		private ActionListener getAddActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что точно хотите добавить запись?\nВводимые параментры:"
							+"\nВыбранный элемент: "+elements.getSelectedItem()
							+"\nВыбранный тип аглошихты: "+sinterTypes.getSelectedItem(),
							"Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка добавления записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;						
						
						if(Main.getDB().addAnalyzedElement(
									elements.getSelectedID(),
									sinterTypes.getSelectedID()
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
		 * Edit content of analyzed elements table
		 * @return ActionListener with edit action
		 */
		private ActionListener getEditActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					
					if(table.getSelectedRow() == -1) {
						JOptionPane.showMessageDialog(null, "Выберите редактируемый элемент!\nТаблица была обновлена", "Ошибка", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
									
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что точно хотите изменить запись?\nВводимые параментры:"
									+"\nВыбранный элемент: "+elements.getSelectedItem()
									+"\nВыбранный тип аглошихты: "+sinterTypes.getSelectedItem(),
									"Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка изменения записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;						
						
						if(Main.getDB().updateAnalyzedElement(
								Integer.parseInt((String) editData[0]),
								elements.getSelectedID(),
								sinterTypes.getSelectedID()
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
	}
}
