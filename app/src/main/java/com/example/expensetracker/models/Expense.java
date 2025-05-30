package com.example.expensetracker.models;
// classe qui représente une dépense
public class Expense {

    // titre de la dépense (ex : "restaurant")
    private String title;

    // montant converti en usd (utilisé pour le graphique)
    private float amount;

    // montant d'origine saisi par l'utilisateur
    private float originalAmount;

    // devise d'origine (ex : "eur", "usd", "jpy")
    private String baseCurrency;

    // catégorie de la dépense (ex : "loisir", "transport")
    private String category;

    // constructeur pour créer une dépense avec tous les champs
    public Expense(String title, float originalAmount, String baseCurrency, float amount, String category) {
        this.title = title;
        this.originalAmount = originalAmount;
        this.baseCurrency = baseCurrency;
        this.amount = amount;
        this.category = category;
    }

    // getter pour le titre
    public String getTitle() {
        return title;
    }

    // getter pour le montant en usd
    public float getAmount() {
        return amount;
    }

    // getter pour la catégorie
    public String getCategory() {
        return category;
    }

    // getter pour le montant saisi par l'utilisateur
    public float getOriginalAmount() {
        return originalAmount;
    }

    // getter pour la devise d'origine
    public String getBaseCurrency() {
        return baseCurrency;
    }
}

