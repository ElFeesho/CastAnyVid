package fd.com.castanyvid;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class CastAVidApplication extends Application {

    private CastService castService;

    public static CastService getCastService(Context context) {
        return ((CastAVidApplication) (context.getApplicationContext())).castService;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        castService = new CastService(new GoogleApiCastDeviceFinder(this), new GoogleApiCastProvider(this));
    }

    private static class GoogleApiCastProvider implements CastService.CastProvider
    {
        private Context context;
        private GoogleApiClient apiClient;
        private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
            public boolean waitingForReconnect;

            @Override
            public void onConnected(Bundle bundle) {
                if(waitingForReconnect)
                {
                    waitingForReconnect = false;
                    // ... reconnect some stuff!
                    listener.castSessionAvailable(new GoogleCastSession(apiClient));
                }
                else
                {
                    try
                    {
                        Cast.CastApi.launchApplication(apiClient, CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID, false).setResultCallback(
                            new ResultCallback<Cast.ApplicationConnectionResult>() {
                                @Override
                                public void onResult(Cast.ApplicationConnectionResult result) {
                                    Status status = result.getStatus();
                                    if (status.isSuccess()) {
                                        listener.castSessionAvailable(new GoogleCastSession(apiClient));
                                    } else {
                                        teardown();
                                    }
                                }

                            });
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onConnectionSuspended(int reason) {
                waitingForReconnect = true;
                listener.castSessionLost();
            }
        };
        private CastProviderListener listener;
        private Cast.Listener castConnectionListener = new Cast.Listener()
        {
            @Override
            public void onApplicationStatusChanged() {
                super.onApplicationStatusChanged();
            }

            @Override
            public void onApplicationDisconnected(int statusCode) {
                super.onApplicationDisconnected(statusCode);
                listener.castSessionLost();
            }
        };

        private GoogleApiClient.OnConnectionFailedListener connectionFailedCallbacks = new GoogleApiClient.OnConnectionFailedListener()
        {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {

            }
        };

        public GoogleApiCastProvider(Context context)
        {
            this.context = context;
        }

        @Override
        public void setListener(CastProviderListener listener) {
            this.listener = listener;
        }

        @Override
        public void castRequestedForDevice(CastDevice device) {
            Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                    .builder(device, castConnectionListener);

            apiClient = new GoogleApiClient.Builder(context)
                    .addApi(Cast.API, apiOptionsBuilder.build())
                    .addConnectionCallbacks(connectionCallbacks)
                    .addOnConnectionFailedListener(connectionFailedCallbacks)
                    .build();

            apiClient.connect();
        }

        private void teardown() {
        }
    }


}
