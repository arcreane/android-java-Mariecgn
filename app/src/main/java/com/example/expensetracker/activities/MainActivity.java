package com.example.expensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.adapters.ExpenseAdapter;
import com.example.expensetracker.models.Expense;
import com.example.expensetracker.utils.SharedPrefManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Expense> expenseList;
    private ExpenseAdapter adapter;
    private RecyclerView recyclerView;
    private PieChart pieChart;
    private Button btnAddExpense;
    private TextView emptyMessage;
    private TextView textTotal; // ✅ AJOUTÉ

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastX, lastY, lastZ;
    private long lastShakeTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerExpenses);
        pieChart = findViewById(R.id.pieChart);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        emptyMessage = findViewById(R.id.emptyMessage);
        textTotal = findViewById(R.id.textTotal); // ✅

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float delta = Math.abs(x + y + z - lastX - lastY - lastZ);
                long currentTime = System.currentTimeMillis();

                if (delta > 15 && (currentTime - lastShakeTime) > 1000) {
                    lastShakeTime = currentTime;

                    List<Expense> toDelete = adapter.getSelectedExpenses();
                    if (!toDelete.isEmpty()) {
                        StringBuilder message = new StringBuilder("Supprimer les dépenses suivantes ?\n\n");
                        for (Expense e : toDelete) {
                            message.append("- ").append(e.getTitle()).append("\n");
                        }

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Confirmation")
                                .setMessage(message.toString())
                                .setPositiveButton("Supprimer", (dialog, which) -> {
                                    expenseList.removeAll(toDelete);
                                    SharedPrefManager.saveExpenses(MainActivity.this, expenseList);
                                    adapter.notifyDataSetChanged();
                                    updatePieChart();
                                    Toast.makeText(MainActivity.this, toDelete.size() + " dépense(s) supprimée(s)", Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Annuler", (dialog, which) ->
                                        Toast.makeText(MainActivity.this, "Suppression annulée", Toast.LENGTH_SHORT).show())
                                .show();
                    } else {
                        Toast.makeText(MainActivity.this, "Aucune dépense sélectionnée.", Toast.LENGTH_SHORT).show();
                    }
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI);

        expenseList = SharedPrefManager.getExpenses(this);
        adapter = new ExpenseAdapter(expenseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivityForResult(intent, 1);
        });

        updatePieChart();
    }
//total depenses
    private void updatePieChart() {
        HashMap<String, Float> categoryTotals = new HashMap<>();
        float totalAmount = 0f; // ✅

        for (Expense e : expenseList) {
            float current = categoryTotals.getOrDefault(e.getCategory(), 0f);
            categoryTotals.put(e.getCategory(), current + e.getAmount());
            totalAmount += e.getAmount(); // ✅
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Dépenses par catégorie");
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(android.R.color.holo_purple));
        colors.add(getResources().getColor(android.R.color.holo_blue_light));
        colors.add(getResources().getColor(android.R.color.holo_green_light));
        colors.add(getResources().getColor(android.R.color.holo_orange_light));
        colors.add(getResources().getColor(android.R.color.holo_red_light));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();

        // ✅ MET À JOUR LE TOTAL
        textTotal.setText(String.format("Total : %.2f€", totalAmount));

        if (expenseList.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            textTotal.setVisibility(View.GONE); // ✅
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            pieChart.setVisibility(View.VISIBLE);
            textTotal.setVisibility(View.VISIBLE); // ✅
            emptyMessage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            expenseList = SharedPrefManager.getExpenses(this);
            adapter = new ExpenseAdapter(expenseList);
            recyclerView.setAdapter(adapter);
            updatePieChart();
        }
    }
}
