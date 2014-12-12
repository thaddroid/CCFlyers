package com.thaddroid.apps.ccflyers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

public class flyersActivity extends Activity {
	
	private ProgressDialog pd;
	
	private Intent intent = new Intent();
	private Bundle bundle = new Bundle();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_PROGRESS);
	    this.setProgressBarVisibility(true);
		setContentView(R.layout.layout_flyers);
		
		intent = this.getIntent();
		bundle = intent.getExtras();
		
		final String name = bundle.getString("market_name");
		//String source = bundle.getString("source_url");
		String imgSrc = bundle.getString("image_src");
		
		WebView webView = (WebView)findViewById(R.id.webview);
		webView.getSettings().setLoadWithOverviewMode(true);
	    webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setDisplayZoomControls(false);
		
		
		webView.setBackgroundColor(0);
		//webView.loadDataWithBaseURL("","<img src='"+imgSrc+"'/>","text/html", "UTF-8", "");
		
		//webView.loadData(imgSrc, "text/html", "UTF-8");
		webView.setWebChromeClient(new WebChromeClient() {
		    public void onProgressChanged(WebView view, int progress) {
		    	flyersActivity.this.setTitle("Loading...");
		        flyersActivity.this.setProgress(progress * 100);
		        if(progress == 100){
		        	flyersActivity.this.setTitle(name);
              	}
		    }
		});
		
		if(imgSrc.equals("<p></p>")){
			Toast.makeText(flyersActivity.this, "請連結網絡以下載圖片！！", Toast.LENGTH_SHORT).show();
		}
		
		webView.loadDataWithBaseURL("",imgSrc,"text/html", "UTF-8", "");
		//tiv = new TouchImageView(flyersActivity.this);
		
		//pd = ProgressDialog.show(flyersActivity.this, name, "Downloading Latest Flyers", true, false);
		//pf.execute(url);
		
		//tiv.setImageBitmap(bitmap);
	}
	
}
