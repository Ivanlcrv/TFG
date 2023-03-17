package com.example.app.ui.shopping;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.Item;
import com.example.app.databinding.ActivityAddItemBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItemActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        com.example.app.databinding.ActivityAddItemBinding binding = ActivityAddItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final EditText nameEditText = binding.editNameItem;
        final EditText amountEditText = binding.editAmount;
        final Button addButton = binding.add;

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(nameEditText.getText() != null && amountEditText.getText() != null &&
                        !nameEditText.getText().toString().equals("") && !amountEditText.getText().toString().equals("")){
                    addButton.setEnabled(true);
                }
                else addButton.setEnabled(false);
            }
        };
        nameEditText.addTextChangedListener(afterTextChangedListener);
        amountEditText.addTextChangedListener(afterTextChangedListener);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                FirebaseUser user = mAuth.getCurrentUser();
                Item item = new Item(nameEditText.getText().toString(), amountEditText.getText().toString());
                myRef.child("shopping").child(user.getUid()).child(item.getName()).setValue(item.getAmount());
                Toast.makeText(getApplicationContext(), "Se ha añadido " + item.getAmount() + " de " + item.getName(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}
