package com.example.bipul.fauxify;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
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
import java.util.ArrayList;


public class MyOrdersFragment extends Fragment {

    private static final String TAG = "MyOrdersFragment";

    private ArrayList<MyOrders> mOrderList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private MyOrdersAdapter mMyOrderAdapter;
    CardView mNoOrder;

    public MyOrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_orders, container, false);

        mNoOrder = (CardView) rootview.findViewById(R.id.cardview_no_orders);

        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerview_fragment_myorders);
        mMyOrderAdapter = new MyOrdersAdapter(mOrderList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(rootview.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mMyOrderAdapter);

        prepareOrderData();
        return rootview;
    }

    private void prepareOrderData() {
        new BGTaskOrderData().execute();
    }

    private class BGTaskOrderData extends AsyncTask<Void, Void, String> {

        String json_url;
        String JSON_STRING;
        JSONArray jsonArray;
        JSONObject jobject;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            pd = ProgressDialog.show(getContext(), "", getString(R.string.loading_orders), false);

            SharedPreferences sharedPref;
            String userId, userToken;
            sharedPref = MyOrdersFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            userId = sharedPref.getString("userId", null);
            userToken = sharedPref.getString("userToken", null);

            String utfUserId = null;
            try {
                utfUserId = URLEncoder.encode(userId, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            json_url = getString(R.string.request_url) + "fauxusers/" + utfUserId + "/fauxorders" + "?access_token=" + userToken;
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
                //Log.d(TAG, "result: " + resultjson);

                jsonArray = new JSONArray(resultjson);

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            String totalitems, totalitemprice, orderconfirmed, orderdelivered, dishesinfo, deliveryfee;

            if(jsonArray==null || jsonArray.length()==0){
                mNoOrder.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }

            else if (jsonArray != null) {
                //Log.d(TAG, "Jsonobject length: " + String.valueOf(jsonArray.length()));
                for (int j = (jsonArray.length() - 1); j >= 0; j--) {

                    try {
                        jobject = jsonArray.getJSONObject(j);

                        JSONObject joDelivery;
                        joDelivery = jobject.getJSONObject("delivery");
                        JSONObject joOrderinfo;
                        joOrderinfo = jobject.getJSONObject("orderinfo");

                        totalitems = joOrderinfo.getString("totalitems");
                        totalitemprice = joOrderinfo.getString("totalitemprice");
                        dishesinfo = joOrderinfo.getString("dishesinfo");
                        deliveryfee = joOrderinfo.getString("deliveryfee");

                        String oconfirmed = joDelivery.getString("orderconfirmed");
                        String odelivered = joDelivery.getString("orderdelivered");

                        if (oconfirmed.equals("1")) {
                            orderconfirmed = "Confirmed";
                        } else {
                            orderconfirmed = "Not Confirmed";
                        }

                        if (odelivered.equals("1")) {
                            orderdelivered = "Delivered";
                        } else {
                            orderdelivered = "Not Delivered";
                        }

                        MyOrders orders = new MyOrders(jobject.getString("orderid"), totalitems,
                                jobject.getString("ordertotal"), jobject.getString("ordertiming"), orderconfirmed,
                                orderdelivered, totalitemprice, dishesinfo, jobject.getString("customeraddress"),
                                jobject.getString("restName"), deliveryfee);

                        mOrderList.add(orders);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mMyOrderAdapter.notifyDataSetChanged();

            } else Log.d(TAG, "JsonArray length is zero");

            pd.dismiss();
        }
    }
}
