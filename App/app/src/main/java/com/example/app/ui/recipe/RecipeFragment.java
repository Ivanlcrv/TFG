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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RecipeFragment extends Fragment {

    private FragmentRecipeBinding binding;
    private Context context;
    private DatabaseReference myRef;
    private final LinkedList<Recipe> recipeList = new LinkedList<>();
    private RecyclerView recyclerView;
    private RecipeAdapter mAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = getContext();
        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddRecipeActivity.class);
            startActivity(intent);
        });

        myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        recyclerView = binding.recyclerViewRecipe;

        myRef.child("recipes").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Recipe> list_aux = new ArrayList<>();
                for (DataSnapshot idSnapshot: snapshot.getChildren()) {
                    if(Objects.equals(idSnapshot.getKey(), "public"))
                        for(DataSnapshot recipeSnapshot: idSnapshot.getChildren())
                            for(DataSnapshot r: recipeSnapshot.getChildren()){
                                Recipe recipe = new Recipe(r.child("name").getValue(String.class), r.child("description").getValue(String.class),
                                        (List<Pair<String, String>>) r.child("list").getValue(), r.child("type").getValue(String.class));
                                list_aux.add(recipe);
                            }
                    else {
                        assert user != null;
                        if (Objects.equals(idSnapshot.getKey(), user.getUid()))
                            for(DataSnapshot r: idSnapshot.getChildren()){
                                Recipe recipe = new Recipe(r.child("name").getValue(String.class), r.child("description").getValue(String.class),
                                        (List<Pair<String, String>>) r.child("list").getValue(), r.child("type").getValue(String.class));
                                list_aux.add(recipe);
                            }
                    }
                }

                for(Recipe r: list_aux)
                    if(!recipeList.contains(r))recipeList.add(r);

                Iterator<Recipe> iterator = recipeList.iterator();

                while (iterator.hasNext()){
                    Recipe r = iterator.next();
                    if(r.getType().equals("private") && !list_aux.contains(r))iterator.remove();
                }

                mAdapter = new RecipeAdapter(context, recipeList);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                myRef.child("recipes").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recipeList.clear();
                        for (DataSnapshot idSnapshot: task.getResult().getChildren()) {
                            if(Objects.equals(idSnapshot.getKey(), "public"))
                                for(DataSnapshot recipeSnapshot: idSnapshot.getChildren())
                                    for(DataSnapshot r: recipeSnapshot.getChildren()){
                                        Recipe recipe = new Recipe(idSnapshot.child("name").getValue(String.class), idSnapshot.child("description").getValue(String.class),
                                                (List<Pair<String, String>>) idSnapshot.child("list").getValue(), idSnapshot.child("type").getValue(String.class));
                                        if(!recipeList.contains(recipe) && Objects.requireNonNull(r.getKey()).toLowerCase(Locale.ROOT).contains(Objects.requireNonNull(binding.editSearchRecipe.getText()).toString().toLowerCase(Locale.ROOT)))
                                            recipeList.add(recipe);
                                    }
                            else {
                                assert user != null;
                                if (Objects.equals(idSnapshot.getKey(), user.getUid()))
                                    for(DataSnapshot r: idSnapshot.getChildren()){
                                        Recipe recipe = new Recipe(idSnapshot.child("name").getValue(String.class), idSnapshot.child("description").getValue(String.class),
                                                (List<Pair<String, String>>) idSnapshot.child("list").getValue(), idSnapshot.child("type").getValue(String.class));
                                        if(!recipeList.contains(recipe) && Objects.requireNonNull(r.getKey()).toLowerCase(Locale.ROOT).contains(Objects.requireNonNull(binding.editSearchRecipe.getText()).toString().toLowerCase(Locale.ROOT)))
                                            recipeList.add(recipe);
                                    }
                            }
                        }
                        mAdapter = new RecipeAdapter(context, recipeList);
                        recyclerView.setAdapter(mAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    }
                });
            }
        };
        binding.editSearchRecipe.addTextChangedListener(afterTextChangedListener);
        return root;
    }
}