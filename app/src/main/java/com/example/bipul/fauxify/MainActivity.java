package com.example.bipul.fauxify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar myToolbar;
    static String requestURL;
    FragmentTransaction fragmentTransaction;
    NavigationView navigationView;

// 192.168.0.100 ip for use in mobile
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, new RestaurantFragment());
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Restaurants");


        navigationView = (NavigationView) findViewById(R.id.navigationView);
        assert navigationView != null;
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
                        fragmentTransaction.replace(R.id.main_container, new OrdersFragment());
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
    public boolean onNavigationItemSelected(MenuItem item) {

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {

        }
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

}

