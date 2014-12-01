package fd.com.castanyvid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.cast.CastDevice;

import fd.com.castanyvid.castservice.CastService;


public class MainActivity extends ActionBarActivity {

    private CastDeviceListPresenter castDeviceListPresenter;
    private CastMediaPresenter castMediaPresenter;
    private CastPlaybackControlPresenter castPlaybackControlPresenter;
    private CastLocalContentPresenter castLocalContentPresenter;

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

        castDeviceListPresenter = new CastDeviceListPresenter(new CastDeviceListPresenter.CastDeviceListPresenterListener() {
            @Override
            public void castRequested(CastDevice device) {
                castService.requestCast(device);
            }

            @Override
            public void stopCastRequested() {
                castService.stopCasting();
            }
        }, new SpinnerCastDeviceListView((Spinner) findViewById(R.id.cast_device_list_view), (Button) findViewById(R.id.cast_device_list_cast), (Button) findViewById(R.id.cast_device_list_stop_casting)));

        EditText castContentUri = (EditText) findViewById(R.id.cast_content_uri);
        castContentUri.setText(getIntent().getDataString());

        castMediaPresenter = new CastMediaPresenter(new EditTextCastMediaView((EditText) findViewById(R.id.cast_content_uri), (Button) findViewById(R.id.cast_play_content)));

        castPlaybackControlPresenter = new CastPlaybackControlPresenter(new SeekbarPlaybackControlView((SeekBar) findViewById(R.id.playback_position), (TextView) findViewById(R.id.currentPosition), (TextView) findViewById(R.id.duration), findViewById(R.id.play_button), findViewById(R.id.pause_button), (ProgressBar) findViewById(R.id.buffer_indicator)));

        castLocalContentPresenter = new CastLocalContentPresenter(new LocalContentView((EditText)findViewById(R.id.cast_local_content_uri), (Button)findViewById(R.id.cast_select_local_content), (Button)findViewById(R.id.cast_play_local_content)), new CastLocalContentPresenter.CastLocalContentSearchProvider() {
            @Override
            public void searchForLocalContent() {
                startActivityForResult(new Intent(Intent.ACTION_PICK).setType("video/*"), 1);
            }
        });

        castService.addListener(castDeviceListPresenter, castMediaPresenter, castPlaybackControlPresenter, castLocalContentPresenter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            castLocalContentPresenter.displayUri(data.getDataString());
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    CastAVidApplication.getCastService(this).requestVolumeIncrease();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    CastAVidApplication.getCastService(this).requestVolumeDecrement();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CastService castService = CastAVidApplication.getCastService(this);
        castService.stopSearchingForDevices();
        castService.removeListener(castDeviceListPresenter);
        castService.removeListener(castMediaPresenter);
        castService.removeListener(castPlaybackControlPresenter);
        castService.removeListener(castLocalContentPresenter);

    }
}
