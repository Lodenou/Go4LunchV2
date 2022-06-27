package com.lodenou.go4lunchv2.model.nearbysearch;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class OpeningHours {

    @SerializedName("open_now")
    @Expose
    private Boolean openNow;

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

}
