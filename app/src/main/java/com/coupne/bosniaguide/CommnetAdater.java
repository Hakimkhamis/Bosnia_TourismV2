package com.coupne.bosniaguide;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommnetAdater extends RecyclerView.Adapter<CommnetAdater.Holder> {
    List<Commit> commits;
    public CommnetAdater(Collection<Commit> values) {
        this.commits = new ArrayList<>(values);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Commit c = commits.get(i);

        holder.tvCommnet.setText(String.format("%s\n%s", c.getName(), c.getValue()));
    }

    @Override
    public int getItemCount() {
        return commits != null ? commits.size() : 0;
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final TextView tvCommnet;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tvCommnet = itemView.findViewById(R.id.tvCommnet);
        }
    }

    public void addCommit(Commit commit){
        if (commits == null){
            commits = new ArrayList<>();
        }
        commits.add(commit);
        notifyDataSetChanged();
    }
}
