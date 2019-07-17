//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.android.print.sdk.usb;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;
import com.android.print.sdk.IPrinterPort;
import com.android.print.sdk.util.DefaultUtils;

@TargetApi(12)
public class USBPort implements IPrinterPort {
    private static final String TAG = "USBPrinter";
    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private UsbEndpoint inEndpoint;
    private UsbEndpoint outEndpoint;
    private boolean isOldUSB;
    private Handler mHandler;
    private int mState;
    private Context mContext;
    private static final String ACTION_USB_PERMISSION = "com.android.usb.USB_PERMISSION";
    private USBPort.ConnectThread mConnectThread;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.w("USBPrinter", "receiver action: " + action);
            if ("com.android.usb.USB_PERMISSION".equals(action)) {
                synchronized(this) {
                    USBPort.this.mContext.unregisterReceiver(USBPort.this.mUsbReceiver);
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra("device");
                    if (intent.getBooleanExtra("permission", false) && USBPort.this.mUsbDevice.equals(device)) {
                        USBPort.this.connect();
                    } else {
                        USBPort.this.setState(102);
                        Log.e("USBPrinter", "permission denied for device " + device);
                    }
                }
            }

        }
    };

    public USBPort(Context context, UsbDevice usbDevice, Handler handler) {
        this.mContext = context;
        this.mUsbManager = (UsbManager)this.mContext.getSystemService("usb");
        this.mUsbDevice = usbDevice;
        this.mHandler = handler;
        this.mState = 103;
    }

    public void open() {
        Log.d("USBPrinter", "connect to: " + this.mUsbDevice.getDeviceName());
        if (this.mState != 103) {
            this.close();
        }

        if (isUsbPrinter(this.mUsbDevice)) {
            if (this.mUsbManager.hasPermission(this.mUsbDevice)) {
                this.connect();
            } else {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.android.usb.USB_PERMISSION"), 0);
                IntentFilter filter = new IntentFilter("com.android.usb.USB_PERMISSION");
                this.mContext.registerReceiver(this.mUsbReceiver, filter);
                this.mUsbManager.requestPermission(this.mUsbDevice, pendingIntent);
            }
        } else {
            this.setState(102);
        }

    }

    private void connect() {
        this.mConnectThread = new ConnectThread();
        this.mConnectThread.start();
    }

    public void close() {
        DefaultUtils.Log("USBPrinter", "close()");
        if (this.connection != null) {
            this.connection.releaseInterface(this.usbInterface);
            this.connection.close();
            this.connection = null;
        }

        this.mConnectThread = null;
        if (this.mState != 102) {
            this.setState(103);
        }

    }

    public int write(byte[] data) {
        return this.connection != null ? this.connection.bulkTransfer(this.outEndpoint, data, data.length, 3000) : -1;
    }

    public byte[] read() {
        if (this.connection != null) {
            byte[] retData = new byte[64];
            int readLen = this.connection.bulkTransfer(this.inEndpoint, retData, retData.length, 3000);
            Log.w("USBPrinter", "read length:" + readLen);
            if (readLen > 0) {
                if (readLen == 64) {
                    return retData;
                }

                byte[] realData = new byte[readLen];
                System.arraycopy(retData, 0, realData, 0, readLen);
                return realData;
            }
        }

        return null;
    }

    public boolean isOldUSB() {
        return this.isOldUSB;
    }

    public static boolean isUsbPrinter(UsbDevice device) {
        int vendorId = device.getVendorId();
        int productId = device.getProductId();
        DefaultUtils.Log("USBPrinter", "device name: " + device.getDeviceName());
        DefaultUtils.Log("USBPrinter", "vid:" + vendorId + " pid:" + productId);
        return 1155 == vendorId && 22304 == productId || 1659 == vendorId && 8965 == productId;
    }

    private synchronized void setState(int state) {
        DefaultUtils.Log("USBPrinter", "setState() " + this.mState + " -> " + state);
        if (this.mState != state) {
            this.mState = state;
            if (this.mHandler != null) {
                this.mHandler.obtainMessage(this.mState).sendToTarget();
            }
        }

    }

    public int getState() {
        return this.mState;
    }

    private class ConnectThread extends Thread {
        private ConnectThread() {
        }

        public void run() {
            boolean hasError = true;
            if (USBPort.this.mUsbManager.hasPermission(USBPort.this.mUsbDevice)) {
                USBPort.this.isOldUSB = 1659 == USBPort.this.mUsbDevice.getVendorId() && 8965 == USBPort.this.mUsbDevice.getProductId();

                try {
                    USBPort.this.usbInterface = USBPort.this.mUsbDevice.getInterface(0);

                    for(int i = 0; i < USBPort.this.usbInterface.getEndpointCount(); ++i) {
                        UsbEndpoint ep = USBPort.this.usbInterface.getEndpoint(i);
                        if (ep.getType() == 2) {
                            if (ep.getDirection() == 0) {
                                USBPort.this.outEndpoint = ep;
                            } else {
                                USBPort.this.inEndpoint = ep;
                            }
                        }
                    }

                    USBPort.this.connection = USBPort.this.mUsbManager.openDevice(USBPort.this.mUsbDevice);
                    if (USBPort.this.connection != null && USBPort.this.connection.claimInterface(USBPort.this.usbInterface, true)) {
                        hasError = false;
                    }
                } catch (Exception var5) {
                    var5.printStackTrace();
                }
            }

            synchronized(this) {
                USBPort.this.mConnectThread = null;
            }

            if (hasError) {
                USBPort.this.setState(102);
                USBPort.this.close();
            } else {
                USBPort.this.setState(101);
            }

        }
    }
}
