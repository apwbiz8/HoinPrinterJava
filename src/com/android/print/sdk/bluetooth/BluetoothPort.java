//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.android.print.sdk.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Build.VERSION;
import android.util.Log;
import com.android.print.sdk.IPrinterPort;
import com.android.print.sdk.util.DefaultUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothPort implements IPrinterPort {
    //private static final String TAG = "BluetoothPort";
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private BluetoothAdapter mAdapter;
    private BluetoothPort.ConnectThread mConnectThread;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Context mContext;
    private Handler mHandler;
    private int mState;
    private int readLen;
    private final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BroadcastReceiver boundDeviceReceiver;

	public BluetoothPort(BluetoothDevice device, Handler handler) {
        this.mHandler = handler;
        this.mDevice = device;
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mState = 103;
    }

    @SuppressWarnings("unused")
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(action)) {
                BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (!BluetoothPort.this.mDevice.equals(device)) {
                    return;
                }

                switch(device.getBondState()) {
                case 10:
                    BluetoothPort.this.mContext.unregisterReceiver(BluetoothPort.this.boundDeviceReceiver);
                    BluetoothPort.this.setState(102);
                    DefaultUtils.Log("BluetoothPort", "bound cancel");
                    break;
                case 11:
                    DefaultUtils.Log("BluetoothPort", "bounding......");
                    break;
                case 12:
                    DefaultUtils.Log("BluetoothPort", "bound success");
                    BluetoothPort.this.mContext.unregisterReceiver(BluetoothPort.this.boundDeviceReceiver);
                    BluetoothPort.this.PairOrConnect(false);
                }
            }

        }

    };

    public void open() {
        DefaultUtils.Log("BluetoothPort", "connect to: " + this.mDevice.getName());
        if (this.mState != 103) {
            this.close();
        }

        if (this.mDevice.getBondState() == 10) {
            Log.i("BluetoothPort", "device.getBondState() is BluetoothDevice.BOND_NONE");
            this.PairOrConnect(true);
        } else if (this.mDevice.getBondState() == 12) {
            this.PairOrConnect(false);
        }

    }

    private void PairOrConnect(boolean pair) {
        if (pair) {
            IntentFilter boundFilter = new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
            this.mContext.registerReceiver(this.boundDeviceReceiver, boundFilter);
            boolean success = false;

            try {
                Method createBondMethod = BluetoothPort.class.getMethod("createBond");
                success = (Boolean)createBondMethod.invoke(this.mDevice);
            } catch (IllegalAccessException var5) {
                var5.printStackTrace();
            } catch (IllegalArgumentException var6) {
                var6.printStackTrace();
            } catch (InvocationTargetException var7) {
                var7.printStackTrace();
            } catch (NoSuchMethodException var8) {
                var8.printStackTrace();
            }

            Log.i("BluetoothPort", "createBond is success? : " + success);
        } else {
            this.mConnectThread = new ConnectThread();
            this.mConnectThread.start();
        }

    }

    @TargetApi(10)
    private boolean ReTryConnect() {
        DefaultUtils.Log("BluetoothPort", "android SDK version is:" + VERSION.SDK_INT);

        try {
            if (VERSION.SDK_INT >= 10) {
                this.mSocket = this.mDevice.createInsecureRfcommSocketToServiceRecord(this.PRINTER_UUID);
            } else {
                Method method = this.mDevice.getClass().getMethod("createRfcommSocket", Integer.TYPE);
                this.mSocket = (BluetoothSocket)method.invoke(this.mDevice, 1);
            }

            this.mSocket.connect();
            return false;
        } catch (Exception var2) {
            DefaultUtils.Log("BluetoothPort", "connect failed:");
            var2.printStackTrace();
            return true;
        }
    }

    public void close() {
        DefaultUtils.Log("BluetoothPort", "close()");

        try {
            if (this.mSocket != null) {
                this.mSocket.close();
            }
        } catch (IOException var2) {
            DefaultUtils.Log("BluetoothPort", "close socket failed");
            var2.printStackTrace();
        }

        this.mConnectThread = null;
        this.mDevice = null;
        this.mSocket = null;
        if (this.mState != 102) {
            this.setState(103);
        }

    }

    public int write(byte[] data) {
        try {
            if (this.outputStream != null) {
                this.outputStream.write(data);
                this.outputStream.flush();
                return 0;
            } else {
                return -1;
            }
        } catch (IOException var3) {
            DefaultUtils.Log("BluetoothPort", "write error.");
            var3.printStackTrace();
            return -1;
        }
    }

    public byte[] read() {
        byte[] readBuff = null;

        try {
            if (this.inputStream != null && (this.readLen = this.inputStream.available()) > 0) {
                readBuff = new byte[this.readLen];
                this.inputStream.read(readBuff);
            }
        } catch (IOException var3) {
            DefaultUtils.Log("BluetoothPort", "read error");
            var3.printStackTrace();
        }

        Log.w("BluetoothPort", "read length:" + this.readLen);
        return readBuff;
    }

    public synchronized byte[] read(int timeout) {
        byte[] receiveBytes = null;

        try {
            while((this.readLen = this.inputStream.available()) <= 0) {
                timeout -= 50;
                if (timeout <= 0) {
                    break;
                }

                try {
                    Thread.sleep(50L);
                } catch (InterruptedException var4) {
                    var4.printStackTrace();
                }
            }

            if (this.readLen > 0) {
                receiveBytes = new byte[this.readLen];
                this.inputStream.read(receiveBytes);
            }
        } catch (IOException var5) {
            DefaultUtils.Log("BluetoothPort", "read error1");
            var5.printStackTrace();
        }

        return receiveBytes;
    }

    private synchronized void setState(int state) {
        DefaultUtils.Log("BluetoothPort", "setState() " + this.mState + " -> " + state);
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
            boolean hasError = false;
            BluetoothPort.this.mAdapter.cancelDiscovery();

            try {
                BluetoothPort.this.mSocket = BluetoothPort.this.mDevice.createRfcommSocketToServiceRecord(BluetoothPort.this.PRINTER_UUID);
                BluetoothPort.this.mSocket.connect();
            } catch (IOException var5) {
                DefaultUtils.Log("BluetoothPort", "ConnectThread failed. retry.");
                var5.printStackTrace();
                hasError = BluetoothPort.this.ReTryConnect();
            }

            synchronized(this) {
                BluetoothPort.this.mConnectThread = null;
            }

            if (!hasError) {
                try {
                    BluetoothPort.this.inputStream = BluetoothPort.this.mSocket.getInputStream();
                    BluetoothPort.this.outputStream = BluetoothPort.this.mSocket.getOutputStream();
                } catch (IOException var3) {
                    hasError = true;
                    DefaultUtils.Log("BluetoothPort", "Get Stream failed");
                    var3.printStackTrace();
                }
            }

            if (hasError) {
                BluetoothPort.this.setState(102);
                BluetoothPort.this.close();
            } else {
                BluetoothPort.this.setState(101);
            }

        }
    }
}
