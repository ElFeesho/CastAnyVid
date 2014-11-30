package fd.com.castanyvid;

import android.os.Handler;

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
                startTimeUpdating();
            } else if (playerState == MediaStatus.PLAYER_STATE_BUFFERING) {
                listener.mediaBuffering();
                stopTimeUpdating();
            } else if (playerState == MediaStatus.PLAYER_STATE_PAUSED) {
                listener.mediaPaused();
                stopTimeUpdating();
            } else if (playerState == MediaStatus.PLAYER_STATE_UNKNOWN) {
                listener.mediaBuffering();
                stopTimeUpdating();
            }
        }
    };
    private final RemoteMediaPlayer remoteMediaPlayer;
    private final GoogleApiClient apiClient;
    private final RemoteMediaPlayer.OnMetadataUpdatedListener metadataUpdateListener = new RemoteMediaPlayer.OnMetadataUpdatedListener() {
        @Override
        public void onMetadataUpdated() {
            remoteMediaPlayer.requestStatus(apiClient);
            listener.mediaLoaded(remoteMediaPlayer.getMediaInfo().getContentId(), new Timestamp(remoteMediaPlayer.getApproximateStreamPosition()), new Duration(remoteMediaPlayer.getStreamDuration()));
        }
    };
    private Handler repeatHandler = new Handler();
    private Runnable pollTimeRunnable = new Runnable() {
        @Override
        public void run() {
            listener.mediaPositionUpdate(new Timestamp(remoteMediaPlayer.getApproximateStreamPosition()));
            repeatHandler.postDelayed(this, 250);
        }
    };
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
        remoteMediaPlayer.requestStatus(apiClient);
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
                    listener.mediaLoaded(remoteMediaPlayer.getMediaInfo().getContentId(), new Timestamp(remoteMediaPlayer.getApproximateStreamPosition()), new Duration(remoteMediaPlayer.getStreamDuration()));
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
                    startTimeUpdating();
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
                    stopTimeUpdating();
                }
            }
        });
    }

    private void startTimeUpdating() {
        repeatHandler.removeCallbacks(pollTimeRunnable);
        repeatHandler.post(pollTimeRunnable);
    }

    private void stopTimeUpdating() {
        repeatHandler.removeCallbacks(pollTimeRunnable);
    }
}
