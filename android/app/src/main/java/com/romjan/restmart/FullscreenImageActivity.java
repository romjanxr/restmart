package com.romjan.restmart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.romjan.restmart.databinding.ActivityFullscreenImageBinding;

import java.util.ArrayList;

public class FullscreenImageActivity extends AppCompatActivity {

    private ActivityFullscreenImageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullscreenImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<String> imageUrls = getIntent().getStringArrayListExtra("image_urls");
        int currentPosition = getIntent().getIntExtra("current_position", 0);

        FullscreenImageAdapter adapter = new FullscreenImageAdapter(imageUrls);
        binding.fullscreenImageSlider.setAdapter(adapter);
        binding.fullscreenImageSlider.setCurrentItem(currentPosition, false);

        binding.btnClose.setOnClickListener(v -> finish());
    }
}
