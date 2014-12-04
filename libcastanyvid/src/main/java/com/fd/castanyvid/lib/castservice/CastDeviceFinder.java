package com.fd.castanyvid.lib.castservice;

public interface CastDeviceFinder {
    public void addListener(CastDeviceFinderListener listener);

    public void startSearching();

    public void stopSearching();

    public interface CastDeviceFinderListener {
        public void castDeviceFound(CastDevice device);

        public void castDeviceLost(CastDevice device);
    }
}