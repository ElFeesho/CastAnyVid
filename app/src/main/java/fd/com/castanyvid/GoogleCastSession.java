package fd.com.castanyvid;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import java.io.IOException;

public class GoogleCastSession implements CastService.CastSession {

    private final RemoteMediaPlayer.OnStatusUpdatedListener statusUpdateListener = new RemoteMediaPlayer.OnStatusUpdatedListener() {
        @Override
        public void onStatusUpdated() {
            int playerState = remoteMediaPlayer.getMediaStatus().getPlayerState();

            if (playerState == MediaStatus.PLAYER_STATE_PLAYING) {
                listener.mediaPlaying();
            } else if (playerState == MediaStatus.PLAYER_STATE_BUFFERING) {
                listener.mediaBuffering();
            } else if (playerState == MediaStatus.PLAYER_STATE_PAUSED) {
                listener.mediaPaused();
            } else if (playerState == MediaStatus.PLAYER_STATE_UNKNOWN) {
                listener.mediaBuffering();
            }
        }
    };
    private final RemoteMediaPlayer.OnMetadataUpdatedListener metadataUpdateListener = new RemoteMediaPlayer.OnMetadataUpdatedListener() {
        @Override
        public void onMetadataUpdated() {

        }
    };

    private final RemoteMediaPlayer remoteMediaPlayer;
    private final GoogleApiClient apiClient;
    private Listener listener;

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
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void loadUrl(String url) {
        MediaInfo mediaInfo = new MediaInfo.Builder(url).setStreamType(MediaInfo.STREAM_TYPE_BUFFERED).setContentType("video/mp4").build();
        remoteMediaPlayer.load(apiClient, mediaInfo, true).setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
            @Override
            public void onResult(RemoteMediaPlayer.MediaChannelResult mediaChannelResult) {
                if (mediaChannelResult.getStatus().isSuccess()) {
                    listener.mediaLoaded(new Timestamp(remoteMediaPlayer.getApproximateStreamPosition()), new Duration(remoteMediaPlayer.getStreamDuration()));
                }
            }
        });
    }

    @Override
    public void scrubTo(Timestamp timestamp) {
        remoteMediaPlayer.seek(apiClient, timestamp.milliseconds);
    }

    @Override
    public void play() {
        remoteMediaPlayer.play(apiClient).setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
            @Override
            public void onResult(RemoteMediaPlayer.MediaChannelResult mediaChannelResult) {
                if (mediaChannelResult.getStatus().isSuccess()) {
                    listener.mediaPlaying();
                }
            }
        });
    }

    @Override
    public void pause() {
        remoteMediaPlayer.pause(apiClient).setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
            @Override
            public void onResult(RemoteMediaPlayer.MediaChannelResult mediaChannelResult) {
                if (mediaChannelResult.getStatus().isSuccess()) {
                    listener.mediaPaused();
                }
            }
        });
    }
}
