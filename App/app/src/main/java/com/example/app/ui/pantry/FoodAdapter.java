package com.example.app.ui.pantry;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Food;
import com.example.app.FoodActivity;
import com.example.app.InfoUserAdmin;
import com.example.app.R;

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

    class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {

        public final TextView foodItemView;
        final FoodAdapter mAdapter;

        public FoodViewHolder(@NonNull View itemView, FoodAdapter foodAdapter) {
            super(itemView);
            foodItemView = itemView.findViewById(R.id.food_list);
            mAdapter = foodAdapter;
            foodItemView.setOnClickListener(this);
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
        public boolean onTouch(View v, MotionEvent event) {
            return false;
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
