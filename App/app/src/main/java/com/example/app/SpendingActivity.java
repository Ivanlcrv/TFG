package com.example.app;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app.databinding.ActivitySpendingBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.Pair;

public class SpendingActivity extends AppCompatActivity {
    private ActivitySpendingBinding binding;
    private BarChart barChart;
    private BarData barData;
    private BarDataSet barDataSet;
    private ArrayList<BarEntry> barEntriesArrayList;
    private DatabaseReference myRef;
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpendingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myRef = FirebaseDatabase.getInstance().getReference();
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton check = group.findViewById(checkedId);
            getBarEntries(check.getText().toString());
        });
        barChart = binding.idBarChart;
        getBarEntries("Daily");

    }
    private void getBarEntries(String interval) {
        barEntriesArrayList = new ArrayList<>();
        myRef.child("shopping").child(uid).child("expenses").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(interval.equals("Daily")){
                        float i = 1;
                        for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                            barEntriesArrayList.add(new BarEntry(i, dataSnapshot.getValue(Float.class)));
                            i++;
                        }

                    }
                    else if(interval.equals("Monthly")){
                        float i = 1;
                        for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                            barEntriesArrayList.add(new BarEntry(i, dataSnapshot.getValue(Float.class)));
                            i++;
                        }
                    }
                    else{

                    }
                }
                else Toast.makeText(SpendingActivity.this, "No expenses", Toast.LENGTH_SHORT).show();
                barDataSet = new BarDataSet(barEntriesArrayList, "Data user");
                barData = new BarData(barDataSet);
                barChart.setData(barData);
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(R.color.primary_blue);
                barDataSet.setValueTextSize(16f);
                barChart.getDescription().setEnabled(false);
            }
        });
    }
}