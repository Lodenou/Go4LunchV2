package com.lodenou.go4lunchv2.ui.fragments.listview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.lodenou.go4lunchv2.R;
import com.lodenou.go4lunchv2.model.nearbysearch.Result;
import com.lodenou.go4lunchv2.service.Utils;
import com.lodenou.go4lunchv2.ui.YourLunchActivity;

import java.util.List;

public class ListViewRecyclerViewAdapter extends RecyclerView.Adapter<ListViewViewHolder>{

    private List<Result> mRestaurants;
    Context context;

    public ListViewRecyclerViewAdapter(List<Result> restaurants) {
        this.mRestaurants = restaurants;

    }

    @NonNull
    @Override
    public ListViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.fragment_list_view_item, parent, false);

        return new ListViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewViewHolder holder, int position) {
        final Result restaurant = mRestaurants.get(position);

        TextView restaurantName = holder.mContentView;
        TextView restaurantAddress = holder.mRestaurantAddress;
        TextView openingHours = holder.mOpeningHours;
        final TextView distance = holder.mDistance;
//        final TextView workmatesNumber = holder.mWorkmatesNumber;
        TextView ratingStars = holder.mRatingStars;
        ImageView restaurantImage = holder.mRestaurantImage;


        restaurantName.setText(restaurant.getName());
        restaurantAddress.setText(restaurant.getVicinity());
        openingHours.setText(Utils.isOpenOrNot(restaurant.getOpeningHours()));

        // Distance setting
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Location locationResto = new Location(LocationManager.GPS_PROVIDER);
                locationResto.setLatitude(restaurant.getGeometry().getLocation().getLat());
                locationResto.setLongitude(restaurant.getGeometry().getLocation().getLng());
                Float distance1 = location.distanceTo(locationResto);
                int i = distance1.intValue();
                distance.setText(i + " m");
            }
        });

        // 3 stars version
        double ratingstars3 = restaurant.getRating();
        ratingstars3 = Math.round(ratingstars3*3/5*100.0)/100.0;
        ratingStars.setText(ratingstars3+"");

        // Restaurant photo
         if (restaurant.getPhotos() != null && restaurant.getPhotos().size() > 0) {
             Glide.with(context).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&maxheight=400&photoreference=" +
                             restaurant.getPhotos().get(0).getPhotoReference() + "&key=" + R.string.API_KEY2)
                     .into(restaurantImage);
         }

         // onClick on item start YourLunchActivity
         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Context context = view.getContext();
                 Intent intent = new Intent(context, YourLunchActivity.class);;
                 context.startActivity(intent);
             }
         });

    }
    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }
}
