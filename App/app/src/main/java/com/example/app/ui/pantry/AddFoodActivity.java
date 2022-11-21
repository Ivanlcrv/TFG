package com.example.app.ui.pantry;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.app.databinding.ActivityAddFoodBinding;

public class AddFoodActivity extends AppCompatActivity {

    private ActivityAddFoodBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

}
