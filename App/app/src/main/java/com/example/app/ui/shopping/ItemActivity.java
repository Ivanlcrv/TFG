package com.example.app.ui.shopping;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.databinding.ActivityItemBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ItemActivity extends AppCompatActivity {
    private ActivityItemBinding binding;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myRef = FirebaseDatabase.getInstance().getReference();
        binding = ActivityItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String name = getIntent().getStringExtra("item");

        myRef.child("shopping").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("list").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String name_item = itemSnapshot.getKey();
                    assert name_item != null;
                    if (name_item.equals(name)) {
                        binding.nameItemFill.setHint(name);
                        binding.amountItemFill.setHint(itemSnapshot.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error loading item information.", Toast.LENGTH_LONG).show();
            }
        });

        binding.saveItem.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Update item info")
                .setMessage("Do you really want to update information about this item")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String item_name = Objects.requireNonNull(binding.nameItemFill.getText()).toString();
                    String item_amount = Objects.requireNonNull(binding.amountItemFill.getText()).toString();
                    if (!item_name.equals("") && !item_amount.equals("")) {
                        myRef.child("shopping").child(uid).child("list").child(name).removeValue();
                        myRef.child("shopping").child(uid).child("list").child(item_name).setValue(item_amount);
                        Toast.makeText(this, "Name and amount have been update", Toast.LENGTH_SHORT).show();
                    } else if (!item_amount.equals("")) {
                        myRef.child("shopping").child(uid).child("list").child(name).setValue(item_amount);
                        Toast.makeText(this, "Amount has been update", Toast.LENGTH_SHORT).show();
                    } else {
                        myRef.child("shopping").child(uid).child("list").child(name).removeValue();
                        myRef.child("shopping").child(uid).child("list").child(item_name).setValue(binding.amountItemFill.getHint());
                        Toast.makeText(this, "Name has been update", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                })
                .setNegativeButton(android.R.string.no, null).show());


    }

}
