package com.example.app.ui.pantry;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Food;

import com.example.app.databinding.FragmentPantryBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

public class PantryFragment extends Fragment {

    private FragmentPantryBinding binding;
    private Context context;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private final LinkedList<Food> foodList = new LinkedList<Food>();
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
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        myRef.child("pantry").child(user.getUid()).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                foodList.clear();
                for (DataSnapshot foodSnapshot: snapshot.getChildren()) {
                    String name = foodSnapshot.getKey();
                    String amount = foodSnapshot.getValue(String.class);
                    Food food = new Food(name, amount);
                    if(!foodList.contains(food)) {
                        foodList.addLast(food);
                    }
                }
                recyclerView = binding.recyclerViewPantry;
                mAdapter = new FoodAdapter(getContext(), foodList);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}