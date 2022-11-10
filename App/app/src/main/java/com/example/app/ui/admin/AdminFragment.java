package com.example.app.ui.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.User;
import com.example.app.databinding.FragmentAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

public class AdminFragment extends Fragment  {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private final LinkedList<User> userList = new LinkedList<User>();
    private static final String TAG = "MyActivity";
    private FragmentAdminBinding binding;
    private RecyclerView recyclerView;
    private UserAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("users").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if(!user.getAdmin() && !userList.contains(user)) {
                        Log.d(TAG, "Username: " + user.getUsername());
                        userList.addLast(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });

        recyclerView = binding.recyclerViewAdmin;
        mAdapter = new UserAdapter(this, userList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}