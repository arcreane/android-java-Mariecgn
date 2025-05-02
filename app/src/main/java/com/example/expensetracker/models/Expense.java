package com.example.expensetracker.models;

public class Expense {
    private String title;
    private float amount;
    private String category;

    public Expense(String title, float amount, String category) {
        this.title = title;
        this.amount = amount;
        this.category = category;
    }

    public String getTitle() { return title; }
    public float getAmount() { return amount; }
    public String getCategory() { return category; }
}
