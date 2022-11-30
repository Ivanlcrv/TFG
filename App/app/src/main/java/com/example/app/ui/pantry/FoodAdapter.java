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

import com.example.app.Food;
import com.example.app.FoodActivity;
import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.LinkedList;

public class FoodAdapter  extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final LinkedList<Food> foodList;
    private LayoutInflater mInflater;
    private Context context;
    private Food actual_food;

    public FoodAdapter(Context pantryFragment, LinkedList<Food> foodList) {
        mInflater = LayoutInflater.from(pantryFragment);
        this.foodList = foodList;
        context = pantryFragment;
    }

    class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public final TextView foodItemView;
        public final CardView foodCardView;
        final FoodAdapter mAdapter;

        public FoodViewHolder(@NonNull View itemView, FoodAdapter foodAdapter) {
            super(itemView);
            foodItemView = itemView.findViewById(R.id.food_list);
            foodCardView = itemView.findViewById(R.id.bin);

            foodCardView.setOnClickListener(v -> new AlertDialog.Builder(context)
                    .setTitle("Delete food")
                    .setMessage("Do you really want to remove " + foodItemView.getText().toString() + " from your pantry")
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                        myRef.child("pantry").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(foodItemView.getText().toString()).removeValue();
                        Toast.makeText(context, foodItemView.getText().toString() + " has been remove from your pantry", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(android.R.string.no, null).show());

            mAdapter = foodAdapter;
            foodItemView.setOnClickListener(this);
            foodItemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, FoodActivity.class);
            int mPosition = getLayoutPosition();
            actual_food = foodList.get(mPosition);
            intent.putExtra("food", actual_food.getName());
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            foodCardView.setVisibility(CardView.VISIBLE);
            return true;
        }
    }
    @NonNull
    @Override
    public FoodAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.food_item, parent, false);
        return new FoodAdapter.FoodViewHolder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.FoodViewHolder holder, int position) {
        String mCurrent = foodList.get(position).getName();
        holder.foodItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }
}
