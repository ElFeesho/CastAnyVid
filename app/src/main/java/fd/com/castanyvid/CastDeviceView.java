package fd.com.castanyvid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.TextView;

import com.google.android.gms.cast.CastDevice;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by sawczc01 on 28/11/2014.
 */
public class CastDeviceView extends TextView {

    public CastDeviceView(Context context) {
        this(context, null, 0);
    }

    public CastDeviceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CastDeviceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCastDevice(CastDevice castDevice)
    {
        setText(castDevice.getFriendlyName());
        setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_help, 0, 0, 0);
        loadIconImage(castDevice.getIcon(128, 128).getUrl());
    }

    private void loadIconImage(Uri url) {
        new AsyncTask<Uri, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(Uri... params) {
                try {
                    URL imageUrl = new URL(params[0].toString());
                    URLConnection connection = imageUrl.openConnection();
                    connection.connect();
                    return BitmapFactory.decodeStream(connection.getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if(bitmap != null) {
                    BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
                    setCompoundDrawablesWithIntrinsicBounds(bd, null, null, null);
                }
                else
                {
                    setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_help, 0, 0, 0);
                }
            }
        }.execute(url);
    }
}
