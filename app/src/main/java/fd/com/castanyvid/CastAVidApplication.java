package fd.com.castanyvid;

import android.app.Application;
import android.content.Context;

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

}
