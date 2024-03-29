package com.example.bipul.fauxify;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
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

public class RestaurantDetailsActivity extends AppCompatActivity {

    ViewPager mViewPager;
    Integer mResRatingInt, mNoOfCategoriesInt;
    public static String resId, resName, restMinimumOrder, resDeliveryFee, resFreeDelAmount;
    TabLayout mTabLayout;
    Toolbar mToolbar;
    CoordinatorLayout mRestaurantDetailCoordinatorLayout;
    CollapsingToolbarLayout mCollapsingToolbar;
    public static boolean isVeg;
    Switch mVegSwitch;
    static String restStatus;
    RestaurantMenuAdapter mRestaurantMenuAdapter;
    FloatingActionButton mCheckoutFAButton;
    TextView mRestDelTimeTextView, mRestMinOrderTextView, mResTypeTextView;

    @Override
    //to modify the back pressing from this activity so that cart can be emptied before going to restaurants page
    public void onBackPressed() {

        isVeg = false;
        if (DishesAdapter.currentOrders.size() != 0) {
            AlertDialog.Builder alertbuilder = new AlertDialog.Builder(this);
            alertbuilder.setMessage(R.string.cart_already_has_items_empty_ques)
                    .setCancelable(true)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), R.string.cart_emptied, Toast.LENGTH_SHORT).show();
                            DishesAdapter.currentOrders.clear();
                            RestaurantDetailsActivity.super.onBackPressed();
                        }
                    })

                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = alertbuilder.create();
            alert.setTitle(getString(R.string.oops));
            alert.show();

        } else {
            super.onBackPressed();
        }
    }

    @Override   //this is to duplicate the effect of back button on HOME (UP) button of actionbar
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

        mViewPager = (ViewPager) findViewById(R.id.menu_viewpager);
        assert mViewPager != null;
        mViewPager.setOffscreenPageLimit(10);

        mTabLayout = (TabLayout) findViewById(R.id.menu_tabs);
        mCheckoutFAButton = (FloatingActionButton) findViewById(R.id.fabCheckout);
        mVegSwitch = (Switch) findViewById(R.id.switchVeg);

        mRestaurantDetailCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.restaurantDetailsCoordinatorLayout);

        resId = getIntent().getStringExtra("resId");
        resName = getIntent().getStringExtra("restaurantName");
        restMinimumOrder = getIntent().getStringExtra("restaurantMinOrder");
        resDeliveryFee = getIntent().getStringExtra("Deliveryfee");
        resFreeDelAmount = getIntent().getStringExtra("freeDeliveryAmount");
        restStatus = getIntent().getStringExtra("restStatus");

        mRestDelTimeTextView = (TextView) findViewById(R.id.restDelTimeCollapse);
        mRestMinOrderTextView = (TextView) findViewById(R.id.restMinOrderCollapse);
        mResTypeTextView = (TextView) findViewById(R.id.restTypeCollapse);
        mResRatingInt = Integer.valueOf(getIntent().getStringExtra("restaurantRating"));

        mRestDelTimeTextView.setText(getIntent().getStringExtra("restaurantDeLTime"));
        String minOrder = "\u20B9 " + restMinimumOrder;
        mRestMinOrderTextView.setText(minOrder);
        mResTypeTextView.setText(getIntent().getStringExtra("restaurantType"));

        if (!restStatus.equals("open")) {
            mCheckoutFAButton.setVisibility(View.GONE);
        }

        mVegSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    isVeg = true;
                    prepareMenu();

                } else {
                    isVeg = false;
                    prepareMenu();
                }
            }
        });

        prepareMenu();

        mToolbar = (Toolbar) findViewById(R.id.toolbar_mainactivity);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbar.setTitle(resName);
        mCollapsingToolbar.setExpandedTitleMarginBottom(130);
        mCollapsingToolbar.setExpandedTitleMarginStart(50);
        mCollapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        mCollapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        mCollapsingToolbar.setContentScrimColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        mCollapsingToolbar.setStatusBarScrimColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        mCheckoutFAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DishesAdapter.currentOrders.size() != 0) {
                    Intent intent = new Intent(RestaurantDetailsActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.select_dishes_to_order, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void prepareMenu() {
        new BGTaskPrepareMenu().execute();
    }

    private class BGTaskPrepareMenu extends AsyncTask<Void, Void, String> {

        String json_url;
        String JSON_STRING;
        JSONArray jsonArray;
        JSONObject jobject;
        ArrayList<String> categories;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            json_url = getString(R.string.request_url) + "restaurants/" + resId;
            pd = ProgressDialog.show(RestaurantDetailsActivity.this, "", getString(R.string.loading_restaurant_menu), false);
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

                Iterator x = job.keys();
                jsonArray = new JSONArray();
                mNoOfCategoriesInt = 0;

                while (x.hasNext()) {

                    String key = (String) x.next();
                    categories.add(key);
                    jsonArray.put(job.get(key));
                    mNoOfCategoriesInt++;
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            mRestaurantMenuAdapter = new RestaurantMenuAdapter(getSupportFragmentManager());
            for (int i = 0; i <= mNoOfCategoriesInt - 1; i++) {

                try {
                    mRestaurantMenuAdapter.addFragments(new RestaurantMenuFragment(), categories.get(i),
                                                        jsonArray.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mViewPager.setAdapter(mRestaurantMenuAdapter);
            mTabLayout.setupWithViewPager(mViewPager);

            pd.dismiss();
        }
    }
}
