package fd.com.castanyvid;

import com.google.android.gms.cast.CastDevice;

import java.util.List;

/**
 * Created by chris on 28/11/14.
 */
public class CastPlaybackControlPresenter implements CastService.CastServiceListener {

    private final CastPlaybackControlView castPlaybackControlView;
    private boolean isScrubbing = false;
    private final CastPlaybackControlView.Listener castPlaybackControlListener = new CastPlaybackControlView.Listener() {
        @Override
        public void scrubTo(Timestamp timestamp) {
            castSession.scrubTo(timestamp);
            isScrubbing = false;
        }

        @Override
        public void pause() {
            castSession.pause();
        }

        @Override
        public void play() {
            castSession.play();
        }

        @Override
        public void startedScrubbing() {
            isScrubbing = true;
        }
    };
    private final CastService.CastSession.Listener castSessionListener = new CastService.CastSession.Listener() {
        @Override
        public void mediaPlaying() {
            castPlaybackControlView.playing();
        }

        @Override
        public void mediaPaused() {
            castPlaybackControlView.paused();
        }

        @Override
        public void mediaLoaded(String contentId, Timestamp timestamp, Duration duration) {
            castPlaybackControlView.setCurrentPlaybackPosition(timestamp);
            castPlaybackControlView.setDuration(duration);
        }

        @Override
        public void mediaPositionUpdate(Timestamp timestamp) {
            if (!isScrubbing) {
                castPlaybackControlView.setCurrentPlaybackPosition(timestamp);
            }
        }

        @Override
        public void mediaBuffering() {
            castPlaybackControlView.buffering();
        }
    };
    private CastService.CastSession castSession;

    public CastPlaybackControlPresenter(CastPlaybackControlView castPlaybackControlView) {
        this.castPlaybackControlView = castPlaybackControlView;
        castPlaybackControlView.setListener(castPlaybackControlListener);
    }

    @Override
    public void castDevicesAvailable(List<CastDevice> castDevices) {
    }

    @Override
    public void castDevicesUnavailable() {
    }

    @Override
    public void castSessionAvailable(CastService.CastSession castSession) {
        this.castSession = castSession;
        castSession.setListener(castSessionListener);
        castPlaybackControlView.allowUse();
    }

    @Override
    public void castSessionUnavailable() {
        castPlaybackControlView.disallowUse();
        castPlaybackControlView.reset();
    }

    @Override
    public void castSessionStopped() {
        castPlaybackControlView.disallowUse();
        castPlaybackControlView.reset();
    }

    public interface CastPlaybackControlView {
        void allowUse();

        void buffering();

        void disallowUse();

        void playing();

        void paused();

        void reset();

        void setListener(Listener listener);

        void setDuration(Duration duration);

        void setCurrentPlaybackPosition(Timestamp timestamp);

        public interface Listener {
            void scrubTo(Timestamp timestamp);

            void pause();

            void play();

            void startedScrubbing();
        }
    }
}
