package com.example.bipul.fauxify;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OrderConfirmed extends AppCompatActivity {

    TextView currentTime, deliveryTime, orderId;
    Handler handler = new Handler();
    int joOrderConfirmation = 0;
    int i = 0;
    Toolbar toolbar;

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
        alertbuilder.setMessage("You can always check your order in 'My Order'")
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(OrderConfirmed.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

        AlertDialog alert = alertbuilder.create();
        alert.setTitle("Redirecting to Restaurants");
        alert.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmed);

        currentTime = (TextView) findViewById(R.id.current_time);
        deliveryTime = (TextView) findViewById(R.id.deliverytime);
        orderId  = (TextView) findViewById(R.id.orderId_orderconfirmed);
        orderId.setText(CartActivity.orderid);

        toolbar = (Toolbar) findViewById(R.id.toolbar_confirmorder);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order Confirmation");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        doTheAutoRefresh();

    }

    private void doTheAutoRefresh() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if(joOrderConfirmation==0) {
                    checkConfirmationStatus();
                    Log.e("checking", "again");
                    handler.postDelayed(this, 5000);

                }
                else if(joOrderConfirmation==1){
                    Log.e("Runnable Stopped", "No longer Running");
                    handler.removeCallbacks(this);
                }

                else {
                    Log.e("invalid", "value of orderconfirmation");
                }
//                if(i==5){
//                    handler.removeCallbacks(this);
//                    Toast.makeText(getApplicationContext(), "runnable stopped", Toast.LENGTH_SHORT).show();
//                }
//
//                else {
//                    Toast.makeText(getApplicationContext(), "It's 5 second already", Toast.LENGTH_SHORT).show();
//                    i++;
//                    currentTime.setText("no is : "+ i);
//                    handler.postDelayed(this, 5000);
//                }
            }
        };

        handler.postDelayed(runnable, 5000);
    }

    private void checkConfirmationStatus() {
        new bgroundtask().execute();
    }

    class bgroundtask extends AsyncTask<Void, Void, String> {

        String json_url;
        String json_checkurl;
        String JSON_STRING;
        String orderId;
        String joDeliveryTime;

        @Override
        protected void onPreExecute() {

            orderId = CartActivity.orderid;
            json_url = MainActivity.requestURL+"Fauxorders/";
            json_checkurl = json_url + orderId;
            Log.e("url", json_checkurl);
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                URL urll = new URL(json_checkurl);
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

                joOrderConfirmation = jodelivery.getInt("orderconfirmed");

                if (joOrderConfirmation == 1) {
                    joDeliveryTime = jodelivery.getString("deliverytime");
                    Log.e("result deliverytime", joDeliveryTime);
                }

                Log.e("result orderconfirm", String.valueOf(joOrderConfirmation));

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if(joOrderConfirmation==1) {
                currentTime.setText("Your Order is being prepared");
                deliveryTime.setText("Delivery in " +joDeliveryTime + " Minutes");
            }
        }
    }
}
