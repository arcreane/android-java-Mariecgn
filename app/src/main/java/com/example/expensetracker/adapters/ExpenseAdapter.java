package com.example.expensetracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.models.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<Expense> expenseList;
    private List<Expense> selectedExpenses = new ArrayList<>();

    public ExpenseAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public List<Expense> getSelectedExpenses() {
        return selectedExpenses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Expense e = expenseList.get(position);

        holder.textTitle.setText(e.getTitle());
        holder.textCategory.setText(e.getCategory());

        //Affiche les deux montants
        String formatted = String.format("%.2f %s ≈ %.2f USD",
                e.getOriginalAmount(), e.getBaseCurrency(), e.getAmount());
        holder.textAmount.setText(formatted);

        //CheckBox de sélection
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedExpenses.contains(e));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedExpenses.contains(e)) selectedExpenses.add(e);
            } else {
                selectedExpenses.remove(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textAmount, textCategory;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textAmount = itemView.findViewById(R.id.textAmount);
            textCategory = itemView.findViewById(R.id.textCategory);
            checkBox = itemView.findViewById(R.id.checkBoxSelect);
        }
    }
}
