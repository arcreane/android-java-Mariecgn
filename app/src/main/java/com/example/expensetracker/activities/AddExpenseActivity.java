package com.example.expensetracker.activities;

import android.os.Bundle;
import android.widget.*;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetracker.R;
import com.example.expensetracker.models.Expense;
import com.example.expensetracker.network.CurrencyApiService;
import com.example.expensetracker.network.CurrencyRepository;
import com.example.expensetracker.network.CurrencyResponse;
import com.example.expensetracker.utils.SharedPrefManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText editTitle, editAmount;
    private Spinner spinnerCategory, spinnerCurrency;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        editTitle = findViewById(R.id.editTitle);
        editAmount = findViewById(R.id.editAmount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        btnSave = findViewById(R.id.btnSave);

        // Spinner catégories
        ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.expense_categories,
                android.R.layout.simple_spinner_item);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        // Spinner devises
        String[] currencies = {"EUR", "USD", "JPY"};
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(currencyAdapter);

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString();
            String baseCurrency = spinnerCurrency.getSelectedItem().toString();

            float amount;
            try {
                amount = Float.parseFloat(editAmount.getText().toString());
            } catch (NumberFormatException e) {
                editAmount.setError("Montant invalide");
                return;
            }

            // Conversion EUR → USD (ou autre)
            CurrencyApiService api = CurrencyRepository.getApiService();
            Call<CurrencyResponse> call = api.getRates(baseCurrency); // Ex: EUR

            call.enqueue(new Callback<CurrencyResponse>() {
                @Override
                public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                    if (response.isSuccessful()) {
                        CurrencyResponse data = response.body();
                        double usdRate = data.conversion_rates.get("USD");

                        float converted = amount * (float) usdRate;

                        Expense expense = new Expense(title, converted, category);
                        ArrayList<Expense> expenses = SharedPrefManager.getExpenses(getApplicationContext());
                        expenses.add(expense);
                        SharedPrefManager.saveExpenses(getApplicationContext(), expenses);

                        setResult(RESULT_OK);
                        finish();
                    }
                    if (!response.isSuccessful()) {
                        Toast.makeText(AddExpenseActivity.this, "Erreur API : " + response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }

                }

                @Override
                public void onFailure(Call<CurrencyResponse> call, Throwable t) {
                    Toast.makeText(AddExpenseActivity.this, "Échec de conversion", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        });
    }
}
