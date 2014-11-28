package fd.com.castanyvid;

import com.google.android.gms.cast.CastDevice;

import java.util.List;

/**
 * Created by chris on 27/11/14.
 */
public class CastDeviceListPresenter implements CastService.CastServiceListener {

    public interface CastDeviceListPresenterListener
    {
        void castRequested(CastDevice device);
    }

    public interface CastDeviceListView
    {
        public interface CastDeviceListViewListener
        {
            void castDeviceSelected(CastDevice selectedCastDevice);
        }

        public void setListener(CastDeviceListViewListener listener);
        public void displayCastDevices(List<CastDevice> castDevices);
        public void displayNoCastDevices();
    }

    private final CastDeviceListView castDeviceListView;
    private final CastDeviceListPresenterListener listener;

    private final CastDeviceListView.CastDeviceListViewListener castDeviceListViewListener = new CastDeviceListView.CastDeviceListViewListener() {
        @Override
        public void castDeviceSelected(CastDevice selectedCastDevice) {
            listener.castRequested(selectedCastDevice);
        }
    };

    public CastDeviceListPresenter(CastDeviceListPresenterListener listener, CastDeviceListView castDeviceListView)
    {
        this.listener = listener;
        this.castDeviceListView = castDeviceListView;
        castDeviceListView.setListener(castDeviceListViewListener);
    }

    @Override
    public void castDevicesAvailable(List<CastDevice> castDevices) {
        castDeviceListView.displayCastDevices(castDevices);
    }

    @Override
    public void castDevicesUnavailable() {
        castDeviceListView.displayNoCastDevices();
    }
}
