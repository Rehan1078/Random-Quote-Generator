package com.example.random_quote_generator.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.random_quote_generator.DataBase.QuoteContract;
import com.example.random_quote_generator.DataBase.QuoteDbHelper;
import com.example.random_quote_generator.MainActivity;
import com.example.random_quote_generator.R;

import java.util.List;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder> {

    private List<String> quotes;
    private Context context;
    private MainActivity mainActivity;

    public QuotesAdapter(Context context, List<String> quotes, MainActivity mainActivity) {
        this.context = context;
        this.quotes = quotes;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quote, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String quote = quotes.get(position);
        holder.quoteTextView.setText(quote);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(quote, position);
            }
        });

    }
    private void showAlertDialog(final String quote, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Quote Actions")
                .setMessage("Choose an action for this quote:")
                .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        shareQuote(quote);
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteQuote(position);
                    }
                })
                .setNeutralButton("Cancel", null)
                .show();
    }

    private void shareQuote(String quote) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, quote);
        context.startActivity(Intent.createChooser(shareIntent, "Share quote via"));
    }

    private void deleteQuote(int position) {
        final String quoteToDelete = quotes.get(position);
        quotes.remove(position);
        notifyItemRemoved(position);
        QuoteDbHelper dbHelper = new QuoteDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = QuoteContract.QuoteEntry.COLUMN_QUOTE + " LIKE ?";
        String[] selectionArgs = { quoteToDelete };
        int deletedRows = db.delete(QuoteContract.QuoteEntry.TABLE_NAME, selection, selectionArgs);
        // Check if deletion was successful
        if (deletedRows > 0) {
            Toast.makeText(context, "Quote Removed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Failed to remove", Toast.LENGTH_SHORT).show();
        }

        if (quotes.isEmpty()) {
            notifyDataSetChanged();
            mainActivity.handleEmptyState();
        }
        db.close();
    }


    @Override
    public int getItemCount() {
        return quotes.size();
    }

    public void addQuote(String quote) {
        quotes.add(quote);
        try {
            notifyDataSetChanged();
        }catch (Exception e){
            // Notify adapter about data change
        }
    }

    public class QuoteViewHolder extends RecyclerView.ViewHolder {

        private TextView quoteTextView;

        public QuoteViewHolder(@NonNull View itemView) {
            super(itemView);
            quoteTextView = itemView.findViewById(R.id.quote_text);
        }


    }
}