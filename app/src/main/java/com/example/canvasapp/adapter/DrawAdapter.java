package com.example.canvasapp.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canvasapp.R;

import java.util.ArrayList;

public class DrawAdapter extends RecyclerView.Adapter<DrawAdapter.ViewHolder> {

    private ArrayList<Bitmap> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_draw, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ivDraw.setImageBitmap(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(ArrayList<Bitmap> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivDraw;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivDraw = itemView.findViewById(R.id.iv_result_draw);
        }
    }
}
