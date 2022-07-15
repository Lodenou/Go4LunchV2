package com.lodenou.go4lunchv2.ui.fragments.listview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.lodenou.go4lunchv2.R;

import com.lodenou.go4lunchv2.data.Go4LunchApi;
import com.lodenou.go4lunchv2.data.Go4LunchStreams;
import com.lodenou.go4lunchv2.model.Restaurant;
import com.lodenou.go4lunchv2.model.nearbysearch.GoogleSearchResults;
import com.lodenou.go4lunchv2.model.nearbysearch.Result;
import com.lodenou.go4lunchv2.service.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class ListViewFragment extends Fragment {



    private Observable<GoogleSearchResults> observable;
    private List<Result> mRestaurants;
    private LatLng currentLatLng;
    RecyclerView mRecyclerView;
    ListViewRecyclerViewAdapter mAdapter;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchRestaurants();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view_list, container, false);
        setRecyclerView(view);
        return view;
    }

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    private void fetchRestaurants(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        Task<Location> task = getFusedLocation().getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {

            @SuppressLint("CheckResult")
            @Override
            public void onSuccess(Location location) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                double lat = location.getLatitude();
                double lng = location.getLongitude();
                observable = executeHttpRequestWithRetrofit_NearbySearch(lat,lng);
                //FIXME
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(getSubscriber());

            }
        });
    }

    private Observable<GoogleSearchResults> executeHttpRequestWithRetrofit_NearbySearch(Double lat, Double lng) {
        String location = lat.toString() + "," + lng.toString();

        observable = Go4LunchApi.retrofit.create(Go4LunchApi.class).getNearbyPlaces(location, 5000);
        return observable;
    }

    private DisposableObserver<GoogleSearchResults> getSubscriber(){
        return new DisposableObserver<GoogleSearchResults>() {

            @Override
            public void onNext(GoogleSearchResults googleSearchResults) {

                //FIXME string return null =>  apicall
                Log.d("TAG", "In onNext():" + googleSearchResults.getResults().toString());
                mRestaurants.clear();
                mRestaurants.addAll(googleSearchResults.getResults());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG","On Error"+Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                Log.e("TAG","On Complete !!");

//                mRestaurants.clear();
//                mRestaurants.addAll(restaurants);
//                mAdapter.notifyDataSetChanged();

            }
        };
    }

    private FusedLocationProviderClient getFusedLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        return fusedLocationProviderClient;
    }

    // Recycler view settings
    private void setRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_list_view);

        this.mRestaurants = new ArrayList<>();

        this.mAdapter = new ListViewRecyclerViewAdapter(mRestaurants);
        mRecyclerView.setAdapter(this.mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }







    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
