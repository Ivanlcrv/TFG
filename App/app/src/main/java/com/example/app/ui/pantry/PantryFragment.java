package com.example.app.ui.pantry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.app.databinding.FragmentPantryBinding;

public class PantryFragment extends Fragment {

    private FragmentPantryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PantryViewModel pantryViewModel =
                new ViewModelProvider(this).get(PantryViewModel.class);

        binding = FragmentPantryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textPantry;
        pantryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}