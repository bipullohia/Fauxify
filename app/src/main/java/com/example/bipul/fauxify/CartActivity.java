package com.example.bipul.fauxify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class CartActivity extends AppCompatActivity {

    private ArrayList<CurrentOrder> itemSummaryList = new ArrayList<>();
    private ArrayList<Address> addressList = new ArrayList<>();
    private CartAddressAdapter cartAddressAdapter;
    static CardView cardViewSelectedAdd, cardViewToSelectAdd;
    Button selectAnotherAdd, addNewAddress, confirmorder;
    Toolbar toolbar;
    public static String orderid, restaurantNameInCart;
    static String finaladdress = null;
    static int totaldishcount = 0, totalpricecount = 0;
    private static TextView RestaurantNameInCart, TotalItemsInCart, TotalPriceInCart, selectedAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        RecyclerView recyclerViewCart;
        recyclerViewCart = (RecyclerView) findViewById(R.id.cart_recyclerview);
        CartItemAdapter cartAdapter;
        cartAdapter = new CartItemAdapter(itemSummaryList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext());
        assert recyclerViewCart != null;
        recyclerViewCart.setLayoutManager(mLayoutManager);
        recyclerViewCart.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCart.setAdapter(cartAdapter);

        toolbar = (Toolbar) findViewById(R.id.toolbar_cartactivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Checkout");

        RecyclerView addressRecyclerView;
        addressRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_addresscart);

        cartAddressAdapter = new CartAddressAdapter(addressList);
        RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(getBaseContext());
        assert addressRecyclerView != null;
        addressRecyclerView.setLayoutManager(nLayoutManager);
        addressRecyclerView.setItemAnimator(new DefaultItemAnimator());
        addressRecyclerView.setAdapter(cartAddressAdapter);

        cardViewSelectedAdd = (CardView) findViewById(R.id.cart_selectedaddress_cardview);
        cardViewToSelectAdd = (CardView) findViewById(R.id.cart_toselectaddress_cardview);
        selectAnotherAdd = (Button) findViewById(R.id.button_selectanotheraddress_cart);
        selectedAddress = (TextView) findViewById(R.id.cart_selectedaddress);
        addNewAddress = (Button) findViewById(R.id.button_addnewaddress_cart);
        confirmorder = (Button) findViewById(R.id.button_confirmorder);

        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, AddAddressInCart.class);
                startActivity(intent);
            }
        });

        selectAnotherAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finaladdress = null;
                cardViewToSelectAdd.setVisibility(View.VISIBLE);
                cardViewSelectedAdd.setVisibility(View.GONE);
            }
        });

        RestaurantNameInCart = (TextView) findViewById(R.id.restname_incart);
        TotalItemsInCart = (TextView) findViewById(R.id.totalitems_incart);
        TotalPriceInCart = (TextView) findViewById(R.id.totalprice_incart);

        prepareCartData();
        prepareDetails();
        prepareAddressDetails();

        confirmorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((finaladdress != null)&&(totaldishcount!=0)) {
                    confirmOrder();
                    Intent intent = new Intent(getApplicationContext(), OrderConfirmed.class);
                    startActivity(intent);
                } else if ((finaladdress == null)&&(totaldishcount!=0)){
                    Toast.makeText(getApplicationContext(), "Select a delivery address", Toast.LENGTH_LONG).show();
                }

                else {
                   Toast.makeText(getApplicationContext(), "Select Dishes to order", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void confirmOrder() {
        Log.e("confirm order", "order confirmed");
        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String json_url;

        @Override
        protected void onPreExecute() {

            json_url = MainActivity.requestURL+"Fauxorders";

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                SharedPreferences sharedPref;
                sharedPref = CartActivity.this.getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
                String email = sharedPref.getString("personEmail", null);
                String customername = sharedPref.getString("personDisplayName", null);

                //String mydate =java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());

                JSONArray jarrayDishesInfo = new JSONArray();

                for (int j = 0; j <= CartItemAdapter.itemSummaryList.size() - 1; j++) {

                    JSONObject jo = new JSONObject();
                    jo.put("dishname", CartItemAdapter.itemSummaryList.get(j).currentdishName);
                    jo.put("dishprice", CartItemAdapter.itemSummaryList.get(j).currentdishPrice);
                    jo.put("dishquantity", CartItemAdapter.itemSummaryList.get(j).currentdishQuantity);
                    jo.put("dishamount",(CartItemAdapter.itemSummaryList.get(j)
                            .currentdishQuantity * Integer.parseInt(CartItemAdapter.itemSummaryList.get(j).currentdishPrice)) );
                    jarrayDishesInfo.put(j,jo);
                }

                JSONObject deliveryinfo = new JSONObject();
                deliveryinfo.put("orderdelivery", 1);
                deliveryinfo.put("ordertiming", date);
                deliveryinfo.put("orderconfirmed", 0);
                deliveryinfo.put("orderdelivered", 0);
                deliveryinfo.put("deliverytime", "Not Available");

                JSONObject orderinfo = new JSONObject();
                orderinfo.put("totalitems", totaldishcount);
                orderinfo.put("totalitemprice", totalpricecount);
                orderinfo.put("dishesinfo", jarrayDishesInfo);

                Random r = new Random();
                orderid = RestaurantDetails.resId + (r.nextInt(8999) + 1000);

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Orderid", orderid);
                jsonObject.accumulate("Restid", RestaurantDetails.resId);
                jsonObject.accumulate("ordertotal", totalpricecount);
                jsonObject.accumulate("customeraddress", finaladdress);
                jsonObject.accumulate("customeremail", email);
                jsonObject.accumulate("customername", customername);
                jsonObject.accumulate("orderinfo", orderinfo);
                jsonObject.accumulate("delivery", deliveryinfo);

                String json = jsonObject.toString();
                OutputStreamWriter out = new OutputStreamWriter(httpURLConnection.getOutputStream());
                out.write(json);
                out.flush();
                out.close();

                StringBuilder sb = new StringBuilder();
                int HttpResult = httpURLConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    System.out.println("" + sb.toString());
                } else {
                    System.out.println(httpURLConnection.getResponseMessage());
                }

                Log.e("test", json);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }


    }


    public static void checkAddressCondition() {

        cardViewToSelectAdd.setVisibility(View.GONE);
        cardViewSelectedAdd.setVisibility(View.VISIBLE);

    }

    public static void setCurrentAddress(String address) {
        selectedAddress.setText(address);
        finaladdress = address;
    }

    public static void prepareDetails() {

        totaldishcount=0; totalpricecount=0;
        for (int j = 0; j <= CartItemAdapter.itemSummaryList.size() - 1; j++) {

            totaldishcount = totaldishcount + CartItemAdapter.itemSummaryList.get(j).currentdishQuantity;
            totalpricecount = totalpricecount + (CartItemAdapter.itemSummaryList.get(j)
                    .currentdishQuantity * Integer.parseInt(CartItemAdapter.itemSummaryList.get(j).currentdishPrice));
        }


        restaurantNameInCart= RestaurantDetails.resName;
        RestaurantNameInCart.setText(restaurantNameInCart);
        TotalItemsInCart.setText(String.valueOf(totaldishcount));
        TotalPriceInCart.setText(String.valueOf(totalpricecount));

    }

    private void prepareCartData() {

        for (int i = 0; i <= DishesAdapter.currentOrders.size() - 1; i++) {
            CurrentOrder currentOrder = new CurrentOrder(
                    DishesAdapter.currentOrders.get(i).getCurrentdishName(),
                    DishesAdapter.currentOrders.get(i).getCurrentdishPrice(),
                    DishesAdapter.currentOrders.get(i).getCurrentdishQuantity());
            itemSummaryList.add(currentOrder);
            Log.e("sehfl", String.valueOf(DishesAdapter.currentOrders.get(i).getCurrentdishName()));
            Log.e("sehfl", String.valueOf(DishesAdapter.currentOrders.get(i).getCurrentdishPrice()));
            Log.e("sehfl", String.valueOf(DishesAdapter.currentOrders.get(i).getCurrentdishQuantity()));

        }
    }

    private void prepareAddressDetails() {
        new bgroundtask().execute();
    }

    class bgroundtask extends AsyncTask<Void, Void, String> {

        String json_url;
        String json_checkurl, checkurl;
        String JSON_STRING;
        String email;
        JSONArray jsonArray;
        String jsonString;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
            sharedPref = CartActivity.this.getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            email = sharedPref.getString("personEmail", null);
            Log.e("email local data", "exists");
            checkurl = email.replace("@", "%40");
            json_url = MainActivity.requestURL+"Fauxusers/";
            json_checkurl = json_url + checkurl;
            Log.e("checkurl", json_checkurl);
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
                    stringBuilder.append(JSON_STRING).append("\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpConnection.disconnect();
                String resultjson = stringBuilder.toString().trim();
                Log.e("result", resultjson);

                JSONObject jobject = new JSONObject(resultjson);
                jsonArray = jobject.getJSONArray("Address");

                if (jsonArray != null) {

                    jsonString = jsonArray.toString();
                    Log.e("Jsonarray length", String.valueOf(jsonArray.length()));
                    Log.e("jsonarray", jsonString);
                } else {
                    Log.e("Jsonarray length", "is zero");
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (jsonArray != null) {
                Log.e("Jsonarray length", String.valueOf(jsonArray.length()));
                for (int j = 0; j <= (jsonArray.length() - 1); j++) {
                    try {
                        Address address = new Address(jsonArray.getString(j));
                        addressList.add(address);
                        Log.e("add", String.valueOf(jsonArray.getString(j)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                cartAddressAdapter.notifyDataSetChanged();


            } else Log.e("Jsonarray length", "is zero");
        }


    }


}

