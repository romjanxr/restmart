package com.romjan.restmart;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.romjan.restmart.databinding.ItemProductImageBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ProductImageViewHolder> {

    private List<Image> images;

    public ProductImageAdapter(List<Image> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ProductImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductImageBinding binding = ItemProductImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductImageViewHolder holder, int position) {
        Image image = images.get(position);
        Picasso.get().load(image.getImage()).into(holder.binding.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), FullscreenImageActivity.class);
            ArrayList<String> imageUrls = images.stream().map(Image::getImage).collect(Collectors.toCollection(ArrayList::new));
            intent.putStringArrayListExtra("image_urls", imageUrls);
            intent.putExtra("current_position", position);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void setImages(List<Image> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    static class ProductImageViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductImageBinding binding;

        public ProductImageViewHolder(ItemProductImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
