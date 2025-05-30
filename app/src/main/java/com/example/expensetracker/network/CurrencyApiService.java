package com.example.expensetracker.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CurrencyApiService {
    // appel http de type get vers une url contenant une devise de base (ex: "eur")
    // {base} sera remplacé dynamiquement par la valeur passée en paramètre
    @GET("v6/942af32d5b95f07fc4a0a154/latest/{base}")
    Call<CurrencyResponse> getRates(@Path("base") String base);


}
