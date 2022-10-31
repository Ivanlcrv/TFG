package com.example.app.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.app.MainActivityAdmin;
import com.example.app.MainActivityUser;
import com.example.app.R;
import com.example.app.databinding.ActivityLoginBinding;
import com.example.app.ui.register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    public static final int TEXT_REQUEST = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            loginViewModel = new LoginViewModel();

            final EditText emailEditText = binding.editEmail;
            final EditText passwordEditText = binding.editPassword;
            final Button loginButton = binding.login;

            loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
                @Override
                public void onChanged(@Nullable LoginFormState loginFormState) {
                    if (loginFormState == null) return;
                    loginButton.setEnabled(loginFormState.isDataValid());
                    if (loginFormState.getUsernameError() != null) emailEditText.setError(getString(loginFormState.getUsernameError()));
                    if (loginFormState.getPasswordError() != null) passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            });

            loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
                @Override
                public void onChanged(@Nullable LoginResult loginResult) {
                    if (loginResult == null) return;
                    if (loginResult.getError() != null) showLoginFailed(loginResult.getError());
                    if (loginResult.getUserName() != null) {
                        updateUiWithUser(loginResult.getUserName());
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUser(user.getUid());
                    }
                }
            });

            TextWatcher afterTextChangedListener = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {loginViewModel.loginDataChanged(emailEditText.getText().toString(), passwordEditText.getText().toString());}
            };

            emailEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.addTextChangedListener(afterTextChangedListener);

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {loginViewModel.login(emailEditText.getText().toString(), passwordEditText.getText().toString());}
            });
        }
        else {
            checkUser(user.getUid());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TEXT_REQUEST)
            if (resultCode == RESULT_OK) {
                FirebaseUser user = mAuth.getCurrentUser();
                checkUser(user.getUid());
            }
    }

    private void updateUiWithUser(String email) {
        String welcome = getString(R.string.welcome) + email;
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();}

    public void register(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivityForResult(registerIntent, TEXT_REQUEST);
    }


    private void checkUser(String uid) {
        DatabaseReference myRef;
        myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child("users").child(uid).child("admin").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) Toast.makeText(getApplicationContext(), "Account delete.", Toast.LENGTH_LONG).show();
                else {
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