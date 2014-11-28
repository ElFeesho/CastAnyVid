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

        @Override
        public void stopCasting() {
            listener.stopCastRequested();
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
        castDeviceListView.lockDeviceSelection();
        castDeviceListView.allowStopCast();
    }

    @Override
    public void castSessionUnavailable() {
        castDeviceListView.unlockDeviceSelection();
        castDeviceListView.allowStartCast();
    }

    @Override
    public void castSessionStopped() {
        castDeviceListView.unlockDeviceSelection();
        castDeviceListView.allowStartCast();
    }

    public interface CastDeviceListView {
        public void setListener(CastDeviceListViewListener listener);

        public void displayCastDevices(List<CastDevice> castDevices);

        public void displayNoCastDevices();

        public void lockDeviceSelection();

        public void unlockDeviceSelection();

        public void allowStartCast();

        public void allowStopCast();

        public interface CastDeviceListViewListener {
            void castDeviceSelected(CastDevice selectedCastDevice);

            void stopCasting();
        }
    }

    public interface CastDeviceListPresenterListener {
        void castRequested(CastDevice device);

        void stopCastRequested();
    }

}
