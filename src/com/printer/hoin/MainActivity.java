package com.printer.hoin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        String json_data = "LUCKY 12@@" + "[{\"12KD4\":40,\"12KD5\":50,\"Barcode\":\"1579165174566\","
        		+ "\"TotalAmount\":\"150\",\"RetailerID\":\"LKKP0031\",\"DrawTime\":\"14:32\","
        		+ "\"TicketTime\":\"01/16/2020 02:29:34:pm\",\"12KD1\":10,\"12KD11\":10,\"12KD12\":10,"
				+ "\"12KD13\":10,\"12KD14\":10,\"12KD15\":10,\"12KD16\":10,\"12KD17\":10,\"12KD18\":10,\"12KD19\":10,\"12KD20\":10,"
				+ "\"10KD2\":20,\"10KD11\":30}]";
        Intent intent = new Intent(this, com.printer.hoin.BluetoothDeviceConnect.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("JSON_DATA", json_data);
        startActivity(intent);
        
        finish();
    }
}
