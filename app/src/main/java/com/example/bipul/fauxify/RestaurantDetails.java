package com.example.bipul.fauxify;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class RestaurantDetails extends AppCompatActivity {

    ViewPager viewPager;
    Integer resRating, noOfCategories;
    public static String resId, resName;
    TabLayout tabLayout;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    public static boolean isVeg;
    Switch switchVeg;
    RestaurantMenuAdapter restaurantMenuAdapter;
    private static final String TAG = "onclickeddd";
    FloatingActionButton fabCheckout;
    static String restMinimumOrder;
    TextView restDelTime, restMinOrder, resType;
    //Button checkOutButton;


    @Override //to modify the back pressing from this activity so that cart can be emptied before going to restaurants page
    public void onBackPressed() {

        if(DishesAdapter.currentOrders.size()!=0) {
            AlertDialog.Builder alertbuilder = new AlertDialog.Builder(this);
            alertbuilder.setMessage("You cart has items from this restaurants. It needs to be emptied to browse other restaurants. Agree?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Cart Emptied!", Toast.LENGTH_SHORT).show();
                            DishesAdapter.currentOrders.clear();
                            RestaurantDetails.super.onBackPressed();
                        }
                    })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = alertbuilder.create();
            alert.setTitle("Oops!");
            alert.show();

        }

        else
        {
            super.onBackPressed();
        }

    }

    @Override   //this is to duplicate the effect of back button on UP button of actionbar
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_restaurant_details);

        viewPager = (ViewPager) findViewById(R.id.menu_viewpager);
        assert viewPager != null;
        viewPager.setOffscreenPageLimit(10);
        tabLayout = (TabLayout) findViewById(R.id.menu_tabs);

        //checkOutButton = (Button)findViewById(R.id.checkout_button);
        fabCheckout = (FloatingActionButton) findViewById(R.id.fabCheckout);
        switchVeg =(Switch) findViewById(R.id.switchVeg);
        resId = getIntent().getStringExtra("resId");
        restDelTime = (TextView) findViewById(R.id.restDelTimeCollapse);
        restMinOrder = (TextView) findViewById(R.id.restMinOrderCollapse);
        resType = (TextView) findViewById(R.id.restTypeCollapse);
        resRating = Integer.valueOf(getIntent().getStringExtra("restaurantRating"));
        Log.e("resId", resId);
        resName = getIntent().getStringExtra("restaurantName");
        restDelTime.setText(getIntent().getStringExtra("restaurantDeLTime"));
        restMinimumOrder = getIntent().getStringExtra("restaurantMinOrder");
        String minOrder = "\u20B9 "+ restMinimumOrder;
        restMinOrder.setText(minOrder);
        resType.setText(getIntent().getStringExtra("restaurantType"));

        Log.e("log value check", String.valueOf(switchVeg.isChecked()));

        switchVeg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    isVeg = true;
                    prepareMenu();
                }
                else {
                    isVeg = false;
                    prepareMenu();
                }
            }
        });

        prepareMenu();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(resName);
        collapsingToolbar.setExpandedTitleMarginBottom(130);
        collapsingToolbar.setExpandedTitleMarginStart(50);
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        collapsingToolbar.setContentScrimColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        collapsingToolbar.setStatusBarScrimColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
//        toolbar= (Toolbar) findViewById(R.id.toolbar_restaurantdetails);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(resName);

//        checkOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (DishesAdapter.currentOrders.size()!=0)
//                {Log.e("hey!", "I heard you");
//                Intent intent = new Intent(RestaurantDetails.this, CartActivity.class);
//                startActivity(intent);}
//
//                else {
//                    Toast.makeText(getApplicationContext(), "Select Dishes to order", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        fabCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DishesAdapter.currentOrders.size()!=0)
                {
                    Intent intent = new Intent(RestaurantDetails.this, CartActivity.class);
                    startActivity(intent);}

                else {
                    Toast.makeText(getApplicationContext(), "Select Dishes to order", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void prepareMenu() {
        new bgroundtask().execute();
    }

    class bgroundtask extends AsyncTask<Void, Void, String> {

        String json_url;
        String JSON_STRING;
        JSONArray jsonArray;
        JSONObject jobject;
        ArrayList<String> categories;

        @Override
        protected void onPreExecute() {

            json_url = MainActivity.requestURL+"Restaurants/" + resId;
            Log.e("json_url", json_url);
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                URL urll = new URL(json_url);
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


                 categories = new ArrayList<String>();

                jobject = new JSONObject(resultjson);
                JSONObject job = jobject.getJSONObject("Menu");

                Log.e("sdkjc", String.valueOf(job));

                Iterator x = job.keys();
                jsonArray = new JSONArray();
                noOfCategories = 0;

                while (x.hasNext()) {

                    String key = (String) x.next();
                    categories.add(key);
                    jsonArray.put(job.get(key));
                    Log.e("sdkjb", key);
                    noOfCategories++;
                }

                Log.e("result", String.valueOf(jsonArray));


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            restaurantMenuAdapter = new RestaurantMenuAdapter(getSupportFragmentManager());
            for (int i = 0; i <= noOfCategories - 1; i++) {


                try {
                    restaurantMenuAdapter.addFragments(new RestaurantMenuFragment(), categories.get(i),jsonArray.getJSONObject(i) );
                    Log.e("dhdv", String.valueOf(jsonArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            viewPager.setAdapter(restaurantMenuAdapter);
            tabLayout.setupWithViewPager(viewPager);


        }
    }


}
