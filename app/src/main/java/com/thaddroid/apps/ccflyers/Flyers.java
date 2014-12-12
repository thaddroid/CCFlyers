package com.thaddroid.apps.ccflyers;

public class Flyers {
	int pages;			//how many pages for a flyer
	String[] src;		//Image source for each page of the flyer
	String pdfSrc = new String();

	public Flyers(int p) {
		pages = p;
		pdfSrc = "";
		src = new String[p];
	}
	
	public void setPDFSrc(String s){
		pdfSrc =  s;
	}
	
	public String getPDFSrc(){
		return pdfSrc;
	}
	
	public int getSize(){
		return pages;
	}
	
	public void setImgSrc(int index, String s){
		src[index] =  s;
	}
	
	public String getImgSrc(int index){
		return src[index];
	}

}
