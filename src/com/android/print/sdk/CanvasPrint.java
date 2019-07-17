//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.android.print.sdk;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.FontMetrics;
import java.util.Locale;

public class CanvasPrint {
    private Canvas canvas;
    public Paint mPaint;
    private Bitmap bitmap;
    private int width;
    private float length = 0.0F;
    private int textSize;
    private float currentY;
    private boolean textExceedNewLine = true;
    private boolean useSplit;
    private String splitStr = " ";
    private boolean textAlignRight;

    public CanvasPrint() {
    }

    public int getLength() {
        return (int)this.length;
    }

    public int getWidth() {
        return this.width;
    }

    public void initCanvas(int w) {
        int h = 5 * w;
        this.bitmap = Bitmap.createBitmap(w, h, Config.ARGB_4444);
        this.canvas = new Canvas(this.bitmap);
        this.canvas.drawColor(-1);
    }

    public void initPaint() {
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(-7829368);
    }

    public void init(PrinterType printerType) {
        if (printerType == PrinterType.T9) {
            this.width = 576;
        } else if (printerType == PrinterType.T5) {
            this.width = 240;
        } else {
            this.width = 384;
        }

        this.initCanvas(this.width);
        this.initPaint();
    }

    public void setFontProperty(FontProperty fp) {
        if (fp.sFace != null) {
            try {
                this.mPaint.setTypeface(fp.sFace);
            } catch (Exception var3) {
                this.mPaint.setTypeface(Typeface.DEFAULT);
            }
        } else {
            this.mPaint.setTypeface(Typeface.DEFAULT);
        }

        if (fp.bBold) {
            this.mPaint.setFakeBoldText(true);
        } else {
            this.mPaint.setFakeBoldText(false);
        }

        if (fp.bItalic) {
            this.mPaint.setTextSkewX(-0.5F);
        } else {
            this.mPaint.setTextSkewX(0.0F);
        }

        if (fp.bUnderLine) {
            this.mPaint.setUnderlineText(true);
        } else {
            this.mPaint.setUnderlineText(false);
        }

        if (fp.bStrikeout) {
            this.mPaint.setStrikeThruText(true);
        } else {
            this.mPaint.setStrikeThruText(false);
        }

        this.mPaint.setTextSize((float)fp.iSize);
    }

    public void setLineWidth(float w) {
        this.mPaint.setStrokeWidth(w);
    }

    public void setTextSize(int size) {
        this.textSize = size;
        this.mPaint.setTextSize((float)this.textSize);
    }

    public void setItalic(boolean italic) {
        if (italic) {
            this.mPaint.setTextSkewX(-0.5F);
        } else {
            this.mPaint.setTextSkewX(0.0F);
        }

    }

    public void setStrikeThruText(boolean strike) {
        this.mPaint.setStrikeThruText(strike);
    }

    public void setUnderlineText(boolean underline) {
        this.mPaint.setUnderlineText(underline);
    }

    public void setFakeBoldText(boolean fakeBold) {
        this.mPaint.setFakeBoldText(fakeBold);
    }

    public void setUseSplit(boolean useSplit) {
        this.useSplit = useSplit;
    }

    public void setUseSplitAndString(boolean useSplit, String splitStr) {
        this.useSplit = useSplit;
        this.splitStr = splitStr;
    }

    public void setTextExceedNewLine(boolean newLine) {
        this.textExceedNewLine = newLine;
    }

    public void setTextAlignRight(boolean alignRight) {
        this.textAlignRight = alignRight;
    }

    public void drawText(int x, int y, String string) {
        this.currentY += this.getFontHeight();
        int validWidth = this.width - x;
        float textWidth = this.getTextWidth(string);
        int pos;
        if (this.textExceedNewLine) {
            for(boolean var6 = false; (pos = this.getValidStringPos(string, validWidth)) > 0 && textWidth > 0.0F; this.currentY = (float)y + this.getFontHeight()) {
                String printStr = string.substring(0, pos);
                if (this.textAlignRight) {
                    float tmpWidth = this.getTextWidth(printStr);
                    this.canvas.drawText(printStr, (float)x + ((float)validWidth - tmpWidth), (float)y, this.mPaint);
                } else {
                    this.canvas.drawText(printStr, (float)x, (float)y, this.mPaint);
                }

                string = string.substring(pos, string.length());
                textWidth -= (float)validWidth;
            }
        } else {
            if (this.textAlignRight) {
                this.canvas.drawText(string, (float)x + ((float)validWidth - textWidth), (float)y, this.mPaint);
            } else {
                this.canvas.drawText(string, (float)x, (float)y, this.mPaint);
            }

            this.currentY += this.getFontHeight();
        }

        if (this.length < this.currentY) {
            this.length = this.currentY;
        }

    }

    public void drawText(int x, String string) {
        this.currentY += this.getFontHeight();
        int validWidth = this.width - x;
        float textWidth = this.getTextWidth(string);
        int pos;
        if (this.textExceedNewLine) {
            for(boolean var5 = false; (pos = this.getValidStringPos(string, validWidth)) > 0 && textWidth > 0.0F; this.currentY += this.getFontHeight()) {
                String printStr = string.substring(0, pos);
                if (this.textAlignRight) {
                    float tmpWidth = this.getTextWidth(printStr);
                    this.canvas.drawText(printStr, (float)x + ((float)validWidth - tmpWidth), this.currentY, this.mPaint);
                } else {
                    this.canvas.drawText(printStr, (float)x, this.currentY, this.mPaint);
                }

                string = string.substring(pos, string.length());
                textWidth -= (float)validWidth;
            }
        } else {
            if (this.textAlignRight) {
                this.canvas.drawText(string, (float)x + ((float)validWidth - textWidth), this.currentY, this.mPaint);
            } else {
                this.canvas.drawText(string, (float)x, this.currentY, this.mPaint);
            }

            this.currentY += this.getFontHeight();
        }

        if (this.length < this.currentY) {
            this.length = this.currentY;
        }

    }

    public void drawText(String string) {
        this.currentY += this.getFontHeight();
        int validWidth = this.width;
        float textWidth = this.getTextWidth(string);
        int pos;
        if (this.textExceedNewLine) {
            for(boolean var4 = false; (pos = this.getValidStringPos(string, validWidth)) > 0 && textWidth > 0.0F; this.currentY += this.getFontHeight()) {
                String printStr = string.substring(0, pos);
                if (this.textAlignRight) {
                    float tmpWidth = this.getTextWidth(printStr);
                    this.canvas.drawText(printStr, (float)validWidth - tmpWidth, this.currentY, this.mPaint);
                } else {
                    this.canvas.drawText(printStr, 0.0F, this.currentY, this.mPaint);
                }

                string = string.substring(pos, string.length());
                textWidth -= (float)validWidth;
            }
        } else {
            if (this.textAlignRight) {
                this.canvas.drawText(string, (float)validWidth - textWidth, this.currentY, this.mPaint);
            } else {
                this.canvas.drawText(string, 0.0F, this.currentY, this.mPaint);
            }

            this.currentY += this.getFontHeight();
        }

        if (this.length < this.currentY) {
            this.length = this.currentY;
        }

    }

    public void drawLine(float startX, float startY, float stopX, float stopY) {
        this.canvas.drawLine(startX, startY, stopX, stopY, this.mPaint);
        float max = 0.0F;
        max = startY > stopY ? startY : stopY;
        if (this.length < max) {
            this.length = max;
        }

    }

    public void drawRectangle(float left, float top, float right, float bottom) {
        this.canvas.drawRect(left, top, right, bottom, this.mPaint);
        float max = 0.0F;
        max = top > bottom ? top : bottom;
        if (this.length < max) {
            this.length = max;
        }

    }

    public void drawEllips(float left, float top, float right, float bottom) {
        RectF re = new RectF(left, top, right, bottom);
        this.canvas.drawOval(re, this.mPaint);
        float max = 0.0F;
        max = top > bottom ? top : bottom;
        if (this.length < max) {
            this.length = max;
        }

    }

    public void drawImage(Bitmap image) {
        this.canvas.drawBitmap(image, 0.0F, this.currentY, (Paint)null);
        this.currentY += (float)image.getHeight();
        if (this.length < this.currentY) {
            this.length = this.currentY;
        }

    }

    public void drawImage(int left, Bitmap image) {
        this.canvas.drawBitmap(image, (float)left, this.currentY, (Paint)null);
        this.currentY += (float)image.getHeight();
        if (this.length < this.currentY) {
            this.length = this.currentY;
        }

    }

    public void drawImage(int left, float top, Bitmap image) {
        this.canvas.drawBitmap(image, (float)left, top, (Paint)null);
        this.currentY += (float)image.getHeight();
        if (this.length < this.currentY) {
            this.length = this.currentY;
        }

    }

    public Bitmap getCanvasImage() {
        return Bitmap.createBitmap(this.bitmap, 0, 0, this.width, this.getLength());
    }

    private float getTextWidth(String text) {
        return this.mPaint.measureText(text);
    }

    public float getCurrentPointY() {
        return this.currentY;
    }

    private float getFontHeight() {
        FontMetrics fm = this.mPaint.getFontMetrics();
        return (float)Math.ceil((double)(fm.descent - fm.ascent));
    }

    private float getCharacterWidth() {
        float spacing = this.mPaint.getFontSpacing();
        String lang = Locale.getDefault().getLanguage();
        if (!lang.equals("ja") && !lang.equals("ko") && !lang.equals("zh")) {
            spacing /= 2.0F;
        }

        return spacing;
    }

    private int getValidStringPos(String string, int validWidth) {
        float textWidth = this.getTextWidth(string);

        while(textWidth > 0.0F && textWidth > (float)validWidth) {
            int subPos = (int)((float)(validWidth * string.length()) / textWidth);
            string = string.substring(0, subPos);
            textWidth = this.getTextWidth(string);
            if (textWidth <= (float)validWidth) {
                if (this.useSplit && string.contains(this.splitStr)) {
                    subPos = string.lastIndexOf(this.splitStr);
                }

                return subPos;
            }
        }

        return string.length();
    }
}
