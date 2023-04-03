package com.example.app.ui.recipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;
import java.util.Objects;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final LinkedList<Recipe> recipeList;
    private final LayoutInflater mInflater;
    private final Context context;
    private final DatabaseReference myRef;
    private final String user;

    public RecipeAdapter(Context recipeFragment, LinkedList<Recipe> recipeList) {
        mInflater = LayoutInflater.from(recipeFragment);
        this.recipeList = recipeList;
        context = recipeFragment;
        myRef = FirebaseDatabase.getInstance().getReference();
        user = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.recipe_item, parent, false);
        return new RecipeAdapter.RecipeViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.RecipeViewHolder holder, int position) {
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

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public final TextView recipeItemView;
        public final CardView recipeCardView;
        public final ImageView imageRecipeView;
        final RecipeAdapter mAdapter;
        boolean permission;
        private boolean public_type;

        public RecipeViewHolder(@NonNull View itemView, RecipeAdapter recipeAdapter) {
            super(itemView);
            permission = false;
            recipeItemView = itemView.findViewById(R.id.recipe_list);
            imageRecipeView = itemView.findViewById(R.id.image_recipe);
            recipeCardView = itemView.findViewById(R.id.bin_recipe);

            recipeCardView.setOnClickListener(v -> new AlertDialog.Builder(context)
                    .setTitle("Delete recipe")
                    .setMessage("Do you really want to remove this recipe: " + recipeItemView.getText().toString())
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(recipeItemView.getText().toString());
                        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (public_type)
                                    myRef.child("recipes").child("public").child(user).child(recipeItemView.getText().toString()).removeValue();
                                else
                                    myRef.child("recipes").child(user).child(recipeItemView.getText().toString()).removeValue();
                                Toast.makeText(context, recipeItemView.getText().toString() + " has been removed from the database", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(exception -> Toast.makeText(context, "An error has occurred while deleting the recipe", Toast.LENGTH_SHORT).show());

                    })
                    .setNegativeButton(android.R.string.no, null).show());
            myRef.child("recipes").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (DataSnapshot idSnapshot : task.getResult().getChildren()) {
                        if (Objects.equals(idSnapshot.getKey(), "public")) {
                            for (DataSnapshot recipeSnapshot : idSnapshot.getChildren()) {
                                if (user.equals(recipeSnapshot.getKey()))
                                    for (DataSnapshot r : recipeSnapshot.getChildren()) {
                                        if (Objects.equals(r.child("name").getValue(String.class), recipeItemView.getText().toString())) {
                                            permission = true;
                                            public_type = true;
                                        }
                                    }
                                else
                                    for (DataSnapshot r : recipeSnapshot.getChildren())
                                        if (Objects.equals(r.child("name").getValue(String.class), recipeItemView.getText().toString())) {
                                            public_type = true;
                                        }
                            }
                        } else if (Objects.equals(idSnapshot.getKey(), user))
                            for (DataSnapshot r : idSnapshot.getChildren())
                                if (Objects.equals(r.child("name").getValue(String.class), recipeItemView.getText().toString())) {
                                    permission = true;
                                    public_type = false;
                                }
                    }
                }
            });
            mAdapter = recipeAdapter;
            recipeItemView.setOnClickListener(this);
            imageRecipeView.setOnClickListener(this);
            recipeItemView.setOnLongClickListener(this);
            imageRecipeView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent;
            int mPosition = getLayoutPosition();
            Recipe actual_recipe = recipeList.get(mPosition);
            if (permission) intent = new Intent(context, RecipeActivity.class);
            else intent = new Intent(context, RecipeViewActivity.class);
            intent.putExtra("recipe", actual_recipe.getName());
            context.startActivity(intent);

        }

        @Override
        public boolean onLongClick(View v) {
            if (permission) recipeCardView.setVisibility(CardView.VISIBLE);
            else
                Toast.makeText(context, "You can't delete a recipe that's not yours", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

}
