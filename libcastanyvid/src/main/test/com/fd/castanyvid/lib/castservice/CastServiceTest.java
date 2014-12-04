package com.fd.castanyvid.lib.castservice;

import com.fd.castanyvid.lib.castservice.fakes.FakeCastDeviceFinder;
import com.fd.castanyvid.lib.castservice.fakes.FakeCastProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CastServiceTest {

    private CastService castService;
    private FakeCastDeviceFinder castDeviceFinder;
    private FakeCastProvider castProvider;

    private static class TestCastServiceListener implements CastServiceListener
    {
        public List<CastDevice> castDeviceList;
        public boolean castDevicesUnavailable_called;

        @Override
        public void castDevicesAvailable(List<CastDevice> castDevices) {
            castDeviceList = castDevices;
        }

        @Override
        public void castDevicesUnavailable() {
            castDevicesUnavailable_called = true;
        }

        @Override
        public void castSessionAvailable(CastSession castSession) {

        }

        @Override
        public void castSessionUnavailable() {
        }

        @Override
        public void castSessionStopped() {

        }

        @Override
        public void castSessionStarting() {

        }
    }



    @Before
    public void setup()
    {
        castDeviceFinder = new FakeCastDeviceFinder();
        castProvider = new FakeCastProvider();
        castService = new CastService(castDeviceFinder, castProvider);
    }

    @Test
    public void whenTheCastServiceIsAskedToSearchForDevices_TheCastDeviceFinderStartsScanning()
    {
        castService.startSearchingForDevices();
        assertTrue(castDeviceFinder.startSearching_called);
    }

    @Test
    public void whenTheCastServiceIsAskedToStopSearchingForDevices_TheCastDeviceFinderStops()
    {
        castService.stopSearchingForDevices();
        assertTrue(castDeviceFinder.stopSearching_called);
    }

    @Test
    public void whenACastDeviceIsFound_attachedListenersAreNotifiedOfCastDeviceListChanged_withTheFoundCastDevicePresentInTheList()
    {
        TestCastServiceListener listener = new TestCastServiceListener();
        castService.addListener(listener);

        castDeviceFinder.reportDeviceFound("id", "name");

        assertEquals("id", listener.castDeviceList.get(0).getDeviceId());
        assertEquals("name", listener.castDeviceList.get(0).getName());
    }


}
