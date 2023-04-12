package com.example.app.ui.pantry;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.databinding.ActivityAddFoodBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddFoodActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        com.example.app.databinding.ActivityAddFoodBinding binding = ActivityAddFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final EditText nameEditText = binding.editNameFood;
        final EditText amountEditText = binding.editAmount;
        final Button addButton = binding.add;

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                addButton.setEnabled(nameEditText.getText() != null && amountEditText.getText() != null &&
                        !nameEditText.getText().toString().equals("") && !amountEditText.getText().toString().equals(""));
            }
        };
        nameEditText.addTextChangedListener(afterTextChangedListener);
        amountEditText.addTextChangedListener(afterTextChangedListener);

        addButton.setOnClickListener(v -> {
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            FirebaseUser user = mAuth.getCurrentUser();
            Food food = new Food(nameEditText.getText().toString(), amountEditText.getText().toString());
            assert user != null;
            myRef.child("pantry").child(user.getUid()).child(food.getName()).setValue(food.getAmount());
            Toast.makeText(getApplicationContext(), "Has been added " + food.getAmount() + " of " + food.getName(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

}
