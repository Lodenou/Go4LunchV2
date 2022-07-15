package com.lodenou.go4lunchv2.data;

import com.lodenou.go4lunchv2.model.autocomplete.Autocomplete;
import com.lodenou.go4lunchv2.model.detail.GoogleDetailResult;
import com.lodenou.go4lunchv2.model.nearbysearch.GoogleSearchResults;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Go4LunchStreams {


    // 1st way to do it
    public static Observable<GoogleSearchResults> streamFetchNearbySearch(String location,
                                                                          int radius) {
        Go4LunchApi googleService = Go4LunchApi.retrofit.create(Go4LunchApi.class);
        return googleService.getNearbyPlaces(location, radius)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
    // 2nd way to do it
//    public static CompositeDisposable streamFetchNearbySearch(String location, int radius) {
//        Go4LunchApi.retrofit.create(Go4LunchApi.class).getNearbyPlaces(location, radius)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .timeout(10, TimeUnit.SECONDS)
//                .subscribe(new Consumer<GoogleSearchResults>() {
//                    @Override
//                    public void accept(GoogleSearchResults googleSearchResults) throws Exception {
//                        displayDataNearby(googleSearchResults);
//                    }
//                });
//
//    }


//    public static Observable<GoogleDetailResult> streamFetchDetailResult(String placeId){
    // 1st way to do it

//        Go4LunchApi googleService = Go4LunchApi.retrofit.create(Go4LunchApi.class);
//        return googleService.getPlaceDetails(placeId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .timeout(10,TimeUnit.SECONDS);

    // 2nd way to do it
    public static CompositeDisposable streamFetchDetailResult(String placeId) {
        CompositeDisposable mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(Go4LunchApi.retrofit.create(Go4LunchApi.class).getPlaceDetails(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS)
                .subscribe(new Consumer<GoogleDetailResult>() {
                    @Override
                    public void accept(GoogleDetailResult googleDetailResult) throws Exception {
                        displayData(googleDetailResult);
                    }
                }));
        return mCompositeDisposable;
    }

    private static void displayData(GoogleDetailResult googleDetailResult) {

    }

    public static Observable<Autocomplete> streamFetchAutocompleteResult(String input,
                                                                         String location,
                                                                         int radius) {
        Go4LunchApi googleService = Go4LunchApi.retrofit.create(Go4LunchApi.class);
        return googleService.getAutocomplete(input, location, radius)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
