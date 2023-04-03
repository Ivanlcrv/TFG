package com.example.app.ui.menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.databinding.FragmentMenuBinding;
import com.example.app.databinding.FragmentRecipeBinding;
import com.example.app.ui.recipe.AddRecipeActivity;
import com.example.app.ui.recipe.Recipe;
import com.example.app.ui.recipe.RecipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MenuFragment extends Fragment {

    private final LinkedList<Recipe> recipeList = new LinkedList<>();
    private Context context;
    private DatabaseReference myRef;
    private RecyclerView recyclerViewMenu;
    private RecyclerView recyclerViewHistory;
    private MenuAdapter mAdapter;
    private FragmentMenuBinding binding;
    private final HashSet<String> pantrySet = new HashSet<>();
    private final LinkedList<Recipe> recipeHistory = new LinkedList<>();


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = getContext();


        myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        recyclerViewMenu = binding.recyclerViewMenu;
        recyclerViewHistory = binding.recyclerViewHistory;

        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton check = group.findViewById(checkedId);
            if(check.getText().toString().equals("MENU")){
                recyclerViewMenu.setVisibility(View.VISIBLE);
                recyclerViewHistory.setVisibility(View.INVISIBLE);
            }
            else {
                recyclerViewMenu.setVisibility(View.INVISIBLE);
                recyclerViewHistory.setVisibility(View.VISIBLE);
            }
        });

        myRef.child("pantry").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pantrySet.clear();
                for(DataSnapshot data: snapshot.getChildren()){
                    pantrySet.add(data.getKey());
                }
                myRef.child("recipes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        recipeList.clear();
                        for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                            if (Objects.equals(idSnapshot.getKey(), "public"))
                                for (DataSnapshot recipeSnapshot : idSnapshot.getChildren())
                                    for (DataSnapshot r : recipeSnapshot.getChildren()) {
                                        Recipe recipe = new Recipe(r.child("name").getValue(String.class), r.child("description").getValue(String.class),
                                                (List<Pair<String, String>>) r.child("list").getValue(), r.child("type").getValue(String.class));
                                        List<String> l = new ArrayList<>();
                                        for(DataSnapshot list: r.child("list").getChildren()){
                                            l.add(list.child("first").getValue(String.class));
                                        }
                                        boolean control = true;
                                        for(String ingredient: l){
                                            if(!pantrySet.contains(ingredient)) control = false;
                                        }
                                        if(control) recipeList.add(recipe);
                                    }
                            else {
                                assert user != null;
                                if (Objects.equals(idSnapshot.getKey(), user.getUid()))
                                    for (DataSnapshot r : idSnapshot.getChildren()) {
                                        Recipe recipe = new Recipe(r.child("name").getValue(String.class), r.child("description").getValue(String.class),
                                                (List<Pair<String, String>>) r.child("list").getValue(), r.child("type").getValue(String.class));
                                        List<String> l = new ArrayList<>();
                                        for(DataSnapshot list: r.child("list").getChildren()){
                                            l.add(list.child("first").getValue(String.class));
                                        }
                                        boolean control = true;
                                        for(String ingredient: l){
                                            if(!pantrySet.contains(ingredient)) control = false;
                                        }
                                        if(control) recipeList.add(recipe);
                                    }
                            }
                        }
                        mAdapter = new MenuAdapter(context, recipeList);
                        recyclerViewMenu.setAdapter(mAdapter);
                        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(context));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myRef.child("menu").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot _snapshot) {
                recipeHistory.clear();
                List<Recipe> recipes = new ArrayList<>();
                myRef.child("recipes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                            if (Objects.equals(idSnapshot.getKey(), "public"))
                                for (DataSnapshot recipeSnapshot : idSnapshot.getChildren())
                                    for (DataSnapshot r : recipeSnapshot.getChildren()) {
                                        Recipe recipe = new Recipe(r.child("name").getValue(String.class), r.child("description").getValue(String.class),
                                                (List<Pair<String, String>>) r.child("list").getValue(), r.child("type").getValue(String.class));
                                        recipes.add(recipe);
                                    }
                            else {
                                assert user != null;
                                if (Objects.equals(idSnapshot.getKey(), user.getUid()))
                                    for (DataSnapshot r : idSnapshot.getChildren()) {
                                        Recipe recipe = new Recipe(r.child("name").getValue(String.class), r.child("description").getValue(String.class),
                                                (List<Pair<String, String>>) r.child("list").getValue(), r.child("type").getValue(String.class));
                                        recipes.add(recipe);

                                    }
                            }
                        }
                        mAdapter = new MenuAdapter(context, recipeHistory);
                        recyclerViewHistory.setAdapter(mAdapter);
                        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(context));
                        for(DataSnapshot name: _snapshot.getChildren()){
                            for(Recipe recipe: recipes){
                                if(recipe.getName().equals(name)) recipeHistory.add(recipe);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return root;
    }
}