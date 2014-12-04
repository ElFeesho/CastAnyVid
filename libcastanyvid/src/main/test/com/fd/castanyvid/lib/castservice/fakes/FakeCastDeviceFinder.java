package com.fd.castanyvid.lib.castservice.fakes;

import com.fd.castanyvid.lib.castservice.CastDeviceFinder;

public class FakeCastDeviceFinder implements CastDeviceFinder {

    private CastDeviceFinderListener listener;
    public boolean stopSearching_called;
    public boolean startSearching_called;

    @Override
    public void addListener(CastDeviceFinderListener listener) {
        this.listener = listener;
    }

    @Override
    public void startSearching() {
        startSearching_called = true;
    }

    @Override
    public void stopSearching() {
        stopSearching_called = true;
    }

    public void reportDeviceFound(String id, String name) {
        listener.castDeviceFound(new FakeCastDevice(id, name));
    }
}
