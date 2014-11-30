package fd.com.castanyvid;

import com.google.android.gms.cast.CastDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CastService {

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
    private CastSession castSession;
    private final CastProvider.CastProviderListener castProviderListener = new CastProvider.CastProviderListener() {
        @Override
        public void castSessionAvailable(CastSession session) {
            castSession = session;
            reportCastSessionAvailabilityChanged();
        }

        @Override
        public void castSessionLost() {
            castSession = null;
            reportCastSessionAvailabilityChanged();
        }

        @Override
        public void castSessionStopped() {
            reportCastSessionStopped();
        }
    };

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

    private boolean currentlyCasting() {
        return castSession != null;
    }

    public interface CastDeviceFinder {
        public void addListener(CastDeviceFinderListener listener);

        public void startSearching();

        public void stopSearching();

        public interface CastDeviceFinderListener {
            public void castDeviceFound(CastDevice device);

            public void castDeviceLost(CastDevice device);
        }
    }

    public interface CastSession {
        void setListener(Listener listener);

        void loadUrl(String url);

        void scrubTo(Timestamp timestamp);

        void play();

        void pause();

        void increaseVolume();

        void decreaseVolume();

        public interface Listener {
            void mediaPlaying();

            void mediaPaused();

            void mediaLoaded(String contentId, Timestamp timestamp, Duration duration);

            void mediaPositionUpdate(Timestamp timestamp);

            void mediaBuffering();
        }

    }

    public interface CastProvider {
        void setListener(CastProviderListener listener);

        void castRequestedForDevice(CastDevice device);

        void stopCasting();

        public interface CastProviderListener {
            void castSessionAvailable(CastSession session);

            void castSessionLost();

            void castSessionStopped();
        }
    }

    public interface CastServiceListener {
        void castDevicesAvailable(List<CastDevice> castDevices);

        void castDevicesUnavailable();

        void castSessionAvailable(CastSession castSession);

        void castSessionUnavailable();

        void castSessionStopped();
    }


}
