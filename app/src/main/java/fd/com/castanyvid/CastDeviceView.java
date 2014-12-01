package fd.com.castanyvid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.TextView;

import com.google.android.gms.cast.CastDevice;

import fd.com.castanyvid.imageservice.ImageService;

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
        CastAVidApplication.getImageService(getContext()).getImage(url.toString(), new ImageService.Listener() {
            @Override
            public void imageRetrieved(Bitmap image) {
                BitmapDrawable bd = new BitmapDrawable(getResources(), image);
                setCompoundDrawablesWithIntrinsicBounds(bd, null, null, null);
            }

            @Override
            public void failed() {
                setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_menu_help, 0, 0, 0);
            }
        });
    }
}
