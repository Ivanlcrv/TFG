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

import com.example.app.databinding.FragmentAdminBinding;
import com.example.app.ui.account.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

public class AdminFragment extends Fragment {

    private static final String TAG = "MyActivity";
    public static Fragment f;
    private final LinkedList<User> userList = new LinkedList<>();
    private FragmentAdminBinding binding;
    private RecyclerView recyclerView;
    private UserAdapter mAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        f = this;
        binding = FragmentAdminBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    assert user != null;
                    if (!user.getAdmin() && !userList.contains(user)) {
                        userList.addLast(user);
                    }
                }
                recyclerView = binding.recyclerViewAdmin;
                mAdapter = new UserAdapter(getContext(), userList);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
        return root;
    }


}