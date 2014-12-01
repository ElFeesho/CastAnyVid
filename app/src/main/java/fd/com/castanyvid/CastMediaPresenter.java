package fd.com.castanyvid;

import com.google.android.gms.cast.CastDevice;

import java.util.List;

import fd.com.castanyvid.castservice.CastService;
import fd.com.castanyvid.castservice.CastServiceListener;
import fd.com.castanyvid.castservice.CastSession;

public class CastMediaPresenter implements CastServiceListener {

    public interface CastMediaView {
        void displayMediaUrl(String mediaUrl);

        void allowUse();

        void disallowUse();

        void setListener(Listener listener);

        public interface Listener {
            void castMedia(String mediaUrl);
        }
    }

    private final CastMediaView castMediaView;
    private final CastMediaView.Listener castMediaViewListener = new CastMediaView.Listener() {
        @Override
        public void castMedia(String mediaUrl) {
            castSession.loadUrl(mediaUrl);
        }
    };

    CastSession.Listener sessionListener = new CastSession.Listener() {
        @Override
        public void mediaPlaying() {}

        @Override
        public void mediaPaused() {}

        @Override
        public void mediaLoaded(String contentId, Timestamp timestamp, Duration duration) {
            castMediaView.displayMediaUrl(contentId);
        }

        @Override
        public void mediaPositionUpdate(Timestamp timestamp) {}

        @Override
        public void mediaBuffering() {}
    };

    private CastSession castSession;

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
    public void castSessionAvailable(CastSession castSession) {
        castMediaView.allowUse();
        this.castSession = castSession;

        this.castSession.addListener(sessionListener);
    }

    @Override
    public void castSessionUnavailable() {
        teardownSession();
    }

    @Override
    public void castSessionStopped() {
        teardownSession();
    }

    @Override
    public void castSessionStarting() {}

    private void teardownSession() {
        castMediaView.disallowUse();
        if(castSession != null) {
            castSession.removeListener(sessionListener);
        }
        castSession = null;
    }
}
