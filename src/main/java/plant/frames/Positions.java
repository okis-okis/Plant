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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import plant.IFrame;
import plant.Main;
import plant.lib.CompositionButtonEditor;
import plant.lib.PossibilitiesButtonEditor;
import plant.lib.TableButton;

/**
 * Class for create frame for working with Positions table
 * @author olegk
 * @version 0.1
 * @since 19.10.2022
 * @see IFrame
 */
public class Positions extends IFrame{

	/**
	 * Titles of columns
	 */
	private static Object[] columnNames = {"Идентификатор должности", "Наименование должности", "Возможности"};
	
	/**
	 * PositionsIFrame class constructor
	 * @param title Title of form
	 */
	public Positions(){
		super("Должности");
		
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
				new PositionFrame(1);
		    }
		});
		managePanel.add(addRow);
			
		JButton editRow = new JButton("Изменить");
		editRow.addActionListener(getEditActionListener());
		managePanel.add(editRow);
		
		JButton deleteRow = new JButton("Удалить");
		deleteRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){  
				int answer = JOptionPane.showConfirmDialog(null,
						"Вы уверены, что хотите удалить выбранные записи?",
						"Информационное окно", 
				        JOptionPane.YES_NO_OPTION);

				if(answer == JOptionPane.YES_OPTION) {
					for(int id: getSelectedID()) {
						if(Main.getDB().deletePosition(id) == false) {
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
		
	    setVisible(true);
	}
	
	/**
	 * Get selected IDs from Positions table
	 */
	protected int[] getSelectedID() {
		int[] result = new int[table.getSelectedRows().length];
		int counter = 0;
		for(int rowNumber: this.table.getSelectedRows()){
			result[counter] = Integer.parseInt((String)getRow(rowNumber)[0]);
			counter++;
		}
		
		if(counter==0) {
			return null;
		}
		
		return result;
	}
	
	/**
	 * Updating table: get new data from Positions table
	 */
	public void updateTable() {
		Object[][] data = Main.getDB().getPositions(getColumnLength());
		
		table = new JTable(data, columnNames);
		
		table.getColumn("Возможности").setCellRenderer(new TableButton());
		
		PossibilitiesButtonEditor editor = new PossibilitiesButtonEditor(new JCheckBox());
		int[] id = new int[data.length];
		for(int i=0;i<data.length;i++) {
			id[i] = Integer.parseInt((String)data[i][0]);
			table.setValueAt("Возможности", i, 2); 
		}
		editor.setRowID(id);
		
	    table.getColumn("Возможности").setCellEditor(editor);
	    
		
		table.getModel().addTableModelListener(this);
		
		if(sp != null) {
			panel.remove(sp);
		}
		
		panel.add(sp = new JScrollPane(table), BorderLayout.CENTER);
		panel.updateUI();
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
	
	public int getRowID(int row) {
		try {
			return Integer.valueOf((String)table.getValueAt(row, 0));
		}catch(Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
	    	alert.setTitle("Ошибка");
	    	alert.setHeaderText("Ошибка получения идентификатора записи");
	    	alert.setContentText(e.getMessage());
	    	alert.show();
			System.out.println(e);
		}
		return -1;
	}
	
	private ActionListener getEditActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e){
				int[] data = getSelectedID();
				if(data != null) {
					try {
						(new PositionFrame(0)).setEditContent();
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
	 * Process for table change event </br>
	 * This event need for table edit
	 */
	public void tableChanged(TableModelEvent e) {
		updateTable();
	}
	
	/**
	 * Get count of columns in Positions table
	 * @return
	 */
	public static int getColumnLength() {
		return columnNames.length; 
	}
	
	/**
	 * SubFrame for adding new data to Positions table
	 * @author olegk
	 * @version 0.1
	 * @since 20.20.2022
	 */
	private class PositionFrame extends JFrame{
		
		/**
		 * Mode of frame
		 * 0 - editing
		 * 1 and others - adding
		 */
		private int modeFrame;
		
		JTextField positionField;
		
		private Object[] editData;
		
		/**
		 * Class constructor
		 */
		public PositionFrame(int modeFrame) {
			//Set title of frame
			super((modeFrame==0?"Редактирование ":"Добавление ")+"записи");
			
			this.modeFrame = modeFrame;
			
			//Frame settings
			JPanel mainPanel = new JPanel(new BorderLayout());
			JPanel panel = new JPanel(new GridBagLayout());
			
			//Position sections
	
			positionField = new JTextField();
			positionField.setColumns(20);
			panel.add(new JLabel("Наименование должности"), Main.getManagementController().createGbc(0, 0));
			panel.add(positionField, Main.getManagementController().createGbc(0, 1));
			
			//Manage section
			//Add new position button
			JButton MineBtn;
			if(modeFrame==0) {
				MineBtn = new JButton("Редактировать должность");
				MineBtn.addActionListener(getEditActionListener());
			}
			else {
				MineBtn = new JButton("Добавить должность");
				MineBtn.addActionListener(getAddActionListener());
			}
			
			MineBtn.setMaximumSize(new Dimension(300, 150));
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
			
			//Form settings
			this.setSize(300, 150);
			this.setVisible(true);
		}
		
		/**
		 * Close sub frame (this frame)
		 */
		protected void close() {
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		
		 /*Set to fields values of table row by id.
		 * @throws ParseException Error handling
		 * @see ParseException
		 */
		public void setEditContent() throws ParseException {
			editData = getRow(table.getSelectedRow());
			
			positionField.setText((String)editData[1]);
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
						
						if(Main.getDB().addPosition(positionField.getText())) {
							result = "Запись была добавлена в БД";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
						}		
						JOptionPane.showMessageDialog(null, result, "Результат добавления", messageCode);
						updateTable();
						positionField.setText(null);
					}
			    }
			};
		}
		
		private ActionListener getEditActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					//Checking editing
			        int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите отредактировать запись?", "Подтверждение действий", 
					        JOptionPane.YES_NO_OPTION);
			        if(answer == JOptionPane.YES_OPTION) {
			        	//Edit table row
			        	int id = Integer.parseInt((String)editData[0]);
			        	String position = (String) positionField.getText();
			        
			        	if(Main.getDB().updatePosition(id, position)) {
			        		JOptionPane.showMessageDialog(null, "Запись была успешно изменена!", "Результат обновления", JOptionPane.INFORMATION_MESSAGE);
			        	}else {
			        		JOptionPane.showMessageDialog(null, "Произошла ошибка при обновлении записи. Возможно, некоторые пользователи устроены на эту должность, из-за чего операция невозможна. Переведите работников на другую должность или удалите их и повторите попытку.", "Результат обновления", JOptionPane.ERROR_MESSAGE);
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
