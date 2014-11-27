package fd.com.castanyvid;

import com.google.android.gms.cast.CastDevice;

import java.util.List;

/**
 * Created by chris on 27/11/14.
 */
public class CastDeviceListPresenter implements CastService.CastServiceListener {

    private final CastDeviceListView castDeviceListView;

    public interface CastDeviceListView
    {
        public void displayCastDevices(List<CastDevice> castDevices);
        public void displayNoCastDevices();
    }

    public CastDeviceListPresenter(CastDeviceListView castDeviceListView)
    {
        this.castDeviceListView = castDeviceListView;
    }

    @Override
    public void castDeviceListChanged(List<CastDevice> castDevices) {
        if(castDevices.size() > 0)
        {
            castDeviceListView.displayCastDevices(castDevices);
        }
        else
        {
            castDeviceListView.displayNoCastDevices();
        }
    }
}
