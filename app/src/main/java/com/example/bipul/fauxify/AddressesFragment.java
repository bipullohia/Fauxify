package com.example.bipul.fauxify;


import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * A simple {@link Fragment} subclass.
 */

public class AddressesFragment extends Fragment {

    private ArrayList<Address> mAddressList = new ArrayList<>();
    private RecyclerView mAddressRecyclerView;
    FloatingActionButton mAddAddressFAButton;
    private AddressAdapter mAddressAdapter;
    static TextView noSavedAdd;

    public AddressesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_addresses, container, false);

        noSavedAdd = (TextView) view.findViewById(R.id.no_saved_address);

        mAddressRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_fragaddress);
        mAddressAdapter = new AddressAdapter(mAddressList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        mAddressRecyclerView.setLayoutManager(mLayoutManager);
        mAddressRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAddressRecyclerView.setAdapter(mAddressAdapter);

        mAddAddressFAButton = (FloatingActionButton) view.findViewById(R.id.fab_addaddress);
        mAddAddressFAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddAddressInfo.class);
                getContext().startActivity(intent);
            }
        });

        getAddresses();
        return view;
    }

    private void getAddresses() {
        new BGTaskGetAddresses().execute();
    }

    private class BGTaskGetAddresses extends AsyncTask<Void, Void, String> {

        String urlFinal;
        String JSON_STRING;

        JSONArray jsonArray;
        String jsonString;
        int status;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            pd = ProgressDialog.show(getContext(), "", "Loading Address info...", false);

            SharedPreferences sharedPref;
            String userId, userToken;
            sharedPref = AddressesFragment.this.getActivity().getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
            userId = sharedPref.getString("userId", null);
            userToken = sharedPref.getString("userToken", null);

            String utfUserId = null;
            try {
                utfUserId = URLEncoder.encode(userId, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            urlFinal = MainActivity.requestURL + "Fauxusers/" + utfUserId + "?access_token=" + userToken;
            Log.e("json_url", urlFinal);
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

                status = 1;

            } catch (IOException | JSONException e) {
                status = 0;
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if(jsonArray==null || jsonArray.length()==0){

                noSavedAdd.setVisibility(View.VISIBLE);
                mAddressRecyclerView.setVisibility(View.GONE);
            }

            else if (jsonArray != null && status == 1) {
                Log.e("Jsonarray length", String.valueOf(jsonArray.length()));
                for (int j = 0; j <= (jsonArray.length() - 1); j++) {
                    try {
                        Address address = new Address(jsonArray.getString(j));
                        mAddressList.add(address);
                        Log.e("add", String.valueOf(jsonArray.getString(j)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mAddressAdapter.notifyDataSetChanged();
            }

            else {
                Toast.makeText(getContext(), "Couldn't load Address info", Toast.LENGTH_SHORT).show();
            }

            pd.dismiss();
        }
    }
}
