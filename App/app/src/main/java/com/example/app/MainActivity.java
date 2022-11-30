package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.app.databinding.ActivityMainAdminBinding;
import com.example.app.databinding.ActivityMainBinding;
import com.example.app.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {

    private FirebaseAuth mAuth;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        }
        else{
            checkUser(user.getUid());
        }
    }
    private void checkUser(String uid) {
        DatabaseReference myRef;
        myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("users").child(uid).child("admin").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) Toast.makeText(getApplicationContext(), "Account delete.", Toast.LENGTH_LONG).show();
                else {
                    if(task.getResult().getValue() == null) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);

                    }
                    if((Boolean)task.getResult().getValue()){
                        Intent intent_amin = new Intent(getApplicationContext(), MainActivityAdmin.class);
                        startActivity(intent_amin);

                    }
                    else {
                        Intent intent_user = new Intent(getApplicationContext(), MainActivityUser.class);
                        startActivity(intent_user);

                    }
                }
            }
        });

    }
}