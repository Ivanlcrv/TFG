package com.example.app;

import android.os.Bundle;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.databinding.ActivityRecipeBinding;
import com.example.app.databinding.ActivityRecipeViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class RecipeViewActivity extends AppCompatActivity {
    private ActivityRecipeViewBinding binding;
    private DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myRef = FirebaseDatabase.getInstance().getReference();
        binding = ActivityRecipeViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String name = getIntent().getStringExtra("recipe");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        myRef.child("recipes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot idSnapshot: snapshot.getChildren()) {
                    if(Objects.equals(idSnapshot.getKey(), "public"))
                        for(DataSnapshot recipeSnapshot: idSnapshot.getChildren())
                            for(DataSnapshot r: recipeSnapshot.getChildren()){
                                Recipe recipe = new Recipe(r.child("name").getValue(String.class), r.child("description").getValue(String.class),
                                        (List<Pair<String, String>>) r.child("list").getValue(), r.child("type").getValue(String.class));
                            }
                    else {
                        if (Objects.equals(idSnapshot.getKey(), uid))
                            for(DataSnapshot r: idSnapshot.getChildren()){
                                Recipe recipe = new Recipe(r.child("name").getValue(String.class), r.child("description").getValue(String.class),
                                        (List<Pair<String, String>>) r.child("list").getValue(), r.child("type").getValue(String.class));
                            }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error loading recipe information.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
