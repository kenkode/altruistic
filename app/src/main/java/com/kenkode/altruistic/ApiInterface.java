package com.kenkode.altruistic;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET(Common.availableBusesUrl)
    Call<BusesResponse> getAvailableBuses();
}
