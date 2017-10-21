package com.example.bipul.fauxify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Bipul Lohia on 8/28/2016.
 */

public class AddressConfirmation extends AppCompatActivity implements View.OnClickListener {

    TextView mFinalAddressTextView;
    Button mConfirmButton;
    String mAddress;
    JSONArray jArrayOldAddresses;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_confirmation_layout);

        mConfirmButton = (Button) findViewById(R.id.confirm_button);
        mFinalAddressTextView = (TextView) findViewById(R.id.confirm_address);
        mFinalAddressTextView.setText(getIntent().getStringExtra("Address"));
        mAddress = getIntent().getStringExtra("Address");

        mToolbar = (Toolbar) findViewById(R.id.toolbar_addressconfirm);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.address_confirmation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mConfirmButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        sendAddress();

        Toast.makeText(getBaseContext(), R.string.address_added_successfully, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void sendAddress() {
        new BGTaskAddAddress().execute();
    }

    private class BGTaskAddAddress extends AsyncTask<Void, Void, String> {

        String userId, userToken, urlFinal, JSON_STRING;
        String[] savedaddress;

        @Override
        protected void onPreExecute() {

            SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);

            userId = sharedPref.getString("userId", null);
            userToken = sharedPref.getString("userToken", null);

            String utfUserId = null;
            try {
                utfUserId = URLEncoder.encode(userId, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            urlFinal = getString(R.string.request_url) + "Fauxusers/" + utfUserId + "?access_token=" + userToken;
            Log.i("checkurl", urlFinal);
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(urlFinal);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING).append("\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                String result_checkjson = stringBuilder.toString().trim();
                Log.e("result", result_checkjson);

                JSONObject jobject = new JSONObject(result_checkjson);
                jArrayOldAddresses = jobject.getJSONArray("Address");

                if (jArrayOldAddresses != null){

                savedaddress = new String[jArrayOldAddresses.length()];

                for (int j = 0; j <= (jArrayOldAddresses.length() - 1); j++) {
                    savedaddress[j] = jArrayOldAddresses.getString(j);
                    Log.i("saved addresses", savedaddress[j]);
                }}

                else {
                    Log.i("Addressconfirm activity", "No previous address found");
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            try {

                URL urll = new URL(urlFinal);
                HttpURLConnection httpConnection = (HttpURLConnection) urll.openConnection();

                httpConnection.setDoOutput(true);
                httpConnection.setDoInput(true);

                httpConnection.setRequestMethod("PUT");
                httpConnection.setRequestProperty("Accept", "application/json");
                httpConnection.setRequestProperty("Content-Type", "application/json");

                JSONArray jsonArray = new JSONArray();

                if (jArrayOldAddresses != null){

                for (int i = 0; i <= jArrayOldAddresses.length() - 1; i++) {
                    jsonArray.put(savedaddress[i]);
                    Log.i("posting addresses", savedaddress[i]);
                }}

                else {
                    Log.i("Addressconfirm activity", "No previous address found while posting");
                }

                jsonArray.put(mAddress);

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Address", jsonArray);

                String json = jsonObject.toString();

                OutputStreamWriter out = new OutputStreamWriter(httpConnection.getOutputStream());
                out.write(json);
                out.flush();
                out.close();

                StringBuilder sb = new StringBuilder();
                int HttpResult = httpConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(httpConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    System.out.println("" + sb.toString());
                } else {
                    System.out.println(httpConnection.getResponseMessage());
                }

                Log.i("Final posted Addresses", json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}

