package fd.com.castanyvid;

import android.content.Context;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;

import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;

import fd.com.castanyvid.castservice.CastDeviceFinder;
import fd.com.castanyvid.castservice.CastService;

class GoogleApiCastDeviceFinder implements CastDeviceFinder {
    private final MediaRouteSelector mediaRouteSelector;
    private final MediaRouter mediaRouter;
    private android.support.v7.media.MediaRouter.Callback mediaRouterCallback = new MediaRouter.Callback() {
        @Override
        public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteAdded(router, route);
            reportCastDeviceFound(CastDevice.getFromBundle(route.getExtras()));
        }

        @Override
        public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteRemoved(router, route);
            reportCastDeviceLost(CastDevice.getFromBundle(route.getExtras()));
        }

        @Override
        public void onRouteChanged(MediaRouter router, MediaRouter.RouteInfo route) {
            super.onRouteChanged(router, route);
        }
    };

    private CastDeviceFinderListener castDeviceFinderListener;

    private void reportCastDeviceFound(CastDevice castDevice) {
        castDeviceFinderListener.castDeviceFound(castDevice);
    }

    private void reportCastDeviceLost(CastDevice castDevice) {
        castDeviceFinderListener.castDeviceLost(castDevice);
    }

    public GoogleApiCastDeviceFinder(Context context) {
        mediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(CastMediaControlIntent.categoryForCast(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID))
                .build();

        mediaRouter = MediaRouter.getInstance(context);
    }


    @Override
    public void addListener(CastDeviceFinderListener listener) {
        castDeviceFinderListener = listener;
    }

    @Override
    public void startSearching() {
        mediaRouter.addCallback(mediaRouteSelector, mediaRouterCallback,
                MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY);
    }

    @Override
    public void stopSearching() {
        mediaRouter.removeCallback(mediaRouterCallback);
    }
}