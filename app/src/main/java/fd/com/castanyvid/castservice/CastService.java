package fd.com.castanyvid.castservice;

import com.google.android.gms.cast.CastDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CastService {

    private final CastDeviceFinder castDeviceFinder;
    private final CastProvider castProvider;
    private final List<CastServiceListener> listeners = new ArrayList<CastServiceListener>();
    private final List<CastDevice> castDevices = new ArrayList<CastDevice>();

    private final CastDeviceFinder.CastDeviceFinderListener castDeviceFinderListener = new CastDeviceFinder.CastDeviceFinderListener() {
        @Override
        public void castDeviceFound(CastDevice device) {
            for(CastDevice foundDevice : castDevices)
            {
                if(foundDevice.getDeviceId().contentEquals(device.getDeviceId()))
                {
                    castDevices.remove(foundDevice);
                    break;
                }
            }
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
        public void establishingCastSession() {
            reportCastSessionStarting();
        }

        @Override
        public void castSessionAvailable(CastSession session) {
            castSession = session;
            castDeviceFinder.stopSearching();
            reportCastSessionAvailabilityChanged();
        }

        @Override
        public void castSessionLost() {
            castSession = null;
            castDeviceFinder.startSearching();
            reportCastSessionAvailabilityChanged();
        }

        @Override
        public void castSessionStopped() {
            castDeviceFinder.startSearching();
            reportCastSessionStopped();
        }
    };


    private CastSession castSession;

    public CastService(CastDeviceFinder castDeviceFinder, CastProvider castProvider) {
        this.castDeviceFinder = castDeviceFinder;
        castDeviceFinder.addListener(castDeviceFinderListener);

        this.castProvider = castProvider;
        castProvider.setListener(castProviderListener);
    }

    private void reportCastSessionStopped() {
        for (CastServiceListener listener : listeners) {
            listener.castSessionStopped();
        }
    }

    public void addListener(CastServiceListener... newListener) {
        listeners.addAll(Arrays.asList(newListener));
        reportCastDeviceListChanged();
        reportCastSessionAvailabilityChanged();
    }

    public void removeListener(CastServiceListener listener) {
        listeners.remove(listener);
    }

    public void startSearchingForDevices() {
        castDeviceFinder.startSearching();
    }

    public void stopSearchingForDevices() {
        castDeviceFinder.stopSearching();
    }

    public void requestCast(CastDevice device) {
        castProvider.castRequestedForDevice(device);
    }

    public void stopCasting() {
        castSession.endSession();
        castProvider.stopCasting();
    }

    public void requestVolumeIncrease() {
        if (currentlyCasting()) {
            castSession.increaseVolume();
        }
    }

    public void requestVolumeDecrement() {
        if (currentlyCasting()) {
            castSession.decreaseVolume();
        }
    }


    private void reportCastDeviceListChanged() {

        sortDeviceList();

        for (CastServiceListener listener : listeners) {
            if (castDevices.size() > 0) {
                listener.castDevicesAvailable(Collections.unmodifiableList(castDevices));
            } else {
                listener.castDevicesUnavailable();
            }
        }
    }

    private void reportCastSessionAvailabilityChanged() {
        for (CastServiceListener listener : listeners) {
            if (currentlyCasting()) {
                listener.castSessionAvailable(castSession);
            } else {
                listener.castSessionUnavailable();
            }
        }
    }

    private void reportCastSessionStarting() {
        for (CastServiceListener listener : listeners)
        {
            listener.castSessionStarting();
        }
    }

    private boolean currentlyCasting() {
        return castSession != null;
    }

    private void sortDeviceList() {
        Collections.sort(castDevices, new Comparator<CastDevice>() {
            @Override
            public int compare(CastDevice lhs, CastDevice rhs) {
                return lhs.getFriendlyName().compareTo(rhs.getFriendlyName());
            }
        });
    }
}
