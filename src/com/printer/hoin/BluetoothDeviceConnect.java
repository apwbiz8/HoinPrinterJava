package com.printer.hoin;

import com.android.print.sdk.PrinterInstance;

import com.android.print.sdk.PrinterConstants.Connect;
import com.android.print.sdk.util.OtherUtils;
import com.android.print.sdk.util.DefaultUtils;
import com.printer.hoin.utils.JsonDataToImage;
import com.printer.hoin.utils.PrintUtils;
import android.app.ActionBar.LayoutParams;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class BluetoothDeviceConnect extends Activity {
	
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String EXTRA_RE_PAIR = "re_pair";
    

    private Button connectButton;
    //private Button printButton;
    private ImageView ticketImage;
	private static BluetoothOperation myOpertion;
	private static PrinterInstance mPrinter;
	private RelativeLayout relativelayout;
	public static boolean isConnected = false;
	
	public static String json_data = "";
	public static String printer_index = "2";
	
	public static final int CONNECT_DEVICE = 1;
    public static final int ENABLE_BT = 2;
    //private int width = 1024;
    private int height = 480;
    private ProgressDialog dialog;
    private static JsonDataToImage jsonToimage;
    private static Bitmap ticketBitmap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        getDeviceSize();

        relativelayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(
	        LayoutParams.MATCH_PARENT,
	        LayoutParams.MATCH_PARENT
        );
        relativelayout.setLayoutParams(layoutparams);        
        relativelayout.setBackgroundColor(Color.parseColor("#FFFFFF"));    
        
        json_data = getIntent().getStringExtra("JSON_DATA");
        
        jsonToimage = new JsonDataToImage();
        jsonToimage.getJsonData(json_data);
        //Toast.makeText(getBaseContext(), json_data, Toast.LENGTH_SHORT).show();
        
        if(Integer.parseInt(printer_index) == 1)
        	ticketBitmap = jsonToimage.DrawDataToImage(380);
        else
        	ticketBitmap = jsonToimage.DrawDataToImage(384);
        
        OtherUtils.decodeBitmap(OtherUtils.scalingBitmap(ticketBitmap, 384), 255);
		
        showComponent();
        
        setContentView(relativelayout);

    }
    
    public static void DirectlyPrint() {
    	jsonToimage = new JsonDataToImage();
        jsonToimage.getJsonData(json_data);
        
        if(Integer.parseInt(printer_index) == 1)
        	ticketBitmap = jsonToimage.DrawDataToImage(380);
        else
        	ticketBitmap = jsonToimage.DrawDataToImage(384);
        
		mPrinter = myOpertion.getPrinter();
		PrintUtils.printImage(ticketBitmap, mPrinter, Integer.parseInt(printer_index));
    }

    public static boolean ConnectBluetooth() {
    	if(!myOpertion.BluetoothEnableCheck())
    	{
    		isConnected = false;
    		return false;
    	}
    	return true;
    }
    private void getDeviceSize() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		//width = size.x;
		height = size.y;
    }
    
    private void showComponent() {
    	LinearLayout Lin0 = new LinearLayout(this);
        Lin0.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        Lin0.setOrientation(LinearLayout.VERTICAL);
        
        relativelayout.addView(Lin0);
        
        LinearLayout Lin2 = new LinearLayout(this);
        Lin2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height * 1 / 2));
        Lin2.setOrientation(LinearLayout.VERTICAL);
        Lin2.setGravity(Gravity.CENTER);
        Lin0.addView(Lin2);
        
        
        LinearLayout.LayoutParams LayoutParamsTicket = new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        );
        ticketImage = new ImageView(this);
        ticketImage.setLayoutParams(LayoutParamsTicket);
        ticketImage.setBackgroundColor(Color.WHITE);
        ticketImage.setImageBitmap(ticketBitmap);
        Lin2.addView(ticketImage);
        
        
        LinearLayout Lin11 = new LinearLayout(this);
        Lin11.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Lin11.setOrientation(LinearLayout.VERTICAL);
        Lin11.setGravity(Gravity.BOTTOM);
        Lin0.addView(Lin11);
        
        LinearLayout Lin1 = new LinearLayout(this);
        Lin1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height / 2));
        Lin1.setOrientation(LinearLayout.VERTICAL);
        Lin1.setGravity(Gravity.CENTER);
        Lin0.addView(Lin1);
        
        
        LinearLayout.LayoutParams LayoutParamsButtons = new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        );
        
        connectButton = new Button(this);
        connectButton.setLayoutParams(LayoutParamsButtons);
        connectButton.setGravity(Gravity.CENTER);
        connectButton.setText("Connect Printer");
        connectButton.setHeight(150);
        connectButton.setTextColor(Color.WHITE);
        connectButton.setBackgroundColor(Color.parseColor("#4d7b47"));
        connectButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	openConn();
            }
        });
        
        Lin1.addView(connectButton);
        /*
        LinearLayout Lin2 = new LinearLayout(this);
        Lin2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height / 4));
        Lin2.setOrientation(LinearLayout.VERTICAL);
        Lin2.setGravity(Gravity.CENTER);
        Lin0.addView(Lin2);
        
        
        LinearLayout.LayoutParams LayoutParamsPrint = new LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        );
        
        printButton = new Button(this);
        printButton.setLayoutParams(LayoutParamsPrint);
        printButton.setGravity(Gravity.CENTER);
        printButton.setText("Print");
        printButton.setWidth(width - 100);
        printButton.setHeight(150);
        //printButton.setTextColor(Color.parseColor("#bbb7b7"));
        //printButton.setBackgroundColor(Color.parseColor("#888686"));
        //printButton.setEnabled(false);
        printButton.setTextColor(Color.WHITE);
        printButton.setBackgroundColor(Color.parseColor("#24569a"));
        printButton.setEnabled(true);

        printButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	printFunction();
            }
        });
        
        Lin2.addView(printButton);
        
        LinearLayout Lin22 = new LinearLayout(this);
        Lin22.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height / 4));
        Lin22.setOrientation(LinearLayout.VERTICAL);
        Lin22.setGravity(Gravity.CENTER);
        Lin0.addView(Lin22);
        */
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle("Connecting...");
		dialog.setMessage("Please Wait...");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

    }
    
    private void openConn(){
    	if (!isConnected) {
	    	myOpertion = new BluetoothOperation(BluetoothDeviceConnect.this, mHandler);
	    	myOpertion.chooseDevice();
	    } else {
			myOpertion.close();
			myOpertion = null;
			mPrinter = null;
			isConnected = false;
		}
    }
    
    public static void CloseConn() {
    	myOpertion.close();
		myOpertion = null;
		mPrinter = null;
		isConnected = false;
    }

    @Override
	protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
    	switch (requestCode) {
	        case CONNECT_DEVICE:			
				if (resultCode == Activity.RESULT_OK) { 
					dialog.show();
					new Thread(new Runnable(){ 
						public void run() {  
							myOpertion.open(data); 
						} 
					}).start(); 
					
				}
			 
	        	break;
	        case ENABLE_BT:
	            if (resultCode == Activity.RESULT_OK){
	            	myOpertion.chooseDevice();
	            }else{
	            	Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_SHORT).show();
	            }
	            break;
        }
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				switch (msg.what) {
					case Connect.SUCCESS:
						isConnected = true;
						mPrinter = myOpertion.getPrinter();
						Toast.makeText(getBaseContext(), "Success connected device.", Toast.LENGTH_SHORT).show();
						PrintUtils.printImage(ticketBitmap, mPrinter, Integer.parseInt(printer_index));
						
						AsyncTaskExample asyncTask=new AsyncTaskExample();
			            asyncTask.execute("close");
			            
						break;
					case Connect.FAILED:
						isConnected = false;
						Toast.makeText(getBaseContext(), "Connect failed...", Toast.LENGTH_SHORT).show();
						break;
					case Connect.CLOSED:
						isConnected = false;
						Toast.makeText(getBaseContext(), "Connect close...", Toast.LENGTH_SHORT).show();
						break;
					default:
						break;
				}
				
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
			}
			catch (Exception e) {
                e.printStackTrace();
            } 
		}
	};

	
	private class AsyncTaskExample extends AsyncTask<String, String, Integer> {
      @Override
      protected void onPreExecute() {
         super.onPreExecute();
         
      }
      @Override
      protected Integer doInBackground(String... strings) {
    	  int sleepCnt = 0;
    	  while(true)
    	  {
    		  try {
    			  Thread.sleep(50);
    		  } catch (InterruptedException e) {
    			  e.printStackTrace();
    		  }
    		  if(sleepCnt == 60)
    		  {
    			  //myOpertion.close();
    			  //myOpertion = null;
    			  //mPrinter = null;
    			  finish();
    			  break;
    		  }
    		  sleepCnt++;
    	  }
    	  return sleepCnt;
      }
      @Override
      protected void onPostExecute(Integer value) {
         super.onPostExecute(value);
      }
	}
	
}
