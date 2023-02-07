package com.example.app.ui.recipe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.app.Food;
import com.example.app.R;
import com.example.app.Recipe;
import com.example.app.databinding.ActivityAddRecipeBinding;
import com.example.app.ui.pantry.FoodAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddRecipeActivity extends AppCompatActivity {

    protected ActivityAddRecipeBinding binding;
    private FirebaseAuth mAuth;
    private String checked;
    private List<Pair<String, String>> list;
    private ListView listView;
    private ListviewAdapter adpter;
    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checked="private";
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        list = new ArrayList<>();
        listView = (ListView)findViewById(R.id.listview);
        listView.setItemsCanFocus(true);
        Pair<String, String> p = new Pair<>("","");
        list.add(p);
        adpter = new ListviewAdapter(this,list);
        listView.setAdapter(adpter);

        final EditText nameEditText = binding.editNameRecipe;
        final EditText descriptionEditText = binding.editDescription;
        final Button addButton = binding.add;
        final RadioGroup radioGroup = binding.radioType;
        final Button uploadButton = binding.upload;

        ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    Uri selectedImageUri = data.getData();
                    try {
                        selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        uploadButton.setText("Uploaded image");
                        addButton.setEnabled(nameEditText.getText() != null && descriptionEditText.getText() != null &&
                                !nameEditText.getText().toString().equals("") && !descriptionEditText.getText().toString().equals("") && uploadButton.getText().toString().equals("Uploaded image")
                                && adpter.getEmpty());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        uploadButton.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            launchSomeActivity.launch(i);
        });



        binding.imageadd.setOnClickListener(v -> {
            list = adpter.getList();
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
            adpter = new ListviewAdapter(getApplicationContext(),list);
            listView.setAdapter(adpter);
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
            adpter = new ListviewAdapter(getApplicationContext(),list);
            listView.setAdapter(adpter);
        });


        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton check = group.findViewById(checkedId);
            checked = check.getText().toString();
            addButton.setEnabled(nameEditText.getText() != null && descriptionEditText.getText() != null &&
                    !nameEditText.getText().toString().equals("") && !descriptionEditText.getText().toString().equals("") && uploadButton.getText().toString().equals("Uploaded image")
                    && adpter.getEmpty());
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
                addButton.setEnabled(nameEditText.getText() != null && descriptionEditText.getText() != null &&
                        !nameEditText.getText().toString().equals("") && !descriptionEditText.getText().toString().equals("") && uploadButton.getText().toString().equals("Uploaded image")
                        && adpter.getEmpty());
            }
        };

        nameEditText.addTextChangedListener(afterTextChangedListener);
        descriptionEditText.addTextChangedListener(afterTextChangedListener);

        addButton.setOnClickListener(v -> {
            Recipe recipe = new Recipe(nameEditText.getText().toString(), descriptionEditText.getText().toString(),  adpter.getList(), checked);
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
            FirebaseUser user = mAuth.getCurrentUser();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference recipeRef = storageRef.child(recipe.getName()+".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            if(checked.equals("Public")){
                myRef.child("recipes").child("public").addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Recipe> recipesList = new ArrayList<>();
                        for (DataSnapshot recipeSnapshot: snapshot.getChildren()) {
                            Recipe r = recipeSnapshot.getValue(Recipe.class);
                            recipesList.add(r);
                        }
                        if(!recipesList.contains(recipe.getName())) {
                            recipeRef.putBytes(data);
                            myRef.child("recipes").child("public").child(user.getUid()).child(recipe.getName()).setValue(recipe);
                        }
                        else  Toast.makeText(getApplicationContext(), "The recipe: " + recipe.getName() + " already exists", Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

            }
            else {
                myRef.child("recipes").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Recipe> recipesList = new ArrayList<>();
                        for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                            String name = recipeSnapshot.child("name").getValue(String.class);
                            String description = recipeSnapshot.child("description").getValue(String.class);
                            String type = recipeSnapshot.child("type").getValue(String.class);
                            List<Pair<String, String>> list = (List<Pair<String, String>>) recipeSnapshot.child("list").getValue();
                            Recipe recipe = new Recipe(name, description, list, type);
                            recipesList.add(recipe);
                        }
                        if (!recipesList.contains(recipe.getName())) {
                            recipeRef.putBytes(data);
                            myRef.child("recipes").child(user.getUid()).child(recipe.getName()).setValue(recipe);
                        } else
                            Toast.makeText(getApplicationContext(), "The recipe: " + recipe.getName() + " already exists", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            Toast.makeText(getApplicationContext(), "The recipe: " + recipe.getName() + " has been added successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
