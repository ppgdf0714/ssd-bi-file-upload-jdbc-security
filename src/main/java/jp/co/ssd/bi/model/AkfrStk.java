package jp.co.ssd.bi.model;

public class AkfrStk {
	
	private String 課名;
	private String ユニット名;
	private String 案件名;
	private int	件数;
	
	@Override
    public String toString() {
        return "AkfrkerStk{" +
                "課名=" + 課名 +
                ", ユニット名='" + ユニット名 + '\'' +
                ", 案件名='" + 案件名+ '\'' +
                '}';
    }
	public String get課名() {
		return 課名;
	}
	public void set課名(String 課名) {
		this.課名 = 課名;
	}
	public String getユニット名() {
		return ユニット名;
	}
	public void setユニット名(String ユニット名) {
		this.ユニット名 = ユニット名;
	}
	public String get案件名() {
		return 案件名;
	}
	public void set案件名(String 案件名) {
		this.案件名 = 案件名;
	}
	public int get件数() {
		return 件数;
	}
	public void set件数(int 件数) {
		this.件数 = 件数;
	}
	
	

}
