package com.example.bipul.fauxify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle mActionBarToggle;
    DrawerLayout mDrawerLayout;
    Toolbar mToolbar;
    public static String rupeesymbol;
    TextView mHeadNameTextView, mHeadEmailTextView, mHeadNumberTextView;
    FragmentTransaction fragmentTransaction;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.tap_back_again_exit, Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
//        }

// following commented out portion is for generating key-hash
//        MessageDigest md = null;
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    getPackageName(),
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//            }
//        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {
//
//        }
//        Log.i("SecretKey = ", Base64.encodeToString(md.digest(), Base64.DEFAULT));

        mToolbar = (Toolbar) findViewById(R.id.toolbar_mainactivity);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

//        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.main_container, new RestaurantFragment(), "RestFragment");
//        fragmentTransaction.commit();
//        getSupportActionBar().setTitle("Restaurants");

        View header = navigationView.getHeaderView(0);
        mHeadNameTextView = (TextView) header.findViewById(R.id.navhead_name);
        mHeadEmailTextView = (TextView) header.findViewById(R.id.navhead_email);
        mHeadNumberTextView = (TextView) header.findViewById(R.id.navhead_contact);

        SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        String personEmail = sharedPref.getString("personEmail", null);
        String personDisplayName = sharedPref.getString("personDisplayName", null);
        String personContactNumber = sharedPref.getString("personContactNumber", null);
        Log.i("defaultpersonContactNo", personContactNumber);
        mHeadNameTextView.setText(personDisplayName);
        mHeadEmailTextView.setText(personEmail);
        mHeadNumberTextView.setText(personContactNumber);

        //if contact no. isn't there in sharedpref, get it from DB (although it should definitely be there)
        if (personContactNumber == null || personContactNumber.matches("")) {

            Log.i("pNo not avail-shredpref", "territory");
            new BGTaskGetPhoneNum().execute();
        }

        // below code is to implement default fragment of Restaurants by default when app is started
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new RestaurantFragment()).commit();
            navigationView.getMenu().getItem(0).setChecked(true);
            getSupportActionBar().setTitle(R.string.restaurants);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.nav_option_restaurants:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new RestaurantFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle(R.string.restaurants);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_option_orders:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new MyOrdersFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle(R.string.my_orders);
                        getSupportActionBar().setSubtitle(null);
                        mDrawerLayout.closeDrawers();

                        break;

                    case R.id.nav_option_offers:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new OffersFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle(R.string.offers);
                        getSupportActionBar().setSubtitle(null);
                        mDrawerLayout.closeDrawers();
                        break;

                    case R.id.nav_option_addresses:
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.main_container, new AddressesFragment());
                        fragmentTransaction.commit();
                        getSupportActionBar().setTitle(R.string.addresses);
                        getSupportActionBar().setSubtitle(null);
                        mDrawerLayout.closeDrawers();
                        break;

//                    case R.id.nav_option_help:
//                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.main_container, new HelpFragment());
//                        fragmentTransaction.commit();
//                        getSupportActionBar().setTitle("Help");
//                        getSupportActionBar().setSubtitle(null);
//                        drawerLayout.closeDrawers();
//                        break;

                    case R.id.logout:
                        Log.i("Sign out button clicked", " success");
                        Toast.makeText(MainActivity.this, R.string.logged_out, Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();

                        Log.e("pref ", "cleared");
                        Intent intent = new Intent(getApplicationContext(), GoogleSignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                        finish();
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

        if (mActionBarToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarToggle.syncState();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    private class BGTaskGetPhoneNum extends AsyncTask<Void, Void, String> {

        String urlFinal;
        String JSON_STRING;
        String phoneNumber;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            String userId, userToken;
            sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            userId = sharedPref.getString("userId", null);
            userToken = sharedPref.getString("userToken", null);

            String utfUserId = null;
            try {
                utfUserId = URLEncoder.encode(userId, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            urlFinal = getString(R.string.request_url) + "Fauxusers/" + utfUserId + "?access_token=" + userToken;
            Log.e("json_url", urlFinal);
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL urll = new URL(urlFinal);
                HttpURLConnection httpConnection = (HttpURLConnection) urll.openConnection();

                InputStream inputStream = httpConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpConnection.disconnect();
                String resultjson = stringBuilder.toString().trim();
                Log.e("result", resultjson);

                JSONObject jobject = new JSONObject(resultjson);
                phoneNumber = jobject.getString("Contact");

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (phoneNumber != null && !phoneNumber.matches("")) {
                mHeadNumberTextView.setVisibility(View.VISIBLE);
                mHeadNumberTextView.setText(phoneNumber);

                SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("personContactNumber", phoneNumber);
                editor.apply();
            }
        }
    }
}