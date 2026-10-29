package com.aibal.expense;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private EditText etTitle, etAmount;
    private Button btnAdd;
    private TextView tvTotal;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etAmount = view.findViewById(R.id.etAmount);
        btnAdd = view.findViewById(R.id.btnAdd);
        tvTotal = view.findViewById(R.id.tvTotal);

        dbHelper = new DatabaseHelper(requireContext());

        updateTotal();

        btnAdd.setOnClickListener(v -> addExpense());

        return view;
    }

    private void addExpense() {
        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(amountStr)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        boolean inserted = dbHelper.addExpense(new Expense(title, amount, date));
        if (inserted) {
            Toast.makeText(getContext(), "Expense Added!", Toast.LENGTH_SHORT).show();
            etTitle.setText("");
            etAmount.setText("");
            updateTotal();
        } else {
            Toast.makeText(getContext(), "Error adding expense", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTotal() {
        double total = dbHelper.getTotalExpenses();
        tvTotal.setText(String.format(Locale.getDefault(), "Total Spent: ₹%.2f", total));
    }
}