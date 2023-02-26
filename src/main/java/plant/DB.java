package plant;

import java.awt.Desktop;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import plant.frames.AnalyzedElements;
import plant.frames.Bunkers;
import plant.frames.BunkersFilling;
import plant.frames.BunkersPurpose;
import plant.frames.Echelons;
import plant.frames.Elements;
import plant.frames.MaterialComposition;
import plant.frames.Mines;
import plant.frames.Positions;
import plant.frames.SinterTypes;
import plant.frames.Stacks;
import plant.frames.Vans;
import plant.frames.Workers;
import plant.lib.WaitLayerUI;

/** 
 * <b>Class for working with DataBase</b></br>
 * Special class for working with database
 * </br>
 * Updates:</br>
 * 0.2 version - add options for working with Workers table
 * 
 * @author olegk
 * @version 0.2
 * @since 18.10.2022
 */
public class DB {
	//Settings of database
	private String ServerHost 	= "127.0.0.1"; 			//Host of server
	private String ServerPort 	= "3306";				//Post of server
	private String user 		= "oleg";					//User name
	private String password 	= "password";					//Password for user
	private String dbName 		= "xray";				//Name of DB, that will use
	
	//Connection with database
	private Connection connection;
	
	//Working with connection variable
	
	/**
	 *Open connection with database</br> 
	 *Automatically starts working when the program starts
	*/
	public Boolean openConnection() {
		
		//JFrame f = createUI();
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Properties info = new Properties();
			
			info.put("user", user);
			info.put("password", password);
			
			connection = DriverManager.getConnection("jdbc:mysql://"+ServerHost+":"+ServerPort+"/"+dbName+"?characterEncoding=utf8", info);
		} catch (Exception exception) {
			
            System.out.println("Database error: "+exception);
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
		return true;
	}
	
	public JFrame createUI() {
	    JFrame f = new JFrame ("Соединение с базой данных");
	     
	    final WaitLayerUI layerUI = new WaitLayerUI();
	    JLayer<JPanel> jlayer = new JLayer<JPanel>(new JPanel(), layerUI);
	     
	    layerUI.start();
	 
	    f.add (jlayer);
	     
	    f.setSize(300, 200);
	    f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	    f.setLocationRelativeTo (null);
	    f.setVisible (true);
	    
	    return f;
	  }

	/**
	 *Close connection with database</br> 
	 *Automatically stop working when the program stops
	*/
	public Boolean closeConnection() {
		try {
			connection.close();
		} catch (Exception exception) {
            System.out.println("Database error: "+exception);
            JOptionPane.showMessageDialog(null, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
		return true;
	}
	
	public Boolean login(String username, String password) {
		try {
			String request = "SELECT COUNT(idWorker) FROM xray.workers WHERE login LIKE MD5('"+username+"') AND password LIKE MD5('"+password+"')";
			String result = String.valueOf(getResult(request, 1)[0][0]);
		if(result.equals("1")) {
			return true;
		}
		}catch(Exception e) {
			System.out.println(e);
		}
		return false;
	}
	
	public int getWorkerIDByUsername(String username) {
		return Integer.parseInt(String.valueOf(getResult("SELECT idWorker FROM workers WHERE login LIKE MD5('"+username+"');", 1)[0][0]));		
	}
	
	public Object[][] getPrivilegesByWorkerID(int id){
		String request = "SELECT p.`Mines view`,\r\n"
				+ "p.`Echelons view`,\r\n"
				+ "p.`Vans view`,\r\n"
				+ "p.`Positions view`,\r\n"
				+ "p.`Workers view`,\r\n"
				+ "p.`Elements view`,\r\n"
				+ "p.`Analyzed elements view`,\r\n"
				+ "p.`Sinter types view`,\r\n"
				+ "p.`Bunkers purpose view`,\r\n"
				+ "p.`Bunkers view`,\r\n"
				+ "p.`Bunkers filling view`,\r\n"
				+ "p.`Stacks view`,\r\n"
				+ "p.`Materail compositions view`\r\n"
				+ "FROM workers w INNER JOIN positions p ON w.position = p.idPosition WHERE idWorker = "+id+";";
			
		//System.out.println(request);
			
		Object[] privil = getResult(request, 13)[0];
			
		Object[][] result = new Object[13][2];
		result[0][0] = "Mines view";
		result[1][0] = "Echelons view";
		result[2][0] = "Vans view";
		result[3][0] = "Positions view";
		result[4][0] = "Workers view";
		result[5][0] = "Elements view";
		result[6][0] = "AnalyzedElements view";
		result[7][0] = "SinterTypes view";
		result[8][0] = "BunkersPurpose view";
		result[9][0] = "Bunkers view";
		result[10][0] = "BunkersFilling view";
		result[11][0] = "Stacks view";
		result[12][0] = "MaterialComposition view";
			
		for(int i=0;i<13;i++) {
			if(privil[i].equals("0")) {
				result[i][1] = false;
			} else {
				result[i][1] = true;
			}
		}
		return result;
	}
	
	/**
	 * Function for get result by request
	 * @param request String This variable consist request for result
	 * 		  You can use function WHERE, JOIN and other operations
	 * @param columnLength Int Count of columns
	 * @return Array of object data with select result
	 */
	public Object[][] getResult(String request, int columnLength){
		try {
			Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet resultSet = statement.executeQuery(request);
			resultSet.last();
			int w = resultSet.getRow(); 
			
			Object[][] data = new Object[w][columnLength]; 
			
			resultSet.beforeFirst(); 
			
			int i = 0; 
			
			while (resultSet.next()) {
				// цикл по столбцам
				 for(int j=1;j<columnLength+1;j++) {
				 data[i][j-1] = resultSet.getString(j);
				 }
				 i++; 
	        }
			
			resultSet.close();
	        statement.close();
	        
			return data;
		}catch(Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.out.println("Database error: "+e);
		}
		return null;
	}
	
	/**
	 * Execute statement and print error (if any)
	 * @param pstmt Formed request (statement)
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 * @see PreparedStatement
	 */
	private Boolean executeStatement(PreparedStatement pstmt) {
		try {
			if(pstmt.executeUpdate() == 0) {
				return false;
			}
			return true;
		} catch(Exception e) {
			int answer = JOptionPane.showConfirmDialog(null, "Возможно, произошла ошибка.\nПодробнее об ошибках можно узнать в документации\nЖелаете открыть документацию?", "Ошибка!", JOptionPane.INFORMATION_MESSAGE);
			if(answer == JOptionPane.YES_OPTION) {
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			        try {
			            desktop.browse(new URI("https://okis-okis.github.io/DB-Doc/"));
			        } catch (Exception exp) {
			            exp.printStackTrace();
			        }
			    }
			}
			System.out.println("Database error: "+e);
		}
		
		return false;
	}
	
	/**
	 * Get Mines table data</br>
	 * used getResult function
	 * @param columnLength Count of columns
	 * @return Array of object data with select result
	 * @see getResult function
	 */
	public Object[][] getMines(int columnLength){
		return getResult("SELECT * FROM `"+dbName+"`.`mines`;", columnLength);		
	}
	
	/**
	 * Get Mines table data</br>
	 * used getResult function
	 * @return Array of object data with select result
	 * @see getResult function
	 */
	public Object[][] getMines(){
		return getResult("SELECT * FROM `"+dbName+"`.`mines`;", Mines.getColumnLength());		
	}
	
	/**
	 * <b>Delete note from Mines table</b></br>
	 * Function for delete mine
	 * @param idMine int id of mine note
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 * @see deleteRow
	 */
	public Boolean deleteMine(int idMine) {
		return deleteRow("mines", "id", idMine);
	}
	
	/**
	 * Function for add new Mine to Mines table
	 * @param title	String title of new Mine (string up to 100 characters) </br>
	 * 		  Other chars will cut
	 * @param additionalInfo String Addition information about Mine 
	 * @return true if note was add</br>
	 * 		   false if get error
	 */
	public Boolean addMine(String title, String additionalInfo) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					 "INSERT INTO `xray`.`mines` (`Title`, `Additional`) VALUES (?, ?);");
			pstmt.setString(1, title.length()>100?title.substring(0, 100):title);
			pstmt.setString(2, additionalInfo);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Update row in Mine table
	 * @param id int id of Mine note, that will update
	 * @param title String new title of new Mine (string up to 100 characters) </br>
	 * 		  		Other chars will cut
	 * @param additional String new addition information about Mine
	 * @return true if note update successfully</br>
	 * 		   false if get error
	 */
	public Boolean updateMine(int id, String title, String additional) {
		try {
			PreparedStatement pstmt = connection.prepareStatement ("UPDATE `xray`.`mines` SET `Title`= ?, `Additional`=? WHERE id = ?;");
			pstmt.setString(1, title.length()>100?title.substring(0, 100):title);
			pstmt.setString(2, additional);
			pstmt.setInt(3, id);
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}

	/**
	 * Get Mines table data</br>
	 * used getResult function
	 * @param columnLength int count of columns in Position table
	 * @return Array of object data with select result
	 * @see getResult function
	 */
	public Object[][] getPositions(int columnLength){
		return getResult("SELECT * FROM `xray`.`positions`;", columnLength);		
	}
	
	/**
	 * Get Mines table data</br>
	 * used getResult function
	 * @return Array of object data with select result
	 * @see getResult function
	 */
	public Object[][] getPositions(){
		return getResult("SELECT * FROM `xray`.`positions`;", Positions.getColumnLength());		
	}
	
	public Object[] getPossibilitiesByPositionID(int id){
		return getResult("SELECT * FROM `xray`.`positions` WHERE idPosition = "+id+";", 28)[0];		
	}
	
	/**
	 * Function for add new Position to Positions table
	 * @param positionTitle	String Position title
	 * @return true if note was add</br>
	 * 		   false if get error
	 */
	public Boolean addPosition(String positionTitle) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					 "INSERT INTO `xray`.`positions` (`Position title`) VALUES (?);");
			pstmt.setString(1, positionTitle);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * <b>Delete note from Positions table</b></br>
	 * Function for delete position
	 * @param idPosition int id of position note
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 * @see deleteRow
	 */
	public Boolean deletePosition(int idPosition) {
		return deleteRow("positions", "idPosition", idPosition);
	}
	
	/**
	 * Update row in Positions table
	 * @param id int This variable mean if of update note
	 * @param position String New Position title
	 * @return true if note update successfully</br>
	 * 		   false if get error
	 */
	public Boolean updatePosition(int id, String position) {
		try {
			PreparedStatement pstmt = connection.prepareStatement ("UPDATE `xray`.`positions` SET `Position title`= ? WHERE idPosition = ?;");
			pstmt.setString(1, position);
			pstmt.setInt(2, id);
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Function for delete row from table
	 * @param tableName String Table name when will delete note
	 * @param columnName String Title of column for delete.</br>
	 * 					Example: WHERE columnName = idRow
	 * @param idRow Int Id of note, that you want delete
	 * @return true if note was delete </br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean deleteRow(String tableName, String columnName, int idRow) {
		try {
			PreparedStatement pstmt = connection.prepareStatement ("DELETE FROM "+tableName+" WHERE "+columnName+" = ?;");
			//pstmt.setString(1, tableName);
			//pstmt.setString(2, columnName);
			pstmt.setInt(1, idRow);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}

	/**
	 * Get workers info from table of database
	 * @param length int - length of columns
	 * @return Array of object data with select result
	 */
	public Object[][] getWorkers(int length) {
		return getResult("SELECT * FROM workers;", length);
	}
	
	/**
	 * Get workers with positions title with filter
	 * @return Array of object data with filter
	 */
	public Object[][] getWorkersWithFilter(Object id, Object fullName, Object position) {
		String filter = "";
		
		Boolean comma = false;
		
		if(id != null) {
			filter += "w.idWorker = "+id;
			comma = true;
		}
		
		if(fullName != null) {
			if(comma) {
				filter += "AND ";
			}
			filter += "w.FullName LIKE '%"+fullName+"%'";
			comma = true;
		}
		
		if(position != null) {
			if(comma) {
				filter += "AND ";
			}
			filter += "p.`Position title` LIKE '"+position+"'";
		}
		
		String request = "SELECT w.idWorker, w.FullName, p.`Position title` FROM workers w INNER JOIN positions p ON w.Position = p.idPosition";
		
		if(!filter.equals("")) {
			request+=" WHERE "+filter;
		}
		
		request+=";";
		
		return getResult(request, 3);
	}
	
	/**
	 * Get workers with positions title
	 * @return Array of object data with select result
	 */
	public Object[][] getWorkersWithPositionTitle() {
		return getResult("SELECT w.idWorker, w.FullName, p.`Position title` FROM workers w INNER JOIN positions p ON w.Position = p.idPosition;", 3);
	}
	
	/**
	 * Get workers info from table of database
	 * @return Array of object data with select result
	 */
	public Object[][] getWorkers() {
		return getResult("SELECT * FROM workers;", Workers.getColumnLength());
	}
	
	/**
	 * Update row in Positions table
	 * @param id int This variable mean if of update note
	 * @param fullName String Full name of worker
	 * @param posID Int ID of position from Positions table 
	 * @return true if note update successfully</br>
	 * 		   false if get error
	 */
	public Boolean updateWorker(int id, String fullName, int posID) {
		try {
			PreparedStatement pstmt = connection.prepareStatement ("UPDATE `workers` SET `idWorker` = ?,`FullName` = ?, "
					+ "`Position` = ? WHERE `idWorker` = ?;");
			pstmt.setInt(1, id);
			pstmt.setInt(4, id);
			pstmt.setString(2, fullName);
			pstmt.setInt(3, posID);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * <b>Delete note from Workers table</b></br>
	 * Function for delete worker
	 * @param idWorker int id of worker note
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 * @see deleteRow
	 */
	public Boolean deleteWorker(int idWorker) {
		return deleteRow("workers", "idWorker", idWorker);
	}
	
	/**
	 * Function for add new worker to Workers table
	 * @param fullName Full Name of new worker 
	 * @param posID ID of new worker position (foreign key)
	 * @return true if note was add</br>
	 * 		   false if get error
	 */
	public Boolean addWorker(String fullName, int posID) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					 "INSERT INTO `workers` (`FullName`,`Position`)"
					 + "VALUES (?, ?);");
			pstmt.setString(1, fullName);
			pstmt.setInt(2, posID);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Get Echelons table data</br>
	 * used getResult function
	 * @param columnLength Count of columns
	 * @return Array of object data with select result
	 * @see getResult function
	 */
	public Object[][] getEchelons(int columnLength){
		return getResult("SELECT * FROM echelons;", columnLength);		
	}
	
	/**
	 * Get Echelons table data</br>
	 * used getResult function
	 * @param columnLength Count of columns
	 * @return Array of object data with select result
	 * @see getResult function
	 */
	public Object[] getEchelonByID(int idEchelon){
		return getResult("SELECT * FROM echelons WHERE idEchelon = "+idEchelon+";", Echelons.getColumnLength())[0];		
	}
	
	/**
	 * Get echelons id and number (for custom CumboBox)
	 * @return Array of object data with result
	 * @see getResult function
	 */
	public Object[][] getEchelonNumbers(){
		return getResult("SELECT idEchelon, EchelonNumber FROM echelons", 2);		
	}
	
	/**
	 * <b>Delete note from Echelons table</b></br>
	 * Function for delete echelon
	 * @param idEchelon int id of echelon note
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 * @see deleteRow
	 */
	public Boolean deleteEchelon(int idEchelon) {
		return deleteRow("echelons", "idEchelon", idEchelon);
	}
	
	/**
	 * Function for add new echelon to Echelons table
	 * @param echelonNumber int with echelon number
	 * @param mineID int with mine id (foreign key)
	 * @param startArrivalDate String Start arrival date
	 * @param finishArrivalDate String Finish arrival date
	 * @param additional String additional information about echelon
	 * @return true if note was add</br>
	 * 		   false if get error 
	 */
	public Boolean addEchelon(int echelonNumber, int mineID, String startArrivalDate, String finishArrivalDate, String additional) {
		try {
			String request = "INSERT INTO `echelons`(`EchelonNumber`, `Mine`, "
					 +(startArrivalDate!=null?"`StartArrivalDate`, ":"")
					 +(finishArrivalDate!=null?"`FinishArrivalDate`, ":"")
					 +"`Additional`)"
					 + "VALUES (?, ?, "
					 + (startArrivalDate!=null?"'"+startArrivalDate+"', ":"")
					 + (finishArrivalDate!=null?"'"+finishArrivalDate+"', ":"")
					 +"?);";
			
			PreparedStatement pstmt = connection.prepareStatement (request);
			
			pstmt.setInt(1, echelonNumber);
			pstmt.setInt(2, mineID);
			pstmt.setString(3, additional);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Update row in Echelon table
	 * @return true if note update successfully</br>
	 * 		   false if get error
	 */
	public Boolean updateEchelon(int idEchelon, int echelonNumber, int idMine, String startArrivalDate, String finishArrivalDate, String additional) {
		try {
			String request = "UPDATE `echelons` SET `EchelonNumber` = ?, "
					+ "`Mine` = ?, "
					+ (startArrivalDate!=null?"`StartArrivalDate` = '"+startArrivalDate+"', ":"")
					+ (finishArrivalDate!=null?"`FinishArrivalDate` = '"+finishArrivalDate+"', ":"")
					+ "`Additional` = ? WHERE `idEchelon` = ?;";
			
			PreparedStatement pstmt = connection.prepareStatement (request);
			
			pstmt.setInt(1, echelonNumber);
			pstmt.setInt(2, idMine);
			pstmt.setString(3, additional);
			pstmt.setInt(4, idEchelon);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Get Echelons table data</br>
	 * used getResult function
	 * @param columnLength Count of columns
	 * @return Array of object data with select result
	 * @see getResult function
	 */
	public Object[][] getPurposes(){
		return getResult("SELECT * FROM bunkerspurpose;", BunkersPurpose.getColumnLength());		
	}
	
	/**
	 * Function for add new purpose to Purposes table
	 * @param purpose String with purpose
	 * @param additional String with additional information
	 * @return true if note was add</br>
	 * 		   false if get error 
	 */
	public Boolean addPurpose(String purpose, String additional) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					"INSERT INTO `bunkerspurpose`"
					+ "(`Purpose`,`Additional`)"
					+ "VALUES"
					+ "(?, ?)");
			
			pstmt.setString(1, purpose);
			pstmt.setString(2, additional);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * <b>Delete note from Bunkers purpose table</b></br>
	 * Function for delete echelon
	 * @param idPurpose int id of purpose note
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 * @see deleteRow
	 */
	public Boolean deletePurpose(int idPurpose) {
		return deleteRow("bunkerspurpose", "id", idPurpose);
	}
	
	/**
	 * Get purpose by id
	 * @param id of purpose
	 * @return array of objects with result
	 */
	public Object[] getPurposeByID(int id){
		return getResult("SELECT * FROM bunkerspurpose WHERE id = "+id+";", BunkersPurpose.getColumnLength())[0];		
	}
	
	/**
	 * Function for update purpose
	 * @param id of updating purpose
	 * @param purpose String with new purpose
	 * @param additional String with additional information
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean updatePurpose(int id, String purpose, String additional) {
		try {
			String request = "UPDATE `bunkerspurpose` SET `Purpose` = ?,"
					+ "`Additional` = ?"
					+ "WHERE `id` = ?";
			
			PreparedStatement pstmt = connection.prepareStatement (request);
			
			pstmt.setString(1, purpose);
			pstmt.setString(2, additional);
			pstmt.setInt(3, id);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Get bunkers info from table of database
	 * @return Array of object data with select result
	 */
	public Object[][] getBunkers(){
		return getResult("SELECT * FROM bunkers;", Bunkers.getColumnLength());		
	}
	
	/**
	 * Get bunkers info with purposes title instead purposes id from table of database
	 * @return Array of object data with select result
	 */
	public Object[][] getBunkersWithPurpose(){
		return getResult("SELECT b.BunkerNumber, p.purpose FROM bunkers b INNER JOIN bunkerspurpose p ON b.purpose = p.id;", Bunkers.getColumnLength());		
	}
	
	/**
	 * Function for add new bunker
	 * @param bunkerNumber int with bunker number
	 * @param purposeID int with bunker purpose id
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean addBunker(int bunkerNumber, int purposeID) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					"INSERT INTO `bunkers` (`BunkerNumber`, `Purpose`) VALUES (?, ?);");
			
			pstmt.setInt(1, bunkerNumber);
			pstmt.setInt(2, purposeID);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * <b>Delete note from Bunkers purpose table</b></br>
	 * Function for delete echelon
	 * @param idPurpose int id of purpose note
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 * @see deleteRow
	 */
	public Boolean deleteBunker(int bunkerNumber) {
		return deleteRow("bunkers", "BunkerNumber", bunkerNumber);
	}
	
	/**
	 * Get bunker info by bunker number
	 * @param number int number of bunker for filter
	 * @return Array of objects with result
	 */
	public Object[] getBunkerByNumber(int number){
		return getResult("SELECT * FROM bunkers WHERE bunkernumber = "+number+";", Bunkers.getColumnLength())[0];		
	}
	
	/**
	 * Update bunker information
	 * @param number int number of bunker
	 * @param newNumber int new number of bunker
	 * @param purpose int id of purpose from Purposes table
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean updateBunker(int number, int newNumber, int purpose) {
		try {
			String request = "UPDATE `bunkers` SET `BunkerNumber` = ?,"
					+ "`Purpose` = ? "
					+ "WHERE `BunkerNumber` = ?";
			
			PreparedStatement pstmt = connection.prepareStatement (request);
			
			pstmt.setInt(1, newNumber);
			pstmt.setInt(2, purpose);
			pstmt.setInt(3, number);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Get composition informations from DB 
	 * @return Array with result
	 */
	public Object[][] getCompositions(){
		return getResult("SELECT * FROM materialcomposition;", MaterialComposition.getColumnLength());		
	}
	
	/**
	 * Get info from Compositions table and Worker FullName instead worker id from Compositions table 
	 * @return Result array
	 */
	public Object[][] getCompositionsWithWorker(){
		return getResult("SELECT c.id, c.MgO, c.CaO, c.Al2O3, c.SiO2, c.P, c.S, c.`Fe total`, w.FullName, c.FixationTime, c.Additional FROM materialcomposition c INNER JOIN workers w ON c.Worker = w.idWorker;", MaterialComposition.getColumnLength());		
	}
	
	/**
	 * Get compositions with worker full name with filter by id
	 * @param id of composition
	 * @return Result array of elements
	 */
	public Object[] getCompositionWithWorkerByID(int id){
		return getResult("SELECT c.id, c.MgO, c.CaO, c.Al2O3, c.SiO2, c.P, c.S, c.`Fe total`, w.FullName, c.FixationTime, c.Additional FROM materialcomposition c INNER JOIN workers w ON c.Worker = w.idWorker WHERE id = "+id+";", MaterialComposition.getColumnLength())[0];		
	}
	
	/**
	 * Get compositions info with filter by id
	 * @param id int id of necessary composition
	 * @return Array with result objects
	 */
	public Object[] getCompositionByID(int id){
		return getResult("SELECT * FROM materialcomposition WHERE id = "+id+";", MaterialComposition.getColumnLength())[0];		
	}
	
	public Boolean addComposition(float MgO, float CaO, float Al2O3, float SiO2, float P, float S, float FeTotal, int workerID, String dateTime, String Additional) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					"INSERT INTO `materialcomposition` "
					+ "(`MgO`, `CaO`, `Al2O3`, `SiO2`, `P`, `S`, `Fe total`, `Worker`, `FixationTime`, `Additional`) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, '"+dateTime+"', ?);");
			
			pstmt.setFloat(1, MgO);
			pstmt.setFloat(2, CaO);
			pstmt.setFloat(3, Al2O3);
			pstmt.setFloat(4, SiO2);
			pstmt.setFloat(5, P);
			pstmt.setFloat(6, S);
			pstmt.setFloat(7, FeTotal);
			pstmt.setInt(8, workerID);
			pstmt.setString(9, Additional);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Get compositions info with filter by id and dates
	 * @param id (int) of composition
	 * @param startDate (String) of composition
	 * @param finishDate (String) of composition
	 * @return Result array of compositions
	 */
	public Object[][] getCompositionWithFilter(Object id, String startDate, String finishDate){
		
		String request = "SELECT c.id, c.MgO, c.CaO, c.Al2O3, c.SiO2, c.P, c.S, c.`Fe total`, w.FullName, c.FixationTime, c.Additional FROM materialcomposition c INNER JOIN workers w ON c.Worker = w.idWorker";
		
		String filter = "";
		Boolean addEnd = false;
		
		if(id != null) {
			filter+="`id` = " + Integer.parseInt(String.valueOf(id));
			addEnd = true;
		}
		
		if(startDate != "" && startDate != null) {
			
			if(addEnd) {
				filter += " AND ";
			}else {
				addEnd = true;
			}
			
			filter += "`FixationTime` >= CAST('"+startDate+"' AS DATETIME)";
		}
		
		if(finishDate != "" && finishDate != null) {
			if(addEnd) {
				filter += " AND ";
			}
			
			filter += "`FixationTime` <= CAST('"+finishDate+"' AS DATETIME)";
		}
		
		if(filter != "") {
			request += " WHERE "+filter;
		}
		
		request+=";";
		
		return getResult(request, MaterialComposition.getColumnLength());		
	}
	
	/**
	 * Update composition info
	 * @param id (int) of composition
	 * @param MgO, CaO, Al2O3, SiO2, P, S, FeTotal (integers) The value of the concentration of a substance in a material
	 * @param workerID (int) ID of the employee who performed the check
	 * @param dateTime (String) Date and time of the check
	 * @param Additional (String) Additional information about composition
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean updateComposition(int id, float MgO, float CaO, float Al2O3, float SiO2, float P, float S, float FeTotal, int workerID, String dateTime, String Additional) {
		try {
			String request = "UPDATE `materialcomposition` SET"
					+ "`MgO` = ?,"
					+ "`CaO` = ?,"
					+ "`Al2O3` = ?,"
					+ "`SiO2` = ?,"
					+ "`P` = ?,"
					+ "`S` = ?,"
					+ "`Fe total` = ?,"
					+ "`Worker` = ?,"
					+ "`FixationTime` = ?,"
					+ "`Additional` = ?"
					+ "WHERE `id` = ?;";
			
			PreparedStatement pstmt = connection.prepareStatement (request);
			
			pstmt.setFloat(1, MgO);
			pstmt.setFloat(2, CaO);
			pstmt.setFloat(3, Al2O3);
			pstmt.setFloat(4, SiO2);
			pstmt.setFloat(5, P);
			pstmt.setFloat(6, S);
			pstmt.setFloat(7, FeTotal);
			pstmt.setInt(8, workerID);
			pstmt.setString(9, dateTime);
			pstmt.setString(10, Additional);
			pstmt.setInt(11, id);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Delete composition from table
	 * @param id of delete composition
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean deleteComposition(int id) {
		return deleteRow("materialcomposition", "id", id);
	}
	
	/**
	 * Get all information from elements table
	 * @return Result array of compositions
	 */
	public Object[][] getElements(){
		return getResult("SELECT * FROM elements;", Elements.getColumnLength());		
	} 
	
	/**
	 * Add new element to Elements table
	 * @param elementNumber (int) ordinal number of the element in the periodic table
	 * @param elementTitle (String) Element name (up to 2 characters)
	 * @param Ka energy level
	 * @param Kb energy level
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean addElement(int elementNumber, String elementTitle, float Ka, float Kb) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					"INSERT INTO `elements` (`ElementNumber`, `ElementTitle`, `Ka`, `Kb`)"
					+ "VALUES (?, ?, ?, ?);");
			
			pstmt.setInt(1, elementNumber);
			pstmt.setString(2, elementTitle);
			pstmt.setFloat(3, Ka);
			pstmt.setFloat(4, Kb);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Get element info from table by element number
	 * @param elementNumber (integer) ordinal number of the element in the periodic table
	 * @return Array with element info or null
	 */
	public Object[] getElementByNumber(int elementNumber){
		return getResult("SELECT * FROM elements WHERE ElementNumber = "+elementNumber+";", Elements.getColumnLength())[0];		
	} 
	
	/**
	 * Get all elements number and title from Elements table
	 * @return Array with result elements
	 */
	public Object[][] getElementsTitle(){
		return getResult("SELECT ElementNumber, ElementTitle FROM elements;", 2);		
	} 
	
	/**
	 * Function for update information about element
	 * @param oldElementNumber (int) - old element number
	 * @param elementNumber (int) - new element number
	 * @param elementTitle (String) - new element title
	 * @param Ka (int) - energy level Ka
	 * @param Kb (int) - energy level Kb
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean updateElement(int oldElementNumber, int elementNumber, String elementTitle, float Ka, float Kb) {
		try {
			String request = "UPDATE `elements` SET"
					+ "`ElementNumber` = ?,"
					+ "`ElementTitle` = ?,"
					+ "`Ka` = ?,"
					+ "`Kb` = ?"
					+ "WHERE `ElementNumber` = ?;";
			
			PreparedStatement pstmt = connection.prepareStatement (request);
			
			pstmt.setInt(1, elementNumber);
			pstmt.setString(2, elementTitle);
			pstmt.setFloat(3, Ka);
			pstmt.setFloat(4, Kb);
			pstmt.setInt(5, oldElementNumber);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Get sinter types information
	 * @return Result array
	 */
	public Object[][] getSinterTypes() {
		return getResult("SELECT * FROM sintertypes;", SinterTypes.getColumnLength());
	}
	
	/**
	 * Get sinters id and title from sinter table
	 * @return Result array
	 */
	public Object[][] getSinterTypesTitle() {
		return getResult("SELECT id, SinterTitle FROM sintertypes;", 2);
	}
	
	/**
	 * Add new sinter type
	 * @param sinterTitle (String) title of sinter type
	 * @param compositionID (int) id of composition of standard material
	 * @param additional (String) additional information
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean addSinterType(String sinterTitle, int compositionID, String additional) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					"INSERT INTO `sintertypes`(`SinterTitle`, `Composition`, `Additional`)"
					+ "VALUES(?,?,?);");
			
			pstmt.setString(1, sinterTitle);
			pstmt.setInt(2, compositionID);
			pstmt.setString(3, additional);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Function for delete element
	 * @param elementNumber (int) - ordinal number of exist element
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean deleteElement(int elementNumber) {
		return deleteRow("elements", "ElementNumber", elementNumber);
	}
	
	/**
	 * Get sinter type info by id
	 * @param id (int) - id of exist sinter type 
	 * @return Array of sinter type info
	 */
	public Object[] getSinterTypeById(int id) {
		return getResult("SELECT * FROM sintertypes WHERE id = "+id+";", SinterTypes.getColumnLength())[0];
	}
	
	/**
	 * Function for update sinter type info
	 * @param id (int) of exist sinter type
	 * @param sinterTitle (String) - new sinter title
	 * @param compositionID (int) - id of composition of standard material
	 * @param additional (String) Additional information
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean updateSinterType(int id, String sinterTitle, int compositionID, String additional) {
		try {
			String request = "UPDATE `sintertypes` SET"
					+ "`SinterTitle` = ?,"
					+ "`Composition` = ?,"
					+ "`Additional` = ?"
					+ "WHERE `id` = ?";
			
			PreparedStatement pstmt = connection.prepareStatement (request);
			
			pstmt.setString(1, sinterTitle);
			pstmt.setInt(2, compositionID);
			pstmt.setString(3, additional);
			pstmt.setInt(4, id);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Delete sinter type
	 * @param id (int) of exist sinter type
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean deleteSinterType(int id) {
		return deleteRow("sintertypes", "id", id);
	}
	
	/**
	 * Get analyzed elements information with name (foreign key)
	 * @return Array of result
	 */
	public Object[][] getAnalyzedElementsWithNames(){
		return getResult("SELECT a_e.id, e.ElementTitle, s_t.SinterTitle "
				+ "FROM analyzedelements a_e "
				+ "INNER JOIN elements e ON a_e.element = e.ElementNumber "
				+ "INNER JOIN sintertypes s_t ON a_e.sintertype = s_t.id;", 
				AnalyzedElements.getColumnLength());		
	} 
	
	/**
	 * Add new analyzed element. </br>
	 * Combines (fixes) the analyzed element for a specific type of agglomerate
	 * @param elementNumber (int) - exist element number
	 * @param sinterType (int) - key of sinter type
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean addAnalyzedElement(int elementNumber, int sinterType) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					"INSERT INTO `analyzedelements`"
					+ "(`Element`,`SinterType`)"
					+ "VALUES (?, ?);");
			
			pstmt.setInt(1, elementNumber);
			pstmt.setInt(2, sinterType);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Update analyzed element information
	 * @param id (int) of exist note from table
	 * @param elementNumber (int) - exist element number
	 * @param sinterType (int) - key of sinter type
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean updateAnalyzedElement(int id, int elementNumber, int sinterType) {
		try {			
			String request = "UPDATE `analyzedelements` SET "
					+ "`Element` = ?,"
					+ "`SinterType` = ?"
					+ " WHERE `id` = ?;";
			
			PreparedStatement pstmt = connection.prepareStatement (request);
			
			pstmt.setInt(1, elementNumber);
			pstmt.setInt(2, sinterType);
			pstmt.setInt(3, id);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Delete analyzed element from table
	 * @param id (int) of exist note from table
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean deleteAnalyzedElement(int id) {
		return deleteRow("analyzedelements", "id", id);
	}
	
	/**
	 * Get information about stacks
	 * @return Array with result
	 */
	public Object[][] getStacks() {
		return getResult("SELECT * FROM stacks;", Stacks.getColumnLength());
	}
	
	/**
	 * Get information about stacks
	 * @return Array with result
	 */
	public Object[] getStackPassport(int idPass) {
		return getResult("SELECT s.id, s.StackNumber, c.id, c.MgO, c.CaO, c.Al2O3, c.SiO2, c.P, c.S, c.`Fe total`, w.FullName, c.FixationTime, s.fixedtime FROM stacks s INNER JOIN materialcomposition c ON s.composition = c.id INNER JOIN workers w ON c.Worker = w.idWorker WHERE s.id = "+idPass+";", 13)[0];
	}
	
	public Object[][] getStacksWithFilter(Object stackNumber, Object worker, Object startDate, Object finishDate) {
		String request = "SELECT s.id, s.StackNumber, c.id, c.MgO, c.CaO, c.Al2O3, c.SiO2, c.P, c.S, c.`Fe total`, w.FullName, c.FixationTime, s.fixedtime FROM stacks s INNER JOIN materialcomposition c ON s.composition = c.id INNER JOIN workers w ON c.Worker = w.idWorker ";
		String filter = "";
		
		if(stackNumber!=null) {
			filter += "s.StackNumber = "+stackNumber; 
		}
		
		if(worker!=null) {
			if(filter.length()!=0) {
				filter += " AND ";
			}
			filter+= "w.FullName LIKE \""+worker+"\"";
		}
		
		if(startDate!=null) {
			if(filter.length()!=0) {
				filter += " AND ";
			}
			filter+= "c.FixationTime >= '"+startDate+"'";
		}
		
		if(finishDate!=null) {
			if(filter.length()!=0) {
				filter += " AND ";
			}
			filter+= "c.FixationTime <= '"+finishDate+"'";
		}
		
		if(filter != "") {
			request += " WHERE "+filter;
		}
		
		request+=";";
		System.out.println(request);
		return getResult(request, 13);
	}
	
	/**
	 * Get stacks information with filter by stack number
	 * @param number (int) of stack (from 1 to 5)
	 * @return Result array
	 */
	public Object[][] getStacksWithFilter(Object number) {
		String request = "SELECT * FROM stacks ";
		
		if(number != null) {
			request+="WHERE StackNumber = "+number;
		}
		
		request+=";";
		return getResult(request, Stacks.getColumnLength());
	}
	
	/**
	 * Add new stack operation to table
	 * @param stackNumber (int) of stack (from 1 to 5)
	 * @param compositionNumber (int) - id of composition of stack (foreign key)
	 * @param dateTime (String) - date time of operation
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean addStack(int stackNumber, int compositionNumber, String dateTime) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					"INSERT INTO `stacks`"
					+ "(`StackNumber`, `Composition`, `FixedTime`) "
					+ "VALUES (?, ?, ?);");
			
			pstmt.setInt(1, stackNumber);
			pstmt.setInt(2, compositionNumber);
			pstmt.setString(3, dateTime);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Update stack operation information
	 * @param id (int) of exist table note
	 * @param stackNumber (int) of stack (from 1 to 5)
	 * @param compositionNumber (int) - id of composition of stack (foreign key)
	 * @param dateTime (String) - date time of operation
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean updateStack(int id, int stackNumber, int compositionNumber, String dateTime) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					"UPDATE `stacks` SET"
					+ "`StackNumber` = ?, "
					+ "`Composition` = ?, "
					+ "`FixedTime` = ? "
					+ "WHERE `id` = ?;");
			
			pstmt.setInt(1, stackNumber);
			pstmt.setInt(2, compositionNumber);
			pstmt.setString(3, dateTime);
			pstmt.setInt(4, id);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Delete stack operation table note
	 * @param id of delete stack operation
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean deleteStack(int id) {
		return deleteRow("stacks", "id", id);
	}
	
	/**
	 * Get information about
	 * @return Result array
	 */
	public Object[][] getVans() {
		return getResult("SELECT v.id, v.VanNumber, e.echelonNumber, v.Weight, v.Composition, v.Stack FROM vans v INNER JOIN echelons e ON v.echelon = e.idEchelon;", Vans.getColumnLength());
	}
	
	/**
	 * Get information about vans wit
	 * @return Result array
	 */
	public Object[][] getVansWithMines() {
		return getResult("SELECT v.id, v.VanNumber, e.echelonNumber, m.title, v.Weight, v.Composition, v.Stack FROM vans v INNER JOIN echelons e ON v.echelon = e.idEchelon INNER JOIN mines m ON e.mine = m.id;", 7);
	}
	
	/**
	 * Get information about vans
	 * @return Result array
	 */
	public Object[] getVanPassport(int idVan) {
		return getResult("SELECT v.id, "
				+ "v.VanNumber, e.echelonNumber, m.title, "
				+ "v.Weight, v.Composition,"
				+ "c.MgO, c.CaO, c.Al2O3, c.SiO2, "
				+ "c.P, c.S, c.`Fe total`, w.FullName, "
				+ "c.FixationTime, v.Stack FROM vans v "
				+ "INNER JOIN echelons e ON v.echelon = e.idEchelon "
				+ "INNER JOIN mines m ON e.mine = m.id "
				+ "INNER JOIN materialcomposition c ON v.Composition = c.id "
				+ "INNER JOIN workers w ON c.worker = w.idWorker WHERE v.id = "+idVan+";", 16)[0];
	}
	
	/**
	 * Get information about vans with filter
	 * @return Result array
	 */
	public Object[][] getVanPassportWithFilter(Object vanNumber, Object echelonNumber, Object mines, Object worker) {
		
		String request = "SELECT v.id, "
				+ "v.VanNumber, e.echelonNumber, m.title, "
				+ "v.Weight, v.Composition,"
				+ "c.MgO, c.CaO, c.Al2O3, c.SiO2, "
				+ "c.P, c.S, c.`Fe total`, w.FullName, "
				+ "c.FixationTime, v.Stack FROM vans v "
				+ "INNER JOIN echelons e ON v.echelon = e.idEchelon "
				+ "INNER JOIN mines m ON e.mine = m.id "
				+ "INNER JOIN materialcomposition c ON v.Composition = c.id "
				+ "INNER JOIN workers w ON c.worker = w.idWorker ";
		
		String filter = "";
		
		if(vanNumber!=null) {
			filter+="v.VanNumber = "+vanNumber;
		}
		
		if(echelonNumber!=null) {
			if(filter.length()!=0) {
				filter+=" AND ";
			}
			filter+="e.echelonNumber = "+echelonNumber;
		}
		
		if(mines!=null) {
			if(filter.length()!=0) {
				filter+=" AND ";
			}
			filter+="m.title LIKE \""+mines+"\"";
		}
		
		if(worker!=null) {
			if(filter.length()!=0) {
				filter+=" AND ";
			}
			filter+="w.FullName LIKE \""+worker+"\"";
		}
		
		if(filter.length()!=0) {
			filter=" WHERE "+filter;
		}
		
		request += filter+ ";";
		return getResult(request, 16);
	}
	
	/**
	 * Add new van operation
	 * @param vanNumber (int) - number of van (relative to the echelon)
	 * @param echelonID (int) - id of exist in table echelon (foreign key)
	 * @param weight (float) - weight of transported material
	 * @param compositionID (int) - id of composition (foreign key)
	 * @param stackID (int) - id of stack operation (foreign key)
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean addVan(int vanNumber, int echelonID, float weight, int compositionID, int stackID) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					"INSERT INTO `vans`"
					+ "(`VanNumber`, `Echelon`, `Weight`, `Composition`, `Stack`) "
					+ "VALUES (?, ?, ?, ?, ?);");
			
			pstmt.setInt(1, vanNumber);
			pstmt.setInt(2, echelonID);
			pstmt.setFloat(3, weight);
			pstmt.setInt(4, compositionID);
			pstmt.setInt(5, stackID);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Update van information
	 * @param vanID
	 * @param vanNumber (int) - number of van (relative to the echelon)
	 * @param echelonID (int) - id of exist in table echelon (foreign key)
	 * @param weight (float) - weight of transported material
	 * @param compositionID (int) - id of composition (foreign key)
	 * @param stackID (int) - id of stack operation (foreign key)
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean updateVan(int vanID, int vanNumber, int echelonID, float weight, int compositionID, int stackID) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					"UPDATE `vans` SET "
					+ "`VanNumber` = ?,"
					+ "`Echelon` = ?,"
					+ "`Weight` = ?,"
					+ "`Composition` = ?,"
					+ "`Stack` = ? "
					+ "WHERE `id` = ?;");
			
			pstmt.setInt(1, vanNumber);
			pstmt.setInt(2, echelonID);
			pstmt.setFloat(3, weight);
			pstmt.setInt(4, compositionID);
			pstmt.setInt(5, stackID);
			pstmt.setInt(6, vanID);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Delete van from table
	 * @param id of delete van
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean deleteVan(int id) {
		return deleteRow("vans", "id", id);
	}
	
	/**
	 * Get information about bunkers filling operations
	 * @return Result array
	 */
	public Object[][] getBunkersFilling() {
		return getResult("SELECT * FROM bunkersfilling;", BunkersFilling.getColumnLength());
	}
	
	/**
	 * Add new bunkers filling operation table note
	 * @param bunkerNumber (int) - foreign key from bunkers table
	 * @param stackID (int) - foreign key to stacks operations 
	 * @param fixedTime (String) - date and time of operation
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean addBunkersFilling(int bunkerNumber, int stackID, String fixedTime) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					"INSERT INTO `bunkersfilling` (`Bunker`, `Stack`, `FixedTime`)"
					+ " VALUES "
					+ "(?, ?, ?);");
			
			pstmt.setInt(1, bunkerNumber);
			pstmt.setInt(2, stackID);
			pstmt.setString(3, fixedTime);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Update bunker filling information
	 * @param id (int) of exist table note 
	 * @param bunkerNumber (int) - foreign key from bunkers table
	 * @param stackID (int) - foreign key to stacks operations 
	 * @param fixedTime (String) - date and time of operation
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean updateBunkerFilling(int id, int bunkerNumber, int stackID, String fixedTime) {
		try {
			PreparedStatement pstmt = connection.prepareStatement (
					"UPDATE `bunkersfilling` SET "
					+ "`Bunker` = ?,"
					+ "`Stack` = ?,"
					+ "`FixedTime` = ?"
					+ "WHERE `id` = ?;");
			
			pstmt.setInt(1, bunkerNumber);
			pstmt.setInt(2, stackID);
			pstmt.setString(3, fixedTime);
			pstmt.setInt(4, id);
			
			return executeStatement(pstmt);
		}catch(Exception e) {
			System.out.println("Database error: "+e);
		}

		return false;
	}
	
	/**
	 * Delete bunker filling information
	 * @param id (int) of delete operation from bunkerFilling table
	 * @return true if note was delete</br>
	 * 		   false if get error (note was't delete)
	 */
	public Boolean deleteBunkerFilling(int id) {
		return deleteRow("bunkersfilling", "id", id);
	}
}
