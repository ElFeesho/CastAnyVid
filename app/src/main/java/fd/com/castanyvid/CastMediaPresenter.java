package fd.com.castanyvid;

import com.google.android.gms.cast.CastDevice;

import java.util.List;

public class CastMediaPresenter implements CastService.CastServiceListener {

    private final CastMediaView castMediaView;
    private final CastMediaView.Listener castMediaViewListener = new CastMediaView.Listener() {
        @Override
        public void castMedia(String mediaUrl) {
            castSession.loadUrl(mediaUrl);
        }
    };
    private CastService.CastSession castSession;

    public CastMediaPresenter(CastMediaView castMediaView) {
        this.castMediaView = castMediaView;
        castMediaView.setListener(castMediaViewListener);
    }

    @Override
    public void castDevicesAvailable(List<CastDevice> castDevices) {
    }

    @Override
    public void castDevicesUnavailable() {
    }

    @Override
    public void castSessionAvailable(CastService.CastSession castSession) {
        castMediaView.allowUse();
        this.castSession = castSession;
    }

    @Override
    public void castSessionUnavailable() {
        castMediaView.disallowUse();
        castSession = null;
    }

    public interface CastMediaView {
        void displayMediaUrl(String mediaUrl);

        void allowUse();

        void disallowUse();

        void setListener(Listener listener);

        public interface Listener {
            void castMedia(String mediaUrl);
        }
    }


}
