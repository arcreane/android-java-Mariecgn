package com.example.expensetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.expensetracker.models.Expense;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

// classe utilitaire pour sauvegarder et récupérer les dépenses localement via sharedpreferences
public class SharedPrefManager {

    // nom du fichier sharedpreferences
    private static final String PREF_NAME = "expenses_pref";

    // clé utilisée pour stocker la liste de dépenses
    private static final String KEY_EXPENSES = "expenses";

    // méthode pour sauvegarder la liste des dépenses en json dans sharedpreferences
    public static void saveExpenses(Context context, ArrayList<Expense> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EXPENSES, new Gson().toJson(list)); // conversion de la liste en json
        editor.apply(); // sauvegarde les changements
    }

    // méthode pour récupérer la liste des dépenses depuis sharedpreferences
    public static ArrayList<Expense> getExpenses(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_EXPENSES, null); // récupère le json sauvegardé

        if (json != null) {
            // convertit le json en arraylist<expense> avec gson
            Type type = new TypeToken<ArrayList<Expense>>() {}.getType();
            return new Gson().fromJson(json, type);
        }

        // si aucune donnée trouvée, retourne une liste vide
        return new ArrayList<>();
    }

    // méthode pour effacer toutes les dépenses sauvegardées
    public static void clearExpenses(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_EXPENSES).apply(); // supprime la clé contenant les dépenses
    }
}
