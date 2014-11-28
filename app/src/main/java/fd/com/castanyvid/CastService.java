package fd.com.castanyvid;

import com.google.android.gms.cast.CastDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chris on 27/11/14.
 */
public class CastService {

    public interface CastDeviceFinder {
        public interface CastDeviceFinderListener {
            public void castDeviceFound(CastDevice device);

            public void castDeviceLost(CastDevice device);
        }

        public void addListener(CastDeviceFinderListener listener);

        public void startSearching();

        public void stopSearching();
    }

    public interface CastSession
    {
        public void loadUrl(String url);
    }

    public interface CastProvider
    {
        public interface CastProviderListener
        {
            void castSessionAvailable(CastSession session);
            void castSessionLost();
        }

        void setListener(CastProviderListener listener);
        void castRequestedForDevice(CastDevice device);
    }

    public interface CastServiceListener {
        public void castDevicesAvailable(List<CastDevice> castDevices);
        public void castDevicesUnavailable();
    }

    private final CastDeviceFinder castDeviceFinder;
    private final CastProvider castProvider;

    private final List<CastServiceListener> listeners = new ArrayList<CastServiceListener>();
    private final List<CastDevice> castDevices = new ArrayList<CastDevice>();

    private final CastDeviceFinder.CastDeviceFinderListener castDeviceFinderListener = new CastDeviceFinder.CastDeviceFinderListener() {
        @Override
        public void castDeviceFound(CastDevice device) {
            castDevices.add(device);
            reportCastDeviceListChanged();
        }

        @Override
        public void castDeviceLost(CastDevice device) {
            castDevices.remove(device);
            reportCastDeviceListChanged();
        }
    };

    private final CastProvider.CastProviderListener castProviderListener = new CastProvider.CastProviderListener() {
        @Override
        public void castSessionAvailable(CastSession session) {

        }

        @Override
        public void castSessionLost() {

        }
    };

    public CastService(CastDeviceFinder castDeviceFinder, CastProvider castProvider) {
        this.castDeviceFinder = castDeviceFinder;
        castDeviceFinder.addListener(castDeviceFinderListener);

        this.castProvider = castProvider;
        castProvider.setListener(castProviderListener);
    }

    public void addListener(CastServiceListener listener) {
        listeners.add(listener);
        reportCastDeviceListChanged();
    }

    public void removeListener(CastServiceListener listener)
    {
        listeners.remove(listener);
    }

    public void startSearchingForDevices() {
        castDeviceFinder.startSearching();
    }

    public void stopSearchingForDevices() {
        castDeviceFinder.stopSearching();
    }

    private void reportCastDeviceListChanged() {
        for(CastServiceListener listener : listeners)
        {
            if(castDevices.size()>0) {
                listener.castDevicesAvailable(Collections.unmodifiableList(castDevices));
            }
            else
            {
                listener.castDevicesUnavailable();
            }
        }
    }

    public void requestCast(CastDevice device) {
        castProvider.castRequestedForDevice(device);
    }
}
