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

import com.bumptech.glide.Glide;
import com.example.app.Recipe;
import com.example.app.RecipeActivity;
import com.example.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;

public class RecipeAdapter  extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final LinkedList<Recipe> recipeList;
    private LayoutInflater mInflater;
    private Context context;
    private Recipe actual_recipe;
    private DatabaseReference myRef;
    private String user;

    public RecipeAdapter(Context recipeFragment, LinkedList<Recipe> recipeList) {
        mInflater = LayoutInflater.from(recipeFragment);
        this.recipeList = recipeList;
        context = recipeFragment;
        myRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public final TextView recipeItemView;
        public final CardView recipeCardView;
        public final ImageView imageRecipeView;
        final RecipeAdapter mAdapter;
        private boolean public_type;

        public RecipeViewHolder(@NonNull View itemView, RecipeAdapter recipeAdapter) {
            super(itemView);
            recipeItemView = itemView.findViewById(R.id.recipe_list);
            imageRecipeView = itemView.findViewById(R.id.image_recipe);
            recipeCardView = itemView.findViewById(R.id.bin_recipe);
            recipeCardView.setOnClickListener(v -> new AlertDialog.Builder(context)
                    .setTitle("Delete recipe")
                    .setMessage("Do you really want to remove this recipe: " + recipeItemView.getText().toString())
                    .setIcon(R.drawable.ic_warning)
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(recipeItemView.getText().toString());
                        storageRef.delete();
                        if(public_type) myRef.child("recipes").child("public").child(user).child(recipeItemView.getText().toString()).removeValue();
                        else myRef.child("recipes").child(user).child(recipeItemView.getText().toString()).removeValue();
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
            myRef.child("recipes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        boolean permission = false;
                        for (DataSnapshot idSnapshot: task.getResult().getChildren()) {
                            if(idSnapshot.getKey().equals("public")){
                                for(DataSnapshot recipeSnapshot: idSnapshot.getChildren()){
                                    for(DataSnapshot r: recipeSnapshot.getChildren()){
                                        if(r.child("name").getValue(String.class).equals(recipeItemView.getText().toString())){
                                            permission = true;
                                            public_type = true;
                                        }

                                    }
                                }
                            }
                            else if (idSnapshot.getKey().equals(user)){
                                for(DataSnapshot r: idSnapshot.getChildren()){
                                    if(r.child("name").getValue(String.class).equals(recipeItemView.getText().toString())){
                                        permission = true;
                                        public_type = false;
                                    }
                                }
                            }
                        }
                        if(permission){
                            recipeCardView.setVisibility(CardView.VISIBLE);
                        }
                        else Toast.makeText(context, "You can't delete a recipe that's not yours", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(mCurrent);
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
                holder.imageRecipeView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }
}
