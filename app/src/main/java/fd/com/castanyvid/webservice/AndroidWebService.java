package fd.com.castanyvid.webservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.InputStream;

import fd.com.castanyvid.castservice.CastSession;

/**
 * Created by sawczc01 on 01/12/2014.
 */
public class AndroidWebService implements WebService {
    @Override
    public void startUp(Listener listener) {

    }

    @Override
    public void shutdown(Listener listener) {

    }

    @Override
    public String host(InputStream data) {
        return null;
    }
}
