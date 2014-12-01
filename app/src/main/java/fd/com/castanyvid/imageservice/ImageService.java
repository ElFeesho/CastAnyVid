package fd.com.castanyvid.imageservice;

import android.graphics.Bitmap;

import fd.com.castanyvid.CastAVidApplication;

/**
* Created by sawczc01 on 01/12/2014.
*/
public interface ImageService {
    public void getImage(String url, Listener listener);

    public interface Listener {
        void imageRetrieved(Bitmap image);

        void failed();
    }
}
