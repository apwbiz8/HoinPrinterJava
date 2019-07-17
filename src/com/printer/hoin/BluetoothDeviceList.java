package com.printer.hoin;

import java.util.ArrayList;
import java.util.Set;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class BluetoothDeviceList extends Activity {
	
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String EXTRA_RE_PAIR = "re_pair";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayList<String> mPairedDevicesArrayAdapter;
    private Button scanButton;
	private RelativeLayout relativelayout;
	private LinearLayout LayoutDevices;
	private ArrayList<Button> buttonArray;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
          
        mPairedDevicesArrayAdapter = new ArrayList<String>();
        buttonArray = new ArrayList<Button>();

        relativelayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(
	        LayoutParams.MATCH_PARENT,
	        LayoutParams.MATCH_PARENT
        );
        relativelayout.setLayoutParams(layoutparams);        
        relativelayout.setBackgroundColor(Color.parseColor("#c7d4f2"));    
        
        showComponent();
        
        setContentView(relativelayout);

        setResult(Activity.RESULT_CANCELED);
    }
    
    private void showComponent() {
    	LinearLayout Lin0 = new LinearLayout(this);
        Lin0.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        Lin0.setOrientation(LinearLayout.VERTICAL);
        
        relativelayout.addView(Lin0);
        
        LinearLayout Lin1 = new LinearLayout(this);
        Lin1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Lin1.setOrientation(LinearLayout.VERTICAL);
        Lin1.setGravity(Gravity.BOTTOM);
        Lin0.addView(Lin1);
        
        
        LinearLayout.LayoutParams LayoutParamsButtons = new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        );
        
        scanButton = new Button(this);
        scanButton.setLayoutParams(LayoutParamsButtons);
        scanButton.setGravity(Gravity.CENTER);
        scanButton.setText("Scan for new devices");
        scanButton.setTextColor(Color.WHITE);
        scanButton.setBackgroundColor(Color.parseColor("#4d7b47"));
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                scanButton.setTextColor(Color.parseColor("#bbb7b7"));
                scanButton.setBackgroundColor(Color.parseColor("#888686"));
                scanButton.setEnabled(false);
            }
        });
        
        Lin1.addView(scanButton);
        
        
        LinearLayout Lin2 = new LinearLayout(this);
        Lin2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        Lin2.setOrientation(LinearLayout.VERTICAL);
        Lin0.addView(Lin2);
        
        LinearLayout.LayoutParams LaylayoutScrollview = new LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        );
        
        ScrollView scroll = new ScrollView(getBaseContext());
        scroll.setBackgroundColor(Color.parseColor("#66ffff"));
        scroll.setLayoutParams(LaylayoutScrollview);        
       
        Lin2.addView(scroll);
      
        
        LayoutDevices = new LinearLayout(this);
        LayoutDevices.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        LayoutDevices.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(LayoutDevices);
        
        try {
        	mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        	// Get a set of currently paired devices
            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
            // If there are paired devices, add each one to the ArrayAdapter
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                	mPairedDevicesArrayAdapter.add(device.getName()
                			+ " ( paired )"
                			+ "\n" + device.getAddress());
                }
            }
        }
        catch(Exception e) {
        	Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        
        
        for(int i = 0; i < mPairedDevicesArrayAdapter.size(); i++){
        	
			LayoutDevices.addView(setButton(mPairedDevicesArrayAdapter.get(i)));
        }

    }
    private Button setButton(String str) {
		LinearLayout.LayoutParams LayoutparamBtn = new LinearLayout.LayoutParams(
		LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );

		Button btn = new Button(this); 
		  
    	btn.setLayoutParams(LayoutparamBtn);
    	btn.setGravity(Gravity.LEFT);
    	btn.setText(str);
    	btn.setBackgroundColor(Color.parseColor("#dcdad9"));
    	btn.setBottom(3);
    	btn.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
		    	String info = ((Button) v).getText().toString();
		    	if(info.length() > 17)
		    	{
			    	String address = info.substring(info.length() - 17);
			    	returnToPreviousActivity(address, false);
		    	}
		    }
		});
    	
		buttonArray.add(btn);

		return btn;
    }
    
    private void returnToPreviousActivity(final String address, boolean re_pair)
    {
    	// Cancel discovery because it's costly and we're about to connect
    	if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Create the result Intent and include the MAC address
        Intent intent = new Intent(this, BluetoothDeviceConnect.class);
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        intent.putExtra(EXTRA_RE_PAIR, re_pair);

        // Set result and finish this Activity
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    
    @Override
	protected void onStop() {
		// Make sure we're not doing discovery anymore
    	try {
	        if (mBtAdapter != null && mBtAdapter.isDiscovering()) {
	            mBtAdapter.cancelDiscovery();
	        }
    	}
        catch(Exception e) {
        	Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
		// Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
        super.onStop();
	}

	@Override
	protected void onResume() {
		// Register for broadcasts when a device is discovered and discovery has finished
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        super.onResume();
	}

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle("Scanning for devices...");

        LayoutDevices.removeAllViewsInLayout();
        mPairedDevicesArrayAdapter.clear();
        buttonArray.clear();
        
        // If we're already discovering, stop it
        try {
	        
        	if (mBtAdapter.isDiscovering()) {
	            mBtAdapter.cancelDiscovery();
	        }
            
            mBtAdapter.startDiscovery();

        }
        catch(Exception e) {
        	Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

	// The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            try {
            // When discovery finds a device
	            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	                // Get the BluetoothDevice object from the Intent
	                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	                // If it's already paired, skip it, because it's been listed already
	                String state = "";
	                if(device.getBondState() == BluetoothDevice.BOND_BONDED)
	                	state = "paired";
	                else
	                	state = "not paired";
	                String itemName = device.getName()
	            			+ " ( " + state +" )"
	            			+ "\n" + device.getAddress();
	
	                mPairedDevicesArrayAdapter.remove(itemName);
	            	mPairedDevicesArrayAdapter.add(itemName);
	            	
	            // When discovery is finished, change the Activity title
	            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
	                setProgressBarIndeterminateVisibility(false);
	                setTitle("Please select a device to connect");
	                if (mPairedDevicesArrayAdapter.size() == 0) {
	                    String noDevices = "No devices found";
	                    mPairedDevicesArrayAdapter.add(noDevices);                    
	                }
	                scanButton.setTextColor(Color.WHITE);
	                scanButton.setBackgroundColor(Color.parseColor("#4d7b47"));
	                scanButton.setEnabled(true);
	            }
            }
            catch(Exception e) {
            	e.getStackTrace();
            }
            
            for(int i = 0; i < mPairedDevicesArrayAdapter.size(); i++)
            {
                for(Button btn : buttonArray)
                {
                	if(btn.getText() == mPairedDevicesArrayAdapter.get(i))
    	                LayoutDevices.removeView(btn);
                }
            	LayoutDevices.addView(setButton(mPairedDevicesArrayAdapter.get(i)));
            }
        }
    };

}
