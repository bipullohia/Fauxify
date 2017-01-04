package com.example.bipul.fauxify;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
public class AddressesFragment extends Fragment {

    Button addAddressButton;
    private ArrayList<Address> addressList = new ArrayList<>();
    private RecyclerView addressRecyclerView;
    FloatingActionButton fabAddAddress;
    private AddressAdapter addressAdapter;
    JSONArray jsonArray;

    public AddressesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addresses, container, false);

        addressRecyclerView = (RecyclerView) view.findViewById(R.id.address_recycler_view);

        addressAdapter = new AddressAdapter(addressList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        addressRecyclerView.setLayoutManager(mLayoutManager);
        addressRecyclerView.setItemAnimator(new DefaultItemAnimator());
        addressRecyclerView.setAdapter(addressAdapter);

        fabAddAddress = (FloatingActionButton) view.findViewById(R.id.fabAddAddress);
        fabAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddAddressInfo.class);
                getContext().startActivity(intent);
            }
        });

        addAddresses();
        return view;

    }

    private void addAddresses() {
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
            sharedPref = AddressesFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            email = sharedPref.getString("personEmail", null);
            Log.e("email local data", "exists");
            checkurl = email.replace("@", "%40");
            json_url = MainActivity.requestURL+"Fauxusers/";
            json_checkurl = json_url+ "/"+ checkurl;
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
                    stringBuilder.append(JSON_STRING + "\n");
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

                addressAdapter.notifyDataSetChanged();


            } else Log.e("Jsonarray length", "is zero");
        }


    }

}
