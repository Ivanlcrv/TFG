package com.example.app.ui.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.app.databinding.FragmentRecipeBinding;

public class RecipeFragment extends Fragment {

    private FragmentRecipeBinding binding;
    private Context context;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private final LinkedList<Recipe> recipeList = new LinkedList<Recipe>();
    private RecyclerView recyclerView;
    private RecipeAdapter mAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = getContext();
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddRecipeActivity.class);
                startActivity(intent);
            }
        });

        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        recyclerView = binding.recyclerViewRecipe;
        /*revisar
        myRef.child("recipes").child(user.getUid()).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot recipeSnapshot: snapshot.getChildren()) {

                }
                mAdapter = new RecipeAdapter(getContext(), recipeList);
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        */
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                myRef.child("recipes").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            recipeList.clear();
                            for (DataSnapshot recipeSnapshot: task.getResult().getChildren()) {

                            }
                            mAdapter = new RecipeAdapter(getContext(), recipeList);
                            recyclerView.setAdapter(mAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        }
                    }
                });
            }
        };

        binding.editSearch.addTextChangedListener(afterTextChangedListener);

        return root;
    }
}