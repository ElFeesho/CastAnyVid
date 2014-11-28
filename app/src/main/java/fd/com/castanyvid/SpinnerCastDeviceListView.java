package fd.com.castanyvid;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.android.gms.cast.CastDevice;

import java.util.List;

/**
 * Created by chris on 27/11/14.
 */
public class SpinnerCastDeviceListView implements CastDeviceListPresenter.CastDeviceListView {

    private final Spinner castDeviceSpinner;
    private final Button castDeviceCastButton;
    private final Button castDeviceStopCastButton;
    private CastDeviceListViewListener listener;

    public SpinnerCastDeviceListView(final Spinner castDeviceSpinner, Button castDeviceCastButton, Button castDeviceStopCastButton) {
        this.castDeviceSpinner = castDeviceSpinner;
        this.castDeviceCastButton = castDeviceCastButton;
        this.castDeviceStopCastButton = castDeviceStopCastButton;

        castDeviceCastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.castDeviceSelected((CastDevice) castDeviceSpinner.getSelectedItem());
            }
        });

        castDeviceStopCastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.stopCasting();
            }
        });
    }

    @Override
    public void setListener(CastDeviceListViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void displayCastDevices(List<CastDevice> castDevices) {
        castDeviceSpinner.setAdapter(new CastDeviceSpinnerAdapter(castDevices));
        castDeviceCastButton.setEnabled(true);

        castDeviceCastButton.setEnabled(true);
        castDeviceStopCastButton.setEnabled(true);
    }

    @Override
    public void displayNoCastDevices() {
        castDeviceSpinner.setAdapter(null);

        castDeviceCastButton.setEnabled(false);
        castDeviceStopCastButton.setEnabled(false);
    }

    @Override
    public void lockDeviceSelection() {
        castDeviceSpinner.setEnabled(false);
    }

    @Override
    public void unlockDeviceSelection() {
        castDeviceSpinner.setEnabled(true);
    }

    @Override
    public void allowStartCast() {
        castDeviceCastButton.setVisibility(View.VISIBLE);
        castDeviceStopCastButton.setVisibility(View.GONE);
    }

    @Override
    public void allowStopCast() {
        castDeviceCastButton.setVisibility(View.GONE);
        castDeviceStopCastButton.setVisibility(View.VISIBLE);
    }

    private static class CastDeviceSpinnerAdapter implements SpinnerAdapter {
        private final List<CastDevice> castDevices;
        private DataSetObserver observer;

        public CastDeviceSpinnerAdapter(List<CastDevice> castDevices) {
            this.castDevices = castDevices;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            this.observer = observer;
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            this.observer = null;
        }

        @Override
        public int getCount() {
            return castDevices.size();
        }

        @Override
        public CastDevice getItem(int position) {
            return castDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CastDeviceView view;
            if (convertView != null) {
                view = (CastDeviceView) convertView;
            } else {
                view = (CastDeviceView) LayoutInflater.from(parent.getContext()).inflate(R.layout.li_castdevice, null);
            }
            view.setCastDevice(getItem(position));
            return view;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return castDevices.isEmpty();
        }
    }
}
