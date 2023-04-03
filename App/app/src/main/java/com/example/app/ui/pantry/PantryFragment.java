package com.example.app.ui.pantry;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.databinding.FragmentPantryBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;

public class PantryFragment extends Fragment {

    private final LinkedList<Food> foodList = new LinkedList<Food>();
    private FragmentPantryBinding binding;
    private Context context;
    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private FoodAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPantryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = getContext();
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddFoodActivity.class);
                startActivity(intent);
            }
        });

        myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        recyclerView = binding.recyclerViewPantry;

        myRef.child("pantry").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();
                for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                    String name = foodSnapshot.getKey();
                    String amount = foodSnapshot.getValue(String.class);
                    Food food = new Food(name, amount);
                    if (!foodList.contains(food)) {
                        foodList.addLast(food);
                    }
                }
                mAdapter = new FoodAdapter(context, foodList);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                myRef.child("pantry").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            foodList.clear();
                            for (DataSnapshot foodSnapshot : task.getResult().getChildren()) {
                                String name = foodSnapshot.getKey();
                                String amount = foodSnapshot.getValue(String.class);
                                Food food = new Food(name, amount);
                                if (!foodList.contains(food) && food.getName().toLowerCase(Locale.ROOT).contains(Objects.requireNonNull(binding.editSearch.getText()).toString().toLowerCase(Locale.ROOT))) {
                                    foodList.addLast(food);
                                }
                            }
                            mAdapter = new FoodAdapter(context, foodList);
                            recyclerView.setAdapter(mAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        }
                    }
                });
            }
        };

        binding.editSearch.addTextChangedListener(afterTextChangedListener);

        return root;
    }
}