package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.app.databinding.ActivityMainBinding;
import com.example.app.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        } else {
            checkUser(user.getUid());
        }
    }

    private void checkUser(String uid) {
        DatabaseReference myRef;
        myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("users").child(uid).child("admin").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Account delete.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            } else {
                if (task.getResult().getValue() == null) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                }
                if ((Boolean) task.getResult().getValue()) {
                    Intent intent_amin = new Intent(getApplicationContext(), MainActivityAdmin.class);
                    startActivity(intent_amin);

                } else {
                    Intent intent_user = new Intent(getApplicationContext(), MainActivityUser.class);
                    startActivity(intent_user);

                }
            }
        });

    }
}