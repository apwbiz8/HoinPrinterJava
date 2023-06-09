//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.android.print.sdk.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.util.Log;
import com.android.print.sdk.PrinterInstance;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DefaultUtils {
    public DefaultUtils() {
    }

    public static Bitmap compressBitmap(Bitmap srcBitmap, int maxLength) {
        Bitmap destBitmap = null;

        try {
            Options opts = new Options();
            byte[] srcBytes = bitmap2Bytes(srcBitmap);
            BitmapFactory.decodeByteArray(srcBytes, 0, srcBytes.length, opts);
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;
            double ratio;
            int destWidth;
            int destHeight;
            if (srcWidth > srcHeight) {
                ratio = (double)(srcWidth / maxLength);
                destWidth = maxLength;
                destHeight = (int)((double)srcHeight / ratio);
            } else {
                ratio = (double)(srcHeight / maxLength);
                destHeight = maxLength;
                destWidth = (int)((double)srcWidth / ratio);
            }

            Options newOpts = new Options();
            newOpts.inSampleSize = (int)ratio + 1;
            newOpts.inJustDecodeBounds = false;
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            destBitmap = BitmapFactory.decodeByteArray(srcBytes, 0, srcBytes.length, newOpts);
        } catch (Exception var12) {
        }

        return destBitmap;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        while((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }

        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;
    }

    public static Bitmap getImageFromBytes(byte[] bytes, Options opts) {
        if (bytes != null) {
            return opts != null ? BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts) : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
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

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static File saveFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;

        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception var13) {
            var13.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException var12) {
                    var12.printStackTrace();
                }
            }

        }

        return file;
    }

    public static int printBitmap2File(Bitmap bitmap, String filePath) {
        File file;
        if (filePath.endsWith(".png")) {
            file = new File(filePath);
        } else {
            file = new File(filePath + ".png");
        }

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(CompressFormat.PNG, 100, fos);
            fos.close();
            return 0;
        } catch (Exception var4) {
            var4.printStackTrace();
            return -1;
        }
    }

    public static byte[] bitmap2PrinterBytes(Bitmap bitmap, int left) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] imgbuf = new byte[(width / 8 + left + 4) * height];
        byte[] bitbuf = new byte[width / 8];
        int[] p = new int[8];
        int s = 0;
        System.out.println("+++++++++++++++ Total Bytes: " + (width / 8 + 4) * height);

        for(int y = 0; y < height; ++y) {
            int n;
            for(n = 0; n < width / 8; ++n) {
                int m;
                for(m = 0; m < 8; ++m) {
                    if (bitmap.getPixel(n * 8 + m, y) == -1) {
                        p[m] = 0;
                    } else {
                        p[m] = 1;
                    }
                }

                m = p[0] * 128 + p[1] * 64 + p[2] * 32 + p[3] * 16 + p[4] * 8 + p[5] * 4 + p[6] * 2 + p[7];
                bitbuf[n] = (byte)m;
            }

            if (y != 0) {
                ++s;
                imgbuf[s] = 22;
            } else {
                imgbuf[s] = 22;
            }

            ++s;
            imgbuf[s] = (byte)(width / 8 + left);

            for(n = 0; n < left; ++n) {
                ++s;
                imgbuf[s] = 0;
            }

            for(n = 0; n < width / 8; ++n) {
                ++s;
                imgbuf[s] = bitbuf[n];
            }

            ++s;
            imgbuf[s] = 21;
            ++s;
            imgbuf[s] = 1;
        }

        return imgbuf;
    }

    public static byte[] bitmap2PrinterBytes_stylus(Bitmap bitmap, int multiple, int left) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth() + left;
        boolean need_0a = false;
        int maxWidth = 240;
        byte[] imgBuf;
        if (width < maxWidth) {
            imgBuf = new byte[(height / 8 + 1) * (width + 6)];
            need_0a = true;
        } else {
            imgBuf = new byte[(height / 8 + 1) * (width + 5) + 2];
        }

        byte[] tmpBuf = new byte[width + 5];
        int[] p = new int[8];
        int s = 0;
        boolean allZERO = true;

        for(int y = 0; y < height / 8 + 1; ++y) {
            int t = 0;
            tmpBuf[t] = 27;
            t = t + 1;
            tmpBuf[t] = 42;
            ++t;
            tmpBuf[t] = (byte)multiple;
            ++t;
            tmpBuf[t] = (byte)(width % maxWidth);
            ++t;
            tmpBuf[t] = (byte)(width / maxWidth > 0 ? 1 : 0);
            allZERO = true;

            for(int i = 0; i < width; ++i) {
                for(i = 0; i < 8; ++i) {
                    if (y * 8 + i < height && i >= left) {
                        p[i] = bitmap.getPixel(i - left, y * 8 + i) == -1 ? 0 : 1;
                    } else {
                        p[i] = 0;
                    }
                }

                i = p[0] * 128 + p[1] * 64 + p[2] * 32 + p[3] * 16 + p[4] * 8 + p[5] * 4 + p[6] * 2 + p[7];
                ++t;
                tmpBuf[t] = (byte)i;
                if (i != 0) {
                    allZERO = false;
                }
            }

            if (allZERO) {
                if (s == 0) {
                    imgBuf[s] = 27;
                } else {
                    ++s;
                    imgBuf[s] = 27;
                }

                ++s;
                imgBuf[s] = 74;
                ++s;
                imgBuf[s] = 8;
            } else {
                for(int i = 0; i < t + 1; ++i) {
                    if (i == 0 && s == 0) {
                        imgBuf[s] = tmpBuf[i];
                    } else {
                        ++s;
                        imgBuf[s] = tmpBuf[i];
                    }
                }

                if (need_0a) {
                    ++s;
                    imgBuf[s] = 10;
                }
            }
        }

        if (!need_0a) {
            ++s;
            imgBuf[s] = 13;
            ++s;
            imgBuf[s] = 10;
        }

        byte[] realBuf = new byte[s + 1];

        for(int y = 0; y < s + 1; ++y) {
            realBuf[y] = imgBuf[y];
        }

        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < realBuf.length; ++i) {
            String temp = Integer.toHexString(realBuf[i] & 255);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }

            sb.append(temp + " ");
            if (i != 0 && i % 100 == 0 || i == realBuf.length - 1) {
                Log.e("12345", sb.toString());
                sb = new StringBuffer();
            }
        }

        return realBuf;
    }

    public static int getStringCharacterLength(String line) {
        int length = 0;

        for(int j = 0; j < line.length(); ++j) {
            if (line.charAt(j) > 256) {
                length += 2;
            } else {
                ++length;
            }
        }

        return length;
    }

    public static int getSubLength(String line, int width) {
        int length = 0;

        for(int j = 0; j < line.length(); ++j) {
            if (line.charAt(j) > 256) {
                length += 2;
            } else {
                ++length;
            }

            if (length > width) {
                int temp = line.substring(0, j - 1).lastIndexOf(" ");
                if (temp != -1) {
                    return temp;
                }

                return j - 1 == 0 ? 1 : j - 1;
            }
        }

        return line.length();
    }

    public static boolean isNum(byte temp) {
        return temp >= 48 && temp <= 57;
    }

    public static void Log(String tag, String msg) {
        if (PrinterInstance.DEBUG) {
            Log.i(tag, msg);
        }

    }
}
