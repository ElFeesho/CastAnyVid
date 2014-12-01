package fd.com.castanyvid.imageservice;

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

public class InMemoryImageService implements ImageService {
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private Executor threadStrategy = Executors.newFixedThreadPool(3);

    private HashMap<String, Bitmap> imageCache = new HashMap<>();

    @Override
    public void getImage(final String url, final Listener listener) {
        if(imageCache.containsKey(url))
        {
            listener.imageRetrieved(imageCache.get(url));
        } else {
            threadStrategy.execute(new Runnable() {
                @Override
                public void run() {
                    URL imageUrl = null;
                    try {
                        imageUrl = new URL(url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        notifyError(listener);
                        return;
                    }
                    URLConnection connection = null;
                    try {
                        connection = imageUrl.openConnection();
                        connection.connect();
                        Bitmap result = BitmapFactory.decodeStream(connection.getInputStream());
                        imageCache.put(url, result);
                        notifySuccess(result, listener);
                    } catch (IOException e) {
                        e.printStackTrace();
                        notifyError(listener);
                        return;
                    }
                }
            });
        }
    }

    private void notifySuccess(final Bitmap bitmap, final Listener listener) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.imageRetrieved(bitmap);
            }
        });
    }

    private void notifyError(final Listener listener) {
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                listener.failed();
            }
        });
    }
}
