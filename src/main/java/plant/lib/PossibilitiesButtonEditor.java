package plant.lib;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import plant.Main;
import plant.frames.Positions;

public class PossibilitiesButtonEditor extends DefaultCellEditor 
{
  private String label;
  private int[] rowID;
  
  public PossibilitiesButtonEditor(JCheckBox checkBox)
  {
    super(checkBox);
  }
  
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) 
  {
    label = (value == null) ? "Modify" : value.toString();
    JButton button = new JButton((String)value);
    button.addActionListener(
    		new ActionListener()
    	    {
    	        public void actionPerformed(ActionEvent event)
    	        {    		    	
    	        	Object[] data = Main.getDB().getPossibilitiesByPositionID(rowID[row]);
    	        	
    	        	JOptionPane.showMessageDialog(null, generateResponse(0, "рудников", data[2])+
    	        			generateResponse(0, "эшелонов", data[3])+
    	        			generateResponse(0, "вагонов", data[4])+
    	        			generateResponse(0, "должностей", data[5])+
    	        			generateResponse(0, "работников", data[6])+
    	        			generateResponse(0, "элементов", data[7])+
    	        			generateResponse(0, "анализируемых элементов", data[8])+
    	        			generateResponse(0, "типов шихты", data[9])+
    	        			generateResponse(0, "назначений бункеров", data[10])+
    	        			generateResponse(0, "бункеров", data[11])+
    	        			generateResponse(0, "наполнений бункеров", data[12])+
    	        			generateResponse(0, "штабелей", data[13])+
    	        			generateResponse(0, "составов материала", data[14])
    	        			, "Возможности", JOptionPane.INFORMATION_MESSAGE);
    	        }
    	      }
    	    );
    
    return button;
  }
  
  private String generateResponse(int mode, String tableName, Object data) {
	  return ((mode==0)?"Просмотр":"Редактирование")+ " таблицы "+tableName+": "+getStatus(data)+"\n";
  }
  
  private String getStatus(Object answer) {
	return ((String)answer).equals((String)"1")?"Да":"Нет";  
  }
  
  public void setRowID(int[] id) {
	  rowID = id;
  }
  
  public Object getCellEditorValue() 
  {
    return new String(label);
  }
  
}