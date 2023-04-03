package com.example.app.ui.pantry;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.LinkedList;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final LinkedList<Food> foodList;
    private final LayoutInflater mInflater;
    private final Context context;
    private final FirebaseUser user;
    private final DatabaseReference myRef;

    public FoodAdapter(Context pantryFragment, LinkedList<Food> foodList) {
        mInflater = LayoutInflater.from(pantryFragment);
        this.foodList = foodList;
        context = pantryFragment;
        myRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public FoodAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.food_item, parent, false);
        return new FoodAdapter.FoodViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.FoodViewHolder holder, int position) {
        String mCurrent = foodList.get(position).getName();
        holder.foodItemView.setText(mCurrent);
        holder.fill();
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public final TextView foodItemView;
        public final CardView foodCardView;
        final FoodAdapter mAdapter;
        private String amount;
        private String name;

        public FoodViewHolder(@NonNull View itemView, FoodAdapter foodAdapter) {
            super(itemView);
            foodItemView = itemView.findViewById(R.id.food_list);
            foodCardView = itemView.findViewById(R.id.bin);

            foodCardView.setOnClickListener(v -> new AlertDialog.Builder(context)
                    .setTitle("Delete food")
                    .setMessage("Do you really want to remove " + foodItemView.getText().toString() + " from your pantry")
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        assert user != null;
                        myRef.child("pantry").child(user.getUid()).child(name).removeValue();
                        Toast.makeText(context, foodItemView.getText().toString() + " has been removed from your pantry", Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(context)
                                .setTitle("Shopping list food")
                                .setMessage("Do you want to add " + foodItemView.getText().toString() + " to your shopping list")
                                .setIcon(R.drawable.ic_warning)
                                .setPositiveButton("Yes", (dialog_shopping, whichButton_shopping) -> {
                                    myRef.child("shopping").child(user.getUid()).child("list").child(name).setValue(amount);
                                    Toast.makeText(context, foodItemView.getText().toString() + " has been added to your shopping list", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("No", null).show();
                    })
                    .setNegativeButton(android.R.string.no, null).show());

            mAdapter = foodAdapter;
            foodItemView.setOnClickListener(this);
            foodItemView.setOnLongClickListener(this);
        }

        private void fill() {
            name = foodItemView.getText().toString();
            myRef.child("pantry").child(user.getUid()).child(name).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        amount = task.getResult().getValue(String.class);
                    } else amount = "";
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, FoodActivity.class);
            int mPosition = getLayoutPosition();
            Food actual_food = foodList.get(mPosition);
            intent.putExtra("food", actual_food.getName());
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            foodCardView.setVisibility(CardView.VISIBLE);
            return true;
        }
    }
}
