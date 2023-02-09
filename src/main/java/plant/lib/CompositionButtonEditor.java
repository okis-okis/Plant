package plant.lib;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import plant.Main;

public class CompositionButtonEditor extends DefaultCellEditor 
{
  private String label;
  
  public CompositionButtonEditor(JCheckBox checkBox)
  {
    super(checkBox);
  }
  
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) 
  {
    label = (value == null) ? "Modify" : value.toString();
    JButton button = new JButton(label);
    button.addActionListener(
    		new ActionListener()
    	    {
    	        public void actionPerformed(ActionEvent event)
    	        {
    	        	Object[] data = Main.getDB().getCompositionWithWorkerByID(Integer.parseInt((String)value));
    	        	JOptionPane.showConfirmDialog(null,
							"Вещество состоит из:\n"
							+ "\nMgO: "+data[1]
							+ "\nCaO: "+data[2]
							+ "\nAl2O3: "+data[3]
							+ "\nSiO2: "+data[4]
							+ "\nP: "+data[5]
							+ "\nS: "+data[6]
							+ "\nFe суммарное: "+data[7]
							+ "\nРаботник: " + data[8]
							+ "\nДата и время: " + data[9]
							+ "\nПримечание: " + data[10],
							"Состав", 
					        JOptionPane.OK_OPTION);
    	        }
    	      }
    	    );
    
    return button;
  }
  
  public Object getCellEditorValue() 
  {
    return new String(label);
  }
  
}