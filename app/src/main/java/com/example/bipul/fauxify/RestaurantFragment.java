package com.example.bipul.fauxify;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class RestaurantFragment extends Fragment {

    private ArrayList<Restaurant> mRestaurantList = new ArrayList<>();
    SwipeRefreshLayout mSwipeRefreshLayout;
    private RestaurantAdapter mResAdapter;

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) getActivity())
                .setActionBarTitle(getString(R.string.restaurants));
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_restaurant, container, false);

        RecyclerView recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view);

        mResAdapter = new RestaurantAdapter(mRestaurantList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(rootview.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mResAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mRestaurantList.clear();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(RestaurantFragment.this).attach(RestaurantFragment.this).commit();

            }
        });

        prepareRestaurantData();
        return rootview;
    }

    private void prepareRestaurantData() {
        new BGTaskPrepareRestaurantData().execute();
    }

    private class BGTaskPrepareRestaurantData extends AsyncTask<Void, Void, String> {

        String json_url;
        String JSON_STRING;
        JSONArray jsonArray;
        JSONObject jobject;
        int status;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            json_url = getString(R.string.request_url) + "restaurants";
            pd = ProgressDialog.show(getContext(), "", getString(R.string.fetching_restaurants), false);
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
                    stringBuilder.append(JSON_STRING).append("\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpConnection.disconnect();
                String resultjson = stringBuilder.toString().trim();
                //Log.d(TAG, "result: " + resultjson);

                status = 1;
                jsonArray = new JSONArray(resultjson);

            } catch (IOException | JSONException e) {
                status = 0;
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (jsonArray != null && status == 1) {
                for (int j = 0; j <= (jsonArray.length() - 1); j++) {
                    try {
                        jobject = jsonArray.getJSONObject(j);
                        Restaurant restaurant = new Restaurant(jobject.getString("Restname"), jobject.getString("Resttype"),
                                jobject.getString("Ordercapacity"), jobject.getString("Deliversin"), jobject.getString("Minorder"),
                                jobject.getString("restaurantId"), jobject.getString("Deliveryfee"),
                                jobject.getString("freeDeliveryAmount"), jobject.getString("RestaurantStatus"));
                        mRestaurantList.add(restaurant);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mResAdapter.notifyDataSetChanged();

            } else if (status == 0) {
                Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
            }

            pd.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}