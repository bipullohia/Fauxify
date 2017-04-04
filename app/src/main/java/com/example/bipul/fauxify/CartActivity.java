package com.example.bipul.fauxify;

import android.app.ProgressDialog;
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

    private ArrayList<CurrentOrder> itemSummaryList = new ArrayList<>();
    private ArrayList<Address> addressList = new ArrayList<>();
    private CartAddressAdapter cartAddressAdapter;
    static CardView cardViewSelectedAdd, cardViewToSelectAdd;
    Button selectAnotherAdd, addNewAddress, confirmorder;
    Toolbar toolbar;
    EditText userMessage;
    public static String orderid, restaurantNameInCart;
    static String finaladdress = null;
    static int totaldishcount = 0, totalitempricecount = 0, deliveryfee = 0, grandtotalamount = 0;
    static TextView RestaurantNameInCart, TotalItemsInCart, TotalItemPriceInCart, GrandTotalAmount, DeliveryFee, selectedAddress;


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

//    @Override   //this is to duplicate the effect of back button on UP button of actionbar
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Review Order");

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

        userMessage = (EditText) findViewById(R.id.userMessage);
        userMessage.setOnKeyListener(this);

        DeliveryFee = (TextView) findViewById(R.id.deliveryfee_incart);
        GrandTotalAmount = (TextView) findViewById(R.id.grandtotalprice_incart);

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
        TotalItemPriceInCart = (TextView) findViewById(R.id.totalitemprice_incart);

        prepareCartData();
        prepareDetails();

        // after preparation of details, now we will check for deivery fee implementation

        checkDeliveryFee();


        prepareAddressDetails();

        confirmorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((finaladdress != null) && totaldishcount != 0) {

                    if (totalitempricecount >= Integer.parseInt(RestaurantDetails.restMinimumOrder)) {


                        confirmOrder();
                        DishesAdapter.currentOrders.clear();
                        Intent intent = new Intent(getApplicationContext(), OrderConfirmationActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Minimum order value is Rs "
                                + RestaurantDetails.restMinimumOrder, Toast.LENGTH_LONG).show();
                    }
                } else if ((finaladdress == null) && (totaldishcount != 0)) {
                    Toast.makeText(getApplicationContext(), "Select a delivery address", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Add dishes to cart", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public static void checkDeliveryFee() {


        Log.i("totalitempricecount", String.valueOf(totalitempricecount));
        Log.i("resFreedelAmount", String.valueOf(Integer.valueOf(RestaurantDetails.resFreeDelAmount)));

        if (totalitempricecount < Integer.valueOf(RestaurantDetails.resFreeDelAmount)) {

            deliveryfee = Integer.valueOf(RestaurantDetails.resDeliveryFee);

        } else {
            deliveryfee = 0;
        }

        DeliveryFee.setText(String.valueOf(deliveryfee));
        grandtotalamount = totalitempricecount + deliveryfee;
        GrandTotalAmount.setText(String.valueOf(grandtotalamount));

    }

    private void confirmOrder() {
        Log.e("confirm order", "order confirmed");
        new BTaskConfirmOrder().execute();
    }

    public String convertTime(int x) {

        if (x < 10) {
            return "0" + String.valueOf(x);
        } else {
            return String.valueOf(x);
        }

    }



    class BTaskConfirmOrder extends AsyncTask<Void, Void, String> {
        String urlFinal;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            //pd = ProgressDialog.show(getApplicationContext(), "", "Sending Order info", false);

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

            urlFinal = MainActivity.requestURL + "fauxusers/" + utfUserId + "/fauxorders" + "?access_token=" + userToken;
            Log.e("json_url", urlFinal);
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
                String date = (DateFormat.format("dd-MM-yyyy hh:mm:ss", new java.util.Date()).toString());

                String hours, minutes, seconds, days, months, yearshort, timestamp;
                Random random = new Random();

                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                int min = Calendar.getInstance().get(Calendar.MINUTE);
                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                int second = Calendar.getInstance().get(Calendar.SECOND);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int year = Calendar.getInstance().get(Calendar.YEAR);
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
//
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

//                Random r = new Random();
//                orderid = RestaurantDetails.resId + (r.nextInt(8999) + 1000);

                orderid = timestamp;

                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("orderid", orderid);
                jsonObject.accumulate("fauxuserId", email);
                jsonObject.accumulate("restaurantId", RestaurantDetails.resId);

                jsonObject.accumulate("restName", RestaurantDetails.resName);

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

                Log.e("test", json);
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


        restaurantNameInCart = RestaurantDetails.resName;
        RestaurantNameInCart.setText(restaurantNameInCart);
        TotalItemsInCart.setText(String.valueOf(totaldishcount));
        TotalItemPriceInCart.setText(String.valueOf(totalitempricecount));

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
        new btaskPrepareAddDetails().execute();
    }

    class btaskPrepareAddDetails extends AsyncTask<Void, Void, String> {

        String urlFinal;
        String JSON_STRING;
        String userId, userToken;
        JSONArray jsonArray;
        String jsonString;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            //pd = ProgressDialog.show(getBaseContext(), "", "Loading Address info", false);

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

            urlFinal = MainActivity.requestURL + "Fauxusers/" + utfUserId + "?access_token=" + userToken;

            Log.e("checkurl", urlFinal);


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

            //pd.dismiss();
        }


    }


}

