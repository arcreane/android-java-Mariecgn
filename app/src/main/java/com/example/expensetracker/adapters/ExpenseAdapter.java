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

// adapte les objets Expense pour les afficher dans une RecyclerView
public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    // liste des dépenses à afficher
    private List<Expense> expenseList;

    // liste des dépenses sélectionnées (par checkbox)
    private List<Expense> selectedExpenses = new ArrayList<>();

    // booléen qui dit si le mode sélection est activé
    private boolean selectionMode = false;

    // interface pour écouter les appuis longs sur un item
    public interface OnItemLongClickListener {
        void onItemLongClick(Expense expense);
    }

    // listener qu’on définira depuis l’activité
    private OnItemLongClickListener longClickListener;

    // méthode pour assigner un listener
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    // constructeur : on passe la liste des dépenses à l’adaptateur
    public ExpenseAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    // méthode pour vider la sélection
    public void clearSelection() {
        selectedExpenses.clear();
        notifyDataSetChanged(); // rafraîchit l'affichage
    }

    // getter : renvoie les dépenses sélectionnées
    public List<Expense> getSelectedExpenses() {
        return selectedExpenses;
    }

    // active ou désactive le mode sélection (affiche les checkboxes)
    public void setSelectionMode(boolean active) {
        this.selectionMode = active;
        notifyDataSetChanged();
    }

    // renvoie l’état du mode sélection
    public boolean isSelectionMode() {
        return selectionMode;
    }

    // méthode qui crée la vue d’un item (appelée par recyclerview)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
        return new ViewHolder(view);
    }

    // lie les données d’une dépense à la vue affichée
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Expense e = expenseList.get(position);

        // met à jour les textes
        holder.textTitle.setText(e.getTitle());
        holder.textCategory.setText(e.getCategory());

        // format de l’affichage du montant
        String formatted = String.format("%.2f %s ≈ %.2f USD",
                e.getOriginalAmount(), e.getBaseCurrency(), e.getAmount());
        holder.textAmount.setText(formatted);

        // affiche ou cache la checkbox selon le mode sélection
        holder.checkBox.setVisibility(selectionMode ? View.VISIBLE : View.GONE);

        // évite les bugs de recyclage sur les checkboxes
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedExpenses.contains(e));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedExpenses.contains(e)) selectedExpenses.add(e);
            } else {
                selectedExpenses.remove(e);
            }
        });

        // appui long sur un item : active la sélection et coche
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(e);
            }
            if (!selectedExpenses.contains(e)) selectedExpenses.add(e);
            notifyDataSetChanged(); // met à jour l’affichage
            return true;
        });
    }

    // renvoie le nombre d’éléments à afficher
    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    // classe interne pour contenir les vues d’un item
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