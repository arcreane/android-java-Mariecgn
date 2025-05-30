package com.example.expensetracker.network;

import java.util.Map;

// classe modèle pour représenter la réponse JSON renvoyée par l'api de taux de change
public class CurrencyResponse {
    // code de la devise de base utilisée pour la conversion (ex : "EUR", "USD")
    public String base_code;

    // map contenant les taux de conversion vers d'autres devises (clé = nom devise, valeur = taux)
    public Map<String, Double> conversion_rates;
}
