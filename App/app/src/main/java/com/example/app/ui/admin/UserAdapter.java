package com.example.app.ui.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.InfoUserAdmin;
import com.example.app.R;
import com.example.app.User;

import java.util.LinkedList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final LinkedList<User> userList;
    private LayoutInflater mInflater;
    private Context context;
    private User usuario_actual;

    public UserAdapter(Context adminFragment, LinkedList<User> userList) {
        mInflater = LayoutInflater.from(adminFragment);
        this.userList = userList;
        context = adminFragment;
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView userItemView;
        final UserAdapter mAdapter;

        public UserViewHolder(@NonNull View itemView, UserAdapter userAdapter) {
            super(itemView);
            userItemView = itemView.findViewById(R.id.user_of_list);
            mAdapter = userAdapter;
            userItemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, InfoUserAdmin.class);
            int mPosition = getLayoutPosition();
            usuario_actual = userList.get(mPosition);
            intent.putExtra("user", usuario_actual.getEmail());
            context.startActivity(intent);

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
