package com.thaddroid.apps.ccflyers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivityV2 extends Activity {
	private final String TAG = getClass().getSimpleName();
	
	//CONSTANTS
	private int numberOfMarkets;
	private final String FILE_NAME = "ccflyers.txt";
	
	//MEMBER VARIABLES
	private SuperMarketsAdapter mSuperMarketsAdapter;
	private ArrayList<SuperMarket> superMarketList;
	private String[] marketName;
	private int[] imageArray;
	private String[] urlArray;
	private ProgressDialog progressBar;
	private FlyersDownloader mFlyersDownloader;
	private int[] checkArray;
	private List<String> dateList = new ArrayList<String>();
	
	//BOOLEAN VARIABLES
	private boolean isListUpdated = false;
	private boolean fileIsExist = false;
	private boolean isUpdateAll = false;
	
	//CONTROL VARIABLES
	private ListView ccFlyersListView;
	private ListHolder holder;
	private RelativeLayout refreshButtonRelativeLayout;
	private RelativeLayout exitButtonRelativeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ccflyers);
		createControlReference();
		
		marketName = getResources().getStringArray(R.array.supermarket_title_list);
		numberOfMarkets = marketName.length;
		dateList.clear();
		initImageArray();
		initURLArray();
		initListItem();
	}
	
	private void createControlReference(){
		refreshButtonRelativeLayout = (RelativeLayout)findViewById(R.id.refreshButtonRelativeLayout);
		refreshButtonRelativeLayout.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				Log.d(TAG, "REFRESH BUTTON IS PRESSED");
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivityV2.this);
				alertDialogBuilder.setTitle("舊Flyers會被覆蓋！！");
				alertDialogBuilder.setMessage("是否繼續更新？？");
				alertDialogBuilder.setCancelable(false);
				alertDialogBuilder.setPositiveButton("繼續", new OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						didPressRefresh();
					}
					
					});
				alertDialogBuilder.setNegativeButton("取消", new OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
					
				});
				
				AlertDialog alertDialog = alertDialogBuilder.create();
				
				alertDialog.show();
				
				return false;
			}
			
		});
		
		exitButtonRelativeLayout = (RelativeLayout)findViewById(R.id.exitButtonRelativeLayout);
		exitButtonRelativeLayout.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(TAG, "EXIT BUTTON IS PRESSED");
				onBackPressed();
				return false;
			}
			
		});;
		
		ccFlyersListView = (ListView)findViewById(R.id.ccFlyersListView);
		ccFlyersListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				String imageHTML = genImageHTMLString(position);
				
				Intent flyerIntent = new Intent(MainActivityV2.this, flyersActivity.class);
				flyerIntent.putExtra("market_name", superMarketList.get(position).getName());
				flyerIntent.putExtra("image_src", imageHTML);
				startActivity(flyerIntent);
			}
			
		});
	}
	
	private String genImageHTMLString(int index){
		StringBuilder sb = new StringBuilder();
		
		if(superMarketList.get(index).flyer == null){
			Toast.makeText(this, "NO FLYERS", Toast.LENGTH_SHORT).show();
			return "";
		}
		
		for(int i=0; i<superMarketList.get(index).getPages(); i++){
			sb.append("<p>")
				.append("<img src=\""+superMarketList.get(index).getImgSrc(i)+"\">")
				.append("</p>");
		}
		
		return sb.toString();
	}
	
	//INITIALIZATION
	private boolean loadListFromFile(){		
		File loadFile = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
		
		try{
			if (loadFile.exists()) {
				FileInputStream fis = new FileInputStream(loadFile);
				InputStreamReader isr = new InputStreamReader(fis);
				// ObjectInputStream ois = new ObjectInputStream(fis);
				BufferedReader br = new BufferedReader(isr);
				String tempString = "";
				dateList.clear();
				while ((tempString = br.readLine()) != null) {
					dateList.add(tempString);
				}
				// dateList = (ArrayList<String>)ois.readObject();

				br.close();
				isr.close();
				// ois.close();
				fis.close();

				updateScreenValue();
			} else {
				return false;
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
			return false;
		}catch(Exception ioe){
			ioe.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void updateScreenValue(){
		SuperMarket superMarket;
		for (int i = 0; i < superMarketList.size(); i++) {
			if ((superMarket = superMarketList.get(i)) != null) {
				superMarket.setStartDate(dateList.get(i*3 + 1));
				superMarket.setExpiredDate(dateList.get(i*3 + 2));
//				if (Integer.parseInt(getCurDate()) > Integer.parseInt(dateList.get(i + 2))) {
//					superMarket.Updated(false);
//				} else {
//					superMarket.Updated(true);
//				}
				if(datesCompareToToday(superMarket.getExpiredDate())){
					superMarket.Updated(false);
				}else{
					superMarket.Updated(true);
				}
			}
		}
		mSuperMarketsAdapter.notifyDataSetChanged();
	}
	
	private boolean datesCompareToToday(String date){
		boolean isExpired = false;
		
		DateFormat format = new SimpleDateFormat("EEE, MMM d, yyyy");
		try {
			Date expiredDate = format.parse(date);
			
			if(new Date().after(expiredDate)){
				isExpired = true;
			}else{
				isExpired = false;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isExpired = true;
		}
		
		return isExpired;
	}
	
	private void updateDataList(){
		dateList.clear();
		for(int i=0; i<superMarketList.size(); i++){
			SuperMarket singleSuperMarket = superMarketList.get(i);
			dateList.add(singleSuperMarket.getName());
			dateList.add(singleSuperMarket.getStartDate());
			dateList.add(singleSuperMarket.getExpiredDate());
		}
	}
	
	private void initURLArray(){
		if(urlArray == null){
			urlArray = new String[numberOfMarkets];
		}
		
		urlArray[0] = "http://flyers.smartcanucks.ca/grand-fortune-food-mart-canada";
		urlArray[1] = "http://flyers.smartcanucks.ca/farm-fresh-supermarket-canada";
		urlArray[2] = "http://flyers.smartcanucks.ca/food-depot-supermarket-canada";
		urlArray[3] = "http://flyers.smartcanucks.ca/tt-supermarket-canada";
		urlArray[4] = "http://flyers.smartcanucks.ca/tone-tai-supermarket-canada";
		urlArray[5] = "http://flyers.smartcanucks.ca/top-food-supermarket-canada";
		urlArray[6] = "http://flyers.smartcanucks.ca/foodymart-canada";
		urlArray[7] = "http://flyers.smartcanucks.ca/fu-yao-supermarket-canada";
		urlArray[8] = "http://flyers.smartcanucks.ca/first-choice-supermarket-canada";
		urlArray[9] = "http://flyers.smartcanucks.ca/hong-tai-supermarket-canada";
		urlArray[10] = "http://flyers.smartcanucks.ca/oriental-food-mart-canada";
		urlArray[11] = "http://flyers.smartcanucks.ca/seasons-food-mart-canada";
		urlArray[12] = "http://flyers.smartcanucks.ca/sun-food-supermarket-canada";
		urlArray[13] = "http://flyers.smartcanucks.ca/sunny-food-mart-canada";
		urlArray[14] = "http://flyers.smartcanucks.ca/btrust-supermarket-canada";
		urlArray[15] = "http://flyers.smartcanucks.ca/yuan-ming-supermarket-canada";
		urlArray[16] = "http://flyers.smartcanucks.ca/bestco-food-mart-canada";
		urlArray[17] = "http://flyers.smartcanucks.ca/food-basics-canada";
		urlArray[18] = "http://flyers.smartcanucks.ca/freshco-canada";
		urlArray[19] = "http://flyers.smartcanucks.ca/no-frills-canada";
		urlArray[20] = "http://flyers.smartcanucks.ca/price-chopper-canada";
		urlArray[21] = "http://flyers.smartcanucks.ca/galleria-supermarket-canada";
	}
	
	private void initImageArray(){
		if(imageArray == null){
			imageArray = new int[numberOfMarkets];
		}
		
		imageArray[0] = R.drawable.da_fu;
		imageArray[1] = R.drawable.da_shi_jie;
		imageArray[2] = R.drawable.da_zhong_hua;
		imageArray[3] = R.drawable.t_t;
		imageArray[4] = R.drawable.ding_tai;
		imageArray[5] = R.drawable.duo_fu;
		imageArray[6] = R.drawable.feng_tai;
		imageArray[7] = R.drawable.fu_yao;
		imageArray[8] = R.drawable.guan_ye;
		imageArray[9] = R.drawable.hong_tai;
		imageArray[10] = R.drawable.hua_sheng;
		imageArray[11] = R.drawable.seasons;
		imageArray[12] = R.drawable.sunfood;
		imageArray[13] = R.drawable.sunny;
		imageArray[14] = R.drawable.xin_da;
		imageArray[15] = R.drawable.yuanming;
		imageArray[16] = R.drawable.bestco;
		imageArray[17] = R.drawable.food_basics;
		imageArray[18] = R.drawable.freshco;
		imageArray[19] = R.drawable.no_frills;
		imageArray[20] = R.drawable.price_chopper;
		imageArray[21] = R.drawable.galleria;
	}
	
	private void initListItem(){
		if(superMarketList == null){
			superMarketList = new ArrayList<SuperMarket>();
		}
		
		superMarketList.clear();
		for(int i=0; i<marketName.length; i++){
			SuperMarket superMarket = new SuperMarket(marketName[i], i);
			superMarket.setBannerResId(imageArray[i]);
			superMarketList.add(superMarket);
		}
	}
	
	//BUTTONS METHOD
	private void didPressRefresh(){
		if(checkInternetConnection()){
			if(checkRequiresUpdate()){
				mFlyersDownloader = new FlyersDownloader();
				mFlyersDownloader.execute(checkArray);
			}else{
				Toast.makeText(this, "Flyers已經是最新旳，不用更新了！！", Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(this, "沒有網絡連線，請連線後再嘗試更新！！", Toast.LENGTH_SHORT).show();
		}
	}
	
	//PRIVATE METHOD
	private boolean saveListToFile(){
		File savedFile = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
		
		try{
			//savedFile.createNewFile();
			if(!savedFile.exists()){
				if(!savedFile.createNewFile()){
					return false;
				}
			}else{
				//savedFile.
			}
			
			FileOutputStream fos = new FileOutputStream(savedFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			for(int i=0; i<dateList.size(); i++){
				oos.writeBytes(dateList.get(i)+"\n");
			}
			oos.close();
			fos.close();
			
			fileIsExist = true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private String getCurDate(){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(Calendar.getInstance().getTime());
	}
	
	private static boolean checkURLExists(String URLName){
	    try {
	      HttpURLConnection.setFollowRedirects(false);
	      // note : you may also need
	      //        HttpURLConnection.setInstanceFollowRedirects(false)
	      HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
	      con.setRequestProperty( "Accept-Encoding", "" ); 
	      con.setRequestMethod("HEAD");
	      if(con.getResponseCode() == HttpURLConnection.HTTP_OK){
	    	  return true;
	      }else{
	    	  return false;
	      }
	    }
	    catch (Exception e) {
	       e.printStackTrace();
	       return false;
	    }
	}
	
	private boolean checkInternetConnection(){
		ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Service.CONNECTIVITY_SERVICE);
		
		if(cm!=null && cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnected()){
			return true;
		}
		
		return false;
	}
	
	private boolean checkRequiresUpdate(){
		int checkCount = 0;
		int[] tempArray = new int[superMarketList.size()];
		
		if (!isUpdateAll) {
			for (int i = 0; i < superMarketList.size(); i++) {
				if (!superMarketList.get(i).isUpdated()) {
					tempArray[checkCount] = i;
					checkCount++;
				}
			}
		}else{
			for (int i = 0; i < superMarketList.size(); i++) {
				tempArray[checkCount] = i;
				checkCount++;
			}
		}
		
		if(checkCount > 0){
			checkArray = new int[checkCount];
			for(int i=0; i<checkCount; i++){
				checkArray[i] = tempArray[i];
			}
			return true;
		}else{
			return false;
		}
	}
	
	//ADAPTER
	private class SuperMarketsAdapter extends BaseAdapter{
		private Context context;
		private ArrayList<SuperMarket> superMarketArray;
		
		private SuperMarketsAdapter(Context context, ArrayList<SuperMarket> arrayList){
			this.context = context;
			this.superMarketArray = arrayList;
		}

		@Override
		public int getCount() {
			return superMarketArray.size();
		}

		@Override
		public Object getItem(int position) {
			return superMarketArray.get(position);
		}

		@Override
		public long getItemId(int position) {
			return superMarketArray.get(position).hashCode();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			holder = null;
			
			final SuperMarket singleSuperMarket = superMarketArray.get(position);
			if(row == null){
				LayoutInflater inflater = ((Activity)context).getLayoutInflater();
				row = inflater.inflate(R.layout.listitem, parent, false);
				
				holder = new ListHolder();
				holder.listItemImageView = (ImageView)row.findViewById(R.id.listItemBannerImageView);
				holder.listItemMarketNameTextView = (TextView)row.findViewById(R.id.listItemMarketNameTextView);
				holder.listItemWeekDiscountTextView = (TextView)row.findViewById(R.id.listItemWeekDiscountTextView);
				holder.listItemDiscountPeriodTextView = (TextView)row.findViewById(R.id.listItemDiscountPeriodTextView);
			
				row.setTag(holder);
			}else{
				holder = (ListHolder) row.getTag();
			}
			
			if(singleSuperMarket != null){
				holder.listItemMarketNameTextView.setText(singleSuperMarket.getName());
				
				holder.listItemImageView.setImageResource(singleSuperMarket.getBannerResId());
				
				if(singleSuperMarket.isUpdated()){
					holder.listItemWeekDiscountTextView.setText("本週特價");
				}else{
					holder.listItemWeekDiscountTextView.setText("上週特價");
				}
				
				if(singleSuperMarket.getStartDate()!=null && singleSuperMarket.getExpiredDate()!=null &&
						!singleSuperMarket.getStartDate().equals("0") && !singleSuperMarket.getExpiredDate().equals("0")){
					holder.listItemDiscountPeriodTextView.setText(
							singleSuperMarket.getStartDate()+" To " + 
							singleSuperMarket.getExpiredDate());
				}else{
					holder.listItemDiscountPeriodTextView.setText("NA");
				}
			}
			
			return row;
		}
		
	}
	
	private class ListHolder{
		public ImageView listItemImageView;
		public TextView listItemMarketNameTextView;
		public TextView listItemWeekDiscountTextView;
		public TextView listItemDiscountPeriodTextView;
		
	}
	
	//ASYNCTASK
	private class FlyersDownloader extends AsyncTask<int[], Void, Boolean>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			progressBar = new ProgressDialog(MainActivityV2.this);
			progressBar.setMax(100);
			progressBar.setMessage("Checking updates....");
			progressBar.setCancelable(false);
			progressBar.setIndeterminate(false);
			progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressBar.show();
		}

		@Override
		protected Boolean doInBackground(int[]... params) {
			int[] updateArray = params[0];
			int arraySize = updateArray.length;
			boolean somethingUpdated = false;
			
			if(arraySize > 0){
				somethingUpdated = decodeDataFromServer(updateArray, progressBar);
			}
			
			return somethingUpdated;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			progressBar.dismiss();
			
			if(result){
				mSuperMarketsAdapter.notifyDataSetChanged();
				//ccFlyersListView.invalidate();
				Toast.makeText(MainActivityV2.this, "更新完成！！", Toast.LENGTH_SHORT).show();
				isListUpdated = true;
			}else{
				Toast.makeText(MainActivityV2.this, "更新失敗！！", Toast.LENGTH_SHORT).show();
			}
		}
		
		private boolean decodeDataFromServer(int[] array, ProgressDialog mProgressBar){
			Document document;
			//String imageSrc="";
			int count=0;
			int errorCount=0;
			
			for(int m:array){
				//imageSrc="";
				String startDate="";
				String endDate="";
				String imageURL = "";
				if (!urlArray[m].equals("") && checkURLExists(urlArray[m])) {
					try {
						document = Jsoup.connect(urlArray[m]).get();
						Elements elements = document.select("div[class=right]");
						Elements datesElements = elements.select("ul[class=dates] li");
						startDate = datesElements.get(0).text().substring(7);
						endDate = datesElements.get(1).text().substring(5);
						Elements imageElements = elements.select("a[class=button]");
						imageURL = imageElements.attr("href");
						
						superMarketList.get(m).setStartDate(startDate.trim());
						superMarketList.get(m).setExpiredDate(endDate.trim());
						superMarketList.get(m).setURL(imageURL);

						//imageSrc = "http://www.foodymart.com/" + imgName;
						decodeImageFromImageURL((imageURL+"/all"), m);
						//superMarketList.get(m).setImgSrc(0, imageSrc);
					} catch (IOException e) {
						e.printStackTrace();
						errorCount++;
					}
				}else{
					errorCount++;
				}
//				switch(m){
//				case 0:
//					break;
//				case 1:
//					break;
//				case 2:
//					break;
//				case 3:
////					if(checkURLExists(urlArray[3])){
////						try {
////							document = Jsoup.connect(urlArray[3]).get();
////							Elements imgElements = document.select("img");
////							String imgUrl = imgElements.attr("src");
////							
////							Log.d(TAG, document.toString());
////							
////						} catch (IOException e) {
////							e.printStackTrace();
////						}
////					}
//					imageSrc="http://www.tnt-supermarket.com/data/weekly_flyers/tnt_weekly_flyer_image_5_big5.jpg";
//					superMarketList.get(m).setUpFlyers(1);
//					superMarketList.get(m).setImgSrc(0, imageSrc);
//					break;
//				case 4:
//					if (checkURLExists(urlArray[m])) {
//						try {
//							document = Jsoup.connect(urlArray[m]).get();
//							Elements imgElements = document.select("img[class=flyer]");
//							superMarketList.get(m).setUpFlyers(imgElements.size());
//							String imgName = imgElements.attr("src");
//							
//							imageSrc = "http://www.foodymart.com/"+imgName;
//							
//							superMarketList.get(m).setImgSrc(0, imageSrc);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//					break;
//				case 5:
//					break;
//				case 6:
//					if (checkURLExists(urlArray[m])) {
//						try {
//							document = Jsoup.connect(urlArray[m]).get();
//							Elements imgElements = document.select("img[class=flyer]");
//							superMarketList.get(m).setUpFlyers(imgElements.size());
//							String imgName = imgElements.attr("src");
//							
//							imageSrc = "http://www.foodymart.com/"+imgName;
//							
//							superMarketList.get(m).setImgSrc(0, imageSrc);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//					break;
//				case 7:
//					break;
//				case 8:
//					break;
//				case 9:
//					if (checkURLExists(urlArray[m])) {
//						try {
//							document = Jsoup.connect(urlArray[m]).get();
//							Elements imgElements = document.select("img[class=flyer]");
//							superMarketList.get(m).setUpFlyers(imgElements.size());
//							String imgName = imgElements.attr("src");
//							
//							imageSrc = "http://www.foodymart.com/"+imgName;
//							
//							superMarketList.get(m).setImgSrc(0, imageSrc);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//					break;
//				case 10:
//					decodeFromFlyerCenter(m);
//					break;
//				case 11:
//					break;
//				case 12:
//					if (checkURLExists(urlArray[m])) {
//						try {
//							document = Jsoup.connect(urlArray[m]).get();
//							Elements imgElements = document.select("div[class=img_frame img_size_fullwidth alignleft] a img[src]");
//							superMarketList.get(m).setUpFlyers(imgElements.size());
//							for(int i=0; i<imgElements.size(); i++){
//								String imgName = imgElements.get(i).attr("src");
//								superMarketList.get(m).setImgSrc(i, imgName);
//							}
////							String imgName = imgElements.attr("src");
////							
////							superMarketList.get(m).setImgSrc(0, imageSrc);
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//					break;
//				case 13:
//					break;
//				case 14:
//					break;
//				case 15:
//					break;
//				case 16:
//					break;
//				case 17:
//					break;
//				case 18:
//					break;
//				case 19:
//					break;
//				case 20:
//					break;
//				case 21:
//					break;
//				default:
//					break;
//				}
				count++;
				mProgressBar.setProgress(100/array.length*count);
			}
			
			if(errorCount == count){
				return false;
			}
			
			return true;
		}
		
		private void decodeImageFromImageURL(String imageURL, int m){
			//String resultString[] = new String[2];
			Document document;
			String imgName = "";
			
			if (checkURLExists(imageURL)) {
				try {
					document = Jsoup.connect(imageURL).get();
					Elements contentElements = document.select("div[id=all-page]");
					Elements imgElements = contentElements.select("img");
					superMarketList.get(m).setUpFlyers(imgElements.size());
					for(int i=0; i<imgElements.size(); i++){
						imgName = imgElements.get(i).attr("src");
//						if(imgName != null && (index = imgName.indexOf("_thumb")) != -1){
//							resultString[0] = imgName.substring(0, index) + imgName.substring(index+6, imgName.length());
//						}
						//Log.d(TAG, imgName);
						superMarketList.get(m).setImgSrc(i, imgName);
					}
//					String imgName = imgElements.attr("src");
//					
//					superMarketList.get(m).setImgSrc(0, imageSrc);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void decodeFromFlyerCenter(int m){
			String resultString[] = new String[2];
			Document document;
			int index = -1;
			Log.d(TAG, urlArray[m]);
			if (checkURLExists(urlArray[m])) {
				try {
					document = Jsoup.connect(urlArray[m]).get();
					Elements contentElements = document.select("div[class=block_content] ul[class=image_hlist]");
					Elements imgElements = contentElements.select("img");
					superMarketList.get(m).setUpFlyers(imgElements.size());
					for(int i=0; i<imgElements.size(); i++){
						String imgName = imgElements.get(i).attr("src");
						if(imgName != null && (index = imgName.indexOf("_thumb")) != -1){
							resultString[0] = imgName.substring(0, index) + imgName.substring(index+6, imgName.length());
						}
						Log.d(TAG, resultString[0]);
						superMarketList.get(m).setImgSrc(i, resultString[0]);
					}
//					String imgName = imgElements.attr("src");
//					
//					superMarketList.get(m).setImgSrc(0, imageSrc);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	//LIFECYCLE
	@Override
	protected void onResume() {
		super.onResume();
		
		if(mSuperMarketsAdapter == null){
			mSuperMarketsAdapter = new SuperMarketsAdapter(this, superMarketList);
			ccFlyersListView.setAdapter(mSuperMarketsAdapter);
			ccFlyersListView.invalidate();
		}
		
//		if(loadListFromFile()){
//			isUpdateAll = false;
//			fileIsExist = true;
//		}else{
//			isUpdateAll = true;
//			fileIsExist = false;
//		};
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if(isListUpdated){
			updateDataList();
			saveListToFile();
		}
	}
	
}
