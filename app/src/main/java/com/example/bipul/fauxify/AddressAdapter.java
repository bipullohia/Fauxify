package com.example.bipul.fauxify;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder> {

    private static final String TAG = "AddressAdapter";
    private ArrayList<Address> mAddressList;
    private Integer mPosition;

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView userAddress;
        Context context;

        MyViewHolder(View view) {
            super(view);

            context = view.getContext();
            view.setOnLongClickListener(this);

            userAddress = (TextView) view.findViewById(R.id.user_address);
        }

        @Override
        public boolean onLongClick(View v) {

            mPosition = getAdapterPosition();

            AlertDialog.Builder alertbuilder = new AlertDialog.Builder(context);
            alertbuilder.setMessage(R.string.delete_address_ques)
                    .setCancelable(true)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                class BGTaskDeleteAddress extends AsyncTask<Void, Void, String> {

                                    String userId, userToken, urlFinal, JSON_STRING;
                                    String[] savedaddress;
                                    JSONArray jArrayOldAddresses;
                                    JSONArray jArrayNewAddresses;

                                    @Override
                                    protected void onPreExecute() {
                                        SharedPreferences sharedPref = context.getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
                                        userId = sharedPref.getString("userId", null);
                                        userToken = sharedPref.getString("userToken", null);
                                        String utfUserId = null;

                                        try {
                                            utfUserId = URLEncoder.encode(userId, "utf-8");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }

                                        urlFinal = context.getString(R.string.request_url) + "Fauxusers/"
                                                                    + utfUserId + "?access_token=" + userToken;
                                    }

                                    @Override
                                    protected String doInBackground(Void... params) {

                                        try {   //to fetch the already saved addresses from database
                                            URL url = new URL(urlFinal);
                                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                                            InputStream inputStream = httpURLConnection.getInputStream();
                                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                                            StringBuilder stringBuilder = new StringBuilder();
                                            while ((JSON_STRING = bufferedReader.readLine()) != null) {
                                                stringBuilder.append(JSON_STRING + "\n");
                                            }

                                            bufferedReader.close();
                                            inputStream.close();
                                            httpURLConnection.disconnect();

                                            String result_checkjson = stringBuilder.toString().trim();
                                            //Log.d(TAG, "result: " + result_checkjson);

                                            JSONObject jobject = new JSONObject(result_checkjson);

                                            //jArrayOldAddresses stores the initial number of addresses
                                            jArrayOldAddresses = jobject.getJSONArray("Address");

                                            if (jArrayOldAddresses != null && jArrayOldAddresses.length()!=0) {

                                                savedaddress = new String[jArrayOldAddresses.length()];

                                                for (int j = 0; j <= (jArrayOldAddresses.length() - 1); j++) {
                                                    savedaddress[j] = jArrayOldAddresses.getString(j);
                                                    //Log.d(TAG, "saved addresses: " + savedaddress[j]);
                                                }

                                            } else {
                                                Log.d(TAG, "No Previous Address found");
                                            }

                                        } catch (JSONException | IOException e) {
                                            e.printStackTrace();
                                        }

                                        try {   //to post the new set of addresses which includes the new one

                                            URL urll = new URL(urlFinal);
                                            HttpURLConnection httpConnection = (HttpURLConnection) urll.openConnection();

                                            httpConnection.setDoOutput(true);
                                            httpConnection.setDoInput(true);

                                            httpConnection.setRequestMethod("PUT");
                                            httpConnection.setRequestProperty("Accept", "application/json");
                                            httpConnection.setRequestProperty("Content-Type", "application/json");

                                            jArrayNewAddresses = new JSONArray();

                                            if (jArrayOldAddresses != null && jArrayOldAddresses.length()!=0) {

                                                for (int i = 0; i <= jArrayOldAddresses.length() - 1; i++) {

                                                    if (i != mPosition) {

                                                        Address address = new Address(savedaddress[i]); //adding the new addresses to list
                                                        mAddressList.add(address);
                                                        jArrayNewAddresses.put(savedaddress[i]);
                                                        //Log.d(TAG, "posting addresses: " + savedaddress[i]);
                                                    }
                                                }

                                            } else {
                                                Log.d(TAG, "No Previous Address found while posting");
                                            }

                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.accumulate("Address", jArrayNewAddresses);

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

                                            //Log.d(TAG, "test-finalAddressPost: " + json);

                                        } catch (IOException | JSONException e) {
                                            e.printStackTrace();
                                        }

                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(String s) {

                                        notifyDataSetChanged(); //it updates the new address adapter with new list
                                        if(jArrayNewAddresses.length()==0){ //it will update the textview which says no saved
                                            AddressesFragment.noSavedAdd.setVisibility(View.VISIBLE); //addresses since no of Add is zero
                                        }
                                    }
                                }

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    mAddressList.clear(); //clearing the old address list
                                    Toast.makeText(context, R.string.address_deleted, Toast.LENGTH_SHORT).show();
                                    deleteAddress();
                                }

                                private void deleteAddress() {
                                    new BGTaskDeleteAddress().execute();
                                }
                            }
                    )

                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = alertbuilder.create();
            alert.setTitle(context.getString(R.string.alert));
            alert.show();

            return true;
        }
    }

    AddressAdapter(ArrayList<Address> addressList) {
        this.mAddressList = addressList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rowlayout_address_fragment, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Address address = mAddressList.get(position);
        holder.userAddress.setText(address.getUserAddress());
    }

    @Override
    public int getItemCount() {
        return mAddressList.size();
    }
}
