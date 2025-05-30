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

    // déclaration des éléments de l'interface
    private EditText editTitle, editAmount;
    private Spinner spinnerCategory, spinnerCurrency;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // lie cette activité à son layout xml
        setContentView(R.layout.activity_add_expense);

        // récupère les éléments visuels depuis le layout
        editTitle = findViewById(R.id.editTitle);
        editAmount = findViewById(R.id.editAmount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        btnSave = findViewById(R.id.btnSave);

        // configuration du spinner des catégories (ex: nourriture, transport…)
        ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.expense_categories,
                android.R.layout.simple_spinner_item);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        // configuration du spinner des devises disponibles
        String[] currencies = {"EUR", "USD", "JPY"};
        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(currencyAdapter);

        // action lorsque l'utilisateur clique sur "enregistrer"
        btnSave.setOnClickListener(v -> {
            // récupération des champs remplis
            String title = editTitle.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString();
            String baseCurrency = spinnerCurrency.getSelectedItem().toString();

            float amount;
            try {
                // conversion du texte en float (montant saisi)
                amount = Float.parseFloat(editAmount.getText().toString());
            } catch (NumberFormatException e) {
                // si erreur : message d'erreur et on arrête
                editAmount.setError("Montant invalide");
                return;
            }

            // appel de l'api pour obtenir le taux de conversion vers l'USD
            CurrencyApiService api = CurrencyRepository.getApiService();
            Call<CurrencyResponse> call = api.getRates(baseCurrency); // Ex: EUR

            // appel asynchrone
            call.enqueue(new Callback<CurrencyResponse>() {
                @Override
                public void onResponse(Call<CurrencyResponse> call, Response<CurrencyResponse> response) {
                    // si succès
                    if (response.isSuccessful()) {
                        CurrencyResponse data = response.body();
                        double usdRate = data.conversion_rates.get("USD");

                        float converted = amount * (float) usdRate;

                        // conversion du montant vers l'USD
                        float originalAmount = amount;
                        float convertedAmount = originalAmount * (float) usdRate;

                        // création d'un objet Expense avec toutes les infos
                        Expense expense = new Expense(title, originalAmount, baseCurrency, convertedAmount, category);

                        // récupération de la liste existante + ajout de la nouvelle
                        ArrayList<Expense> expenses = SharedPrefManager.getExpenses(getApplicationContext());
                        expenses.add(expense);
                        SharedPrefManager.saveExpenses(getApplicationContext(), expenses);

                        // retour à l'écran précédent
                        setResult(RESULT_OK);
                        finish();
                    }
                    // si la réponse échoue malgré tout
                    if (!response.isSuccessful()) {
                        Toast.makeText(AddExpenseActivity.this, "Erreur API : " + response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }

                }

                // si l'appel api échoue complètement (pas de réseau, etc)
                @Override
                public void onFailure(Call<CurrencyResponse> call, Throwable t) {
                    Toast.makeText(AddExpenseActivity.this, "Échec de conversion", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
        });
    }
}
