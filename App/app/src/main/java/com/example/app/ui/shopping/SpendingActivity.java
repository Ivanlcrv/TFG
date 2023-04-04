package com.example.app.ui.shopping;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.databinding.ActivitySpendingBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class SpendingActivity extends AppCompatActivity {
    private BarChart barChart;
    private BarData barData;
    private BarDataSet barDataSet;
    private ArrayList<BarEntry> barEntriesArrayList;
    private DatabaseReference myRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.app.databinding.ActivitySpendingBinding binding = ActivitySpendingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myRef = FirebaseDatabase.getInstance().getReference();
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton check = group.findViewById(checkedId);
            getBarEntries(check.getText().toString());
        });
        barChart = binding.idBarChart;
        barChart.setNoDataText("No expenses");
        getBarEntries("Daily");
        barChart.getXAxis().setEnabled(false);

    }

    private void getBarEntries(String interval) {
        barEntriesArrayList = new ArrayList<>();
        myRef.child("shopping").child(uid).child("expenses").get().addOnCompleteListener(task -> {
            String name = "";
            if (task.isSuccessful()) {
                if (interval.equals("Daily")) {
                    float pos_x = 1;
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        barEntriesArrayList.add(new BarEntry(pos_x, dataSnapshot.getValue(Float.class)));
                        pos_x++;
                    }
                    name = "Daily expenses";
                } else if (interval.equals("Monthly")) {
                    float pos_x = 1;
                    float sum = 0;
                    String month = Objects.requireNonNull(task.getResult().getChildren().iterator().next().getKey()).split("-")[1];
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        String[] date = Objects.requireNonNull(dataSnapshot.getKey()).split("-");
                        if (month.equals(date[1])) sum += dataSnapshot.getValue(Float.class);
                        else {
                            barEntriesArrayList.add(new BarEntry(pos_x, sum));
                            pos_x++;
                            sum = dataSnapshot.getValue(Float.class);
                            month = dataSnapshot.getKey().split("-")[1];
                        }
                    }
                    barEntriesArrayList.add(new BarEntry(pos_x, sum));
                    name = "Monthly expenses";
                } else {
                    float pos_x = 1;
                    float sum = 0;
                    String month = Objects.requireNonNull(task.getResult().getChildren().iterator().next().getKey()).split("-")[2];
                    for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                        String[] date = Objects.requireNonNull(dataSnapshot.getKey()).split("-");
                        if (month.equals(date[2])) sum += dataSnapshot.getValue(Float.class);
                        else {
                            barEntriesArrayList.add(new BarEntry(pos_x, sum));
                            pos_x++;
                            sum = dataSnapshot.getValue(Float.class);
                            month = dataSnapshot.getKey().split("-")[2];
                        }
                    }
                    barEntriesArrayList.add(new BarEntry(pos_x, sum));
                    name = "Yearly expenses";
                }

                barDataSet = new BarDataSet(barEntriesArrayList, "Data user");
                barData = new BarData(barDataSet);
                barChart.setData(barData);
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(R.color.primary_blue);
                barDataSet.setValueTextSize(16f);
                barChart.getDescription().setEnabled(false);
            } else Toast.makeText(SpendingActivity.this, "No expenses", Toast.LENGTH_SHORT).show();
            barDataSet = new BarDataSet(barEntriesArrayList, name);
            barData = new BarData(barDataSet);
            barChart.setData(barData);
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet.setValueTextColor(R.color.primary_blue);
            barDataSet.setValueTextSize(16f);
            barChart.getDescription().setEnabled(false);
            barChart.invalidate();
        });
    }
}