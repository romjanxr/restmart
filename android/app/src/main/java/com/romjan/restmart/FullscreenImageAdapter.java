package com.romjan.restmart;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FullscreenImageAdapter extends RecyclerView.Adapter<FullscreenImageAdapter.FullscreenImageViewHolder> {

    private final ArrayList<String> imageUrls;

    public FullscreenImageAdapter(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public FullscreenImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PhotoView photoView = (PhotoView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fullscreen_image, parent, false);
        return new FullscreenImageViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull FullscreenImageViewHolder holder, int position) {
        Picasso.get().load(imageUrls.get(position)).into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class FullscreenImageViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        public FullscreenImageViewHolder(@NonNull PhotoView photoView) {
            super(photoView);
            this.photoView = photoView;
        }
    }
}
