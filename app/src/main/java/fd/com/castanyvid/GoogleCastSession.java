package fd.com.castanyvid;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;

public class GoogleCastSession implements CastService.CastSession {

    private final RemoteMediaPlayer.OnStatusUpdatedListener statusUpdateListener = new RemoteMediaPlayer.OnStatusUpdatedListener() {
        @Override
        public void onStatusUpdated() {

        }
    };
    private final RemoteMediaPlayer.OnMetadataUpdatedListener metadataUpdateListener = new RemoteMediaPlayer.OnMetadataUpdatedListener() {
        @Override
        public void onMetadataUpdated() {

        }
    };

    private final RemoteMediaPlayer remoteMediaPlayer;
    private final GoogleApiClient apiClient;

    public GoogleCastSession(GoogleApiClient apiClient) {
        this.apiClient = apiClient;

        remoteMediaPlayer = new RemoteMediaPlayer();
        remoteMediaPlayer.setOnStatusUpdatedListener(statusUpdateListener);
        remoteMediaPlayer.setOnMetadataUpdatedListener(metadataUpdateListener);
        try {
            Cast.CastApi.setMessageReceivedCallbacks(apiClient, remoteMediaPlayer.getNamespace(), remoteMediaPlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadUrl(String url) {
        MediaInfo mediaInfo = new MediaInfo.Builder(url).setStreamType(MediaInfo.STREAM_TYPE_BUFFERED).setContentType("video/mp4").build();
        remoteMediaPlayer.load(apiClient, mediaInfo, true);
    }
}
