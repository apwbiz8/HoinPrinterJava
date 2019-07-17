//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.android.print.sdk;

public interface IPrinterPort {
    void open();

    void close();

    int write(byte[] var1);

    byte[] read();

    int getState();
}
