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

    public interface CastServiceListener {
        public void castDeviceListChanged(List<CastDevice> castDevices);
    }

    private final CastDeviceFinder castDeviceFinder;
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

    public CastService(CastDeviceFinder castDeviceFinder) {
        this.castDeviceFinder = castDeviceFinder;
        castDeviceFinder.addListener(castDeviceFinderListener);
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
            listener.castDeviceListChanged(Collections.unmodifiableList(castDevices));
        }
    }

}
