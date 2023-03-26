package com.example.app.ui.shopping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Item;
import com.example.app.SpendingActivity;
import com.example.app.databinding.FragmentShoppingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;

public class ShoppingFragment extends Fragment {

    private final LinkedList<Item> itemList = new LinkedList<>();
    private Context context;
    private DatabaseReference myRef;
    private RecyclerView recyclerView;
    private ItemAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        com.example.app.databinding.FragmentShoppingBinding binding = FragmentShoppingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = getContext();
        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(context, AddItemActivity.class);
            startActivity(intent);
        });

        myRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        recyclerView = binding.recyclerViewShopping;

        assert user != null;
        myRef.child("shopping").child(user.getUid()).child("list").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String name = itemSnapshot.getKey();
                    String amount = itemSnapshot.getValue(String.class);
                    Item item = new Item(name, amount);
                    if (!itemList.contains(item)) {
                        itemList.addLast(item);
                    }
                }
                mAdapter = new ItemAdapter(context, itemList);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
                myRef.child("shopping").child(user.getUid()).child("list").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.clear();
                        for (DataSnapshot itemSnapshot : task.getResult().getChildren()) {
                            String name = itemSnapshot.getKey();
                            String amount = itemSnapshot.getValue(String.class);
                            Item item = new Item(name, amount);
                            if (!itemList.contains(item) && item.getName().toLowerCase(Locale.ROOT).contains(Objects.requireNonNull(binding.editSearch.getText()).toString().toLowerCase(Locale.ROOT))) {
                                itemList.addLast(item);
                            }
                        }
                        mAdapter = new ItemAdapter(context, itemList);
                        recyclerView.setAdapter(mAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    }
                });
            }
        };
        binding.editSearch.addTextChangedListener(afterTextChangedListener);
        binding.buyButton.setOnClickListener(v -> {
            String amount = Objects.requireNonNull(binding.editAmount.getText()).toString();
            if (amount.isEmpty())
                Toast.makeText(context, "Please enter the corresponding amount", Toast.LENGTH_SHORT).show();
            else {
                LinkedList<Item> list = mAdapter.getList();
                boolean any = false;
                for (Item i : list) {
                    if (i.getCheck()) {
                        any = true;
                        myRef.child("shopping").child(user.getUid()).child("list").child(i.getName()).removeValue();
                    }
                }
                if (!any)
                    Toast.makeText(context, "Please select at least 1 item", Toast.LENGTH_SHORT).show();
                else {
                    Calendar calendar = Calendar.getInstance();
                    String date = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR);
                    myRef.child("shopping").child(user.getUid()).child("expenses").child(date).get().addOnCompleteListener(task -> {
                        if (task.getResult().getValue() != null)
                            myRef.child("shopping").child(user.getUid()).child("expenses").child(date).setValue(Integer.parseInt(amount) + Integer.parseInt(Objects.requireNonNull(task.getResult().getValue(String.class))));
                        else
                            myRef.child("shopping").child(user.getUid()).child("expenses").child(date).setValue(amount);
                    });
                    Toast.makeText(context, "The shopping list has been bought", Toast.LENGTH_SHORT).show();
                    binding.editAmount.setText("");
                }
            }
        });
        binding.spendingHistory.setOnClickListener(v -> context.startActivity(new Intent(context, SpendingActivity.class)));
        return root;
    }

}