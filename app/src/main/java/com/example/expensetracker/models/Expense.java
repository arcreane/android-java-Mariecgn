package com.example.expensetracker.models;

public class Expense {
    private String title;
    private float amount; // montant converti en USD
    private float originalAmount; // montant saisi par l'utilisateur
    private String baseCurrency;  // devise d'origine
    private String category;

    public Expense(String title, float originalAmount, String baseCurrency, float amount, String category) {
        this.title = title;
        this.originalAmount = originalAmount;
        this.baseCurrency = baseCurrency;
        this.amount = amount;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public float getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public float getOriginalAmount() {
        return originalAmount;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }
}
