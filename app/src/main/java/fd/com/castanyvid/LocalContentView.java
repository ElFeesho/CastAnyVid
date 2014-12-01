package fd.com.castanyvid;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by sawczc01 on 01/12/2014.
 */
public class LocalContentView implements CastLocalContentPresenter.CastLocalContentView {
    private Listener listener;
    private final EditText localUri;
    private final Button searchButton;
    private final Button playButton;

    public LocalContentView(final EditText localUri, Button searchButton, Button playButton) {
        this.localUri = localUri;
        this.searchButton = searchButton;
        this.playButton = playButton;

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.requestSearch();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.requestPlay(localUri.getText().toString());
            }
        });
    }

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void displayUri(String uri) {
        localUri.setText(uri);
    }

    @Override
    public void allowUse() {
        localUri.setEnabled(true);
        searchButton.setEnabled(true);
        playButton.setEnabled(true);
    }

    @Override
    public void disallowUse() {
        localUri.setEnabled(false);
        searchButton.setEnabled(false);
        playButton.setEnabled(false);
    }
}
