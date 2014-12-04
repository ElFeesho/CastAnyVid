package com.fd.castanyvid.lib.castservice;

public interface CastSession {
    void addListener(Listener listener);

    void removeListener(Listener listener);

    void endSession();

    void loadUrl(String url);

    void scrubTo(Timestamp timestamp);

    void play();

    void pause();

    void increaseVolume();

    void decreaseVolume();

    public interface Listener {
        void mediaPlaying();

        void mediaPaused();

        void mediaLoaded(String contentId, Timestamp timestamp, Duration duration);

        void mediaPositionUpdate(Timestamp timestamp);

        void mediaBuffering();
    }

}