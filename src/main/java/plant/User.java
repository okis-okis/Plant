package plant;

public class User {
	private int id;
	private String username;
	private Object[][] privileges;
	
	public User(String username){
		this.username = username;
		id = Main.getDB().getWorkerIDByUsername(username);
		privileges = Main.getDB().getPrivilegesByWorkerID(id);
		Main.getLogger().debug("Username: "+username);
		Main.getLogger().debug("Worker id: "+id);
	}
	
	public Object[][] getPrivileges() {
		return privileges;
	}
	
	public Boolean getPrivilege(String privilTitle) {
		for(Object[] privilege: privileges) {
			if(privilege[0].equals(privilTitle)) {
				return Boolean.parseBoolean(String.valueOf(privilege[1]));
			}
		}
		return false;
	}
	
	public String getUsername() {
		return username;
	}
}
