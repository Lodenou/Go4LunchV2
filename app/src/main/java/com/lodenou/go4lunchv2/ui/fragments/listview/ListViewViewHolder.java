package com.lodenou.go4lunchv2.ui.fragments.listview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lodenou.go4lunchv2.R;


public class ListViewViewHolder extends RecyclerView.ViewHolder {

    public final TextView mContentView;
    public final TextView mRestaurantAddress;
    public final TextView mOpeningHours;
    public final TextView mDistance;
    public final TextView mWorkmatesNumber;
    public final TextView mRatingStars;
    public final ImageView mRestaurantImage;


    public ListViewViewHolder(@NonNull View itemView) {
        super(itemView);


        mContentView = (TextView) itemView.findViewById(R.id.restaurant_name);
        mRestaurantAddress = itemView.findViewById(R.id.restaurant_address);
        mOpeningHours = itemView.findViewById(R.id.opening_hours);
        mDistance = itemView.findViewById(R.id.distance);
        mWorkmatesNumber = itemView.findViewById(R.id.workmates_number);
        mRatingStars = itemView.findViewById(R.id.rating_stars);
        mRestaurantImage = itemView.findViewById(R.id.restaurant_image);

    }

    }
