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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.Recipe;
import com.example.app.databinding.ActivityAddRecipeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityAddRecipeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        list = new ArrayList<Pair<String, String>>();
        listView = (ListView)findViewById(R.id.listview);
        listView.setItemsCanFocus(true);
        list.add(new Pair<>("",""));
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
                        if(nameEditText.getText() != null && descriptionEditText.getText() != null &&
                                !nameEditText.getText().toString().equals("") && !descriptionEditText.getText().toString().equals("") && uploadButton.getText().toString().equals("Uploaded image")
                                && !adpter.getEmpty()){
                            addButton.setEnabled(true);
                        }
                        else addButton.setEnabled(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                launchSomeActivity.launch(i);
            }
        });



        binding.imageadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add(new Pair<>("",""));
                if(list.size() > 2){
                    ViewGroup.LayoutParams params = listView.getLayoutParams();
                    params.height = 450;
                    listView.setLayoutParams(params);
                    listView.requestLayout();
                }
                adpter = new ListviewAdapter(getApplicationContext(),list);
                listView.setAdapter(adpter);
            }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton check = group.findViewById(checkedId);
            checked = check.getText().toString();
            if(nameEditText.getText() != null && descriptionEditText.getText() != null &&
                    !nameEditText.getText().toString().equals("") && !descriptionEditText.getText().toString().equals("") && uploadButton.getText().toString().equals("Uploaded image")
                    && !adpter.getEmpty()){
                addButton.setEnabled(true);
            }
            else addButton.setEnabled(false);
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
                if(nameEditText.getText() != null && descriptionEditText.getText() != null &&
                        !nameEditText.getText().toString().equals("") && !descriptionEditText.getText().toString().equals("") && uploadButton.getText().toString().equals("Uploaded image")
                        && !adpter.getEmpty()){
                    addButton.setEnabled(true);
                }
                else addButton.setEnabled(false);
            }
        };

        nameEditText.addTextChangedListener(afterTextChangedListener);
        descriptionEditText.addTextChangedListener(afterTextChangedListener);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checked.equals("puclic")){
                    return;
                }
                Recipe recipe = new Recipe(nameEditText.getText().toString(), descriptionEditText.getText().toString(), checked, selectedImageBitmap, adpter.getList());
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                FirebaseUser user = mAuth.getCurrentUser();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference recipeRef = storageRef.child(recipe.getName()+".jpg");
                StorageReference recipeImagesRef = storageRef.child("images/mountains.jpg");
                recipeRef.getName().equals(recipeImagesRef.getName());
                recipeRef.getPath().equals(recipeImagesRef.getPath());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                recipeRef.putBytes(data);
                // revisar !!!!!!!!!!!!!!!!!!!!!                  myRef.child("recipes").child(user.getUid()).child(recipe.getName()).setValue(recipe.getAmount());
                Toast.makeText(getApplicationContext(), "Se ha añadido la receta: " + recipe.getName() + " correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
