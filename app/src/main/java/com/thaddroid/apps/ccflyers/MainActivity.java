package com.thaddroid.apps.ccflyers;

import com.google.android.gms.ads.*;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private final static int SIZE=22;
	
	private Intent intent;
	private Bundle bundle;
	private TableRow[] tr = new TableRow[SIZE];
	private TextView[] tv = new TextView[SIZE];
	private TextView[] tv2 = new TextView[SIZE];
	private TextView[] expiredDate = new TextView[SIZE];
	private ProgressDialog pd;
	private int index;
	private String FILENAME="supermarkets.txt";
	private List<String> iniData;
	
	private boolean needChecked=false;
	private boolean fileIsExist=false;
	private int[] tempArray = new int[SIZE];
	private int[] checkArray;
	private int countCheck=0;
	private boolean firstAdReceived=false;
	
	private AdView adView;
	private ConnectivityManager cm;
	
	SuperMarket s[] = new SuperMarket[SIZE];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		AdBuddiz.setPublisherKey("b14e260c-16f1-4b20-8d8b-051992086b47");
		//AdBuddiz.setTestModeActive();
		AdBuddiz.cacheAds(this);
		if(AdBuddiz.isReadyToShowAd(this)){
			AdBuddiz.showAd(this);
		}
		
//		s[0] = new SuperMarket("大統華超級市場", 0);
//		tr[0] = (TableRow)findViewById(R.id.tableRow1);
//		tv[0] = (TextView)findViewById(R.id.textview1);
//		expiredDate[0] = (TextView)findViewById(R.id.textview1_2);
//		s[0].setCCP("CCP-015.png");
//		s[0].setURL("http://deals.superlife.ca/couponinfo/%e5%a4%a7%e7%bb%9f%e5%8d%8e%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		s[0].setURL("http://tnt.flyercenter.com/flyer/", 1);
//		//s[0].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=1", 2);
//		s[1] = new SuperMarket("鴻泰超級市場", 1);
//		tr[1] = (TableRow)findViewById(R.id.tableRow2);
//		tv[1] = (TextView)findViewById(R.id.textview2);
//		expiredDate[1] = (TextView)findViewById(R.id.textview2_2);
//		s[1].setCCP("CCP-011.png");
//		s[1].setURL("http://deals.superlife.ca/couponinfo/%E9%B8%BF%E6%B3%B0%E6%9C%AC%E5%91%A8%E7%89%B9%E4%BB%B7flyer/", 0);
//		s[1].setURL("http://hongtai.flyercenter.com/flyer/", 1);
//		//s[1].setURL("http://www.dushi.ca/tor/supermarket.php", 2);
//		s[2] = new SuperMarket("大福超級市場", 2);
//		tr[2] = (TableRow)findViewById(R.id.tableRow3);
//		tv[2] = (TextView)findViewById(R.id.textview3);
//		expiredDate[2] = (TextView)findViewById(R.id.textview3_2);
//		s[2].setCCP("CCP-027.png");
//		s[2].setURL("http://deals.superlife.ca/couponinfo/%E5%A4%A7%E7%A6%8F%E6%9C%AC%E5%91%A8%E7%89%B9%E4%BB%B7flyer/", 0);
//		s[2].setURL("http://grandfortune.flyercenter.com/flyer/", 1);
//		//s[2].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=3", 2);
//		s[3] = new SuperMarket("大中華超級市場", 3);
//		tr[3] = (TableRow)findViewById(R.id.tableRow4);
//		tv[3] = (TextView)findViewById(R.id.textview4);
//		expiredDate[3] = (TextView)findViewById(R.id.textview4_2);
//		s[3].setCCP("CCP-032.png");
//		s[3].setURL("http://deals.superlife.ca/couponinfo/%E5%A4%A7%E4%B8%AD%E5%8D%8E%E6%9C%AC%E5%91%A8%E7%89%B9%E4%BB%B7flyer/", 0);
//		s[3].setURL("http://dazhonghua.flyercenter.com/flyer/", 1);
//		//s[3].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=4", 2);
//		s[4] = new SuperMarket("信達超級市場",4);
//		tr[4] = (TableRow)findViewById(R.id.tableRow5);
//		tv[4] = (TextView)findViewById(R.id.textview5);
//		expiredDate[4] = (TextView)findViewById(R.id.textview5_2);
//		s[4].setCCP("CCP-081.png");
//		s[4].setURL("http://deals.superlife.ca/couponinfo/%E4%BF%A1%E8%BE%BE%E6%9C%AC%E5%91%A8%E7%89%B9%E4%BB%B7flyer/", 0);
//		s[4].setURL("http://brust.flyercenter.com/flyer/", 1);
//		//s[4].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=8", 2);
//		s[5] = new SuperMarket("冠業超級市場",5);
//		tr[5] = (TableRow)findViewById(R.id.tableRow6);
//		tv[5] = (TextView)findViewById(R.id.textview6);
//		expiredDate[5] = (TextView)findViewById(R.id.textview6_2);
//		s[5].setCCP("CCP-104.png");
//		s[5].setURL("http://deals.superlife.ca/couponinfo/%e5%86%a0%e4%b8%9a%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		s[5].setURL("http://firstchoicesupermarket.flyercenter.com/flyer/", 1);
//		//s[5].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=11", 2);
//		s[6] = new SuperMarket("豐泰超級市場",6);
//		tr[6] = (TableRow)findViewById(R.id.tableRow7);
//		tv[6] = (TextView)findViewById(R.id.textview7);
//		expiredDate[6] = (TextView)findViewById(R.id.textview7_2);
//		s[6].setCCP("CCP-122.png");
//		s[6].setURL("http://deals.superlife.ca/couponinfo/%e4%b8%b0%e6%b3%b0%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		s[6].setURL("http://fengtai.flyercenter.com/flyer/", 1);
//		//s[6].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=12", 2);
//		s[7] = new SuperMarket("華盛超級市場",7);
//		tr[7] = (TableRow)findViewById(R.id.tableRow8);
//		tv[7] = (TextView)findViewById(R.id.textview8);
//		expiredDate[7] = (TextView)findViewById(R.id.textview8_2);
//		s[7].setCCP("CCP-127.png");
//		s[7].setURL("http://deals.superlife.ca/couponinfo/%e5%8d%8e%e7%9b%9b%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		s[7].setURL("http://oriental.flyercenter.com/flyer/", 1);
//		//s[7].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=13", 2);
//		s[8] = new SuperMarket("大世界超級市場",8);
//		tr[8] = (TableRow)findViewById(R.id.tableRow9);
//		tv[8] = (TextView)findViewById(R.id.textview9);
//		expiredDate[8] = (TextView)findViewById(R.id.textview9_2);
//		s[8].setCCP("CCP-128.png");
//		s[8].setURL("http://deals.superlife.ca/couponinfo/%e5%a4%a7%e4%b8%96%e7%95%8c%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		//s[8].setURL("http://tnt.flyercenter.com/flyer/", 1);
//		//s[8].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=14", 2);
//		s[9] = new SuperMarket("鼎泰超級市場",9);
//		tr[9] = (TableRow)findViewById(R.id.tableRow10);
//		tv[9] = (TextView)findViewById(R.id.textview10);
//		expiredDate[9] = (TextView)findViewById(R.id.textview10_2);
//		s[9].setCCP("CCP-131.png");
//		s[9].setURL("http://deals.superlife.ca/couponinfo/%e9%bc%8e%e6%b3%b0%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		s[9].setURL("http://tonetai.flyercenter.com/flyer/", 1);
//		//s[9].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=15", 2);
//		s[10] = new SuperMarket("多福超級市場",10);
//		tr[10] = (TableRow)findViewById(R.id.tableRow11);
//		tv[10] = (TextView)findViewById(R.id.textview11);
//		expiredDate[10] = (TextView)findViewById(R.id.textview11_2);
//		s[10].setURL("http://deals.superlife.ca/couponinfo/%e5%a4%9a%e7%a6%8f%e8%b6%85%e5%b8%82%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		s[10].setURL("http://topfood.flyercenter.com/flyer/", 1);
//		s[11] = new SuperMarket("鴻華超級市場",11);
//		tr[11] = (TableRow)findViewById(R.id.tableRow12);
//		tv[11] = (TextView)findViewById(R.id.textview12);
//		expiredDate[11] = (TextView)findViewById(R.id.textview12_2);
//		s[11].setCCP("CCP-025.png");
//		s[11].setURL("http://deals.superlife.ca/couponinfo/%e9%b8%bf%e5%8d%8e%e8%b6%85%e5%b8%82%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		//s[11].setURL("http://tnt.flyercenter.com/flyer/", 1);
//		//s[11].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=2", 2);
//		s[12] = new SuperMarket("元明超級市場",12);
//		tr[12] = (TableRow)findViewById(R.id.tableRow13);
//		tv[12] = (TextView)findViewById(R.id.textview13);
//		expiredDate[12] = (TextView)findViewById(R.id.textview13_2);
//		s[12].setURL("http://deals.superlife.ca/couponinfo/%e5%85%83%e6%98%8e%e8%b6%85%e5%b8%82%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		//s[12].setURL("http://tnt.flyercenter.com/flyer/", 1);
//		s[13] = new SuperMarket("福耀超級市場",13);
//		tr[13] = (TableRow)findViewById(R.id.tableRow14);
//		tv[13] = (TextView)findViewById(R.id.textview14);
//		expiredDate[13] = (TextView)findViewById(R.id.textview14_2);
//		s[13].setCCP("CCP-039.png");
//		s[13].setURL("http://deals.superlife.ca/couponinfo/%e7%a6%8f%e8%80%80%e8%b6%85%e5%b8%82%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		s[13].setURL("http://fuyao.flyercenter.com/flyer/", 1);
//		//s[13].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=6", 2);
//		s[14] = new SuperMarket("陽光超級市場",14);
//		tr[14] = (TableRow)findViewById(R.id.tableRow15);
//		tv[14] = (TextView)findViewById(R.id.textview15);
//		expiredDate[14] = (TextView)findViewById(R.id.textview15_2);
//		s[14].setURL("http://deals.superlife.ca/couponinfo/%e9%98%b3%e5%85%89%e8%b6%85%e5%b8%82%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		//s[14].setURL("http://tnt.flyercenter.com/flyer/", 1);
//		s[15] = new SuperMarket("家樂匯超級市場",15);
//		tr[15] = (TableRow)findViewById(R.id.tableRow16);
//		tv[15] = (TextView)findViewById(R.id.textview16);
//		expiredDate[15] = (TextView)findViewById(R.id.textview16_2);
//		s[15].setCCP("CCP-041.png");
//		s[15].setURL("http://deals.superlife.ca/couponinfo/%e5%ae%b6%e4%b9%90%e6%b1%87galleria%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		//s[15].setURL("http://tnt.flyercenter.com/flyer/", 1);
//		//s[15].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=7", 2);
//		s[16] = new SuperMarket("Price Chopper",16);
//		tr[16] = (TableRow)findViewById(R.id.tableRow17);
//		tv[16] = (TextView)findViewById(R.id.textview17);
//		expiredDate[16] = (TextView)findViewById(R.id.textview17_2);
//		s[16].setURL("http://deals.superlife.ca/couponinfo/price-choppers%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		s[16].setURL("http://pricechopper.flyercenter.com/flyer/", 1);
//		s[17] = new SuperMarket("FreshCo.",17);
//		tr[17] = (TableRow)findViewById(R.id.tableRow18);
//		tv[17] = (TextView)findViewById(R.id.textview18);
//		expiredDate[17] = (TextView)findViewById(R.id.textview18_2);
//		s[17].setURL("http://deals.superlife.ca/couponinfo/freshco-%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		s[17].setURL("http://freshco.flyercenter.com/flyer/", 1);
//		s[18] = new SuperMarket("Food Basics",18);
//		tr[18] = (TableRow)findViewById(R.id.tableRow19);
//		tv[18] = (TextView)findViewById(R.id.textview19);
//		expiredDate[18] = (TextView)findViewById(R.id.textview19_2);
//		s[18].setCCP("CCP-090.png");
//		s[18].setURL("http://deals.superlife.ca/couponinfo/food-basic%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		s[18].setURL("http://foodbasics.flyercenter.com/flyer/", 1);
//		//s[18].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=9", 2);
//		s[19] = new SuperMarket("No Frills",19);
//		tr[19] = (TableRow)findViewById(R.id.tableRow20);
//		tv[19] = (TextView)findViewById(R.id.textview20);
//		expiredDate[19] = (TextView)findViewById(R.id.textview20_2);
//		s[19].setURL("http://deals.superlife.ca/couponinfo/nofrills%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		s[19].setURL("http://nofrills.flyercenter.com/flyer/", 1);
//		s[20] = new SuperMarket("佳樂超級市場",20);
//		tr[20] = (TableRow)findViewById(R.id.tableRow21);
//		tv[20] = (TextView)findViewById(R.id.textview21);
//		expiredDate[20] = (TextView)findViewById(R.id.textview21_2);
//		s[20].setCCP("CCP-037.png");
//		//s[20].setURL("http://deals.superlife.ca/couponinfo/nofrills%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
//		//s[20].setURL("http://nofrills.flyercenter.com/flyer/", 1);
//		//s[20].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=5", 2);
//		s[21] = new SuperMarket("百福超級市場",21);
//		tr[21] = (TableRow)findViewById(R.id.tableRow22);
//		tv[21] = (TextView)findViewById(R.id.textview22);
//		expiredDate[21] = (TextView)findViewById(R.id.textview22_2);
//		s[21].setCCP("CCP-073.png");
		//s[20].setURL("http://deals.superlife.ca/couponinfo/nofrills%e6%9c%ac%e5%91%a8%e7%89%b9%e4%bb%b7flyer/", 0);
		//s[20].setURL("http://nofrills.flyercenter.com/flyer/", 1);
		//s[21].setURL("http://www.dushi.ca/tor/supermarket.php?pageid=10", 2);
		
		tv2[0] = (TextView)findViewById(R.id.textview1_1);
		tv2[1] = (TextView)findViewById(R.id.textview2_1);
		tv2[2] = (TextView)findViewById(R.id.textview3_1);
		tv2[3] = (TextView)findViewById(R.id.textview4_1);
		tv2[4] = (TextView)findViewById(R.id.textview5_1);
		tv2[5] = (TextView)findViewById(R.id.textview6_1);
		tv2[6] = (TextView)findViewById(R.id.textview7_1);
		tv2[7] = (TextView)findViewById(R.id.textview8_1);
		tv2[8] = (TextView)findViewById(R.id.textview9_1);
		tv2[9] = (TextView)findViewById(R.id.textview10_1);
		tv2[10] = (TextView)findViewById(R.id.textview11_1);
		tv2[11] = (TextView)findViewById(R.id.textview12_1);
		tv2[12] = (TextView)findViewById(R.id.textview13_1);
		tv2[13] = (TextView)findViewById(R.id.textview14_1);
		tv2[14] = (TextView)findViewById(R.id.textview15_1);
		tv2[15] = (TextView)findViewById(R.id.textview16_1);
		tv2[16] = (TextView)findViewById(R.id.textview17_1);
		tv2[17] = (TextView)findViewById(R.id.textview18_1);
		tv2[18] = (TextView)findViewById(R.id.textview19_1);
		tv2[19] = (TextView)findViewById(R.id.textview20_1);
		tv2[20] = (TextView)findViewById(R.id.textview21_1);
		tv2[21] = (TextView)findViewById(R.id.textview22_1);
		
		iniData = new ArrayList<String>();
		
		adView = (AdView)findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		
		adView.loadAd(adRequest);
		
		adView.setAdListener(new AdListener(){
        	public void onAdFailedToLoad(int errorCode){
        		if(!firstAdReceived){
        			adView.setVisibility(AdView.GONE);
        		}
        	}
        		
        	public void onAdLoaded(){
        		firstAdReceived=true;
        	}
        });
		
		loadListFromFile();
		
		for(int i=0; i<SIZE; i++){
			tv[i].setText(s[i].getName());
		}
		
		checkUpdate(false);
		
		cm = (ConnectivityManager)this.getSystemService(Activity.CONNECTIVITY_SERVICE);
		
		if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected() && needChecked){
			//new ImgSrc().execute();	
		}else if(needChecked){
			Toast.makeText(MainActivity.this, "有更新，請連結網絡後再更新！！", Toast.LENGTH_SHORT).show();
			for(int j=0; j<SIZE; j++){
				expiredDate[j].setText(s[j].getDate());
			}
		}else{
			for(int j=0; j<SIZE; j++){
				expiredDate[j].setText(s[j].getDate());
			}
		}
		
		TableRow.OnClickListener click = new TableRow.OnClickListener(){
			
			@Override
			public void onClick(View v) {
				TableRow trow = (TableRow)v;
				index = Arrays.asList(tr).indexOf(trow);
				intent = new Intent();
				intent.setClass(MainActivity.this, flyersActivity.class);
				bundle = new Bundle();
				bundle.putString("market_name", s[index].getName());
				//bundle.putString("source_url", s[index].getURL());
				bundle.putString("image_src", s[index].getImgSrc(0));
				
				intent.putExtras(bundle);
				
				startActivityForResult(intent, 0);
			}
			
		};
		
		tr[0].setOnClickListener(click);
		tr[1].setOnClickListener(click);
		tr[2].setOnClickListener(click);
		tr[3].setOnClickListener(click);
		tr[4].setOnClickListener(click);
		tr[5].setOnClickListener(click);
		tr[6].setOnClickListener(click);
		tr[7].setOnClickListener(click);
		tr[8].setOnClickListener(click);
		tr[9].setOnClickListener(click);
		tr[10].setOnClickListener(click);
		tr[11].setOnClickListener(click);
		tr[12].setOnClickListener(click);
		tr[13].setOnClickListener(click);
		tr[14].setOnClickListener(click);
		tr[15].setOnClickListener(click);
		tr[16].setOnClickListener(click);
		tr[17].setOnClickListener(click);
		tr[18].setOnClickListener(click);
		tr[19].setOnClickListener(click);
		tr[20].setOnClickListener(click);
		tr[21].setOnClickListener(click);
		
		
	}
	
	//force means force update
	private void checkUpdate(boolean force){
		if(force){
			for(int i=0; i<SIZE; i++){
				tempArray[countCheck]=i;
				countCheck++;
				s[i].Updated(false);
				needChecked=true;
			}
			
		}else{
			for(int i=0; i<SIZE; i++){
				if(checkExpired(getCurDate(), String.valueOf(valueOfDate(s[i].getDate())))){
					tempArray[countCheck]=i;
					countCheck++;
					s[i].Updated(false);
					needChecked=true;
				}
			}
		}
		
		if(needChecked){
			checkArray = new int[countCheck];
			for(int i=0; i<countCheck; i++){
				checkArray[i] = tempArray[i];
			}
		}
	}
	
	private String getCurDate(){
		SimpleDateFormat df = new SimpleDateFormat("MMdd");
		return df.format(Calendar.getInstance().getTime());
	}
	
	private boolean checkExpired(String curDate, String preDate){
		int zero = preDate.length()-4;
		
		if(preDate.length()<7){
			return true;
		}else{
			if(preDate.charAt(preDate.length()-4)=='0'){
				if(valueOfDate(curDate)>valueOfDate(preDate.substring(zero+1, preDate.length()))){
					return true;
				}
			}else{
				if(curDate.length()==3){
					if(valueOfDate(curDate)+1200>valueOfDate(preDate.substring(zero, preDate.length()))){
						return true;
					}
				}else{
					if(valueOfDate(curDate)>valueOfDate(preDate.substring(zero, preDate.length()))){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private void goUpdate(){
		for(int m=0; m<SIZE; m++){
			if(s[m].isUpdated()){
				tv2[m].setText(R.string.weeklypromo);
			}else{
				tv2[m].setText(R.string.lastpromo);
			}
		}
	}
	
	private int valueOfDate(String d){
		String temp = d.replaceAll("[\\D]", "");
		if(temp.equals("")){
			return 0;
		}else if(temp.length()<9){
			return Integer.parseInt(temp);
		}else{
			return Integer.parseInt(temp.substring(4, 8)+temp.substring(12, 16));
		}
	}
	
	private String formatDate(String d){
		String temp = d.replaceAll("[\\D]", "");
		if(temp.length()<9){
			return d;
		}else{
			return ("有效期："+temp.substring(4, 6)+"月"+temp.substring(6, 8)+"日-"
					+temp.substring(12, 14)+"月"+temp.substring(14, temp.length())+"日");
		}
	}
	
	private void loadListFromFile(){
		
		File myfile1 = getFileStreamPath(FILENAME);
		
		try{
			if(myfile1.exists()){
				FileInputStream fis = openFileInput(FILENAME);
				ObjectInputStream ois = new ObjectInputStream(fis);
				iniData = (List<String>)(ois.readObject());
				ois.close();
				fis.close();
				
				for(int i=0; i<SIZE; i++){
					s[i].setImgSrc(0, iniData.get(i*2));
					s[i].setDate(iniData.get(i*2+1));
				}
				
				fileIsExist=true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void writeListToFile(){
		File myfile = getFileStreamPath(FILENAME);
		
		try{
			if(myfile.exists() || myfile.createNewFile()){
				FileOutputStream fos = openFileOutput(FILENAME, MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(iniData);
				
				oos.close();
				fos.close();
				
				fileIsExist=true;
			}
		}catch(Exception e){ 
			e.printStackTrace();
		}		
	}
	
	private String reformatDate(Elements es){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(String.valueOf(valueOfDate(es.text().toString()))));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.add(Calendar.DAY_OF_MONTH, 6);
		
		return (String.valueOf(valueOfDate(es.text().toString()))+sdf.format(c.getTime()));
	}
	
	

	private class ImgSrc extends AsyncTask<Void, Void, Void> {
		String src;
		String date;
		int i=0, arrayIndex=0;
		String customHTML;
		int whichURL=0;
		int inverse[] = {0,1,3,7,9,13,14};
		
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Checking Latest Flyers....");
            pd.setIndeterminate(false);
            pd.setMax(100);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setCancelable(false);
            pd.show();
        }

		@Override
		protected Void doInBackground(Void... params) {
			if(checkArray!=null){
				for(int m:checkArray){
					StringBuilder sb = new StringBuilder();
					Elements dat = new Elements();
					Elements img = new Elements();
					Elements dattmp = new Elements(); 
					//Elements imgtmp = new Elements();
					Elements img0 = new Elements();
					Pattern p = null;
					Matcher ma = null;
					boolean flag=false;
					
					for(whichURL=0; whichURL<4; whichURL++ ){
						
//						if(s[m].getURL(whichURL).equals("NA")){
//							continue;
//						}else{
//							dat.clear();
//						}
						
						try{
						// Connect to the web site
						Document document = new Document("");
					
						switch(whichURL){
						//get date
						case 0:
							dat = document.select("div[class=gallery_content] span");
							//img = document.select("div[class=listM] img[src]");
							img0 = document.select("script");
							p = Pattern.compile("\\stimg*:*'([^']*)'");
							ma = p.matcher(img0.toString());
							if(ma.find()){
								flag = true;
							}
							break;
						case 1:
							dat = document.select("div[class=block_content] span[class=v_date]");
							img = document.select("ul[class=image_hlist] img[src]");
							flag=false;
							break;
						case 2:
							dat = document.select("td a");
							//Log.v("date00", dat.text().toString());
							Element dat2 = dat.last();
							dat.clear();
							dat.add(0, dat2);
							
							if(!dat.isEmpty()){
								String stemp="";// = s[m].getURL(2)+dat.text().toString()+s[m].getCCP();
								if(exists(stemp)){
									Element img2 = new Element(Tag.valueOf("div"), "");
									img2.append(stemp);
									img.clear();
									img.add(img2);
								}else{
									img.empty();
								}
								String ss = reformatDate(dat);
								dat.empty();
								dat.append(ss);
							}
							flag=false;
							break;
						}
						
						} catch (Exception e) {
			                e.printStackTrace();
			            }
						if(!dat.isEmpty() && (!img.isEmpty() || flag)){
							if(!checkExpired(getCurDate(), String.valueOf(valueOfDate(dat.text().toString())))){
								s[m].setServer(whichURL);
								s[m].Updated(true);
								break;
							}else{
								if(whichURL==0){
									dattmp = dat.clone();
								}else{
									dat = dattmp.clone();
								}
							}
						}else{
							if(whichURL>0){
								dat = dattmp.clone();
							}
						}
						s[m].Updated(false);
						s[m].setServer(0);
					};
					
					
					if(!dat.isEmpty()){
						date = dat.text().toString();
					}else{
						date = "0";
					}
					
					if(valueOfDate(date)>valueOfDate(s[m].getDate())){
						i=0;
						boolean isInverse=false;
						for(int a=0; a<inverse.length; a++){
							if(inverse[a]==m){
								isInverse=true;
							}
						}
						if(s[m].getServer()==0){
							do{
								if(isInverse){
								sb.insert(0,"</p>")
									.insert(0,"<img src=\""+ma.group(1)+"\">")
									.insert(0,"<p>");
								}else{
									sb.append("<p>")
									.append("<img src=\""+ma.group(1)+"\">")
									.append("</p>");
								}
								i++;
							}while(ma.find());
						}else if(!img.isEmpty()){
							// Locate the src attribute
							for(Element element : img){
								i++;				
								//Log.v("img", src);
								if(s[m].getServer()==1){
									
									src = element.attr("src");
									int index = src.indexOf("thumb");
									index--;
									
									if(isInverse){
									sb.insert(0,"</p>")
                						.insert(0,"<img src=\""+src.substring(0, index)+src.substring(index+6, src.length())+"\">")
                						.insert(0,"<p>");
									}else{
										sb.append("<p>")
                						.append("<img src=\""+src.substring(0, index)+src.substring(index+6, src.length())+"\">")
                						.append("</p>");
									}
								}else if(s[m].getServer()==2){
									//src = "http://www.dushi.ca/"+element.attr("src");
									src = element.text().toString();
									sb.append("<p>")
            						.append("<img src=\""+src+"\">")
            						.append("</p>");
								}
							}
						}else{
							sb.append("<p>不能取得Flyers, 請容後再更新</p>");
						}
						//s[m].setPages(i);
						customHTML = sb.toString();
						s[m].setImgSrc(0, customHTML);
						date = formatDate(date);
						s[m].setDate(date);
						//src = img.attr("src");
						if(!fileIsExist){
							iniData.add(customHTML);
							iniData.add(date);
						}else{
							iniData.set(m*2, customHTML);
							iniData.set(m*2+1, date);
						}
					}
					
					pd.setProgress((int)(((arrayIndex+1)*100)/countCheck));
					arrayIndex++;
				}
			}
			return null;
		}

		@Override
        protected void onPostExecute(Void result) {
			for(int j=0; j<SIZE; j++){
				expiredDate[j].setText(s[j].getDate());
			}
			goUpdate();
			writeListToFile();
			needChecked=false;
			checkArray=null;
			countCheck=0;
            pd.dismiss();
            Toast.makeText(MainActivity.this, "更新完成！！", Toast.LENGTH_SHORT).show();
        }
		
	}
	
	public static boolean exists(String URLName){
	    try {
	      HttpURLConnection.setFollowRedirects(false);
	      // note : you may also need
	      //        HttpURLConnection.setInstanceFollowRedirects(false)
	      HttpURLConnection con =
	         (HttpURLConnection) new URL(URLName).openConnection();
	      con.setRequestMethod("HEAD");
	      return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
	    }
	    catch (Exception e) {
	       e.printStackTrace();
	       return false;
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_action, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_refresh:
	        	if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
	        		checkUpdate(false);
	        		new ImgSrc().execute();	
	    		}else{
	    			Toast.makeText(MainActivity.this, "未能連結網絡，請連結後再更新！！", Toast.LENGTH_SHORT).show();
	    		}
	            return true;
	        case R.id.action_force:
	        	if(cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
	        		checkUpdate(true);
	        		new ImgSrc().execute();	
	    		}else{
	    			Toast.makeText(MainActivity.this, "未能連結網絡，請連結後再更新！！", Toast.LENGTH_SHORT).show();
	    		}
	            return true;
	        case R.id.action_exit:
	            //super.finish();
	        	Intent newIntent = new Intent(MainActivity.this, MainActivityV2.class);
	        	startActivity(newIntent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	  public void onPause() {
		if(adView!=null){
			adView.pause();
		}
	    super.onPause();
	  }

	  @Override
	  public void onResume() {
	    super.onResume();
	    if(adView!=null){
			adView.resume();
		}
	  }

	  @Override
	  public void onDestroy() {
		  if(adView!=null){
				adView.destroy();
			}
	    super.onDestroy();
	  }
}
