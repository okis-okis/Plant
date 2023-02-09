package plant.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
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
 * Class for create frame for working with Mines table
 * @author olegk
 * @version 0.1
 * @since 19.10.2022
 * @see IFrame
 */
public class Mines extends IFrame{

	/**
	 * Titles of columns
	 */
	private static Object[] columnNames = {"Идентификатор рудника", "Наименование рудника", "Дополнительная информация"};
	
	/**
	 * MinesIFrame class constructor
	 * @param title Title of form
	 */
	public Mines(){
		super("Рудники");
		
		int inset = 50;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(inset, inset, screenSize.width - inset * 2,
				screenSize.height - inset * 2);
		
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
					new MineFrame(1);
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
						if(Main.getDB().deleteMine(id) == false) {
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
	 * Get count of columns in Mines table
	 * @return int value with result 
	 */
	public static int getColumnLength() {
		return columnNames.length; 
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
						(new MineFrame(0)).setEditContent();
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
	 * Get selected IDs from Mines table
	 */
	protected int[] getSelectedID() {
		int[] result = new int[table.getSelectedRows().length];
		int counter = 0;
		for(int rowNumber: table.getSelectedRows()){
			result[counter] = Integer.parseInt((String)getRow(rowNumber)[0]);
			counter++;
		}
		
		if(counter==0) {
			return null;
		}
		
		return result;
	}
	
	/**
	 * Get row data from Mines table by row number
	 */
	protected Object[] getRow(int number) {
		Object[] data = new Object[getColumnLength()];
		
		for(int i=0;i<getColumnLength();i++) {
			data[i] = table.getValueAt(number, i);
		}
		
		return data;
	}
	
	/**
	 * Process for table change event </br>
	 * This event need for table edit
	 */
	public void tableChanged(TableModelEvent e) {
		updateTable();
    }
	
	/**
	 * Updating table: get new data from Mines table
	 */
	public void updateTable() {		
		table = new JTable(Main.getDB().getMines(columnNames.length), columnNames);
		table.getModel().addTableModelListener(this);
		
		if(sp != null) {
			panel.remove(sp);
		}
		
		panel.add(sp = new JScrollPane(table), BorderLayout.CENTER);
		panel.updateUI();
	}
	
	/**
	 * SubFrame for adding new data to Mines table
	 * @author olegk
	 * @version 0.1
	 * @since 19.20.2022
	 */
	private class MineFrame extends JFrame{
		
		/**
		 * Mode of frame
		 * 0 - editing
		 * 1 and others - adding
		 */
		private int modeFrame;
		
		private JTextField titleField;
		private JTextArea additionalArea;
		
		private Object[] editData;
		
		/**
		 * Class constructor
		 */
		public MineFrame(int modeFrame) {
			super((modeFrame==0?"Редактирование ":"Добавление ")+"записи");
			
			this.modeFrame = modeFrame;
			
			//Build interface
			JPanel mainPanel = new JPanel(new BorderLayout());
			JPanel panel = new JPanel(new GridBagLayout());
			
			//Title section
			panel.add(new JLabel("Наименование рудника"), Main.getManagementController().createGbc(0, 0));
			titleField = new JTextField();
			titleField.setColumns(20);
			panel.add(titleField, Main.getManagementController().createGbc(0, 1));
			
			//Additional section
			panel.add(new JLabel("Дополнительная информация"), Main.getManagementController().createGbc(0, 2));
			additionalArea = new JTextArea();
			additionalArea.setColumns(20);
			additionalArea.setRows(5);
			panel.add(new JScrollPane(additionalArea), Main.getManagementController().createGbc(0, 3));
			
			//Add button section
			JButton MineBtn;
			if(this.modeFrame == 0) {
				MineBtn = new JButton("Изменить");
				
				//Clicking event by button processing
				MineBtn.addActionListener(getEditActionListener());
			}else {
				MineBtn = new JButton("Добавить");
				
				//Clicking event by button processing
				MineBtn.addActionListener(getAddActionListener());
			}
			
			mainPanel.add(panel, BorderLayout.CENTER);
			mainPanel.add(MineBtn, BorderLayout.SOUTH);
			setContentPane(mainPanel);
			
			//Add to close program event operation for close database connection
			this.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					//Main.getManagementController().setEnable(true);
				}
			});
			
			//Main.getManagementController().setEnable(false);
			this.setEnabled(true);
			
			//Settings of frame
			this.setSize(250, 300);
			this.setVisible(true);
			
			this.setFocusable(true);
		}
		
		/**
		 * Function using for updating table.</br> 
		 * Set to fields values of table row by id.
		 * @throws ParseException Error handling
		 * @see ParseException
		 */
		public void setEditContent() throws ParseException {
			editData = getRow(table.getSelectedRow());
			
			titleField.setText((String)editData[1]);
			additionalArea.setText((String)editData[2]);
		}
		
		private ActionListener getAddActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите добавить запись?", "Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка добавления записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;
						
						if(Main.getDB().addMine(titleField.getText(), additionalArea.getText())) {
							result = "Запись была добавлена в БД";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
						}		
						JOptionPane.showMessageDialog(null, result, "Результат добавления", messageCode);
						updateTable();
						titleField.setText(null);
						additionalArea.setText(null);
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
					//Checking editing
			        int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите отредактировать запись?", "Подтверждение действий", 
					        JOptionPane.YES_NO_OPTION);
			        
			        if(answer == JOptionPane.YES_OPTION) {
			        	//Edit table row
			        	int id = Integer.parseInt((String)editData[0]);
			        	String title = (String) titleField.getText();
			        	String additional = (String) additionalArea.getText();
			        	
			        	if(Main.getDB().updateMine(id, title, additional)) {
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
	}
}
