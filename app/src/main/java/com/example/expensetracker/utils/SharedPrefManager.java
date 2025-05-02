package com.example.expensetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.expensetracker.models.Expense;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPrefManager {
    private static final String PREF_NAME = "expenses_pref";
    private static final String KEY_EXPENSES = "expenses";

    public static void saveExpenses(Context context, ArrayList<Expense> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EXPENSES, new Gson().toJson(list));
        editor.apply();

    }

    public static ArrayList<Expense> getExpenses(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_EXPENSES, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<Expense>>() {}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public static void clearExpenses(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_EXPENSES).apply();
    }
}
