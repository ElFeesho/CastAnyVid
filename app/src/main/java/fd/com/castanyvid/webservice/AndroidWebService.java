package fd.com.castanyvid.webservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.format.Formatter;

import java.io.InputStream;

import fd.com.castanyvid.MainActivity;

/**
 * Created by sawczc01 on 01/12/2014.
 */
public class AndroidWebService implements WebService {

    public static class AndroidWebServiceService extends Service
    {
        public static final String STOP_SERVICE = "stop";

        public static class AWSSBinder extends Binder
        {
            private final AndroidWebServiceService service;

            public AWSSBinder(AndroidWebServiceService androidWebServiceService) {
                this.service = androidWebServiceService;
            }

            public String host(String data) {
                return service.host(data);
            }
        }

        private StreamHoster streamHoster;

        private String host(String data) {
            streamHoster = new StreamHoster(data);
            streamHoster.startHosting();
            return getHostUrl();
        }

        private String getHostUrl() {
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            int ipAddr = wifiManager.getConnectionInfo().getIpAddress();
            return String.format("http://%s:8081", ipAddrToString(ipAddr));

        }

        private String ipAddrToString(int ipAddr) {
            return  Formatter.formatIpAddress(ipAddr);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return new AWSSBinder(this);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            if(intent != null && intent.getAction() != null && intent.getAction().contentEquals(STOP_SERVICE))
            {
                stopForeground(true);
                stopSelf();
                return START_NOT_STICKY;
            }

            startForeground(1, buildForegroundNotification());
            return START_STICKY;
        }

        private Notification buildForegroundNotification() {
            return new NotificationCompat.Builder(this)
                    .setAutoCancel(false)
                    .setContentInfo("CasyAnyVid Local Video Casting")
                    .setContentText("Your content is ready to be casted!")
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_ONE_SHOT))
                    .setContentTitle("CastAnyVid")
                    .setOngoing(true)
                    .setTicker("CastAnyVid")
                    .build();

        }
    }

    private static class AWSServiceConnection implements ServiceConnection
    {
        private Listener listener;
        private AndroidWebServiceService.AWSSBinder binder;

        public AWSServiceConnection(Listener listener) {
            this.listener = listener;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (AndroidWebServiceService.AWSSBinder)service;
            listener.complete();
            listener = null;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder = null;
        }

        public AndroidWebServiceService.AWSSBinder getBinder()
        {
            return binder;
        }
    }

    private Context context;

    private AWSServiceConnection serviceConnection;


    public AndroidWebService(Context context)
    {
        this.context = context;
    }

    @Override
    public void startUp(Listener listener) {
        context.startService(new Intent(context, AndroidWebServiceService.class));
        serviceConnection = new AWSServiceConnection(listener);
        context.bindService(new Intent(context, AndroidWebServiceService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void shutdown(Listener listener) {
        context.startService(new Intent(context, AndroidWebServiceService.class).setAction(AndroidWebServiceService.STOP_SERVICE));
        context.unbindService(serviceConnection);
    }

    @Override
    public String host(String filePath) {
        return serviceConnection.getBinder().host(filePath);
    }
}
