package qlhp;

public class HocPhan {
	private String sub_ID;
	private String sub_Name;
	private int credit;
	private float price;
	public String getSub_ID() {
		return sub_ID;
	}
	public void setSub_ID(String sub_ID) {
		this.sub_ID = sub_ID;
	}
	public String getSub_Name() {
		return sub_Name;
	}
	public void setSub_Name(String sub_Name) {
		this.sub_Name = sub_Name;
	}
	public int getcredit() {
		return credit;
	}
	public void setcredit(int credit) {
		this.credit = credit;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public HocPhan() {}
	public HocPhan(String sub_ID,String sub_Name,int credit,float price) {
		this.sub_ID=sub_ID;
		this.sub_Name=sub_Name;
		this.credit=credit;
		this.price=price;
	}
	public Object[] toArray() {
        return new Object[]{"", this.sub_ID, this.sub_Name, this.credit, this.price};
    }
	
	@Override
	public String toString() {
		return "SinhVien [sub_ID=" + sub_ID + ", sub_Name=" + sub_Name + ", credit=" + credit + ", price=" + price+ "]";
	}
}
