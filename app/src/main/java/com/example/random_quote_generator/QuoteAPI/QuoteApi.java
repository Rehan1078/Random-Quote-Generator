package com.example.random_quote_generator.QuoteAPI;


import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

    public interface QuoteApi {

        @GET("random")
        Call<List<QuoteModel>> getRandomQuote();
    }
