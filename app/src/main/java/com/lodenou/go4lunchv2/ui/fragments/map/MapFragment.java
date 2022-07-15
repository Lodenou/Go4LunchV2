package com.lodenou.go4lunchv2.ui.fragments.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.lodenou.go4lunchv2.R;
import com.lodenou.go4lunchv2.data.Go4LunchApi;
import com.lodenou.go4lunchv2.data.Go4LunchStreams;
import com.lodenou.go4lunchv2.model.Restaurant;
import com.lodenou.go4lunchv2.model.nearbysearch.GoogleSearchResults;
import com.lodenou.go4lunchv2.service.Utils;
import com.lodenou.go4lunchv2.ui.MainActivity;

import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng currentLatLng;
    Observable<GoogleSearchResults> observable;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapPermission();
    }
    @Override
    public void onResume() {
        super.onResume();
        // refresh map
        if (mMap != null) { //prevent crashing if the map doesn't exist yet (eg. on starting activity)
            mMap.clear();
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // refresh the map
        googleMap.clear();

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
                currentLatLng = new LatLng(lat, lng);
                observable = executeHttpRequestWithRetrofit(lat,lng);
                //FIXME
                observable.subscribe(getSubscriber());

                mMap.setMyLocationEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

            }
        });
    }


    private void mapPermission() {
        Dexter.withContext(requireContext())
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Log.e("Dexter", "There was an error: " + error.toString());
                    }
                }).check();
    }

    private FusedLocationProviderClient getFusedLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        return fusedLocationProviderClient;
    }

    private Observable<GoogleSearchResults> executeHttpRequestWithRetrofit(Double lat, Double lng) {
        String location = lat.toString() + "," + lng.toString();
        observable = Go4LunchStreams.streamFetchNearbySearch(location, 5000);
      return observable;
    }

    private DisposableObserver<GoogleSearchResults> getSubscriber(){
        return new DisposableObserver<GoogleSearchResults>() {

            @Override
            public void onNext(GoogleSearchResults googleSearchResults) {

                //FIXME string return null => check apicall
                Log.d("TAG", "In onNext():" + googleSearchResults.getResults().toString());
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG","On Error"+Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                Log.e("TAG","On Complete !!");

//                GoogleSearchResults gsr = new GoogleSearchResults();
//                Log.e("TAG",gsr.getResults().toString());

            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

