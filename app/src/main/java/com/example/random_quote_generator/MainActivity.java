package com.example.random_quote_generator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.random_quote_generator.Adapter.QuotesAdapter;
import com.example.random_quote_generator.DataBase.QuoteContract;
import com.example.random_quote_generator.DataBase.QuoteDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Switch switchTheme;
    private SharedPreferences sharedPreferences;
    private QuotesAdapter quotesAdapter;
    private List<String> quotesList;
    private QuoteDbHelper dbHelper;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set the theme before calling super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);
        setAppTheme(isDarkMode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views after setContentView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new QuoteDbHelper(this);
        quotesList = loadQuotesFromDatabase(); // Load quotes from SQLite database
        quotesAdapter = new QuotesAdapter(this,quotesList,this);
        recyclerView.setAdapter(quotesAdapter);

        switchTheme = findViewById(R.id.switch_theme);
        switchTheme.setChecked(isDarkMode);

        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save the theme state
                sharedPreferences.edit().putBoolean("isDarkMode", isChecked).apply();
                // Recreate activity to apply the new theme
                recreate();
            }
        });

        // Update switch text initially
        updateSwitchText(isDarkMode);
    }
    public void handleEmptyState() {
        // Show a message or a view to indicate that the list is empty
        Toast.makeText(this, "No quotes available", Toast.LENGTH_SHORT).show();
        // Optionally, you can hide the RecyclerView and show a different view
        recyclerView.setVisibility(View.GONE);
        // emptyView.setVisibility(View.VISIBLE);
    }

    private void setAppTheme(boolean isDarkMode) {
        if (isDarkMode) {
            setTheme(R.style.Theme_Random_Quote_Generator_Dark);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            setTheme(R.style.Theme_Random_Quote_Generator_Light);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void updateSwitchText(boolean isDarkMode) {
        if (isDarkMode) {
            switchTheme.setText("Light Mode");
            // Update other text colors as needed
        } else {
            switchTheme.setText("Dark Mode");
            // Update other text colors as needed
        }
    }

    public void move_to_quoteactivity(View view) {
        Intent intent = new Intent(MainActivity.this, QuoteActivity.class);
        startActivity(intent);
    }

    // Method to load quotes from SQLite database
    private List<String> loadQuotesFromDatabase() {
        List<String> quotes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database you will actually use after this query.
        String[] projection = {
                QuoteContract.QuoteEntry.COLUMN_QUOTE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = QuoteContract.QuoteEntry.COLUMN_QUOTE + " ASC";

        Cursor cursor = db.query(
                QuoteContract.QuoteEntry.TABLE_NAME,   // The table to query
                projection,                         // The array of columns to return (pass null to get all)
                null,                      // The columns for the WHERE clause
                null,                   // The values for the WHERE clause
                null,                       // don't group the rows
                null,                        // don't filter by row groups
                sortOrder                           // The sort order
        );

        while (cursor.moveToNext()) {
            String quote = cursor.getString(cursor.getColumnIndexOrThrow(QuoteContract.QuoteEntry.COLUMN_QUOTE));
            quotes.add(quote);
        }
        cursor.close();
        return quotes;
    }
}