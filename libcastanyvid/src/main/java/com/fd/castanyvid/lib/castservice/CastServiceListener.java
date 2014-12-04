package com.fd.castanyvid.lib.castservice;

import java.util.List;

public interface CastServiceListener {
    void castDevicesAvailable(List<CastDevice> castDevices);

    void castDevicesUnavailable();

    void castSessionAvailable(CastSession castSession);

    void castSessionUnavailable();

    void castSessionStopped();

    void castSessionStarting();
}