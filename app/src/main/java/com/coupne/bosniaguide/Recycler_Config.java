package com.coupne.bosniaguide;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.internal.InternalTokenResult;
import com.squareup.picasso.Picasso;

import java.util.List;



public class Recycler_Config {
    private Context mContext;
    private PlaceDataAdapter mPlaceDataAdapter;
    private String AlbumTitle;

    public void setConfig(RecyclerView recyclerView, Context context, List<PlaceData> placeData, List<String> keys, String albumTitle) {
        mContext = context;
        this.AlbumTitle = albumTitle;
        mPlaceDataAdapter = new PlaceDataAdapter(placeData, keys);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mPlaceDataAdapter);
    }

    class PlaceDataItemView extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView address;
        private TextView lat;
        private TextView lng;
        private TextView longDesciption;
        private TextView name;
        private ImageView imageView;

        private String key;
        private PlaceData placeData;

        public PlaceDataItemView(ViewGroup parent) {
            super(LayoutInflater.from(mContext).inflate(R.layout.place_layout, parent, false));

            name = itemView.findViewById(R.id.place_title);
            imageView = itemView.findViewById(R.id.place_img);
            imageView.setOnClickListener(this);

        }

        public void bind(PlaceData placeData, String key) {
            name.setText(placeData.getName());
            Picasso.get().load(placeData.getImg()).into(imageView);

            this.key = key;
            this.placeData = placeData;
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(mContext, MoreDetailActivity.class);
            intent.putExtra("title", name.getText().toString());
            intent.putExtra("data", placeData);
            intent.putExtra("AlbumTitle", AlbumTitle);
            mContext.startActivity(intent);
        }
    }

    class PlaceDataAdapter extends RecyclerView.Adapter<PlaceDataItemView> {
        private List<PlaceData> mPlaceDataList;
        private List<String> mKeys;

        public PlaceDataAdapter(List<PlaceData> mPlaceDataList, List<String> mKeys) {
            this.mPlaceDataList = mPlaceDataList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public PlaceDataItemView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new PlaceDataItemView(viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull PlaceDataItemView placeDataItemView, int i) {
            placeDataItemView.bind(mPlaceDataList.get(i), mKeys.get(i));
        }

        @Override
        public int getItemCount() {
            return mPlaceDataList.size();
        }
    }
}
