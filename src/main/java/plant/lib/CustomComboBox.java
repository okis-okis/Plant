package plant.lib;

import java.util.Vector;

import javax.swing.JComboBox;

/**
 * Inherited from JComboBox class</br>
 * Class need for consist not only items (position title),
 * also ID of positions and methods for working with it
 * @author olegk
 * @version 0.1
 * @since 20.10.2022
 * @see JComboBox
 */
public class CustomComboBox extends JComboBox{
	
	/**
	 * Empty constructor
	 */
	public CustomComboBox() {
	}
	
	/**
	 * Vector for storing identifiers 
	 */
	private Vector<Integer> ID = new Vector<Integer>();  
	
	/**
	 * Get id of selected item
	 * @return int ID of selected item
	 */
	public int getSelectedID() {
		return (int)ID.toArray()[getSelectedIndex()]; 
	}
	
	/**
	 * Add new identifier to storage</br>
	 * Stored in a pair to item
	 * @param id int ID to add
	 */
	public void addID(int id) {
		ID.add(id);
	}
	
	/**
	 * Set selected item by ID 
	 * @param id ID for select item
	 */
	public void setSelectedID(int id) {
		if(getIndexByID(id)!=-1)
		setSelectedIndex(getIndexByID(id));
	}
	
	/**
	 * Get index of item by it ID
	 * @param id for find index
	 * @return index of item by it ID</br>
	 * index -1 indicates an error
	 */
	public int getIndexByID(int id) {
		for(int i=0;i<ID.toArray().length;i++) {
			if((Integer)ID.toArray()[i] > id) {
				break;
			}
			
			if((Integer)ID.toArray()[i] == id) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Get item by it ID
	 * @param id for find item
	 * @return string with need item
	 */
	public String getItemByID(int id) {
		return (String) this.getItemAt(getIndexByID(id));
	}
	
	/**
	 * Get id by item
	 * @param item String with item for find id
	 * @return id of need item</br>
	 * Result -1 indicates an error
	 */
	public int getIdByItem(String item) {
		for(int i=0;i<this.getItemCount();i++) {
			if(getItemAt(i) == item) {
				return (Integer)ID.toArray()[i];
			}
		}
		return -1;
	}

	
}