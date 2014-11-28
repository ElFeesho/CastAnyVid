package fd.com.castanyvid;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.cast.CastDevice;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private CastDeviceListPresenter castDeviceListPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final CastService castService = CastAVidApplication.getCastService(this);
        castService.startSearchingForDevices();
        Spinner castDeviceListSpinner = (Spinner) findViewById(R.id.cast_device_list_view);
        Button castDeviceListCastButton = (Button) findViewById(R.id.cast_device_list_cast);
        castDeviceListPresenter = new CastDeviceListPresenter(new CastDeviceListPresenter.CastDeviceListPresenterListener() {
            @Override
            public void castRequested(CastDevice device) {
                castService.requestCast(device);
            }
        },new SpinnerCastDeviceListView(castDeviceListSpinner, castDeviceListCastButton));
        castService.addListener(castDeviceListPresenter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CastService castService = CastAVidApplication.getCastService(this);
        castService.stopSearchingForDevices();
        castService.removeListener(castDeviceListPresenter);

    }
}
