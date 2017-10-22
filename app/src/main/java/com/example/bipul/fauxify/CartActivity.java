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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class CartActivity extends AppCompatActivity implements View.OnKeyListener {

    private static final String TAG = "CartActivity";

    private ArrayList<CurrentOrder> mItemSummaryList = new ArrayList<>();
    private ArrayList<Address> mAddressList = new ArrayList<>();
    private CartAddressAdapter mCartAddressAdapter;
    Button mSelectAnotherAddressButton, mAddNewAddressButton, mConfirmOrderButton;
    Toolbar mToolbar;
    EditText mUserMessageEditText;
    public static String orderid, restaurantNameInCart;
    static String finaladdress = null;
    static int totaldishcount = 0, totalitempricecount = 0, deliveryfee = 0, grandtotalamount = 0;
    static CardView cardViewSelectedAdd, cardViewToSelectAdd;
    static TextView RestaurantNameInCart, TotalItemPriceInCart, GrandTotalAmount, DeliveryFee, selectedAddress, noSavedAddCart;

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode== KeyEvent.KEYCODE_ENTER ){

            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return false;
    }

    @Override
    //this is to nullify the value of address selected by user (if selected) so if he returns, it isn't stored
    public void onBackPressed() {

        finaladdress = null;
        super.onBackPressed();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        RecyclerView recyclerViewCart;
        recyclerViewCart = (RecyclerView) findViewById(R.id.cart_recyclerview);
        CartItemAdapter cartAdapter;
        cartAdapter = new CartItemAdapter(mItemSummaryList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext());
        assert recyclerViewCart != null;
        recyclerViewCart.setLayoutManager(mLayoutManager);
        recyclerViewCart.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCart.setAdapter(cartAdapter);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_cartactivity);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.review_order);

        RecyclerView addressRecyclerView;
        addressRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_addresscart);

        mCartAddressAdapter = new CartAddressAdapter(mAddressList);
        RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(getBaseContext());
        assert addressRecyclerView != null;
        addressRecyclerView.setLayoutManager(nLayoutManager);
        addressRecyclerView.setItemAnimator(new DefaultItemAnimator());
        addressRecyclerView.setAdapter(mCartAddressAdapter);

        cardViewSelectedAdd = (CardView) findViewById(R.id.cardview_cart_selectedaddress);
        cardViewToSelectAdd = (CardView) findViewById(R.id.cardview_cart_toselectaddress);
        mSelectAnotherAddressButton = (Button) findViewById(R.id.button_selectanotheraddress_cart);
        selectedAddress = (TextView) findViewById(R.id.cart_selectedaddress);
        mAddNewAddressButton = (Button) findViewById(R.id.button_addnewaddress_cart);
        mConfirmOrderButton = (Button) findViewById(R.id.button_confirmorder);
        noSavedAddCart = (TextView) findViewById(R.id.no_saved_add_in_cart);

        mUserMessageEditText = (EditText) findViewById(R.id.edittext_user_message);
        mUserMessageEditText.setOnKeyListener(this);

        DeliveryFee = (TextView) findViewById(R.id.deliveryfee_incart);
        GrandTotalAmount = (TextView) findViewById(R.id.grandtotalprice_incart);

        mAddNewAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, AddAddressInfoInCartActivity.class);
                startActivity(intent);
            }
        });

        mSelectAnotherAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finaladdress = null;
                cardViewToSelectAdd.setVisibility(View.VISIBLE);
                cardViewSelectedAdd.setVisibility(View.GONE);
            }
        });

        RestaurantNameInCart = (TextView) findViewById(R.id.restname_incart);
        TotalItemPriceInCart = (TextView) findViewById(R.id.totalitemprice_incart);

        prepareCartData();
        prepareDetails();

        // after preparation of details, now we will check for delivery fee implementation
        checkDeliveryFee();
        prepareAddressDetails();

        mConfirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((finaladdress != null) && totaldishcount != 0) {

                    if (totalitempricecount >= Integer.parseInt(RestaurantDetailsActivity.restMinimumOrder)) {
                        confirmOrder();
                        DishesAdapter.currentOrders.clear();
                        Intent intent = new Intent(getApplicationContext(), OrderConfirmationActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Minimum order value is Rs "
                                + RestaurantDetailsActivity.restMinimumOrder, Toast.LENGTH_LONG).show();
                    }

                } else if ((finaladdress == null) && (totaldishcount != 0)) {
                    Toast.makeText(getApplicationContext(), R.string.select_delivery_address, Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), R.string.add_dishes_cart, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void checkDeliveryFee() {

        //Log.d(TAG, "totalitempricecount: " + String.valueOf(totalitempricecount));
        //Log.d(TAG, "resFreedelAmount: " + String.valueOf(Integer.valueOf(RestaurantDetailsActivity.resFreeDelAmount)));

        if (totalitempricecount < Integer.valueOf(RestaurantDetailsActivity.resFreeDelAmount)) {
            deliveryfee = Integer.valueOf(RestaurantDetailsActivity.resDeliveryFee);

        } else {
            deliveryfee = 0;
        }

        DeliveryFee.setText(String.valueOf(deliveryfee));
        grandtotalamount = totalitempricecount + deliveryfee;
        GrandTotalAmount.setText(String.valueOf(grandtotalamount));
    }

    private void confirmOrder() {
        new BGTaskConfirmOrder().execute();
    }

    public String convertTime(int x) {

        if (x < 10) {
            return "0" + String.valueOf(x);
        } else {
            return String.valueOf(x);
        }
    }

    private class BGTaskConfirmOrder extends AsyncTask<Void, Void, String> {

        String urlFinal;

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

            urlFinal = getString(R.string.request_url) + "fauxusers/" + utfUserId + "/fauxorders" + "?access_token=" + userToken;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {

                URL url = new URL(urlFinal);
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
                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss a", new java.util.Date()).toString());
                //Log.d(TAG, "date: " + date);
                String hours, minutes, seconds, days, months, yearshort, timestamp;
                Random random = new Random();

                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int min = Calendar.getInstance().get(Calendar.MINUTE);
                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int second = Calendar.getInstance().get(Calendar.SECOND);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int ampm = Calendar.getInstance().get(Calendar.AM_PM);
                int randomno = random.nextInt(89) + 10;

                hours = convertTime(hour);
                minutes = convertTime(min);
                days = convertTime(day);
                seconds = convertTime(second);
                months = convertTime(month);
                String years = convertTime(year);
                String randomnos = String.valueOf(randomno);

                yearshort = years.substring(2, 4);

                timestamp = yearshort + months + days + hours + minutes + seconds + randomnos;

//                Log.i("hour", hours);
//                Log.i("min", minutes);
//                Log.i("day", days);
//                Log.i("sec", seconds);
//                Log.i("month", months);
//                Log.i("year", yearshort);
//                Log.i("random", randomnos);
//                Log.i("timestamp", timestamp);

                JSONArray jarrayDishesInfo = new JSONArray();

                for (int j = 0; j <= CartItemAdapter.itemSummaryList.size() - 1; j++) {

                    JSONObject jo = new JSONObject();
                    jo.put("dishname", CartItemAdapter.itemSummaryList.get(j).currentdishName);
                    jo.put("dishprice", CartItemAdapter.itemSummaryList.get(j).currentdishPrice);
                    jo.put("dishquantity", CartItemAdapter.itemSummaryList.get(j).currentdishQuantity);
                    jo.put("dishamount", (CartItemAdapter.itemSummaryList.get(j)
                            .currentdishQuantity * Integer.parseInt(CartItemAdapter.itemSummaryList.get(j).currentdishPrice)));
                    jarrayDishesInfo.put(j, jo);
                }

                JSONObject deliveryinfo = new JSONObject();
                deliveryinfo.put("orderdelivery", 1);
                deliveryinfo.put("orderconfirmed", 0);
                deliveryinfo.put("orderdelivered", 0);
                deliveryinfo.put("deliverytime", "Not Available");

                JSONObject orderinfo = new JSONObject();
                orderinfo.put("totalitems", totaldishcount);
                orderinfo.put("totalitemprice", totalitempricecount);
                orderinfo.put("dishesinfo", jarrayDishesInfo);
                orderinfo.put("deliveryfee", deliveryfee);

                orderid = timestamp;

                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("orderid", orderid);
                jsonObject.accumulate("fauxuserId", email);
                jsonObject.accumulate("restaurantId", RestaurantDetailsActivity.resId);

                jsonObject.accumulate("restName", RestaurantDetailsActivity.resName);

                jsonObject.accumulate("customernumber", "9999999999");
                jsonObject.accumulate("customeraddress", finaladdress);
                jsonObject.accumulate("customeremail", email);
                jsonObject.accumulate("customername", customername);

                jsonObject.accumulate("ordertiming", date);
                jsonObject.accumulate("ordertotal", grandtotalamount);
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

                //Log.d(TAG, "test-data: " + json);
            } catch (IOException | JSONException e) {
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

        totaldishcount = 0;
        totalitempricecount = 0;
        for (int j = 0; j <= CartItemAdapter.itemSummaryList.size() - 1; j++) {

            totaldishcount = totaldishcount + CartItemAdapter.itemSummaryList.get(j).currentdishQuantity;
            totalitempricecount = totalitempricecount + (CartItemAdapter.itemSummaryList.get(j)
                    .currentdishQuantity * Integer.parseInt(CartItemAdapter.itemSummaryList.get(j).currentdishPrice));
        }

        restaurantNameInCart = RestaurantDetailsActivity.resName;
        RestaurantNameInCart.setText(restaurantNameInCart);
        TotalItemPriceInCart.setText(String.valueOf(totalitempricecount));
    }

    private void prepareCartData() {

        for (int i = 0; i <= DishesAdapter.currentOrders.size() - 1; i++) {
            CurrentOrder currentOrder = new CurrentOrder(
                    DishesAdapter.currentOrders.get(i).getCurrentdishName(),
                    DishesAdapter.currentOrders.get(i).getCurrentdishPrice(),
                    DishesAdapter.currentOrders.get(i).getCurrentdishQuantity());
            mItemSummaryList.add(currentOrder);
            //Log.d(TAG, "current dish name" + String.valueOf(DishesAdapter.currentOrders.get(i).getCurrentdishName()));
            //Log.d(TAG, "current dish price" + String.valueOf(DishesAdapter.currentOrders.get(i).getCurrentdishPrice()));
            //Log.d(TAG, "current dish quantity" + String.valueOf(DishesAdapter.currentOrders.get(i).getCurrentdishQuantity()));
        }
    }

    private void prepareAddressDetails() {
        new BGTaskPrepareAddDetails().execute();
    }

    private class BGTaskPrepareAddDetails extends AsyncTask<Void, Void, String> {

        String urlFinal;
        String JSON_STRING;
        String userId, userToken;
        JSONArray jsonArray;
        String jsonString;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref;
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
                    stringBuilder.append(JSON_STRING).append("\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpConnection.disconnect();
                String resultjson = stringBuilder.toString().trim();
                //Log.d(TAG, "result: " + resultjson);

                JSONObject jobject = new JSONObject(resultjson);
                jsonArray = jobject.getJSONArray("Address");

                if (jsonArray != null) {

                    jsonString = jsonArray.toString();
                    //Log.d(TAG, "Jsonarray length: " + String.valueOf(jsonArray.length()));
                    //Log.d(TAG, "jsonarray: " + jsonString);

                } else {
                    Log.d(TAG, "JsonArray length is zero");
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if(jsonArray==null || jsonArray.length()==0){
                noSavedAddCart.setVisibility(View.VISIBLE);
            }

            else if (jsonArray != null) {
                //Log.d(TAG, "Jsonarray length: " + String.valueOf(jsonArray.length()));
                for (int j = 0; j <= (jsonArray.length() - 1); j++) {
                    try {
                        Address address = new Address(jsonArray.getString(j));
                        mAddressList.add(address);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mCartAddressAdapter.notifyDataSetChanged();

            } else Log.d(TAG, "JsonArray length is zero");
        }
    }
}

