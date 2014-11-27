package fd.com.castanyvid;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
        castDeviceListPresenter = new CastDeviceListPresenter((CastDeviceListPresenter.CastDeviceListView) findViewById(R.id.cast_device_list_view));
        CastService castService = CastAVidApplication.getCastService(this);
        castService.addListener(castDeviceListPresenter);
        castService.startSearchingForDevices();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CastService castService = CastAVidApplication.getCastService(this);
        castService.stopSearchingForDevices();
        castService.removeListener(castDeviceListPresenter);

    }
}
