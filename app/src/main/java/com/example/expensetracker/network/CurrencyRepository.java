// classe utilitaire pour configurer retrofit et fournir une instance de l'api
package com.example.expensetracker.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyRepository {

    // url de base de l'api utilisée pour obtenir les taux de change
    private static final String BASE_URL = "https://v6.exchangerate-api.com/";

    // création d'une instance retrofit configurée avec la base url et le convertisseur json (gson)
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // conversion automatique json ↔ java
            .build();

    // retourne une implémentation de l'interface CurrencyApiService
    public static CurrencyApiService getApiService() {
        return retrofit.create(CurrencyApiService.class);
    }
}
