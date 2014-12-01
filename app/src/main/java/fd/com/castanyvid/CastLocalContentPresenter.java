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

    private final CastLocalContentView contentView;
    private final CastLocalContentSearchProvider searchProvider;

    private CastLocalContentView.Listener viewListener = new CastLocalContentView.Listener() {
        @Override
        public void requestSearch() {
            searchProvider.searchForLocalContent();
        }

        @Override
        public void requestPlay(String uri) {

        }
    };

    public CastLocalContentPresenter(CastLocalContentView contentView, CastLocalContentSearchProvider searchProvider) {
        this.contentView = contentView;
        this.searchProvider = searchProvider;

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
    }

    @Override
    public void castSessionUnavailable() {
        contentView.disallowUse();
    }

    @Override
    public void castSessionStopped() {
        contentView.disallowUse();
    }

    @Override
    public void castSessionStarting() {

    }
}
