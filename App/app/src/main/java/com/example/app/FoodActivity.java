package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.app.databinding.ActivityFoodBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FoodActivity extends AppCompatActivity {

    private ActivityFoodBinding binding;
    private DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myRef = FirebaseDatabase.getInstance().getReference();
        binding = ActivityFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String name = getIntent().getStringExtra("food");

        myRef.child("pantry").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot foodSnapshot: snapshot.getChildren()) {
                    String name_food = foodSnapshot.getKey();
                    if (name_food.equals(name)) {
                        binding.nameFoodFill.setHint(name);
                        binding.amountFoodFill.setHint(foodSnapshot.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error loading food information.", Toast.LENGTH_LONG).show();
            }
        });

        binding.saveFood.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Update food info")
                .setMessage("Do you really want to update information about this food")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String food_name = binding.nameFoodFill.getText().toString();
                    String food_amount = binding.amountFoodFill.getText().toString();
                    if(!food_name.equals("") && !food_amount.equals("")) {
                        myRef.child("pantry").child(uid).child(name).removeValue();
                        myRef.child("pantry").child(uid).child(food_name).setValue(food_amount);
                        Toast.makeText(this, "Name and amount have been update", Toast.LENGTH_SHORT).show();
                    }
                    else if(!food_amount.equals("")) {
                        myRef.child("pantry").child(uid).child(name).setValue(food_amount);
                        Toast.makeText(this, "Amount has been update", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        myRef.child("pantry").child(uid).child(name).removeValue();
                        myRef.child("pantry").child(uid).child(food_name).setValue(binding.amountFoodFill.getHint());
                        Toast.makeText(this, "Name has been update", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                })
                .setNegativeButton(android.R.string.no, null).show());


    }
}