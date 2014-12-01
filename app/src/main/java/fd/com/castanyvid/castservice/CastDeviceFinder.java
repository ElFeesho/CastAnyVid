package fd.com.castanyvid.castservice;

import com.google.android.gms.cast.CastDevice;

public interface CastDeviceFinder {
    public void addListener(CastDeviceFinderListener listener);

    public void startSearching();

    public void stopSearching();

    public interface CastDeviceFinderListener {
        public void castDeviceFound(CastDevice device);

        public void castDeviceLost(CastDevice device);
    }
}