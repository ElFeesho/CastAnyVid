package fd.com.castanyvid;

import com.google.android.gms.cast.CastDevice;

import java.util.List;

public class CastDeviceListPresenter implements CastService.CastServiceListener {

    private final CastDeviceListView castDeviceListView;
    private final CastDeviceListPresenterListener listener;
    private final CastDeviceListView.CastDeviceListViewListener castDeviceListViewListener = new CastDeviceListView.CastDeviceListViewListener() {
        @Override
        public void castDeviceSelected(CastDevice selectedCastDevice) {
            listener.castRequested(selectedCastDevice);
        }
    };

    public CastDeviceListPresenter(CastDeviceListPresenterListener listener, CastDeviceListView castDeviceListView) {
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

    @Override
    public void castSessionAvailable(CastService.CastSession castSession) {
    }

    @Override
    public void castSessionUnavailable() {
    }

    public interface CastDeviceListPresenterListener {
        void castRequested(CastDevice device);
    }

    public interface CastDeviceListView {
        public void setListener(CastDeviceListViewListener listener);

        public void displayCastDevices(List<CastDevice> castDevices);

        public void displayNoCastDevices();

        public interface CastDeviceListViewListener {
            void castDeviceSelected(CastDevice selectedCastDevice);
        }
    }
}
