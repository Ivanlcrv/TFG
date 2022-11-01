package com.example.app.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.User;

import java.text.CollationElementIterator;
import java.util.LinkedList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final LinkedList<User> userList;
    private LayoutInflater mInflater;

    public UserAdapter(AdminFragment adminFragment, LinkedList<User> userList) {
        mInflater = LayoutInflater.from(adminFragment.getContext());
        this.userList = userList;
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public final TextView userItemView;
        final UserAdapter mAdapter;

        public UserViewHolder(@NonNull View itemView, UserAdapter userAdapter) {
            super(itemView);
            userItemView = itemView.findViewById(R.id.user_of_list);
            mAdapter = userAdapter;
        }

        @Override
        public void onClick(View v) {

        }
    }
    @NonNull
    @Override
    public UserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        String mCurrent = userList.get(position).getUsername();
        holder.userItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
