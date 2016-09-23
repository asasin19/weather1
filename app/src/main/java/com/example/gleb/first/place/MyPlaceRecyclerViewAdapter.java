package com.example.gleb.first.place;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gleb.first.ImageLoader;
import com.example.gleb.first.R;
import com.example.gleb.first.place.dummy.DummyContent;
import com.example.gleb.first.place.dummy.DummyContent.DummyItem;

import java.util.List;

public class MyPlaceRecyclerViewAdapter extends RecyclerView.Adapter<MyPlaceRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;
    private final PlaceList.OnListFragmentInteractionListener mListener;

    public MyPlaceRecyclerViewAdapter(List<DummyItem> items, PlaceList.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    public void addItem(DummyItem item){
        mValues.add(item);
    }

    public void removeItem(int i){
        mValues.remove(i);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_place, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DummyItem item = mValues.get(position);
        holder.mItem = item;
        holder.mCity.setText(item.content.city);
        holder.mTime.setText(item.content.time);
        holder.mDate.setText(item.content.date);
        holder.mIcon.setImageBitmap(ImageLoader.createBitmap(item.content.icon_name));
        holder.mTemperature.setText(item.content.temp);
        holder.mMaxTemp.setText(item.content.max_temp);
        holder.mMinTemp.setText(item.content.min_temp);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mCity;
        public final TextView mTime;
        public final TextView mDate;
        public final ImageView mIcon;
        public final TextView mTemperature;
        public final TextView mMinTemp;
        public final TextView mMaxTemp;
        public String id;
        public DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCity = (TextView) view.findViewById(R.id.place_city);
            mTime = (TextView) view.findViewById(R.id.place_time);
            mDate = (TextView) view.findViewById(R.id.place_date);
            mIcon = (ImageView) view.findViewById(R.id.place_image);
            mTemperature = (TextView) view.findViewById(R.id.place_temperature);
            mMinTemp = (TextView) view.findViewById(R.id.place_min_temperature);
            mMaxTemp = (TextView) view.findViewById(R.id.place_max_temperature);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCity.getText() + "'";
        }
    }
}
