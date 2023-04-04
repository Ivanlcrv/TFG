package com.example.app.ui.menu;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.ui.recipe.Recipe;
import com.example.app.ui.recipe.RecipeViewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;
import java.util.Objects;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private final LinkedList<Recipe> recipeList;
    private final LayoutInflater mInflater;
    private final Context context;
    private final DatabaseReference myRef;
    private final String user;

    public MenuAdapter(Context recipeFragment, LinkedList<Recipe> recipeList) {
        mInflater = LayoutInflater.from(recipeFragment);
        this.recipeList = recipeList;
        context = recipeFragment;
        myRef = FirebaseDatabase.getInstance().getReference();
        user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public MenuAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.recipe_item, parent, false);
        return new MenuAdapter.MenuViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.MenuViewHolder holder, int position) {
        String mCurrent = recipeList.get(position).getName();
        holder.recipeItemView.setText(mCurrent);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(mCurrent);
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.imageRecipeView.setImageBitmap(bitmap);
        }).addOnFailureListener(exception -> {
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView recipeItemView;
        public final ImageView imageRecipeView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeItemView = itemView.findViewById(R.id.recipe_list);
            imageRecipeView = itemView.findViewById(R.id.image_recipe);

            recipeItemView.setOnClickListener(this);
            imageRecipeView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent;
            int mPosition = getLayoutPosition();
            Recipe actual_recipe = recipeList.get(mPosition);
            intent = new Intent(context, RecipeViewActivity.class);
            intent.putExtra("Menu", true);
            intent.putExtra("recipe", actual_recipe.getName());
            context.startActivity(intent);

        }
    }
}

