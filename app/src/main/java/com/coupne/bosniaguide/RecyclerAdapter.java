package com.coupne.bosniaguide;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ImageViewHoler> {

    private int[] images;
    private String[] str;
    private String category_name;
    private Context context;

    public RecyclerAdapter(int[] images, String[] strings,String category_name, Context context) {
        this.images = images;
        this.str = strings;
        this.category_name = category_name;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_layout, parent, false);
        ImageViewHoler imageViewHoler = new ImageViewHoler(view, context, images, str, category_name);

        return imageViewHoler;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHoler holder, final int position) {

        int image_id = images[position];
        String image_str = str[position];
        holder.Album.setImageResource(image_id);
        holder.AlbumTitle.setText(image_str);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class ImageViewHoler extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView Album;
        TextView AlbumTitle;
        Context context;
        int[] images;
        String[] titles;
        String category;

        public ImageViewHoler (View itemView, Context context, int[] images, String[] titles, String category) {
            super(itemView);
            Album = itemView.findViewById(R.id.album);
            AlbumTitle = itemView.findViewById(R.id.album_title);
            itemView.setOnClickListener(this);
            this.context =context;
            this.images = images;
            this.titles = titles;
            this.category = category;
        }

        @Override
        public void onClick(View v) {

            String title;

            switch (category) {
                case "Cities":
                case "Home":
                    title = category;

                    Intent intent = new Intent(context, MoreDetailActivity.class);
                    intent.putExtra("title", title);
                    context.startActivity(intent);
                    return;

                default:
                    title = category + "/" + titles[getAdapterPosition()];

                    Intent intent2 = new Intent(context, DetailActivity.class);
                    intent2.putExtra("AlbumTitle", title);
                    context.startActivity(intent2);
                    return;
            }
        }
    }
}
