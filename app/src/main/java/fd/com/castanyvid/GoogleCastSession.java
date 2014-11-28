package fd.com.castanyvid;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;

public class GoogleCastSession implements CastService.CastSession {

    private RemoteMediaPlayer remoteMediaPlayer;
    private GoogleApiClient apiClient;

    public GoogleCastSession(GoogleApiClient apiClient) {
        this.apiClient = apiClient;

        remoteMediaPlayer = new RemoteMediaPlayer();
        try {
            Cast.CastApi.setMessageReceivedCallbacks(apiClient, remoteMediaPlayer.getNamespace(), remoteMediaPlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadUrl(String url) {

    }
}
