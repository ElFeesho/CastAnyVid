package fd.com.castanyvid.castservice;

import com.google.android.gms.cast.CastDevice;

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