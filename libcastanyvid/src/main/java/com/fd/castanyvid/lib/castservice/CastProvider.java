package com.fd.castanyvid.lib.castservice;

public interface CastProvider {
    void setListener(CastProviderListener listener);

    void castRequestedForDevice(CastDevice device);

    void stopCasting();

    public interface CastProviderListener {
        void establishingCastSession();

        void castSessionAvailable(CastSession session);

        void castSessionLost();

        void castSessionStopped();
    }
}