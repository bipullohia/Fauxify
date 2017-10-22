package com.example.bipul.fauxify;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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


public class OrderConfirmationActivity extends AppCompatActivity {

    TextView mCurrentStatusTextView, mDeliveryTimeTextView, mOrderIdTextView, mRestNameTextView,
             mTotalOrderAmountTextView, mOrderRefreshTextView;
    Handler mHandler = new Handler();
    int mJOOrderConfirmationInt = 0;
    Runnable mRunnable;
    int i = 0;
    Toolbar mToolbar;

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
    public void onBackPressed() {
        AlertDialog.Builder alertbuilder = new AlertDialog.Builder(this);
        alertbuilder.setMessage(R.string.you_can_check_orders_at_myorders)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mHandler.removeCallbacks(mRunnable);
                        Intent intent = new Intent(OrderConfirmationActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        AlertDialog alert = alertbuilder.create();
        alert.setTitle(getString(R.string.redirecting_to_restaurants));
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmed);

        mCurrentStatusTextView = (TextView) findViewById(R.id.awaiting_confirmation);
        mDeliveryTimeTextView = (TextView) findViewById(R.id.delivery_time);
        mOrderIdTextView = (TextView) findViewById(R.id.orderid_orderconfirmed);
        if (mOrderIdTextView != null) {
            mOrderIdTextView.setText(CartActivity.orderid);
        }

        mRestNameTextView = (TextView) findViewById(R.id.resname_order_confirm);
        mTotalOrderAmountTextView = (TextView) findViewById(R.id.totalprice_order_confirm);
        mOrderRefreshTextView = (TextView) findViewById(R.id.orderrefresh_message_fivesec);

        mRestNameTextView.setText(CartActivity.restaurantNameInCart);
        mTotalOrderAmountTextView.setText(String.valueOf(CartActivity.grandtotalamount));

        mToolbar = (Toolbar) findViewById(R.id.toolbar_confirmorder);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.order_confirmation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        doTheAutoRefresh();
    }

    private void doTheAutoRefresh() {

        mRunnable = new Runnable() {
            @Override
            public void run() {

                if (mJOOrderConfirmationInt == 0) {
                    checkConfirmationStatus();
                    Log.e("checking", "again");
                    mHandler.postDelayed(this, 5000);

                } else if (mJOOrderConfirmationInt == 1) {
                    Log.e("Runnable Stopped", "No longer Running");
                    mHandler.removeCallbacks(this);

                } else {
                    Log.e("invalid", "value of orderconfirmation");
                }
            }
        };

        mHandler.postDelayed(mRunnable, 5000);
    }

    private void checkConfirmationStatus() {
        new BGTaskCheckConfirmationStatus().execute();
    }

    private class BGTaskCheckConfirmationStatus extends AsyncTask<Void, Void, String> {

        String json_url;
        String JSON_STRING;
        String orderId;
        String joDeliveryTime;

        @Override
        protected void onPreExecute() {

            orderId = CartActivity.orderid;

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

            json_url = getString(R.string.request_url) + "fauxusers/" + utfUserId + "/fauxorders/" + orderId + "?access_token=" + userToken;
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
                Log.e("result", resultjson);

                JSONObject jobject = new JSONObject(resultjson);
                JSONObject jodelivery = jobject.getJSONObject("delivery");

                mJOOrderConfirmationInt = jodelivery.getInt("orderconfirmed");

                if (mJOOrderConfirmationInt == 1) {
                    joDeliveryTime = jodelivery.getString("deliverytime");
                    Log.e("result deliverytime", joDeliveryTime);
                }

                Log.e("result orderconfirm", String.valueOf(mJOOrderConfirmationInt));

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (mJOOrderConfirmationInt == 1) {
                mCurrentStatusTextView.setText(R.string.your_order_being_prepared);
                mCurrentStatusTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                mDeliveryTimeTextView.setVisibility(View.VISIBLE);
                mOrderRefreshTextView.setVisibility(View.GONE);
                mDeliveryTimeTextView.setText("Your order will be delivered in approximately " + joDeliveryTime + " Minutes");
                mHandler.removeCallbacks(mRunnable);
            }
        }
    }
}
