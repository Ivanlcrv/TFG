package com.example.app;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.app.databinding.ActivityInfoUserAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class InfoUserAdmin extends AppCompatActivity  {

    private ActivityInfoUserAdminBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser usuario_actual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference();

        binding = ActivityInfoUserAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String email = getIntent().getStringExtra("user");

        myRef.child("users").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get Post object and use the values to update the UI

                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if(user.getEmail().equals(email)) {

                        binding.usernameAccountAdmin.setText(user.getUsername());
                        binding.emailAccountAdmin.setText(user.getEmail());
                        binding.genreAccountAdmin.setText(user.getGenre());
                        binding.dateAccountAdmin.setText(user.getDate());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Toast.makeText(getApplicationContext(), "Error loading account information.", Toast.LENGTH_LONG).show();

            }
        });
        binding.deleteAccountUser.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete account")
                .setMessage("Do you really want to delete account")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    myRef.child("users").child("email").removeValue();
                })
                .setNegativeButton(android.R.string.no, null).show());
    }



}