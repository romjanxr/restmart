package com.romjan.restmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.romjan.restmart.databinding.FragmentFilterBottomSheetBinding;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    private FragmentFilterBottomSheetBinding binding;
    private FilterListener listener;

    public static FilterBottomSheetFragment newInstance() {
        return new FilterBottomSheetFragment();
    }

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rsPrice.addOnChangeListener((slider, value, fromUser) -> {
            binding.tvMinPrice.setText(String.format("$%.0f", slider.getValues().get(0)));
            binding.tvMaxPrice.setText(String.format("$%.0f", slider.getValues().get(1)));
        });

        binding.btnApplyFilters.setOnClickListener(v -> {
            String ordering = null;
            if (binding.rbPriceAsc.isChecked()) {
                ordering = "price";
            } else if (binding.rbPriceDesc.isChecked()) {
                ordering = "-price";
            }

            int minPrice = binding.rsPrice.getValues().get(0).intValue();
            int maxPrice = binding.rsPrice.getValues().get(1).intValue();

            if (listener != null) {
                listener.onApplyFilters(ordering, minPrice, maxPrice);
            }
            dismiss();
        });
    }

    public interface FilterListener {
        void onApplyFilters(String ordering, Integer minPrice, Integer maxPrice);
    }
}
