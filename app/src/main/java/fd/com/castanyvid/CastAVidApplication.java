package fd.com.castanyvid;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.e;

public class CastAVidApplication extends Application {

    private CastService castService;

    @Override
    public void onCreate() {
        super.onCreate();

        castService = new CastService(new GoogleApiCastDeviceFinder(this), new GoogleApiCastProvider(this));
    }

    public static CastService getCastService(Context context) {
        return ((CastAVidApplication)(context.getApplicationContext())).castService;
    }

    private static class GoogleApiCastProvider implements CastService.CastProvider
    {
        private Context context;
        private GoogleApiClient apiClient;
        private CastProviderListener listener;

        private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
            public boolean waitingForReconnect;

            @Override
            public void onConnected(Bundle bundle) {
                if(waitingForReconnect)
                {
                    waitingForReconnect = false;
                    // ... reconnect some stuff!
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
                                        ApplicationMetadata applicationMetadata =
                                                result.getApplicationMetadata();
                                        String sessionId = result.getSessionId();
                                        String applicationStatus = result.getApplicationStatus();
                                        boolean wasLaunched = result.getWasLaunched();
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
            }
        };

        private Cast.Listener castConnectionListener = new Cast.Listener()
        {
            @Override
            public void onApplicationStatusChanged() {
                super.onApplicationStatusChanged();
            }

            @Override
            public void onApplicationDisconnected(int statusCode) {
                super.onApplicationDisconnected(statusCode);

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
