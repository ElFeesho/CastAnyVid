package fd.com.castanyvid;

import com.google.android.gms.cast.CastDevice;

import java.util.List;

import fd.com.castanyvid.castservice.CastServiceListener;
import fd.com.castanyvid.castservice.CastSession;

/**
 * Created by sawczc01 on 01/12/2014.
 */
public class CastLocalContentPresenter implements CastServiceListener {

    public interface CastLocalContentView
    {
        interface Listener
        {
            public void requestSearch();
            public void requestPlay(String uri);
        }
        public void setListener(Listener listener);
        public void displayUri(String uri);
        public void allowUse();
        public void disallowUse();
    }

    public interface CastLocalContentSearchProvider
    {
        public void searchForLocalContent();
    }

    public interface CastLocalContentCastProvider
    {
        public interface CastLocalContentCastProviderListener
        {
            public void localStreamAvailable(String atUri);
        }

        public void playContent(String uri, CastLocalContentCastProviderListener listener);
    }

    private final CastLocalContentView contentView;
    private final CastLocalContentSearchProvider searchProvider;
    private final CastLocalContentCastProvider castProvider;
    private CastSession castSession;

    private CastLocalContentView.Listener viewListener = new CastLocalContentView.Listener() {
        @Override
        public void requestSearch() {
            searchProvider.searchForLocalContent();
        }

        @Override
        public void requestPlay(String uri) {
            castProvider.playContent(uri, new CastLocalContentCastProvider.CastLocalContentCastProviderListener() {
                @Override
                public void localStreamAvailable(String atUri) {
                    castSession.loadUrl(atUri);
                }
            });
        }
    };

    public CastLocalContentPresenter(CastLocalContentView contentView, CastLocalContentSearchProvider searchProvider, CastLocalContentCastProvider castProvider) {
        this.contentView = contentView;
        this.searchProvider = searchProvider;
        this.castProvider = castProvider;

        contentView.setListener(viewListener);
    }

    public void displayUri(String uri)
    {
        contentView.displayUri(uri);
    }

    @Override
    public void castDevicesAvailable(List<CastDevice> castDevices) {}

    @Override
    public void castDevicesUnavailable() {}

    @Override
    public void castSessionAvailable(CastSession castSession) {
        contentView.allowUse();
        this.castSession = castSession;
    }

    @Override
    public void castSessionUnavailable() {
        contentView.disallowUse();
        castSession = null;
    }

    @Override
    public void castSessionStopped() {
        contentView.disallowUse();
        castSession = null;
    }

    @Override
    public void castSessionStarting() {

    }
}
