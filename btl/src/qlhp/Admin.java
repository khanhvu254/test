package qlhp;

public class Admin {
	private String admin_ID;
	private String admin_Name;
	private String pass;

	public String getAdmin_ID() {
		return admin_ID;
	}

	public void setAdmin_ID(String admin_ID) {
		this.admin_ID = admin_ID;
	}

	public String getAdmin_Name() {
		return admin_Name;
	}

	public void setAdmin_Name(String admin_Name) {
		this.admin_Name = admin_Name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public Admin(String admin_ID, String admin_Name, String pass) {
		this.admin_ID = admin_ID;
		this.admin_Name = admin_Name;
		this.pass = pass;
	}

	public Admin() {
	}

	@Override
	public String toString() {
		return "admin [admin_ID=" + admin_ID + ", admin_Name=" + admin_Name + ", pass=" + pass + "]";
	}
}

