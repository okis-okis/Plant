package plant.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;

import plant.IFrame;
import plant.Main;

/**
 * Class for create frame for working with bunker purpose table
 * @author olegk
 * @version 0.1
 * @see IFrame
 */
public class BunkersPurpose extends IFrame{

	/**
	 * Titles of columns
	 */
	private static Object[] columnNames = {"Идентификатор", "Назначение", "Примечание"};
	
	/**
	 * Constructor of BunkersPurpose class
	 * @param title Title of frame
	 * @see IFrame
	 */
	public BunkersPurpose() {
		super("Назначения бункеров");
		
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
					new PurposesFrame(1);
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
					for(int id: getSelectedID()) {
						if(Main.getDB().deletePurpose(id) == false) {
							JOptionPane.showMessageDialog(null, "Произошла ошибка при удалении записи с идентификатором "+id, "Результат добавления", JOptionPane.ERROR_MESSAGE);
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
	 * Private method for processing the edit button
	 * @return ActionListener with actions
	 */
	private ActionListener getEditActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				int[] data = getSelectedID();
				if(data != null) {
					try {
						(new PurposesFrame(0)).setEditContent(data[0]);
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
	 * get new data from DB
	 */
	@Override
	protected void updateTable() {
		table = new JTable(Main.getDB().getPurposes(), columnNames);
		table.getModel().addTableModelListener(this);
		
		if(sp != null) {
			panel.remove(sp);
		}
		
		panel.add(sp = new JScrollPane(table), BorderLayout.CENTER);
		panel.updateUI();		
	}

	/**
	 * Get new data from DB by row number
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
	 * Get columns count
	 */
	public static int getColumnLength() {
		return columnNames.length;
	}
	
	/**
	 * Sub action for add/edit content
	 * @author olegk
	 */
	private class PurposesFrame extends JFrame{
		private int modeFrame;
		private JPanel contentPanel;
		
		private JTextField purpose;
		
		private JTextArea additionalArea;
		
		private int purposeID;
		
		/**
		 * Class constructor
		 */
		public PurposesFrame(int modeFrame) {
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
		 * Build up interface of frame</br>
		 * Initialize all components
		 */
		private void initContent() {
			contentPanel = new JPanel(new GridBagLayout());
			
			initPurposeSection();
			initAdditionalSection();
			
			
		}
		
		/**
		 * Set edit content by id bunker position 
		 */
		public void setEditContent(int id) throws ParseException {
			Object[] purposeData = Main.getDB().getPurposeByID(id);

			this.purposeID = Integer.parseInt((String)purposeData[0]); // Идентификатор
			
			purpose.setText((String)purposeData[1]);
			additionalArea.setText((String)purposeData[2]);
		}

		/**
		 * Init section for working with purposes
		 */
		private void initPurposeSection() {
			contentPanel.add(new JLabel("Назначение бункера"), Main.getManagementController().createGbc(0, 0));
			purpose = new JTextField();
			purpose.setColumns(20);
			contentPanel.add(purpose, Main.getManagementController().createGbc(0, 1));
		}

		/**
		 * Init section for working with additional info
		 */
		private void initAdditionalSection() {
			contentPanel.add(new JLabel("Дополнительная информация"), Main.getManagementController().createGbc(0, 2));
			additionalArea = new JTextArea();
			additionalArea.setColumns(20);
			additionalArea.setRows(5);
			contentPanel.add(new JScrollPane(additionalArea), Main.getManagementController().createGbc(0, 3));
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
						
						if(Main.getDB().addPurpose(purpose.getText(), additionalArea.getText())){
							result = "Запись была добавлена в БД";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
							
							purpose.setText("");
							additionalArea.setText("");
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
						
						if(Main.getDB().updatePurpose(purposeID,
								purpose.getText(),
								additionalArea.getText())){
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
