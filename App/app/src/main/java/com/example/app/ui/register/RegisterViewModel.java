package com.example.app.ui.register;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.regex.Pattern;

public class RegisterViewModel {
    private final MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private final MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();

    LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String username, String email, String password, String checked, String date) {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            registerResult.setValue(new RegisterResult(user.getDisplayName()));
                        } else {
                            registerResult.setValue(new RegisterResult(R.string.login_failed));
                        }
                    }
                });
    }

    //Terminar
    public void registerDataChanged(String username, String email, String password, String checked, String date) {
        Integer errUser = isUserNameValid(username);
        Integer errMail = isEmailValid(email);
        Integer errPassword = isPasswordValid(password);
        Integer errGenre = isCheckValid(checked);
        Integer errDate = isDateValid(date);
        if (errDate!= null || errGenre != null || errMail != null || errPassword != null || errUser != null)
            registerFormState.setValue(new RegisterFormState(errUser, errMail,errPassword, errGenre, false));
        else
            registerFormState.setValue(new RegisterFormState(errUser, errMail,errPassword, errGenre, true));
    }

    // A placeholder email validation check
    private Integer isEmailValid(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        if (pattern.matcher(email).matches()) return null;
        else return R.string.invalid_email;
    }

    // A placeholder username validation check
    private Integer isUserNameValid(String user) {
        if (user.isEmpty()) return R.string.invalid_user;
        return null;
    }

    // A placeholder password validation check
    private Integer isPasswordValid(String password) {
        if (password != null && password.trim().length() > 6) return null;
        else return R.string.invalid_password;
    }

    // A placeholder genre validation check
    private Integer isCheckValid(String checked) {
        if (checked == null) return R.string.invalid_genre;
        return null;
    }

    // A placeholder genre validation check
    private Integer isDateValid(String date) {
        Pattern pattern = Pattern.compile("([0-9]{2})/([0-9]{2})/([0-9]{4})");
        if (!pattern.matcher(date).matches()) return R.string.invalid_date;
        return null;
    }

}
