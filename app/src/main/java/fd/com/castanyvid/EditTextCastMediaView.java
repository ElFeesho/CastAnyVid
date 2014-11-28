package fd.com.castanyvid;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditTextCastMediaView implements CastMediaPresenter.CastMediaView {

    private final EditText castMediaUrl;
    private final Button castMediaButton;
    private Listener listener;

    public EditTextCastMediaView(final EditText castMediaUrl, Button castMediaButton) {
        this.castMediaUrl = castMediaUrl;
        this.castMediaButton = castMediaButton;

        castMediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.castMedia(castMediaUrl.getText().toString());
            }
        });
    }

    @Override
    public void displayMediaUrl(String mediaUrl) {
        castMediaUrl.setText(mediaUrl);
    }

    @Override
    public void allowUse() {
        castMediaUrl.setEnabled(true);
        castMediaButton.setEnabled(true);
    }

    @Override
    public void disallowUse() {
        castMediaUrl.setEnabled(false);
        castMediaButton.setEnabled(false);
    }

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
