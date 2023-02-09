package plant;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;

/**
 * Abstract class for new MDI components of application
 * @author olegk
 * @version 0.1
 * @since 19.10.2022
 * @see JInternalFrame, TableModelListener
 */
public abstract class IFrame extends JInternalFrame implements TableModelListener{
	
	/**
	 * Panel for content of interface: table and manage buttons
	 * @see JPanel
	 */
	protected JPanel panel;
	
	/**
	 * Scroll panel for table
	 * @see JScrollPane, JTable
	 */
	protected JScrollPane sp;
	
	/**
	 * Main table for content from database
	 * @see JTable 
	 */
	protected JTable table;
	
	/**
	 * Class constructor
	 * @param title String This variable mean title of program
	 */
	protected IFrame(String title) {
		super(title, true, true, true, true);
	}
	
	/**
	 * Get id of selected rows from database
	 * @return int[] This variable consist from selected rows IDs
	 */
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
	 * Function for table update
	 */
	protected abstract void updateTable();
	
	/**
	 * Function for get data of row by row number
	 * @param number Int This variable is number of row from table
	 * @return Object[] Data of row by row number
	 */
	protected abstract Object[] getRow(int number);	
}
