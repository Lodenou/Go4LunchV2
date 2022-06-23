package com.lodenou.go4lunchv2.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lodenou.go4lunchv2.R;
import com.lodenou.go4lunchv2.databinding.ActivityMainBinding;
import com.lodenou.go4lunchv2.databinding.NavHeaderMainBinding;
import com.lodenou.go4lunchv2.service.PageAdapter;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends AppCompatActivity {

//    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
//    private NavHeaderMainBinding navViewBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // view binding
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // Add binding for nav_view
//         navViewBinding = NavHeaderMainBinding.bind(binding.navView);
//
//        View view = binding.getRoot();
        setContentView(R.layout.activity_main);
//        setContentView(view);
        mAuth = FirebaseAuth.getInstance();
        checkUser();
        createNavMenu();
        configureViewPagerAndTabs();
        setTabIcons();
        setNavMenuOnClicks();
    }

    private void checkUser() {
        // get current user
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        View headerView = ((NavigationView) findViewById(R.id.nav_view))
                .getHeaderView(0);
        if (firebaseUser == null) {
            // User not logged in
            startActivity(new Intent(this, ConnexionActivity.class));
            finish();
        }
        // User logged in
        // Get user info
        else {
            String email = firebaseUser.getEmail();
            TextView navUserMail = headerView
                    .findViewById(R.id.nav_user_mail);
            navUserMail.setText(email);
            Toast.makeText(this, email, Toast.LENGTH_LONG).show();

        }
    }

    // 4 - Declare Subscription
    private Disposable disposable;

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }
    // -----------------
    // ACTIONS
    // -----------------


//        this.streamShowString();


    // ------------------------------
    //  Reactive X
    // ------------------------------

    // 1 - Create Observable
    private Observable<String> getObservable() {
        return Observable.just("Cool !");
    }

    // 2 - Create Subscriber

    private DisposableObserver<String> getSubscriber() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String item) {
//                textView.setText("Observable emits : "+item);
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG", "On Error" + Log.getStackTraceString(e));
            }

            @Override
            public void onComplete() {
                Log.e("TAG", "On Complete !!");
            }
        };
    }

    // 3 - Create Stream and execute it
    private void streamShowString() {
        this.disposable = this.getObservable()
                .subscribeWith(getSubscriber());
    }

    // 5 - Dispose subscription
    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }


    // SET UI

    private void createNavMenu() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        };
    }

    private void setNavMenuOnClicks() {
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_yourlunch:
//                        mUser = new User();
////                        FirebaseUser userf = FirebaseAuth.getInstance().getCurrentUser();
//                        String id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//                        UserHelper.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                mUser = documentSnapshot.toObject(User.class);
//
//                                if (mUser.getRestaurantPlaceId() != null && mUser.getRestaurantPlaceId() != "") {
//                                    Intent intent = new Intent(MainActivity.this, YourLunchActivity.class);
//                                    intent.putExtra("key", mUser.getRestaurantPlaceId());
//                                    startActivity(intent);
//                                } else {
//                                    new AlertDialog.Builder(MainActivity.this)
//                                            .setTitle("Oops")
//                                            .setMessage("You didn't chose a restaurant !")
//                                            .show();
//                                }
//                            }
//                        });
                        return true;
                    case R.id.nav_settings:
                        Intent intent2 = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent2);
                        return true;
                    case R.id.nav_logout:
                        logOut();
                        return true;
                    case R.id.chat:
                        Intent i = new Intent(MainActivity.this, ChatActivity.class);
                        startActivity(i);
                    default:
                        return false;
                }
            }
        });
    }

    private void configureViewPagerAndTabs() {

        ViewPager pager = (ViewPager) findViewById(R.id.activity_main_viewpager);
        //Set Adapter PageAdapter and glue it together
        pager.setAdapter(new PageAdapter(getSupportFragmentManager()));

        TabLayout tabs = (TabLayout) findViewById(R.id.tab_layout);
        // Glue TabLayout and ViewPager together
        tabs.setupWithViewPager(pager);
        // Design purpose. Tabs have the same width
        tabs.setTabMode(TabLayout.MODE_FIXED);
        final ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle("I'm hungry !");
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        toolbar.setTitle("I'm hungry !");
                        break;
                    case 1:
                        toolbar.setTitle("I'm hungry !");
                        break;
                    case 2:
                        toolbar.setTitle("Available workmates");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setTabIcons() {
        int[] tabIcons = {
                R.drawable.ic_baseline_map_24,
                R.drawable.ic_baseline_view_list_24,
                R.drawable.ic_baseline_people_24
        };
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void logOut() {

        // FIREBASE LOGOUT
        FirebaseAuth.getInstance().signOut();
        // GOOGLE LOGOUT
        GoogleSignInOptions gso = new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        googleSignInClient.signOut();
        Intent intent = new Intent(MainActivity.this, ConnexionActivity.class);
        finish();
        startActivity(intent);
        // FACEBOOK LOGOUT
        LoginManager.getInstance().logOut();
    }


}