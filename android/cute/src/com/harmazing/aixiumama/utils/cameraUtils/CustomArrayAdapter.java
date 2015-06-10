package com.harmazing.aixiumama.utils.cameraUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.harmazing.aixiumama.R;

import java.util.List;

/** An array adapter that knows how to render views when given CustomData classes */
public class CustomArrayAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<FilterItem> mItems;
    private Context mContext;

    public CustomArrayAdapter(Context context, List<FilterItem> items) {
        super();
        mContext = context;
        mItems = items;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public FilterItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.filter_item, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            holder.textView = (TextView) convertView.findViewById(R.id.textView_name);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView_icon);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        final int index = position;
        // Populate the text
        holder.textView.setText(getItem(position).strName);



//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //GPUImageFilterTools.createFilterForType(mContext, GPUImageFilterTools.filters.filters.get(index));
//            }
//        });
        // Set the color

        return convertView;
    }

    /** View holder for the views we need access to */
    private static class Holder {
        public TextView textView;
        public ImageView imageView;
    }
}
