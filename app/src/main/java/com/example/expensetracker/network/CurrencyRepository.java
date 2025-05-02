// com.example.expensetracker.network.CurrencyRepository.java
package com.example.expensetracker.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyRepository {

    private static final String BASE_URL = "https://v6.exchangerate-api.com/";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static CurrencyApiService getApiService() {
        return retrofit.create(CurrencyApiService.class);
    }
}
