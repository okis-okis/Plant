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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.TableModelEvent;

import com.toedter.calendar.JDateChooser;

import plant.IFrame;
import plant.Main;
import plant.lib.CustomComboBox;

/**
 * Class for create frame for working with Material Composition table
 * @author olegk
 * @version 0.1
 * @since 02.11.2022
 * @see IFrame 
 * */
public class MaterialComposition extends IFrame{
	
	/**
	 * Columns title
	 */
	protected static Object[] columnNames = {"Идентификатор", "MgO", "CaO", "Al2O3", "SiO2", "P", "S", "Fe суммарное", "Работник", "Время проведения анализа", "Примечание"};
	
	/**
	 * Class constructor
	 * @param title String  Title of frame
	 */
	public MaterialComposition() {
		super("Состав материала");

		panel = new JPanel(new BorderLayout());
		
		updateTable();
							
		panel.add(getManagePanel(), BorderLayout.SOUTH);
								
		setContentPane(panel);	
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
						(new EditCompositionFrame(0)).setEditContent(data[0]);
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
	 * Processing table changes
	 */
	@Override
	public void tableChanged(TableModelEvent arg0) {
		updateTable();		
	}

	/**
	 * Updating tables by getting new data from DB and delete old table</br>
	 * The modified employee column is used
	 */
	@Override
	protected void updateTable() {
		Object[][] data = Main.getDB().getCompositionsWithWorker();
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
	 * Get length of title columns array
	 * @return length of title column array
	 * @see columnNames
	 */
	public static int getColumnLength() {
		return columnNames.length;
	}
	
	/**
	 * Get manager panel for table processing
	 * @return Modificated panel from manage buttons
	 */
	private JPanel getManagePanel() {
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
					new EditCompositionFrame(1);
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
						"Вы уверены, что хотите удалить запись/-си?",
						"Информационное окно", 
				        JOptionPane.YES_NO_OPTION);

				if(answer == JOptionPane.YES_OPTION) {
					
					for(int id: getSelectedID()) {			
						if(Main.getDB().deleteComposition(id) == false) {
							JOptionPane.showMessageDialog(null, "Произошла ошибка при удалении записи с идентификатором "+id, "Результат добавления", JOptionPane.ERROR_MESSAGE);
						}
					}				
					updateTable();
				}
			}
		});
		managePanel.add(deleteRow);
				
		return managePanel;
	}
	
	public static Object[] getColumns(){
		return columnNames;
	}
	
	/**
	 * SubFrame for adding and changing (update) new data to MaterialCompositions table
	 * @author olegk
	 * @version 0.1
	 * @since 02.11.2022
	 */
	private class EditCompositionFrame extends JFrame{
		
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
		
		/**
		 * Float value of MgO of Composition
		 */
		private JSpinner MgO;
		
		/**
		 * Float value of CaO of Composition
		 */
		private JSpinner CaO;
		
		/**
		 * Float value of Al2O3 of Composition
		 */
		private JSpinner Al2O3;
		
		/**
		 * Float value of SiO2 of Composition
		 */
		private JSpinner SiO2;
		
		/**
		 * Float value of P of Composition
		 */
		private JSpinner P;
		
		/**
		 * Float value of S of Composition
		 */
		private JSpinner S;
		
		/**
		 * Float value of Fe total of Composition
		 */
		private JSpinner FeTotal;
		
		/**
		 * ComboBox with Workers
		 * @see CustomComboBox
		 */
		private CustomComboBox workers;
		
		/**
		 * Date of analysis
		 * @see JDateChooser
		 */
		private JDateChooser dateAnalyses;
		
		/**
		 * Time of analysis
		 */
		private JSpinner timeAnalyses;
		
		/**
		 * Additional information
		 */
		private JTextArea additionalArea;
		
		/**
		 * ID of editing composition
		 */
		private int compositionID;
		
		/**
		 * Editing row number
		 */
		private int row = 0;
		
		/**
		 * Float values of composition fields (already converted)
		 */
		float MgOValue, CaOValue, 
			  Al2O3Value, SiO2Value, 
			  PValue, SValue, FeTotalValue;
		
		/**
		 * Editing row data
		 */
		Object[] editData;
		
		/**
		 * Class constructor
		 * @param modeFrame mode of current frame</br>
		 * 0 - editing
		 * 1 and others - adding
		 * @see modeFrame
		 */
		public EditCompositionFrame(int modeFrame) {
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
			
			sensorAction.addActionListener(modeFrame==0?getEditCompositionActionListener():getAddCompositionActionListener());
			
			mainPanel.add(contentPanel, BorderLayout.CENTER);
			mainPanel.add(sensorAction, BorderLayout.SOUTH);
			
			setContentPane(mainPanel);
		}
		
		/**
		 * Function using for updating table.</br> 
		 * Set to fields (Spinner) values of table row by id.
		 * @param id int ID of table row for set to fields
		 * @throws ParseException Error handling
		 * @see ParseException
		 */
		public void setEditContent(int id) throws ParseException {
			editData = Main.getDB().getCompositionByID(id);
			
			this.compositionID = Integer.parseInt((String)editData[0]);
			
			MgO.setValue(Float.parseFloat((String)editData[1]));
			CaO.setValue(Float.parseFloat((String)editData[2]));
			Al2O3.setValue(Float.parseFloat((String)editData[3]));
			SiO2.setValue(Float.parseFloat((String)editData[4]));
			P.setValue(Float.parseFloat((String)editData[5]));
			S.setValue(Float.parseFloat((String)editData[6]));
			FeTotal.setValue(Float.parseFloat((String)editData[7]));
			workers.setSelectedID(Integer.parseInt((String)editData[8]));
			
			String date = ((String)editData[9]).split(" ")[0], 
				   time = ((String)editData[9]).split(" ")[1];
			
			time = time.substring(0, time.length() - 2);
			
			if(editData[9]!=null) {
				dateAnalyses.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date));
			}
			
			try {
				 
	            // Getting the Date from String by
	            // creating object of Instant class
				timeAnalyses.setValue(new SimpleDateFormat("HH:mm:ss").parse(time));
	        }
	 
	        // Catch block to handle exceptions
	        catch (DateTimeParseException e) {
	 
	            // Throws DateTimeParseException
	            // if the string cannot be parsed
	        	Main.getLogger().error(e.getMessage());
	        }
			
			additionalArea.setText((String)editData[10]);
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
			
			initFieldsSection();	
			initWorkerSection();
			initDateTimeSection();
			initAdditionalSection();
		}
		
		/**
		 * Initialization of additional section of content
		 */
		private void initAdditionalSection() {
			contentPanel.add(new JLabel("Дополнительная информация"), Main.getManagementController().createGbc(0, row++));
			additionalArea = new JTextArea();
			additionalArea.setColumns(20);
			additionalArea.setRows(5);
			contentPanel.add(new JScrollPane(additionalArea), Main.getManagementController().createGbc(0, row++));
		}
		
		/**
		 * Initialization of date and time section of content
		 */
		private void initDateTimeSection() {
			contentPanel.add(new JLabel("Выберите дату осуществления проверки"), Main.getManagementController().createGbc(0, row++));
			
			dateAnalyses = new JDateChooser();
			
			dateAnalyses.setDateFormatString("yyyy-MM-dd");
			contentPanel.add(dateAnalyses, Main.getManagementController().createGbc(0, row++));
			
			contentPanel.add(new JLabel("Выберите время осуществления проверки"), Main.getManagementController().createGbc(0, row++));
			
			timeAnalyses = new JSpinner( new SpinnerDateModel() );
			JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeAnalyses, "HH:mm:ss");
			timeAnalyses.setEditor(timeEditor);
			timeAnalyses.setValue(new Date()); // will only show the current time
			
			contentPanel.add(timeAnalyses, Main.getManagementController().createGbc(0, row++));
		}

		/**
		 * Initialize workers section
		 */
		private void initWorkerSection() {
			//Workers section
			contentPanel.add(new JLabel("Выберите работника, который осуществил анализ"), Main.getManagementController().createGbc(0, row++));
			workers = Main.getManagementController().getWorkersComboBox();
			
			contentPanel.add(workers, Main.getManagementController().createGbc(0, row++));
		}
		
		/**
		 * Initialization of fields section with raw composition
		 */
		private void initFieldsSection() {
			MgO = addNewField("MgO", MgO);
			CaO = addNewField("CaO", CaO);
			Al2O3 = addNewField("Al2O3", Al2O3);
			SiO2 = addNewField("SiO2", SiO2);
			P = addNewField("P", P);
			S = addNewField("S", S);
			FeTotal = addNewField("FeTotal", FeTotal);
		}

		/**
		 * Function for adding new composition field to content panel with label and spinner
		 * @param elementTitle String of title of raw composition. Using after operation: "Значение "+elementTitle</br> 
		 * @param spin Raw Composition field
		 * @return JSpinner added and filled component
		 */
		private JSpinner addNewField(String elementTitle, JSpinner spin) {
			contentPanel.add(new JLabel("Значение "+elementTitle), Main.getManagementController().createGbc(0, row++));
			
			spin = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 0.01));
			
			contentPanel.add(spin, Main.getManagementController().createGbc(0, row++));
			
			return spin;
		}

		/**
		 * Configure the form for the correct display
		 */
		private void formSettings() {
			//Main.getManagementController().setEnable(false);
			
			this.setEnabled(true);
             
			//Settings of frame
			this.setSize(350, 750);
			this.setVisible(true);
			
			this.setFocusable(true);
		}
		
		/**
		 * Initialization of compositions variables with raw composition
		 */
		private void initCompositionVariables() {
			
			if(MgO.getValue().getClass().getSimpleName() != "Float") {
			
				MgOValue = ((Double)MgO.getValue()).floatValue(); 		
				CaOValue = ((Double)CaO.getValue()).floatValue(); 
				Al2O3Value = ((Double)Al2O3.getValue()).floatValue(); 
				SiO2Value = ((Double)SiO2.getValue()).floatValue();
				PValue = ((Double)P.getValue()).floatValue();
				SValue = ((Double)S.getValue()).floatValue();
				FeTotalValue = ((Double)FeTotal.getValue()).floatValue();
		
				return;
			}
			
			MgOValue = (Float)MgO.getValue();
			CaOValue = (Float)CaO.getValue();
			Al2O3Value = (Float)Al2O3.getValue();
			SiO2Value = (Float)SiO2.getValue();
			PValue = (Float)P.getValue();
			SValue = (Float)S.getValue();
			FeTotalValue = (Float)FeTotal.getValue();
		}
		
		/**
		 * Private method for processing the add new composition
		 * @return ActionListener with actions
		 */
		private ActionListener getAddCompositionActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					
					initCompositionVariables();
					
					if(getDateTimeAnalyse()==null) {
						
						JOptionPane.showMessageDialog(null, "Вы не указали дату или время проверки состава", "Результат:", JOptionPane.ERROR_MESSAGE);
						
						return;
					}
					
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите добавить запись?\nВводимые параментры:"
							+ "\nMgO: "+MgOValue
							+ "\nCaO: "+CaOValue
							+ "\nAl2O3: "+Al2O3Value
							+ "\nSiO2: "+SiO2Value
							+ "\nP: "+PValue
							+ "\nS: "+SValue
							+ "\nFe суммарное: "+FeTotalValue
							+ "\nРаботник:" + workers.getSelectedItem()
							+ "\nДата и время: " + getDateTimeAnalyse()
							+ "\nПримечание: " + additionalArea.getText(),
							"Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка добавления записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;						
						
						if(Main.getDB().addComposition(
								MgOValue, 
								CaOValue, 
								Al2O3Value, 
								SiO2Value, 
								PValue, 
								SValue, 
								FeTotalValue, 
								workers.getSelectedID(), 
								getDateTimeAnalyse(), 
								additionalArea.getText())
							){
							result = "Запись была добавлена в БД";
							messageCode = JOptionPane.INFORMATION_MESSAGE;
							
							additionalArea.setText("");
						}
						
						
						JOptionPane.showMessageDialog(null, result, "Результат:", messageCode);
						updateTable();
					}
			    }
			};
		}
		
		/**
		 * Function for get string with user chosen date and time
		 * @return String with date and time. Format: "yyyy-MM-dd HH:mm:ss"
		 */
		private String getDateTimeAnalyse() {
			String result = null;
			if(dateAnalyses.getDate() != null) {
				result = (new SimpleDateFormat("yyyy-MM-dd")).format(dateAnalyses.getDate());
				result+= " "+(timeAnalyses.getValue()+"").split(" ")[3];
			}
			return result;
		}
		
		/**
		 * Private method for processing the update button
		 * @return ActionListener with actions
		 */
		private ActionListener getEditCompositionActionListener() {
			return new ActionListener() {
				public void actionPerformed(ActionEvent e){  
					
					initCompositionVariables();
					
					if(getDateTimeAnalyse()==null) {
						
						JOptionPane.showMessageDialog(null, "Вы не указали дату или время проверки состава", "Результат:", JOptionPane.ERROR_MESSAGE);
						
						return;
					}
					
					int answer = JOptionPane.showConfirmDialog(null,
							"Вы уверены, что хотите изменить запись? Вносимые коррективы: \n"
									+ "\nMgO: "+ (String)editData[1] + "->" + MgOValue
									+ "\nCaO: "+(String)editData[2] + "->" +CaOValue
									+ "\nAl2O3: "+ (String)editData[3] + "->" +Al2O3Value
									+ "\nSiO2: "+(String)editData[4] + "->" +SiO2Value
									+ "\nP: "+(String)editData[5] + "->" +PValue
									+ "\nS: "+(String)editData[6] + "->" +SValue
									+ "\nFe суммарное: "+(String)editData[7] + "->" +FeTotalValue
									+ "\nРаботник: " + workers.getItemByID(Integer.parseInt((String)editData[8])) + "->" +workers.getSelectedItem()
									+ "\nДата и время: " + (String)editData[9] +"->"+ getDateTimeAnalyse()
									+ "\nПримечание: \"" + (String)editData[10]+"\" -> \"" + additionalArea.getText()+"\"", 
									"Информационное окно", 
					        JOptionPane.YES_NO_OPTION);
	
					if(answer == JOptionPane.YES_OPTION) {
						String result = "Ошибка изменения записи!";
						int messageCode = JOptionPane.ERROR_MESSAGE;
						
						if(Main.getDB().updateComposition(
								compositionID,
								MgOValue, 
								CaOValue, 
								Al2O3Value, 
								SiO2Value, 
								PValue, 
								SValue, 
								FeTotalValue, 
								workers.getSelectedID(), 
								getDateTimeAnalyse(), 
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
	}
}
