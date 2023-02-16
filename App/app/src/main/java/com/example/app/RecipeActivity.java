package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Pair;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.app.databinding.ActivityRecipeBinding;
import com.example.app.ui.recipe.ListviewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecipeActivity extends AppCompatActivity {

    private ActivityRecipeBinding binding;
    private DatabaseReference myRef;
    private List<Pair<String, String>> list;
    private ListView listView;
    private ListviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myRef = FirebaseDatabase.getInstance().getReference();
        binding = ActivityRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String name = getIntent().getStringExtra("recipe");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        list = new ArrayList<>();
        listView = findViewById(R.id.listview);
        listView.setItemsCanFocus(true);

        adapter = new ListviewAdapter(this,list);
        listView.setAdapter(adapter);

        myRef.child("recipes").addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot idSnapshot: snapshot.getChildren()) {
                    if(Objects.equals(idSnapshot.getKey(), "public")) {
                        for (DataSnapshot recipeSnapshot : idSnapshot.getChildren())
                            if (uid.equals(recipeSnapshot.getKey()))
                                for (DataSnapshot r : recipeSnapshot.getChildren()) {
                                    if (Objects.equals(r.child("name").getValue(String.class), name)) {
                                        binding.nameRecipeFill.setHint(r.child("name").getValue(String.class));
                                        binding.descriptionRecipeFill.setHint(r.child("description").getValue(String.class));
                                        if (r.child("type").getValue(String.class).equals("public"))
                                            binding.radioPublic.setChecked(true);
                                        else binding.radioPrivate.setChecked(true);
                                        for (HashMap<String, String> m : (ArrayList<HashMap<String, String>>) r.child("list").getValue()) {
                                            String n, a;
                                            n = a = "";
                                            boolean x = false;
                                            for (Map.Entry<String, String> aux : m.entrySet()) {
                                                if (x) a = aux.getValue();
                                                else {
                                                    n = aux.getValue();
                                                    x = true;
                                                }
                                            }
                                            list.add(new Pair<>(n, a));

                                        }
                                        if (list.size() > 2) {
                                            ViewGroup.LayoutParams params = listView.getLayoutParams();
                                            params.height = 450;
                                            listView.setLayoutParams(params);
                                            listView.requestLayout();
                                        } else if (list.size() == 2) {
                                            ViewGroup.LayoutParams params = listView.getLayoutParams();
                                            params.height = 300;
                                            listView.setLayoutParams(params);
                                            listView.requestLayout();
                                        } else {
                                            ViewGroup.LayoutParams params = listView.getLayoutParams();
                                            params.height = 150;
                                            listView.setLayoutParams(params);
                                            listView.requestLayout();
                                        }
                                        adapter = new ListviewAdapter(getApplicationContext(), list);
                                        listView.setAdapter(adapter);
                                    }
                                }
                    }
                    else {
                        if (Objects.equals(idSnapshot.getKey(), uid))
                            for(DataSnapshot r: idSnapshot.getChildren()){
                                if(Objects.equals(r.child("name").getValue(String.class), name)){
                                    Recipe recipe = new Recipe(r.child("name").getValue(String.class), r.child("description").getValue(String.class),
                                            (List<Pair<String, String>>) r.child("list").getValue(), r.child("type").getValue(String.class));
                                    binding.nameRecipeFill.setHint(recipe.getName());
                                    binding.descriptionRecipeFill.setHint(recipe.getDescription());
                                    if(recipe.getType().equals("public")) binding.radioPublic.setChecked(true);
                                    else binding.radioPrivate.setChecked(true);
                                    for(HashMap<String, String> m : (ArrayList<HashMap<String, String>>)r.child("list").getValue()){
                                        String n, a;
                                        n = a = "";
                                        boolean x = false;
                                        for(Map.Entry<String, String> aux: m.entrySet()){
                                            if(x) a = aux.getValue();
                                            else {
                                                n = aux.getValue();
                                                x = true;
                                            }
                                        }
                                        list.add(new Pair<>(n, a));

                                    }
                                    if(list.size() > 2){
                                        ViewGroup.LayoutParams params = listView.getLayoutParams();
                                        params.height = 450;
                                        listView.setLayoutParams(params);
                                        listView.requestLayout();
                                    }
                                    else if(list.size() == 2){
                                        ViewGroup.LayoutParams params = listView.getLayoutParams();
                                        params.height = 300;
                                        listView.setLayoutParams(params);
                                        listView.requestLayout();
                                    }
                                    else {
                                        ViewGroup.LayoutParams params = listView.getLayoutParams();
                                        params.height = 150;
                                        listView.setLayoutParams(params);
                                        listView.requestLayout();
                                    }
                                    adapter = new ListviewAdapter(getApplicationContext(),list);
                                    listView.setAdapter(adapter);
                                }
                            }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error loading recipe information.", Toast.LENGTH_LONG).show();
            }
        });

        binding.imageadd.setOnClickListener(v -> {
            list = adapter.getList();
            Pair<String, String> pair = new Pair<>("","");
            list.add(pair);
            if(list.size() > 2){
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = 450;
                listView.setLayoutParams(params);
                listView.requestLayout();
            }
            else if(list.size() == 2){
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = 300;
                listView.setLayoutParams(params);
                listView.requestLayout();
            }
            adapter = new ListviewAdapter(getApplicationContext(),list);
            listView.setAdapter(adapter);
        });

        binding.imageremove.setOnClickListener(v -> {
            if(list.size() > 1) list.remove(list.size()-1);
            if(list.size() > 2){
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = 450;
                listView.setLayoutParams(params);
                listView.requestLayout();
            }
            else if(list.size() == 2){
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = 300;
                listView.setLayoutParams(params);
                listView.requestLayout();
            }
            else {
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = 150;
                listView.setLayoutParams(params);
                listView.requestLayout();
            }
            adapter = new ListviewAdapter(getApplicationContext(),list);
            listView.setAdapter(adapter);
        });

        binding.saveRecipe.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Update recipe info")
                .setMessage("Do you really want to update information about this recipe")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {

                    finish();
                })
                .setNegativeButton(android.R.string.no, null).show());

    }
}