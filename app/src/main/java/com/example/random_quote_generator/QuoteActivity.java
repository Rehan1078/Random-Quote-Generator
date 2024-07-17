package com.example.random_quote_generator;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.random_quote_generator.Adapter.QuotesAdapter;
import com.example.random_quote_generator.DataBase.QuoteContract;
import com.example.random_quote_generator.DataBase.QuoteDbHelper;
import com.example.random_quote_generator.QuoteAPI.QuoteApi;
import com.example.random_quote_generator.QuoteAPI.QuoteModel;
import com.example.random_quote_generator.QuoteAPI.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class QuoteActivity extends AppCompatActivity {

    private TextView quoteTextView;
    private ProgressBar progressBar;
    private QuoteDbHelper dbHelper;
    private ImageView next_quote;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);

        quoteTextView = findViewById(R.id.quote_tv);
        progressBar = findViewById(R.id.progress_bar);
        dbHelper = new QuoteDbHelper(this);
        next_quote = findViewById(R.id.next_quote);
        next_quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRandomQuote();
            }
        });

        fetchRandomQuote();
    }



    public void save_quote(View view) {
        String currentQuote = quoteTextView.getText().toString();

// Check if the quote already exists in the database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {QuoteContract.QuoteEntry.COLUMN_QUOTE};
        String selection = QuoteContract.QuoteEntry.COLUMN_QUOTE + " = ?";
        String[] selectionArgs = {currentQuote};

        Cursor cursor = db.query(
                QuoteContract.QuoteEntry.TABLE_NAME,   // The table to query
                projection,                            // The columns to return
                selection,                             // The columns for the WHERE clause
                selectionArgs,                         // The values for the WHERE clause
                null,                                  // Group by rows
                null,                                  // Filter by row groups
                null                                   // The sort order
        );

        if (cursor.getCount() > 0) {
            Toast.makeText(this, "Quote already present", Toast.LENGTH_SHORT).show();
        } else {
            // Save quote to SQLite database
            SQLiteDatabase writableDb = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(QuoteContract.QuoteEntry.COLUMN_QUOTE, currentQuote);
            long newRowId = writableDb.insert(QuoteContract.QuoteEntry.TABLE_NAME, null, values);

            if (newRowId != -1) {
                Toast.makeText(this, "Quote Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to Save", Toast.LENGTH_SHORT).show();
            }
        }

        cursor.close();
        db.close();

    }


    private void fetchRandomQuote() {
        try {
            QuoteApi quoteApi = RetrofitInstance.getRetrofitInstance().create(QuoteApi.class);

            Call<List<QuoteModel>> call = quoteApi.getRandomQuote();
            call.enqueue(new Callback<List<QuoteModel>>() {

                @Override
                public void onResponse(Call<List<QuoteModel>> call, retrofit2.Response<List<QuoteModel>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<QuoteModel> quotes = response.body();
                        if (!quotes.isEmpty()) {
                            QuoteModel randomQuote = quotes.get(0); // Assuming only one quote is returned
                            quoteTextView.setText(randomQuote.getQ());

                            // Apply text color based on theme
                            if (isDarkMode()) {
                                quoteTextView.setTextColor(getResources().getColor(android.R.color.white));
                                next_quote.setImageResource(R.drawable.forward_white);  // Change image source to white icon
                                //next_quote.setBackgroundTintList(ContextCompat.getColorStateList(QuoteActivity.this, R.color.black));
                            } else {
                                quoteTextView.setTextColor(getResources().getColor(android.R.color.black));
                                next_quote.setImageResource(R.drawable.forward_black);  // Change image source to black icon
                                //next_quote.setBackgroundTintList(ContextCompat.getColorStateList(QuoteActivity.this, R.color.white));
                            }
                        }
                    } else {
                        // Handle unsuccessful response
                        quoteTextView.setText("Failed to fetch quote");
                    }
                }

                @Override
                public void onFailure(Call<List<QuoteModel>> call, Throwable t) {
                    quoteTextView.setText("Error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            quoteTextView.setText("Exception: " + e.getMessage());
        }
    }

    private boolean isDarkMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isDarkMode", false);
    }

    public void move_back(View view) {
        Intent intent = new Intent(QuoteActivity.this, MainActivity.class);
        startActivity(intent);
    }
}