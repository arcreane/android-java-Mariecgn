package com.example.expensetracker.activities;

import android.content.Intent; // ✅ ajout manquant
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;



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
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Expense> expenseList;
    private ExpenseAdapter adapter;
    private RecyclerView recyclerView;
    private PieChart pieChart;
    private Button btnAddExpense;

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
                    expenseList.clear();
                    SharedPrefManager.saveExpenses(MainActivity.this, expenseList);
                    adapter.notifyDataSetChanged();
                    updatePieChart();
                    Toast.makeText(MainActivity.this, "Dépenses réinitialisées !", Toast.LENGTH_SHORT).show();
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_UI);


        // Récupération des dépenses sauvegardées
        expenseList = SharedPrefManager.getExpenses(this);

        adapter = new ExpenseAdapter(expenseList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivityForResult(intent, 1); // ✅ démarre AddExpenseActivity
        });

        updatePieChart();
    }

    private void updatePieChart() {
        HashMap<String, Float> categoryTotals = new HashMap<>();

        for (Expense e : expenseList) {
            float current = categoryTotals.getOrDefault(e.getCategory(), 0f);
            categoryTotals.put(e.getCategory(), current + e.getAmount());
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Dépenses par catégorie");
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.invalidate(); // rafraîchir
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
