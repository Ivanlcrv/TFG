package com.example.app.ui.admin;


import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.ui.account.User;
import com.example.app.databinding.ActivityInfoUserAdminBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class InfoUserAdmin extends AppCompatActivity {

    private ActivityInfoUserAdminBinding binding;
    private DatabaseReference myRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myRef = FirebaseDatabase.getInstance().getReference();

        binding = ActivityInfoUserAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String email = getIntent().getStringExtra("user");

        myRef.child("users").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    assert user != null;
                    if (user.getEmail().equals(email)) {
                        uid = userSnapshot.getKey();
                        binding.usernameAccountAdmin.setText(user.getUsername());
                        binding.emailAccountAdmin.setText(user.getEmail());
                        binding.genreAccountAdmin.setText(user.getGenre());
                        binding.dateAccountAdmin.setText(user.getDate());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error loading account information.", Toast.LENGTH_LONG).show();
            }
        });
        binding.deleteAccountUser.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Delete account")
                .setMessage("Do you really want to delete account")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    myRef.child("users").child(uid).removeValue();
                    Toast.makeText(getApplicationContext(), "User has been delete", Toast.LENGTH_LONG).show();
                    finish();
                })
                .setNegativeButton(android.R.string.no, null).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}