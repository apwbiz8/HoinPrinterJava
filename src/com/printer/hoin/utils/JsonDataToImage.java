package com.printer.hoin.utils;

import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class JsonDataToImage {
	
	public String[][] headerArray;
	public String[][] dataArray;
	public String heading;
	
	public JsonDataToImage() {
		
	}
	
	public void getJsonData(String jsonData) {
		String[] recv_datas = jsonData.split("@@");
		heading = recv_datas[0];
		String bodyData = recv_datas[1];
		
		String str = bodyData.replace("[{", "");
		str = str.replace("}]", "");
		
		String[] s1 = str.split(",");
		Arrays.sort(s1); 
		
		headerArray = new String[5][2];
		dataArray = new String[s1.length - 5][2];
		
		int ii = 0;
		int kk = 0;
		
		for(int i = 0; i < s1.length; i++)
		{
			String paten = "\":";
			String[] s2 = s1[i].split(paten);
			boolean is_header = false;
			int jj = 0;
			int nn = 0;
			for(int j = 0; j < s2.length; j++)
			{
				String data_str = s2[j].replace("\"", "");
				if(data_str.compareTo("TicketTime") == 0)
				{
					is_header = true;
				}
				else if(data_str.compareTo("RetailerID") == 0)
				{
					is_header = true;
				}
				else if(data_str.compareTo("Barcode") == 0)
				{
					is_header = true;
				}
				else if(data_str.compareTo("DrawTime") == 0)
				{
					is_header = true;		
				}
				else if(data_str.compareTo("TotalAmount") == 0)
				{
					is_header = true;
				}
				
				if(is_header)
				{
					headerArray[kk][nn] = data_str;
					if(nn == 1)
						kk ++;
					nn ++;
				}
				else
				{
					dataArray[ii][jj] = data_str;
					if(jj == 1)
						ii ++;
					jj ++;
				}
			}
			
			
		}
		
		//DrawDataToImage();
	}
	
    public Bitmap DrawDataToImage(int width) {

        int height = 215 + 32 * (dataArray.length + 2);
        Bitmap bmOverlay = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawARGB(255, 255, 255, 255);
        //Draw Header
        canvas.drawText(heading, 190, 30, getHeaderPaint());

        String total = "";
        for(int i = 0; i < headerArray.length; i++)
        {
            if(headerArray[i][0].compareTo("TicketTime") == 0)
            {
                //Draw Ticket Time
                //canvas.drawText("Ticket Time:", 17, 56, getLeftPaint());
                //canvas.drawText(headerArray[i][1], 125, 56, getLeftPaint());
                canvas.drawText("Ticket Time: " + headerArray[i][1], 180, 65, getCenterPaint());
            }
            else if(headerArray[i][0].compareTo("RetailerID") == 0)
            {
                //Draw Retailer ID
                //canvas.drawText("Retailer ID:", 32, 80, getLeftPaint());
                //canvas.drawText(headerArray[i][1], 125, 80, getLeftPaint());
                canvas.drawText("Retailer ID: " + headerArray[i][1], 190, 90, getCenterPaint());
            }
            else if(headerArray[i][0].compareTo("Barcode") == 0)
            {
                drawBarcode(canvas, headerArray[i][1], 380, 50, 70, 100);
                //Barcode
                canvas.drawText(headerArray[i][1], 190, 170, getCenterPaint());
            }
            else if(headerArray[i][0].compareTo("DrawTime") == 0)
            {
                //DrawTime
                //canvas.drawText(headerArray[i][1], 160, 172, getCenterPaint());
                canvas.drawText("Draw Time: " + headerArray[i][1], 190, 192, getCenterPaint());
            }
            else if(headerArray[i][0].compareTo("TotalAmount") == 0)
            {
                total = headerArray[i][1];
            }
        }

        canvas.drawLine(10, 200, 370, 200, getLinePaint());
        canvas.drawText("Offer Code", 70, 228, getLeftPaint());
        canvas.drawText("Total", 262, 228, getLeftPaint());

        String[][] codeStrArray = codeSort(dataArray);

        for(int j = 0; j <= codeStrArray.length; j++)
        {
            if(j == codeStrArray.length)
            {
                canvas.drawLine(250, 240 + 30 * j, 260, 240 + 30 * j, getLinePaint());
                canvas.drawLine(265, 240 + 30 * j, 275, 240 + 30 * j, getLinePaint());
                canvas.drawLine(280, 240 + 30 * j, 290, 240 + 30 * j, getLinePaint());
                canvas.drawLine(295, 240 + 30 * j, 305, 240 + 30 * j, getLinePaint());
                canvas.drawLine(310, 240 + 30 * j, 320, 240 + 30 * j, getLinePaint());

                canvas.drawText(total, 285, 265 + 30 * j, getCenterPaint());

                canvas.drawLine(250, 274 + 30 * j, 260, 274 + 30 * j, getLinePaint());
                canvas.drawLine(265, 274 + 30 * j, 275, 274 + 30 * j, getLinePaint());
                canvas.drawLine(280, 274 + 30 * j, 290, 274 + 30 * j, getLinePaint());
                canvas.drawLine(295, 274 + 30 * j, 305, 274 + 30 * j, getLinePaint());
                canvas.drawLine(310, 274 + 30 * j, 320, 274 + 30 * j, getLinePaint());
            }
            else
            {
                if(codeStrArray[j] == null)
                    continue;
                canvas.drawText(codeStrArray[j][0], 90, 258 + 30 * j, getLeftPaint());
                canvas.drawText(codeStrArray[j][1], 285, 258 + 30 * j, getCenterPaint());
            }
        }

        return bmOverlay;
    }
    
    private String[][] codeSort(String[][] values) {
    	ArrayList<Integer> codeArray = new ArrayList<Integer>();
    	
    	for(int i = 0; i < values.length; i++)
    	{
    		if(values[i][0] == null || values[i][0] == "")
            	continue;
    		String str = values[i][0];
    		int ascii_val = 0;
    		int number = 0;
    		
    		String rightStr = "00000000";
    		boolean is_number = false;
    		
    		for(int j = 0; j < str.length(); j++)
    		{
    			String subStr = rightStr.substring(j, rightStr.length());
    			if(j == str.length() - 1)
    				subStr = rightStr.substring(j + 1, rightStr.length());
    			char character = str.charAt(j);
    			int ascii = (int) character;
    			int cc = Character.getNumericValue(character);
    			if(cc < 10)
    			{
    				number += Integer.parseInt(String.valueOf(cc) + subStr);
    				is_number = true;
    				if(j != str.length() - 1)
    					continue;
    			}
    			else
    			{
    				ascii_val += number;
    				is_number = false;
    				number = 0;
    			}
    			
    			if(is_number)
    			{
    				ascii_val += number;
    				is_number = false;
    				number = 0;
    			}
    			else
    				ascii_val += ascii;
    		}
			codeArray.add(ascii_val);
    	}
    	
    	ArrayList<String> strArray = new ArrayList<String>();
    	ArrayList<Integer> indexs = new ArrayList<Integer>();
    	for(int k = 0; k < codeArray.size(); k++)
    		indexs.add(k);
    	
    	boolean flag = true;
    	while(flag) {
	    	if(codeArray.size() == 0)
	    	{
	    		flag = false;
	    		break;
	    	}

	    	int temp = codeArray.get(0);
	    	int num = indexs.get(0);
	    	int rinx = 0;
	    	for(int i = 0; i < codeArray.size(); i++)
	    	{
	    		if(temp > codeArray.get(i))
	    		{
	    			rinx = i;
	    			num = indexs.get(i);
	    			temp = codeArray.get(i);
	    		}
	    	}
	    	
	    	strArray.add(values[num][0]);
    		codeArray.remove(rinx);
    		indexs.remove(rinx);
    	}
    	
    	String[][] codeStrArray = new String[strArray.size()][2];
    	for(int i = 0; i < strArray.size(); i++)
    	{
    		for(int j = 0; j < values.length; j++)
    		{
	    		if(values[j][0] == null || values[j][0] == "")
	            	continue;
	    		if(strArray.get(i).compareTo(values[j][0]) == 0)
	    		{
	    			codeStrArray[i][0] = values[j][0];
	    			codeStrArray[i][1] = values[j][1];
	    			break;
	    		}  
    		}
    	}
    	
    	return codeStrArray;
    }

    private Paint getHeaderPaint() {
        Typeface type = Typeface.create("verdana", Typeface.BOLD);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(24);
        paint.setTypeface(type);
        paint.setTextAlign(Paint.Align.CENTER);

        return paint;
    }
    private Paint getLeftPaint() {

        Typeface type = Typeface.create("verdana", Typeface.BOLD);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setTypeface(type);

        return paint;
    }
    private Paint getCenterPaint() {

        Typeface type = Typeface.create("verdana", Typeface.BOLD);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setTypeface(type);
        paint.setTextAlign(Paint.Align.CENTER);

        return paint;
    }
    private Paint getLinePaint() {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);

        return paint;
    }
	
	public void drawBarcode(Canvas canvas, String barcodeData, int w, int h, int x, int y) {
	    Code128 barcode = new Code128();
	    barcode.setData(barcodeData);
	    barcode.getBitmap(canvas, w, h, x, y);
	}
	
}
