package com.lodenou.go4lunchv2.data;

import com.lodenou.go4lunchv2.R;
import com.lodenou.go4lunchv2.model.autocomplete.Autocomplete;
import com.lodenou.go4lunchv2.model.detail.GoogleDetailResult;
import com.lodenou.go4lunchv2.model.nearbysearch.GoogleSearchResults;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Go4LunchApi {



    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("nearbysearch/json?type=restaurant&key=" + R.string.API_KEY2)
    Call<GoogleSearchResults> getNearbyPlaces(@Query("location") String location, @Query("radius") int radius);

    @GET("details/json?fields=name,vicinity,international_phone_number,website,photo,rating,geometry,place_id,opening_hours&key=" + R.string.API_KEY2)
    Call<GoogleDetailResult> getPlaceDetails(@Query("place_id") String placeId);

    @GET("autocomplete/json?types=establishment&radius=5000&strictbounds&key=" + R.string.API_KEY2)
    Call<Autocomplete> getAutocomplete(@Query("input") String input, @Query("location") String location, @Query("radius") int radius);

}
