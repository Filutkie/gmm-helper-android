package com.filutkie.gmmhelper.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.filutkie.gmmhelper.R;
import com.filutkie.gmmhelper.data.FeatureContract;

public class MarkerCursorAdapter extends CursorAdapter {

    public MarkerCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_markers, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String title =
                cursor.getString(cursor.getColumnIndex(FeatureContract.MarkerEntry.COLUMN_TITLE));
        viewHolder.titleView.setText(title);

        String address =
                cursor.getString(cursor.getColumnIndex(FeatureContract.MarkerEntry.COLUMN_ADDRESS));
        viewHolder.addressView.setText(address);

    }

    private class ViewHolder {

        public final TextView titleView;
        public final TextView addressView;

        public ViewHolder(View view) {
            titleView = (TextView) view.findViewById(R.id.list_item_title_textview);
            addressView = (TextView) view.findViewById(R.id.list_item_address_textview);
        }
    }
}
