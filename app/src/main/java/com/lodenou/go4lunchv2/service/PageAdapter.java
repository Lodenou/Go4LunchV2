package com.lodenou.go4lunchv2.service;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.lodenou.go4lunchv2.ui.fragments.ListViewFragment;
import com.lodenou.go4lunchv2.ui.fragments.MapFragment;
import com.lodenou.go4lunchv2.ui.fragments.WorkmatesFragment;


public class PageAdapter extends FragmentPagerAdapter {



    // Default Constructor
    public PageAdapter(FragmentManager mgr) {
        super(mgr);
    }

    @Override
    public int getCount() {
        return 3; // 3 - Number of page to show
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return MapFragment.newInstance();
        }

        else if(position == 1) {
            return ListViewFragment.newInstance();
        }
        else   {
            return WorkmatesFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {


        switch (position) {
            case 0: //Page number 1
                return "Map View";
            case 1: //Page number 2
                return "List View";
            case 2: //Page number 3
                return "Workmates";
            default:
                return null;
        }
    }


}
