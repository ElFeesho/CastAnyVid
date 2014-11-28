package fd.com.castanyvid;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.cast.CastDevice;


public class MainActivity extends ActionBarActivity {

    private CastDeviceListPresenter castDeviceListPresenter;
    private CastMediaPresenter castMediaPresenter;

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

        castMediaPresenter = new CastMediaPresenter(new EditTextCastMediaView((EditText) findViewById(R.id.cast_content_uri), (Button) findViewById(R.id.cast_play_content)));
        castService.addListener(castDeviceListPresenter, castMediaPresenter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CastService castService = CastAVidApplication.getCastService(this);
        castService.stopSearchingForDevices();
        castService.removeListener(castDeviceListPresenter);
        castService.removeListener(castMediaPresenter);

    }
}
