package plant.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;

import plant.IFrame;
import plant.Main;
import plant.lib.CustomComboBox;

/**
 * Class for create frame for working with bunkers table
 * @author olegk
 * @version 0.1
 * @see IFrame
 */
public class Bunkers extends IFrame{

	/**
	 * Titles of columns
	 */
	private static Object[] columnNames = {"Номер бункера", "Назначение"};
	
	/**
	 * 
	 */
	private int[] bunkerNumbers;
	
	/**
	 * Constructor of Bunkers class
	 * @param title Title of frame
	 * @see IFrame
	 */
	public Bunkers() {
		super("Таблица бункеров");
		
		//Create panel with table, manage buttons and edit area
		panel = new JPanel(new BorderLayout());
								
		updateTable();
								
		//Manage panel
		JPanel managePanel = new JPanel(new FlowLayout());
								
		JButton updateTableBtn = new JButton("Обновить");
		updateTableBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){  
				updateTable();
			}
		});
		managePanel.add(updateTableBtn);
								
		JButton addRow = new JButton("Добавить");
		addRow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					new BunkerFrame(1);
				}
		});
		managePanel.add(addRow);
						
		JButton changeRow = new JButton("Изменить");
		changeRow.addActionListener(getEditActionListener());
		managePanel.add(changeRow);
								
		JButton deleteRow = new JButton("Удалить");
		deleteRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){  
					
				int answer = JOptionPane.showConfirmDialog(null,
						"Вы уверены, что хотите удалить выбранные записи?",
						"Информационное окно", 
				        JOptionPane.YES_NO_OPTION);

				if(answer == JOptionPane.YES_OPTION) {
					for(int row: table.getSelectedRows()) {
						
						if(Main.getDB().deleteBunker(bunkerNumbers[row]) == false) {
							JOptionPane.showMessageDialog(null, "Произошла ошибка при удалении записи с номером "+bunkerNumbers[row], "Результат добавления", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				updateTable();
				}
		});
		managePanel.add(deleteRow);
								
		managePanel.setMaximumSize(new Dimension(200, 150));
							
		panel.add(managePanel, BorderLayout.SOUTH);
								
		setContentPane(panel);
								
		setSize(200, 300);
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
	 * Get count of columns in Bunkers table
	 * @return int value with result 
	 */
	public static int getColumnLength() {
		return columnNames.length;
	}
	
	/**
	 * Update table of Bunkers</br>
	 * Get new data from database
	 */
	@Override
	protected void updateTable() {
		Object[][] data = Main.getDB().getBunkersWithPurpose();
		table = new JTable(data, columnNames);

		bunkerNumbers = new int[data.length];
		
		int count = 0;
		for(Object[] row : data) {
			bunkerNumbers[count] = Integer.parseInt((String)row[0]);
			count++;
		}
		
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
	protected Object[] getRow(int number) {
		
		Object[] data = new Object[getColumnLength()];
		
		for(int i=0;i<getColumnLength();i++) {
			data[i] = table.getValueAt(number, i);
		}
		
		return data;
	}
	
	/**
	 * Action for edit button
	 * @return ActionListener with edit action
	 */
	private ActionListener getEditActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				int[] data = getSelectedID();
				//System.out.println(data[0]);
				if(data != null) {
					try {
						(new BunkerFrame(0)).setEditContent(data[0]);
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
	 * Sub frame for add/edit content
	 * @author olegk
	 */
	private class BunkerFrame extends JFrame{
		private int modeFrame;
		private JPanel contentPanel;
		
		private JTextField newBunkerNumber;
		
		private CustomComboBox purpose;
		
		private int bunkerNumber;
		
		/**
		 * Class constructor
		 */
		public BunkerFrame(int modeFrame) {
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
		 * Get customized panel with components for adding new worker
		 * @see JPanel
		 */
		private void setCustomizedPanel() {
			
			JPanel mainPanel = new JPanel(new BorderLayout());
			
			initContent();			
			
			//Add button section
			JButton sensorAction = new JButton((modeFrame==0?"Редактировать ":"Добавить"));
			
			//Clicking event by button processing
			
			sensorAction.addActionListener(modeFrame==0?getEditPurposeActionListener():getAddPurposeActionListener());
			
			mainPanel.add(contentPanel, BorderLayout.CENTER);
			mainPanel.add(sensorAction, BorderLayout.SOUTH);
			
			setContentPane(mainPanel);
		}
		
		/**
		 * Set edit content to sub frame
		 * @param number (int) - number of selected row
		 * @throws ParseException
		 */
		public void setEditContent(int number) throws ParseException {
			
			Object[] purposeData = Main.getDB().getBunkerByNumber(number);

			this.bunkerNumber = Integer.parseInt((String)purposeData[0]); // Номер
			
			newBunkerNumber.setText((String)purposeData[0]);
			purpose.setSelectedItem((String)purposeData[1]);
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
			
			initNumberSection();
			initPurposeSection();			
		}
		
		/**
		 * Init section for working with bunker numbers
		 */
		private void initNumberSection() {
			//Echelon number section
			contentPanel.add(new JLabel("Номер бункера"), Main.getManagementController().createGbc(0, 0));
			
			NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
			DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
			decimalFormat.setGroupingUsed(false);
			newBunkerNumber = new JFormattedTextField(decimalFormat);
			newBunkerNumber.setColumns(15); //whatever size you wish to set
			
			contentPanel.add(newBunkerNumber, Main.getManagementController().createGbc(0, 1));
		}

		/**
		 * Init section for working with purposes
		 */
		private void initPurposeSection() {
			contentPanel.add(new JLabel("Выберите назначение бункера"), Main.getManagementController().createGbc(0, 2));
			purpose = new CustomComboBox();
			
			for(Object[] row: Main.getDB().getPurposes()) {
				purpose.addID(Integer.parseInt((String) row[0]));
				purpose.addItem((String)row[1]);
			}
			
			contentPanel.add(purpose, Main.getManagementController().createGbc(0, 3));		
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
		 * Private method for processing the add new worker button
		 * @return ActionListener with actions
		 */
		private ActionListener getAddPurposeActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите добавить запись?", "Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка добавления записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;						
						
						if(Main.getDB().addBunker(Integer.parseInt(newBunkerNumber.getText()), purpose.getSelectedID())){
							result = "Запись была добавлена в БД";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
							
							newBunkerNumber.setText("");
							purpose.setSelectedIndex(0);
						}		
						
						JOptionPane.showMessageDialog(null, result, "Результат:", messageCode);
						updateTable();
					}
			    }
			};
		}
		
		/**
		 * Private method for processing the add new worker button
		 * @return ActionListener with actions
		 */
		private ActionListener getEditPurposeActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите изменить запись?", "Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка изменения записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;
						
						if(Main.getDB().updateBunker(
								bunkerNumber,
								Integer.parseInt((String)newBunkerNumber.getText()),
								purpose.getSelectedID()
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
	}
}
