package com.example.expensetracker.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CurrencyApiService {
    @GET("v6/942af32d5b95f07fc4a0a154/latest/{base}")
    Call<CurrencyResponse> getRates(@Path("base") String base);


}
