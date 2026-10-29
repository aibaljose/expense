package com.aibal.expense;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportFragment extends Fragment {

    private RecyclerView rvExpenses;
    private TextView tvReportSummary;
    private EditText etSearch;

    private DatabaseHelper dbHelper;
    private ExpenseAdapter adapter;
    private List<Expense> allExpensesList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        rvExpenses = view.findViewById(R.id.rvReportExpenses);
        tvReportSummary = view.findViewById(R.id.tvReportSummary);
        etSearch = view.findViewById(R.id.etSearch);

        dbHelper = new DatabaseHelper(requireContext());

        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));

        loadAllReports();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void loadAllReports() {
        allExpensesList = dbHelper.getAllExpenses();

        adapter = new ExpenseAdapter(allExpensesList, expense -> {
            dbHelper.deleteExpense(expense.getId());
            Toast.makeText(getContext(), "Expense deleted", Toast.LENGTH_SHORT).show();
            loadAllReports();
        });

        rvExpenses.setAdapter(adapter);
        updateSummary(allExpensesList);
    }

    private void filter(String text) {
        List<Expense> filteredList = new ArrayList<>();
        for (Expense expense : allExpensesList) {
            if (expense.getTitle().toLowerCase().contains(text.toLowerCase()) ||
                    expense.getDate().contains(text)) {
                filteredList.add(expense);
            }
        }
        adapter.updateData(filteredList);
        updateSummary(filteredList);
    }

    private void updateSummary(List<Expense> list) {
        double sum = 0;
        for (Expense e : list) {
            sum += e.getAmount();
        }
        tvReportSummary.setText(String.format(
                Locale.getDefault(),
                "Records: %d | Total: $%.2f", list.size(), sum
        ));
    }
}