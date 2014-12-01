package fd.com.castanyvid;

import android.os.Handler;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.RemoteMediaPlayer;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fd.com.castanyvid.castservice.CastService;
import fd.com.castanyvid.castservice.CastSession;

public class GoogleCastSession implements CastSession {

    private final RemoteMediaPlayer remoteMediaPlayer;
    private final GoogleApiClient apiClient;
    private final Handler repeatHandler = new Handler();

    private final RemoteMediaPlayer.OnStatusUpdatedListener statusUpdateListener = new RemoteMediaPlayer.OnStatusUpdatedListener() {
        @Override
        public void onStatusUpdated() {
            if (remoteMediaPlayer.getMediaStatus() == null) {
                return;
            }

            int playerState = remoteMediaPlayer.getMediaStatus().getPlayerState();

            if (playerState == MediaStatus.PLAYER_STATE_PLAYING) {
                notifyMediaPlaying();
                startTimeUpdating();
            } else if (playerState == MediaStatus.PLAYER_STATE_BUFFERING) {
                notifyMediaBuffering();
                stopTimeUpdating();
            } else if (playerState == MediaStatus.PLAYER_STATE_PAUSED) {
                notifyMediaPaused();
                stopTimeUpdating();
            } else if (playerState == MediaStatus.PLAYER_STATE_UNKNOWN) {
                notifyMediaBuffering();
                stopTimeUpdating();
            }
        }
    };

    private final RemoteMediaPlayer.OnMetadataUpdatedListener metadataUpdateListener = new RemoteMediaPlayer.OnMetadataUpdatedListener() {
        @Override
        public void onMetadataUpdated() {
            if (remoteMediaPlayer.getMediaInfo() != null) {
                remoteMediaPlayer.requestStatus(apiClient);
                notifyMediaLoaded();
            }
        }
    };

    private Runnable pollTimeRunnable = new Runnable() {
        @Override
        public void run() {
            notifyMediaTimeUpdated();
            repeatHandler.postDelayed(this, 250);
        }
    };

    private List<Listener> listeners = new ArrayList<Listener>();

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
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void endSession() {
        stopTimeUpdating();
        listeners.clear();
    }

    @Override
    public void loadUrl(String url) {
        MediaInfo mediaInfo = new MediaInfo.Builder(url).setStreamType(MediaInfo.STREAM_TYPE_BUFFERED).setContentType("video/mp4").build();
        remoteMediaPlayer.load(apiClient, mediaInfo, true).setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
            @Override
            public void onResult(RemoteMediaPlayer.MediaChannelResult mediaChannelResult) {
                if (mediaChannelResult.getStatus().isSuccess()) {
                    notifyMediaLoaded();
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
                    notifyMediaPlaying();
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
                    notifyMediaPaused();
                    stopTimeUpdating();
                }
            }
        });
    }

    @Override
    public void increaseVolume() {
        try {
            Cast.CastApi.setVolume(apiClient, Cast.CastApi.getVolume(apiClient) + 0.1f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decreaseVolume() {
        try {
            Cast.CastApi.setVolume(apiClient, Cast.CastApi.getVolume(apiClient) - 0.1f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startTimeUpdating() {
        repeatHandler.removeCallbacks(pollTimeRunnable);
        repeatHandler.post(pollTimeRunnable);
    }

    private void stopTimeUpdating() {
        repeatHandler.removeCallbacks(pollTimeRunnable);
    }

    private void notifyMediaPlaying() {
        for(Listener listener : listeners) {
            listener.mediaPlaying();
        }
    }

    private void notifyMediaPaused() {
        for(Listener listener : listeners) {
            listener.mediaPaused();
        }
    }

    private void notifyMediaBuffering() {
        for(Listener listener : listeners) {
            listener.mediaBuffering();
        }
    }

    private void notifyMediaTimeUpdated() {
        for(Listener listener : listeners) {
            listener.mediaPositionUpdate(new Timestamp(remoteMediaPlayer.getApproximateStreamPosition()));
        }
    }

    private void notifyMediaLoaded() {
        for(Listener listener : listeners) {
            listener.mediaLoaded(remoteMediaPlayer.getMediaInfo().getContentId(), new Timestamp(remoteMediaPlayer.getApproximateStreamPosition()), new Duration(remoteMediaPlayer.getStreamDuration()));
        }
    }
}
