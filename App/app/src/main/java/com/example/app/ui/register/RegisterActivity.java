package com.example.app.ui.register;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import com.example.app.R;
import com.example.app.databinding.ActivityRegisterBinding;


public class RegisterActivity extends AppCompatActivity {

    String checked;
    private ActivityRegisterBinding binding;
    private RegisterViewModel registerViewModel;
    private Button date;
    private EditText UsernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerViewModel = new RegisterViewModel();

        UsernameEditText = binding.editUsername;
        emailEditText = binding.editEmail;
        passwordEditText = binding.editPassword;
        final Button registerButton = binding.register;
        final RadioGroup radioGroup = binding.radioSex;
        date = binding.date;
        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                if (registerFormState == null) return;
                registerButton.setEnabled(registerFormState.isDataValid());
                if (registerFormState.getUsernameError() != null) {
                    UsernameEditText.setError(getString(registerFormState.getUsernameError()));
                }
                if (registerFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(registerFormState.getPasswordError()));
                }
                if (registerFormState.getEmailError() != null) {
                    emailEditText.setError(getString(registerFormState.getEmailError()));
                }
                if (registerFormState.getGenreError() != null) {
                    binding.genreTitle.setError(getString(registerFormState.getGenreError()));
                }

            }
        });

        registerViewModel.getRegisterResult().observe(this, new Observer<RegisterResult>() {
            @Override
            public void onChanged(@Nullable RegisterResult registerResult) {
                if (registerResult == null) return;
                if (registerResult.getError() != null) showLoginFailed(registerResult.getError());
                if (registerResult.getUserName() != null) {
                    updateUiWithUser(registerResult.getUserName());
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
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
                registerViewModel.registerDataChanged(UsernameEditText.getText().toString(), emailEditText.getText().toString(), passwordEditText.getText().toString(), checked, date.getText().toString());
            }
        };
        UsernameEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerViewModel.register(UsernameEditText.getText().toString(), emailEditText.getText().toString(), passwordEditText.getText().toString(), checked, date.getText().toString());
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton check = group.findViewById(checkedId);
                checked = check.getText().toString();
                if (!checked.isEmpty()) binding.genreTitle.setError(null);
                registerViewModel.registerDataChanged(UsernameEditText.getText().toString(), emailEditText.getText().toString(), passwordEditText.getText().toString(), checked, date.getText().toString());
            }
        });
    }

    private void updateUiWithUser(String email) {
        String welcome = getString(R.string.welcome) + email;
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), getString(R.string.datepicker));
    }

    public void processDatePickerResult(int year, int month, int day) {
        String day_string;
        String month_string;
        if(day < 9) day_string = "0"+Integer.toString(day);
        else day_string = Integer.toString(day);
        if(month < 9) month_string = "0"+Integer.toString(month + 1);
        else month_string = Integer.toString(month + 1);
        String year_string = Integer.toString(year);
        date.setText(day_string + "/" + month_string + "/" + year_string);
        registerViewModel.registerDataChanged(UsernameEditText.getText().toString(), emailEditText.getText().toString(), passwordEditText.getText().toString(), checked, date.getText().toString());
    }
}
