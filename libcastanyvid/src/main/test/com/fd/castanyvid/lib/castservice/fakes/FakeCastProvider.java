package com.fd.castanyvid.lib.castservice.fakes;

import com.fd.castanyvid.lib.castservice.CastDevice;
import com.fd.castanyvid.lib.castservice.CastProvider;

public class FakeCastProvider implements CastProvider {
    private CastProviderListener listener;
    public CastDevice castRequestedForDevice_calledWithDevice;
    public boolean stopCasting_called;

    @Override
    public void setListener(CastProviderListener listener) {
        this.listener = listener;
    }

    @Override
    public void castRequestedForDevice(CastDevice device) {
        castRequestedForDevice_calledWithDevice = device;
    }

    @Override
    public void stopCasting() {
        stopCasting_called = true;
    }
}
