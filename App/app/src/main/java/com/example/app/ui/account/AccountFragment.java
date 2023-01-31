package com.example.app.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.app.R;
import com.example.app.User;
import com.example.app.databinding.FragmentAccountBinding;
import com.example.app.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private TextView date;
    private static AccountFragment instance = null;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private String checked;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        final RadioGroup radioGroup = binding.editGenre;
        View root = binding.getRoot();

        myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser actual_user = mAuth.getCurrentUser();

        myRef.child("users").child(actual_user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get Post object and use the values to update the UI
                User user = snapshot.getValue(User.class);
                if(user != null) {
                    binding.editUsernameAccount.setHint(user.getUsername());
                    binding.editEmailAccount.setHint(user.getEmail());
                    if(user.getGenre().equals("Male")) binding.radioMen.setChecked(true);
                    else if(user.getGenre().equals("Female")) binding.radioWomen.setChecked(true);
                    else binding.radioOther.setChecked(true);
                    binding.editDateAccount.setText(user.getDate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Toast.makeText(getContext(), "Error loading account information.", Toast.LENGTH_LONG).show();

            }
        });
        binding.iconLogout.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Log out")
                .setMessage("Do you really want to log out")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    mAuth.signOut();
                    Toast.makeText(getContext(), "Log out.", Toast.LENGTH_LONG).show();
                    Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                    startActivity(loginIntent);                            })
                .setNegativeButton(android.R.string.no, null).show());
        binding.save.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Update account")
                .setMessage("Do you really want to update your account")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    String username = binding.editUsernameAccount.getText().toString();
                    String email = binding.editEmailAccount.getText().toString();
                    String edit_date = date.getText().toString();
                    if(!username.equals("")) myRef.child("users").child(actual_user.getUid()).child("username").setValue(username);
                    if(!email.equals("")) {
                        myRef.child("users").child(actual_user.getUid()).child("email").setValue(email);
                        actual_user.updateEmail(email);
                    }
                    if(!checked.equals("")) myRef.child("users").child(actual_user.getUid()).child("genre").setValue(checked);
                    if(!edit_date.equals("")) {
                        myRef.child("users").child(actual_user.getUid()).child("date").setValue(edit_date);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show());
        binding.deleteAccount.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Delete account")
                .setMessage("Do you really want to delete your account")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    myRef.child("users").child(actual_user.getUid()).removeValue();
                    myRef.child("pantry").child(actual_user.getUid()).removeValue();
                    myRef.child("recipes").child(actual_user.getUid()).removeValue();
                    actual_user.delete();
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getContext(), "Account delete.", Toast.LENGTH_LONG).show();
                    Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                    startActivity(loginIntent);
                })
                .setNegativeButton(android.R.string.no, null).show());

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton check = group.findViewById(checkedId);
            checked = check.getText().toString();
        });
        date = binding.editDateAccount;
        date.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerAccountFragment();
            newFragment.show(getFragmentManager(), getString(R.string.datepicker));
        });
        instance = this;
        return root;
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
    }

    public static AccountFragment getInstance() {
        return instance;
    }
}