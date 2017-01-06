package com.example.bipul.fauxify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar myToolbar;
    public static String rupeesymbol;
    TextView headName, headEmail, headNumber;
    static String requestURL;
    FragmentTransaction fragmentTransaction;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap 'back' again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    //    @Override
//    public void onBackPressed() {
//        //Checking for fragment count on backstack
//        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().popBackStack();
//        } else if (!doubleBackToExitPressedOnce) {
//            this.doubleBackToExitPressedOnce = true;
//            Toast.makeText(this,"Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
//
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    doubleBackToExitPressedOnce = false;
//                }
//            }, 2000);
//        } else {
//            super.onBackPressed();
//        }
//    }
    // 192.168.0.100 ip for use in mobile
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        rupeesymbol = getApplicationContext().getString(R.string.Rs);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
//        }


        requestURL = "http://192.168.0.103:3000/api/";

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

//        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.main_container, new RestaurantFragment(), "RestFragment");
//        fragmentTransaction.commit();
//        getSupportActionBar().setTitle("Restaurants");


        View header = navigationView.getHeaderView(0);
        headName = (TextView) header.findViewById(R.id.navheadName);
        headEmail = (TextView) header.findViewById(R.id.navheademail);
        headNumber = (TextView) header.findViewById(R.id.navheadcontact);

        SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        String personEmail = sharedPref.getString("personEmail", null);
        String personDisplayName = sharedPref.getString("personDisplayName", null);

        headName.setText(personDisplayName);
        headEmail.setText(personEmail);
        //headNumber.setText("9176907049");


        // below code is to implement default fragment of Restaurants by default when app is started
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new RestaurantFragment()).commit();
            navigationView.getMenu().getItem(0).setChecked(true);
            getSupportActionBar().setTitle("Restaurants");
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_option_restaurants:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new RestaurantFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Restaurants");
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_option_orders:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new MyOrdersFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("My Orders");
                        getSupportActionBar().setSubtitle(null);
                        drawerLayout.closeDrawers();

                        break;

                    case R.id.nav_option_offers:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new OffersFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Offers");
                        getSupportActionBar().setSubtitle(null);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_option_addresses:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new AddressesFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Addresses");
                        getSupportActionBar().setSubtitle(null);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.nav_option_help:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new HelpFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle("Help");
                        getSupportActionBar().setSubtitle(null);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.logout:
                        Log.e("Sign out button clicked", " success");
                        Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();

                        Log.e("pref ", "cleared");
                        Intent intent = new Intent(getApplicationContext(), GoogleSignIn.class);
                        startActivity(intent);


                }
                return true;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        View view = new View(this);
        view.setPadding(16, 0, 0, 0);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

}