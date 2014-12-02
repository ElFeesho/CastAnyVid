package fd.com.castanyvid;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import fd.com.castanyvid.castservice.CastService;
import fd.com.castanyvid.imageservice.ImageService;
import fd.com.castanyvid.imageservice.InMemoryImageService;
import fd.com.castanyvid.webservice.AndroidWebService;
import fd.com.castanyvid.webservice.WebService;

public class CastAVidApplication extends Application {

    private CastService castService;
    private ImageService imageService;
    private WebService webService;

    public static CastService getCastService(Context context) {
        return ((CastAVidApplication) (context.getApplicationContext())).castService;
    }

    public static ImageService getImageService(Context context) {
        return ((CastAVidApplication) (context.getApplicationContext())).imageService;
    }

    public static WebService getWebService(Context context) {
        return ((CastAVidApplication) (context.getApplicationContext())).webService;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        castService = new CastService(new GoogleApiCastDeviceFinder(this), new GoogleApiCastProvider(this));
        imageService = new InMemoryImageService();
        webService = new AndroidWebService(this);
    }


}
