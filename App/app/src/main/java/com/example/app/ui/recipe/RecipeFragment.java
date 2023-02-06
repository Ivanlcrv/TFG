package com.example.app.ui.recipe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Recipe;
import com.example.app.databinding.FragmentRecipeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RecipeFragment extends Fragment {

    private FragmentRecipeBinding binding;
    private Context context;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private final LinkedList<Recipe> recipeList = new LinkedList<Recipe>();
    private RecyclerView recyclerView;
    private RecipeAdapter mAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = getContext();
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddRecipeActivity.class);
                startActivity(intent);
            }
        });

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        recyclerView = binding.recyclerViewRecipe;

        myRef.child("recipes").child("public").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot recipeSnapshot: snapshot.getChildren()) {
                    List<Pair<String, Recipe>> list;
                    list = (List<Pair<String, Recipe>>) recipeSnapshot.getValue();
                    Recipe r = recipeSnapshot.getValue(Recipe.class);
                    recipeList.add(r);
                }
                mAdapter = new RecipeAdapter(getContext(), recipeList);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        myRef.child("recipes").child(user.getUid()).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot recipeSnapshot: snapshot.getChildren()) {
                    String name = recipeSnapshot.child("name").getValue(String.class);
                    String description = recipeSnapshot.child("description").getValue(String.class);
                    List<Pair<String, String>> list;
                    list = (List<Pair<String, String>>) recipeSnapshot.child("list").getValue();
                    Recipe r = new Recipe(name, description, list);
                    recipeList.add(r);
                }
                mAdapter = new RecipeAdapter(getContext(), recipeList);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
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
                myRef.child("recipes").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            recipeList.clear();
                            for (DataSnapshot recipeSnapshot: task.getResult().getChildren()) {

                            }
                            mAdapter = new RecipeAdapter(getContext(), recipeList);
                            recyclerView.setAdapter(mAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        }
                    }
                });
            }
        };

        binding.editSearchRecipe.addTextChangedListener(afterTextChangedListener);

        return root;
    }
}