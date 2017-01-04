package com.example.bipul.fauxify;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantFragment extends Fragment {

    private ArrayList<Restaurant> restaurantList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RestaurantAdapter resAdapter;
    ProgressDialog pd;

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) getActivity())
                .setActionBarTitle("Restaurants");
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_restaurant, container, false);

        recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view);

        resAdapter = new RestaurantAdapter(restaurantList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(rootview.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(resAdapter);


        prepareMovieData();


        return rootview;
    }

    private void prepareMovieData() {
        new bgroundtask().execute();
    }

    class bgroundtask extends AsyncTask<Void, Void, String> {

        String json_url;
        String JSON_STRING;
        JSONArray jsonArray;
        JSONObject jobject;

        @Override
        protected void onPreExecute() {

            json_url = MainActivity.requestURL +"Restaurants";
            Log.e("json_url", json_url);
//            pd=ProgressDialog.show(getContext(),"","Please Wait",false);

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
                Log.e("result", resultjson);

                jsonArray = new JSONArray(resultjson);


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (jsonArray != null) {
                Log.e("Jsonobject length", String.valueOf(jsonArray.length()));
                for (int j = 0; j <= (jsonArray.length() - 1); j++) {
                    try {
                        jobject = jsonArray.getJSONObject(j);
                        Restaurant restaurant = new Restaurant(jobject.getString("Restname"), jobject.getString("Resttype"),
                                jobject.getString("Ordercapacity"), jobject.getString("Deliversin"), jobject.getString("Minorder"),
                                jobject.getString("Restid"), jobject.getString("Deliveryfee"), jobject.getString("freeDeliveryAmount"));
                        restaurantList.add(restaurant);
                        Log.e("add", String.valueOf(jobject.getString("Restname")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                resAdapter.notifyDataSetChanged();
//                pd.dismiss();

            } else Log.e("Jsonarray length", "is zero");
        }

    }

}