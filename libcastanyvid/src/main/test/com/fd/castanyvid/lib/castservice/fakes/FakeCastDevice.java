package com.fd.castanyvid.lib.castservice.fakes;

import com.fd.castanyvid.lib.castservice.CastDevice;

public class FakeCastDevice implements CastDevice {

    private final String id;
    private final String name;

    FakeCastDevice(String id, String name)
    {
        this.id = id;
        this.name = name;
    }


    @Override
    public String getDeviceId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "CD: "+id+"-"+name;
    }
}
