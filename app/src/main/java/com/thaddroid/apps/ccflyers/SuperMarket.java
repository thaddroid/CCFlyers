package com.thaddroid.apps.ccflyers;

public class SuperMarket {
	private int id;
	private String name;
	private int bannerResId;
	private String website;
	private String date;
	private String startDate;
	private String endDate;
	public Flyers flyer;
	private boolean update;
	private int server;
	private String ccp;

	public SuperMarket(String n, int id) {
		this.id = id;
		ccp="";
		name = n;
		website = "";
		date="0";
		update=false;
		bannerResId = 0;
		startDate = "0";
		endDate = "0";
		flyer = null;
	}
	
	public void setBannerResId(int id){
		this.bannerResId = id;
	}
	
	public void setURL(String url){
		website = url;
	}
	
	public void setDate(String d){
		date = d;
	}
	
	public void setUpFlyers(int pages){
		flyer = new Flyers(pages);
	}
	
	public void setImgSrc(int index, String s){
		flyer.setImgSrc(index, s);
	}
	
	public void setServer(int s){
		server = s;
	}
	
	public void setCCP(String c){
		ccp = c;
	}
	
	public void setExpiredDate(String expiredDate){
		this.endDate = expiredDate;
	}
	
	public void setStartDate(String startDate){
		this.startDate = startDate;
	}
	
	public String getStartDate(){
		return startDate;
	}
	
	public String getExpiredDate(){
		return endDate;
	}
	
	public int getId(){
		return id;
	}
	
	public String getCCP(){
		return ccp;
	}
	
	public int getServer(){
		return server;
	}
	
	public String getImgSrc(int index){
		return flyer.getImgSrc(index);
	}
	
	public String getURL(){
		return website;
	}
	
	public String getName(){
		return name;
	}
	
	public int getPages(){
		return flyer.getSize();
	}
	
	public int getBannerResId(){
		return bannerResId;
	}
	
	public String getDate(){
		return date;
	}
	
	public void setPDFSrc(String s){
		flyer.setPDFSrc(s);
	}
	
	public String getPDFSrc(){
		return flyer.getPDFSrc();
	}
	
	public boolean isUpdated(){
		return update;
	}
	
	public void Updated(boolean b){
		update = b;
	}
}
