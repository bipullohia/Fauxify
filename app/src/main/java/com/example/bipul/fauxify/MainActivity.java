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

        //following commented out portion is for generating key-hash

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

        requestURL = "http://fauxify.com/api/";

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
        headNumber.setVisibility(View.GONE);

        SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        String personEmail = sharedPref.getString("personEmail", null);
        String personDisplayName = sharedPref.getString("personDisplayName", null);
        String personContactNumber = sharedPref.getString("personContactNumber", null);
        headName.setText(personDisplayName);
        headEmail.setText(personEmail);

        //if contact no. isn't there in sharedpref, get it from DB
        if (personContactNumber == null || personContactNumber.matches("")) {
            new bgroundtask().execute();
        }


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
                        Log.i("Sign out button clicked", " success");
                        Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();

                        Log.e("pref ", "cleared");
                        Intent intent = new Intent(getApplicationContext(), GoogleSignIn.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
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

    class bgroundtask extends AsyncTask<Void, Void, String> {

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

            urlFinal = MainActivity.requestURL + "Fauxusers/" + utfUserId + "?access_token=" + userToken;
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
                headNumber.setVisibility(View.VISIBLE);
                headNumber.setText("+91" + phoneNumber);

                SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("personContactNumber", phoneNumber);
                editor.apply();
            }

        }

    }
}