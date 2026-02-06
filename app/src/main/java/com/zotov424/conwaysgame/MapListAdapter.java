package com.zotov424.conwaysgame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MapListAdapter extends RecyclerView.Adapter<MapListAdapter.ViewHolder> {
    private final List<MapItem> items = new ArrayList<>();
    private OnMapClickListener clickListener;

    public interface OnMapClickListener {
        void onMapClick(MapItem map);
    }

    public void setOnMapClickListener(OnMapClickListener listener) {
        this.clickListener = listener;
    }

    public void setItems(List<MapItem> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MapItem map = items.get(position);
        holder.numberText.setText(String.valueOf(position + 1));
        holder.nameText.setText(map.name != null ? map.name : "");
        holder.widthText.setText(String.valueOf(map.width));
        holder.heightText.setText(String.valueOf(map.height));
        holder.dateText.setText(formatDate(map.createdAt));
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onMapClick(map);
        });
    }

    private static String formatDate(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) return "—";
        if (createdAt.length() >= 10) return createdAt.substring(0, 10);
        return createdAt;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView numberText;
        final TextView nameText;
        final TextView widthText;
        final TextView heightText;
        final TextView dateText;

        ViewHolder(View itemView) {
            super(itemView);
            numberText = itemView.findViewById(R.id.item_map_number);
            nameText = itemView.findViewById(R.id.item_map_name);
            widthText = itemView.findViewById(R.id.item_map_width);
            heightText = itemView.findViewById(R.id.item_map_height);
            dateText = itemView.findViewById(R.id.item_map_date);
        }
    }
}
