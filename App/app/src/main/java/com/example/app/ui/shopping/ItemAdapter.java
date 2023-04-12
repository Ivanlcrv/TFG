package com.example.app.ui.shopping;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.LinkedList;
import java.util.Objects;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private final LinkedList<Item> itemList;
    private final LayoutInflater mInflater;
    private final Context context;

    public ItemAdapter(Context shoppingFragment, LinkedList<Item> itemList) {
        mInflater = LayoutInflater.from(shoppingFragment);
        this.itemList = itemList;
        context = shoppingFragment;
    }

    @NonNull
    @Override
    public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.item_item, parent, false);
        return new ItemAdapter.ItemViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemViewHolder holder, int position) {
        String mCurrent = itemList.get(position).getName();
        holder.itemView.setText(mCurrent);
        holder.setPosition(position);
    }

    public LinkedList<Item> getList() {
        return itemList;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public final TextView itemView;
        public final CardView itemCardView;
        public final CheckBox checkBox;
        final ItemAdapter mAdapter;
        private int position;

        public ItemViewHolder(@NonNull View _itemView, ItemAdapter itemAdapter) {
            super(_itemView);
            itemView = _itemView.findViewById(R.id.item_list);
            itemCardView = _itemView.findViewById(R.id.bin);
            checkBox = _itemView.findViewById(R.id.checkBox);
            itemCardView.setOnClickListener(v -> new AlertDialog.Builder(context)
                    .setTitle("Delete item")
                    .setMessage("Do you really want to remove " + itemView.getText().toString() + " from your shopping list")
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                        myRef.child("shopping").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("list").child(itemView.getText().toString()).removeValue();
                        Toast.makeText(context, itemView.getText().toString() + " has been remove from your shopping list", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(android.R.string.no, null).show());

            mAdapter = itemAdapter;
            checkBox.setOnClickListener(v -> itemList.get(position).setCheck(checkBox.isChecked()));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setPosition(int pos) {
            position = pos;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ItemActivity.class);
            int mPosition = getLayoutPosition();
            Item actual_item = itemList.get(mPosition);
            intent.putExtra("item", actual_item.getName());
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            itemCardView.setVisibility(CardView.VISIBLE);
            return true;
        }
    }
}

