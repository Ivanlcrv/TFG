package com.example.app.ui.recipe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.databinding.ActivityRecipeViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecipeViewActivity extends AppCompatActivity {
    private ActivityRecipeViewBinding binding;
    private List<Pair<String, String>> list;
    private ListView listView;
    private ListviewPublicRecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        binding = ActivityRecipeViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String name = getIntent().getStringExtra("recipe");
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        list = new ArrayList<>();
        listView = findViewById(R.id.listviewpublic);
        listView.setItemsCanFocus(true);

        adapter = new ListviewPublicRecipeAdapter(this, list);
        listView.setAdapter(adapter);

        if (!getIntent().getExtras().getBoolean("Menu"))
            binding.complete.setVisibility(View.INVISIBLE);

        binding.complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child("menu").child(uid).child(name).setValue(name);
                Toast.makeText(getApplicationContext(), "The recipe has been added to your history", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        myRef.child("recipes").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                    if (Objects.equals(idSnapshot.getKey(), "public")) {
                        for (DataSnapshot recipeSnapshot : idSnapshot.getChildren())
                            for (DataSnapshot r : recipeSnapshot.getChildren()) {
                                if (Objects.equals(r.child("name").getValue(String.class), name)) {
                                    binding.nameRecipeFill.setHint(r.child("name").getValue(String.class));
                                    binding.descriptionRecipeFill.setHint(r.child("description").getValue(String.class));
                                    if (Objects.equals(r.child("type").getValue(String.class), "public")) {
                                        binding.radioPublic.setChecked(true);
                                        binding.radioPublic.setVisibility(View.VISIBLE);
                                    } else {
                                        binding.radioPrivate.setChecked(true);
                                        binding.radioPrivate.setVisibility(View.VISIBLE);
                                    }
                                    for (HashMap<String, String> m : (ArrayList<HashMap<String, String>>) Objects.requireNonNull(r.child("list").getValue())) {
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
                                        Pair<String, String> p = new Pair<>(n, a);
                                        list.add(p);

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
                                    adapter = new ListviewPublicRecipeAdapter(getApplicationContext(), list);
                                    listView.setAdapter(adapter);

                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Objects.requireNonNull(r.child("name").getValue(String.class)));
                                    final long ONE_MEGABYTE = 1024 * 1024;
                                    storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        binding.iconRecipe.setImageBitmap(bitmap);
                                    }).addOnFailureListener(exception -> {
                                    });
                                }
                            }
                    } else {
                        if (Objects.equals(idSnapshot.getKey(), uid))
                            for (DataSnapshot r : idSnapshot.getChildren()) {
                                if (Objects.equals(r.child("name").getValue(String.class), name)) {
                                    Recipe recipe = new Recipe(r.child("name").getValue(String.class), r.child("description").getValue(String.class),
                                            (List<Pair<String, String>>) r.child("list").getValue(), r.child("type").getValue(String.class));
                                    binding.nameRecipeFill.setHint(recipe.getName());
                                    binding.descriptionRecipeFill.setHint(recipe.getDescription());
                                    if (Objects.equals(r.child("type").getValue(String.class), "public")) {
                                        binding.radioPublic.setChecked(true);
                                        binding.radioPublic.setVisibility(View.VISIBLE);
                                    } else {
                                        binding.radioPrivate.setChecked(true);
                                        binding.radioPrivate.setVisibility(View.VISIBLE);
                                    }
                                    for (HashMap<String, String> m : (ArrayList<HashMap<String, String>>) Objects.requireNonNull(r.child("list").getValue())) {
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
                                    adapter = new ListviewPublicRecipeAdapter(getApplicationContext(), list);
                                    listView.setAdapter(adapter);

                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Objects.requireNonNull(r.child("name").getValue(String.class)));
                                    final long ONE_MEGABYTE = 1024 * 1024;
                                    storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        binding.iconRecipe.setImageBitmap(bitmap);
                                    }).addOnFailureListener(exception -> {
                                    });
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
    }
}
