package com.example.app.ui.recipe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.databinding.ActivityRecipeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RecipeActivity extends AppCompatActivity {

    private ActivityRecipeBinding binding;
    private DatabaseReference myRef;
    private List<Pair<String, String>> list;
    private ListView listView;
    private ListviewAdapter adapter;
    private Bitmap selectedImageBitmap;
    private String checked;
    private String type;

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

        adapter = new ListviewAdapter(this, list);
        listView.setAdapter(adapter);

        myRef.child("recipes").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot idSnapshot : snapshot.getChildren()) {
                    if (Objects.equals(idSnapshot.getKey(), "public")) {
                        for (DataSnapshot recipeSnapshot : idSnapshot.getChildren())
                            if (uid.equals(recipeSnapshot.getKey()))
                                for (DataSnapshot r : recipeSnapshot.getChildren()) {
                                    if (Objects.equals(r.child("name").getValue(String.class), name)) {
                                        binding.nameRecipeFill.setHint(r.child("name").getValue(String.class));
                                        binding.descriptionRecipeFill.setHint(r.child("description").getValue(String.class));
                                        if (r.child("type").getValue(String.class).equals("public")) {
                                            binding.radioPublic.setChecked(true);
                                            checked = type = "public";
                                        } else {
                                            binding.radioPrivate.setChecked(true);
                                            checked = type = "private";
                                        }
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

                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Objects.requireNonNull(r.child("name").getValue(String.class)));
                                        final long ONE_MEGABYTE = 1024 * 1024;
                                        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            binding.iconRecipe.setImageBitmap(bitmap);
                                            selectedImageBitmap = bitmap;
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
                                    if (r.child("type").getValue(String.class).equals("public")) {
                                        binding.radioPublic.setChecked(true);
                                        checked = type = "public";
                                    } else {
                                        binding.radioPrivate.setChecked(true);
                                        checked = type = "private";
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
                                    adapter = new ListviewAdapter(getApplicationContext(), list);
                                    listView.setAdapter(adapter);

                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Objects.requireNonNull(r.child("name").getValue(String.class)));
                                    final long ONE_MEGABYTE = 1024 * 1024;
                                    storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        binding.iconRecipe.setImageBitmap(bitmap);
                                        selectedImageBitmap = bitmap;
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

        ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    try {
                        selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        binding.iconRecipe.setImageBitmap(selectedImageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        binding.imageadd.setOnClickListener(v -> {
            list = adapter.getList();
            Pair<String, String> pair = new Pair<>("", "");
            list.add(pair);
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
            }
            adapter = new ListviewAdapter(getApplicationContext(), list);
            listView.setAdapter(adapter);
        });

        binding.imageremove.setOnClickListener(v -> {
            if (list.size() > 1) list.remove(list.size() - 1);
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
        });

        binding.upload.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            launchSomeActivity.launch(i);
        });

        binding.radioType.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton check = group.findViewById(checkedId);
            checked = check.getText().toString();
        });

        binding.saveRecipe.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Update recipe info")
                .setMessage("Do you really want to update information about this recipe")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    Recipe recipe = new Recipe(binding.nameRecipeFill.getText().toString().equals("") ? binding.nameRecipeFill.getHint().toString() : binding.nameRecipeFill.getText().toString(),
                            binding.descriptionRecipeFill.getText().toString().equals("") ? binding.descriptionRecipeFill.getHint().toString() : binding.descriptionRecipeFill.getText().toString()
                            , adapter.getList(), checked.toLowerCase(Locale.ROOT));

                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference recipeRef = storageRef.child(recipe.getName());

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();


                    if (type.equals("public")) {
                        UploadTask uploadTask = recipeRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getApplicationContext(), "An error has occurred while updating the recipe ", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (checked.equals("public") && recipe.getName().equals(name)) {
                                    myRef.child("recipes").child("public").child(uid).child(recipe.getName()).setValue(recipe);
                                } else if (checked.equals("public")) {
                                    myRef.child("recipes").child("public").child(uid).child(name).removeValue();
                                    myRef.child("recipes").child("public").child(uid).child(recipe.getName()).setValue(recipe);
                                    storageRef.child(name).delete();
                                } else {
                                    myRef.child("recipes").child("public").child(uid).child(name).removeValue();
                                    myRef.child("recipes").child(uid).child(recipe.getName()).setValue(recipe);
                                    if (!recipe.getName().equals(name))
                                        storageRef.child(name).delete();
                                }
                            }
                        });
                    } else {
                        UploadTask uploadTask = recipeRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getApplicationContext(), "An error has occurred while updating the recipe ", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (checked.equals("private") && recipe.getName().equals(name)) {
                                    myRef.child("recipes").child(uid).child(recipe.getName()).setValue(recipe);
                                } else if (checked.equals("private")) {
                                    myRef.child("recipes").child(uid).child(name).removeValue();
                                    myRef.child("recipes").child(uid).child(recipe.getName()).setValue(recipe);
                                    storageRef.child(name).delete();
                                } else {
                                    myRef.child("recipes").child(uid).child(name).removeValue();
                                    myRef.child("recipes").child("public").child(uid).child(recipe.getName()).setValue(recipe);
                                    if (!recipe.getName().equals(name))
                                        storageRef.child(name).delete();
                                }
                            }
                        });
                    }
                    finish();
                })
                .setNegativeButton(android.R.string.no, null).show());

    }
}