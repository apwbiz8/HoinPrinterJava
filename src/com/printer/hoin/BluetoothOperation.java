package com.printer.hoin;

import java.lang.reflect.Method;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.android.print.sdk.PrinterConstants.Connect;
import com.android.print.sdk.PrinterInstance;

public class BluetoothOperation {
	private BluetoothAdapter adapter;
	private Context mContext;
	private boolean hasRegBoundReceiver;
	private boolean rePair;

	private BluetoothDevice mDevice;
	private String deviceAddress;
	private Handler mHandler;
	private PrinterInstance mPrinter;
	private boolean hasRegDisconnectReceiver;
	private IntentFilter filter;

	public BluetoothOperation(Context context, Handler handler) {
		try {
			adapter = BluetoothAdapter.getDefaultAdapter();
			mContext = context;
			mHandler = handler;
			hasRegDisconnectReceiver = false;
	
			filter = new IntentFilter();
	        //filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
	        //filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
	        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void open(Intent data) {
		try {
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			
			deviceAddress = data.getExtras().getString(BluetoothDeviceList.EXTRA_DEVICE_ADDRESS);
			mDevice = adapter.getRemoteDevice(deviceAddress);
	
			if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
				PairOrRePairDevice(false, mDevice);
			} else if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
				rePair = data.getExtras().getBoolean(BluetoothDeviceList.EXTRA_RE_PAIR);
				if (rePair) {
					PairOrRePairDevice(true, mDevice);
				} else {
					openPrinter();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	// use device to init printer.
	private void openPrinter() {
		try {
			mPrinter = new PrinterInstance(mContext, mDevice, mHandler);
			// default is gbk...
			// mPrinter.setEncoding("gbk");
			mPrinter.openConnection();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private boolean PairOrRePairDevice(boolean re_pair, BluetoothDevice device) {
		boolean success = false;
		try {
			if (!hasRegBoundReceiver) {
				mDevice = device;
				IntentFilter boundFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
				mContext.registerReceiver(boundDeviceReceiver, boundFilter);
				hasRegBoundReceiver = true;
			}

			if (re_pair) {
				// cancel bond
				Method removeBondMethod = BluetoothDevice.class.getMethod("removeBond");
				success = (Boolean) removeBondMethod.invoke(device);
			} else {
				// Input password
				// Method setPinMethod =
				// BluetoothDevice.class.getMethod("setPin");
				// setPinMethod.invoke(device, 1234);
				// create bond
				Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
				success = (Boolean) createBondMethod.invoke(device);
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	// receive bound broadcast to open connect.
	private BroadcastReceiver boundDeviceReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			try {
				String action = intent.getAction();
				if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					if (!mDevice.equals(device)) {
						return;
					}
					switch (device.getBondState()) {
					case BluetoothDevice.BOND_BONDING:
						break;
					case BluetoothDevice.BOND_BONDED:
						// if bound success, auto init BluetoothPrinter. open
						// connect.
						if (hasRegBoundReceiver) {
							mContext.unregisterReceiver(boundDeviceReceiver);
							hasRegBoundReceiver = false;
						}
						openPrinter();
						break;
					case BluetoothDevice.BOND_NONE:
						if (rePair) {
							rePair = false;
							PairOrRePairDevice(false, device);
						} else if (hasRegBoundReceiver) {
							mContext.unregisterReceiver(boundDeviceReceiver);
							hasRegBoundReceiver = false;
							// bond failed
							mHandler.obtainMessage(Connect.FAILED).sendToTarget();
						}
					default:
						break;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};

	public void close() {
		if (mPrinter != null) {
			mPrinter.closeConnection();
			mPrinter = null;
		}
		if(hasRegDisconnectReceiver){
			mContext.unregisterReceiver(myReceiver);
			hasRegDisconnectReceiver = false;
		}
	}

	public PrinterInstance getPrinter() {
		if (mPrinter != null && mPrinter.isConnected()) {
			if(!hasRegDisconnectReceiver){
				mContext.registerReceiver(myReceiver, filter);
				hasRegDisconnectReceiver = true;
			}
		}
		return mPrinter;
	}
	
	// receive the state change of the bluetooth.
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				String action = intent.getAction();
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	
				if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
					if (device != null && mPrinter != null && mPrinter.isConnected() && device.equals(mDevice)) {
						close();
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	};

	public boolean BluetoothEnableCheck() {
		if (!adapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			((Activity) mContext).startActivityForResult(enableIntent, BluetoothDeviceConnect.ENABLE_BT);
			return false;
		} 
		return true;
	}

	public void chooseDevice() {
		if (!adapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			((Activity) mContext).startActivityForResult(enableIntent, BluetoothDeviceConnect.ENABLE_BT);
		} else {
			Intent intent = new Intent(mContext, BluetoothDeviceList.class);
			((Activity) mContext).startActivityForResult(intent, BluetoothDeviceConnect.CONNECT_DEVICE);
		}
	}
}
