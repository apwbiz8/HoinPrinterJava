//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.android.print.sdk.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import java.util.ArrayList;
//import java.util.List;

public class OtherUtils {
	private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = { "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111" };

    public OtherUtils() {
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = (float)w / (float)width;
        float scaleHeight = (float)h / (float)height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newBmp;
    }

	public static Bitmap scalingBitmap(Bitmap bitmap, int maxWidth) {
		if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0)
	          return null; 
        try {
          int width = bitmap.getWidth();
          int height = bitmap.getHeight();
          
          float scale = 1.0F;
          if (maxWidth <= 0 || width <= maxWidth) {
            scale = maxWidth / width;
          }
          Matrix matrix = new Matrix();
          matrix.postScale(scale, scale);
          return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        } catch (OutOfMemoryError e) {
          return null;
        }
	}
        
    public static byte[] decodeBitmap(Bitmap image, int parting) {
        ArrayList<byte[]> data = decodeBitmapToDataList(image, parting);
        int len = 0;
        for (byte[] srcArray : data) {
          len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : data) {
          System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
          destLen += srcArray.length;
        } 
        return destArray;
    }
    
    public static ArrayList<byte[]> decodeBitmapToDataList(Bitmap image, int parting) {
        if (parting <= 0 || parting > 255)
          parting = 255; 
        if (image == null)
          return null; 
        int width = image.getWidth();
        int height = image.getHeight();
        if (width <= 0 || height <= 0)
          return null; 
        if (width > 2040) {
          Bitmap resizeImage;
          float scale = 2040.0F / width;
          Matrix matrix = new Matrix();
          matrix.postScale(scale, scale);
          
          try {
            resizeImage = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
          } catch (OutOfMemoryError e) {
            return null;
          } 
          ArrayList<byte[]> data = decodeBitmapToDataList(resizeImage, parting);
          resizeImage.recycle();
          return data;
        } 

        
        String widthHexString = Integer.toHexString((width % 8 == 0) ? (width / 8) : (width / 8 + 1));
        if (widthHexString.length() > 2)
        	return null;
        if (widthHexString.length() == 1) {
          widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        
        String zeroStr = "";
        int zeroCount = width % 8;
        if (zeroCount > 0) {
          for (int i = 0; i < 8 - zeroCount; i++) {
            zeroStr = zeroStr + "0";
          }
        }
        ArrayList<String> commandList = new ArrayList<String>();
        
        int time = (height % parting == 0) ? (height / parting) : (height / parting + 1);
        for (int t = 0; t < time; t++) {
          int partHeight = (t == time - 1) ? (height % parting) : parting;

          
          String heightHexString = Integer.toHexString(partHeight);
          if (heightHexString.length() > 2)
        	  return null; 
          if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
          }
          heightHexString = heightHexString + "00";

          
          String commandHexString = "1D763000";
          commandList.add(commandHexString + widthHexString + heightHexString);
          
          ArrayList<String> list = new ArrayList<String>();
          StringBuilder sb = new StringBuilder();
          
          for (int i = 0; i < partHeight; i++) {
            sb.delete(0, sb.length());
            for (int j = 0; j < width; j++) {
              
              int blue, green, red, startHeight = t * parting + i;
              
              int color = image.getPixel(j, startHeight);
              
              if (image.hasAlpha()) {
                
                int alpha = Color.alpha(color);
                
                red = Color.red(color);
                green = Color.green(color);
                blue = Color.blue(color);
                float offset = alpha / 255.0F;
                
                red = 255 + (int)Math.ceil(((red - 255) * offset));
                green = 255 + (int)Math.ceil(((green - 255) * offset));
                blue = 255 + (int)Math.ceil(((blue - 255) * offset));
              } else {
                
                red = Color.red(color);
                green = Color.green(color);
                blue = Color.blue(color);
              } 
              
              if (red > 160 && green > 160 && blue > 160) {
                sb.append("0");
              } else {
                sb.append("1");
              } 
            } 
            if (zeroCount > 0) {
              sb.append(zeroStr);
            }
            list.add(sb.toString());
          } 
          
          ArrayList<String> bmpHexList = new ArrayList<String>();
          for (String binaryStr : list) {
            sb.delete(0, sb.length());
            for (int i = 0; i < binaryStr.length(); i += 8) {
              String str = binaryStr.substring(i, i + 8);
              
              String hexString = binaryStrToHexString(str);
              sb.append(hexString);
            } 
            bmpHexList.add(sb.toString());
          } 

          
          commandList.addAll(bmpHexList);
        } 
        ArrayList<byte[]> data = new ArrayList<byte[]>();
        for (String hexStr : commandList) {
        	data.add(hexStringToBytes(hexStr));
        }
        return data;
    }
    
    public static String binaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
          if (f4.equals(binaryArray[i]))
            hex = hex + hexStr.substring(i, i + 1); 
        } 
        for (int i = 0; i < binaryArray.length; i++) {
          if (b4.equals(binaryArray[i]))
            hex = hex + hexStr.substring(i, i + 1); 
        } 
        return hex;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
          return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
          int pos = i * 2;
          d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        } 
        return d;
    }

    private static byte charToByte(char c) { return (byte)hexStr.indexOf(c); }

    public static byte[] mergerByteArray(byte[]... byteArray) {
        int length = 0;
        for (byte[] item : byteArray) {
          length += item.length;
        }
        byte[] result = new byte[length];
        int index = 0;
        for (byte[] item : byteArray) {
          for (byte b : item) {
            result[index] = b;
            index++;
          } 
        } 
        return result;
    }
    
    public static byte[] initPrinter() { return PrintCommands.initializePrinter(); }
    
    public static byte[] printLineFeed() { return PrintCommands.printLineFeed(); }
    
    public static byte[] emphasizedOn() { return PrintCommands.turnOnEmphasizedMode(); }
    
    public static byte[] emphasizedOff() { return PrintCommands.turnOffEmphasizedMode(); }
    
    public static byte[] alignLeft() { return PrintCommands.selectJustification(0); }
    
    public static byte[] alignCenter() { return PrintCommands.selectJustification(1); }
    
    public static byte[] alignRight() { return PrintCommands.selectJustification(2); }
    
    public static byte[] printLineHeight(int height) { return PrintCommands.setLineSpacing(height); }
    
    public static byte[] fontSizeSetBig(int num) {
      byte realSize = 0;
      switch (num) {
        case 0:
          realSize = 0;
          break;
        case 1:
          realSize = 17;
          break;
        case 2:
          realSize = 34;
          break;
        case 3:
          realSize = 51;
          break;
        case 4:
          realSize = 68;
          break;
        case 5:
          realSize = 85;
          break;
        case 6:
          realSize = 102;
          break;
        case 7:
          realSize = 119;
          break;
      } 
      return PrintCommands.selectCharacterSize(realSize);
    }

    public static byte[] feedPaperCut() { return PrintCommands.selectCutModeAndCutPaper(1, 0); }
    
}
