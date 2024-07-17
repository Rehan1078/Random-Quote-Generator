package com.example.random_quote_generator.DataBase;

import android.provider.BaseColumns;

public class QuoteContract {
    private QuoteContract() {}

    public static class QuoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "quotes";
        public static final String COLUMN_QUOTE = "quote";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_QUOTE + " TEXT)";

        public static final String DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
