package fd.com.castanyvid.castservice;

import com.google.android.gms.cast.CastDevice;

import java.util.List;

public interface CastServiceListener {
    void castDevicesAvailable(List<CastDevice> castDevices);

    void castDevicesUnavailable();

    void castSessionAvailable(CastSession castSession);

    void castSessionUnavailable();

    void castSessionStopped();

    void castSessionStarting();
}