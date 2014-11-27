package fd.com.castanyvid;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.gms.cast.CastDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 27/11/14.
 */
public class SpinnerCastDeviceListView extends Spinner implements CastDeviceListPresenter.CastDeviceListView {

    private static class CastDeviceSpinnerAdapter implements SpinnerAdapter
    {
        private DataSetObserver observer;

        private final List<CastDevice> castDevices;

        public CastDeviceSpinnerAdapter(List<CastDevice> castDevices)
        {
            this.castDevices = castDevices;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view;
            if(convertView != null)
            {
                view = (TextView) convertView;
            }
            else
            {
                view = (TextView) LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, null);
                view.setPadding(8, 8, 8, 8);
            }
            view.setText(getItem(position).getFriendlyName());
            return view;
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
            TextView view;
            if(convertView != null)
            {
                view = (TextView) convertView;
            }
            else
            {
                view = (TextView) LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_spinner_item, null);
                view.setPadding(8, 8, 8, 8);
            }
            view.setText(getItem(position).getFriendlyName());
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


    public SpinnerCastDeviceListView(Context context) {
        super(context);
    }

    public SpinnerCastDeviceListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpinnerCastDeviceListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void displayCastDevices(List<CastDevice> castDevices) {
        setAdapter(new CastDeviceSpinnerAdapter(castDevices));
    }

    @Override
    public void displayNoCastDevices() {
        setAdapter(null);
    }
}
