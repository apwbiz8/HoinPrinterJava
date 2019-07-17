package com.printer.hoin;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

public class RemoteConnectExtension implements FREExtension {
	@Override
    public FREContext createContext(String arg0) {
 
        return new RemoteConnectContext();
    }
 
    @Override
    public void dispose() {
    }
 
    @Override
    public void initialize() {
    }
}
