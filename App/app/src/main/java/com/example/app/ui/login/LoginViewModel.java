package com.example.app.ui.login;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class LoginViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    FirebaseAuth mAuth;
    DatabaseReference myRef;

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        myRef = FirebaseDatabase.getInstance().getReference();
                        assert user != null;
                        myRef.child("users").child(user.getUid()).child("username").get().addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful())
                                loginResult.setValue(new LoginResult(R.string.login_failed));
                            else {
                                if (task1.getResult().getValue() != null)
                                    loginResult.setValue(new LoginResult(String.valueOf(task1.getResult().getValue())));
                                else {
                                    user.delete();
                                    loginResult.setValue(new LoginResult(R.string.account_delete));
                                }
                            }
                        });
                    } else {
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    }
                });
    }

    public void loginDataChanged(String email, String password) {
        if (!isEmailNameValid(email))
            loginFormState.setValue(new LoginFormState(R.string.invalid_email, null));
        else if (!isPasswordValid(password))
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        else loginFormState.setValue(new LoginFormState(true));
    }

    // A placeholder username validation check
    private boolean isEmailNameValid(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        return pattern.matcher(email).matches();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}