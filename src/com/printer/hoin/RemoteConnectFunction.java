package com.printer.hoin;

import android.content.Context;
import android.content.Intent;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREInvalidObjectException;
import com.adobe.fre.FREObject;
import com.adobe.fre.FRETypeMismatchException;
import com.adobe.fre.FREWrongThreadException;
 
public class RemoteConnectFunction implements FREFunction {
 
	//private BluetoothDevicesList deviceListClass;
	private Context context;
	
    @Override
    public FREObject call(FREContext ctx, FREObject[] args) {

    	context = ctx.getActivity();
    	
    	try {
    		
    		String data = args[0].getAsString();          	
    		String[] str_split = data.split("@@");
    		
    		String json_data = str_split[0] + "@@" + str_split[1];
    		com.printer.hoin.BluetoothDeviceConnect.printer_index = str_split[2];
    		
    		if(str_split[0].compareTo("closeGame") == 0)
    		{
    			com.printer.hoin.BluetoothDeviceConnect.CloseConn();
    		}
    		else
    		{
	    		if(com.printer.hoin.BluetoothDeviceConnect.isConnected)
	    		{
	    			if(com.printer.hoin.BluetoothDeviceConnect.ConnectBluetooth())
	    			{
		    			com.printer.hoin.BluetoothDeviceConnect.json_data = json_data;		    			
		    			com.printer.hoin.BluetoothDeviceConnect.DirectlyPrint();
	    			}
	    			else
	    			{
	    				Intent intent = new Intent(context, com.printer.hoin.BluetoothDeviceConnect.class);
			            
			            intent.setAction(Intent.ACTION_VIEW);
			            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			            intent.putExtra("JSON_DATA", json_data);
			            context.startActivity(intent);
	    			}
	    		}
	    		else
	    		{
		    		Intent intent = new Intent(context, com.printer.hoin.BluetoothDeviceConnect.class);
		            
		            intent.setAction(Intent.ACTION_VIEW);
		            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            intent.putExtra("JSON_DATA", json_data);
		            context.startActivity(intent);
	    		}
    		}
            
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (FRETypeMismatchException e) {
            e.printStackTrace();
        } catch (FREInvalidObjectException e) {
            e.printStackTrace();
        } catch (FREWrongThreadException e) {
            e.printStackTrace();
        }
        
        
        return null;
    }
    

}
