package com.example.app.ui.recipe;

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

import com.example.app.Recipe;
import com.example.app.RecipeActivity;
import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.LinkedList;

public class RecipeAdapter  extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final LinkedList<Recipe> recipeList;
    private LayoutInflater mInflater;
    private Context context;
    private Recipe actual_recipe;

    public RecipeAdapter(Context recipeFragment, LinkedList<Recipe> recipeList) {
        mInflater = LayoutInflater.from(recipeFragment);
        this.recipeList = recipeList;
        context = recipeFragment;
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public final TextView recipeItemView;
        public final CardView recipeCardView;
        final RecipeAdapter mAdapter;

        public RecipeViewHolder(@NonNull View itemView, RecipeAdapter recipeAdapter) {
            super(itemView);
            recipeItemView = itemView.findViewById(R.id.recipe_list);
            //bin_recipe
            recipeCardView = itemView.findViewById(R.id.bin_recipe);

            recipeCardView.setOnClickListener(v -> new AlertDialog.Builder(context)
                    .setTitle("Delete recipe")
                    .setMessage("Do you really want to remove this recipe: " + recipeItemView.getText().toString())
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                        //revisar
                        myRef.child("recipes").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(recipeItemView.getText().toString()).removeValue();
                        Toast.makeText(context, recipeItemView.getText().toString() + " has been remove from the database", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(android.R.string.no, null).show());

            mAdapter = recipeAdapter;
            recipeItemView.setOnClickListener(this);
            recipeItemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, RecipeActivity.class);
            int mPosition = getLayoutPosition();
            actual_recipe = recipeList.get(mPosition);
            intent.putExtra("recipe", actual_recipe.getName());
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            recipeCardView.setVisibility(CardView.VISIBLE);
            return true;
        }
    }
    @NonNull
    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.recipe_item, parent, false);
        return new RecipeAdapter.RecipeViewHolder(mItemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.RecipeViewHolder holder, int position) {
        String mCurrent = recipeList.get(position).getName();
        holder.recipeItemView.setText(mCurrent);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }
}
