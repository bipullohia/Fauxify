package com.example.bipul.fauxify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

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

/**
 * Created by Bipul Lohia on 8/26/2016.
 */
public class GoogleSignIn extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    private static final int PROFILE_PIC_SIZE = 400;
    private static final String TAG = "data ";
    String personDisplayName, personGivenName, personFamilyName, personId, personEmail, personGender;
    //private ImageView profile_pic;
    String result_checkjson;
    Toolbar toolbar;

    Integer noOfAddress;


    private SignInButton signInButton;
    public GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_google_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar_googlesignin);
        setSupportActionBar(toolbar);

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Plus.API)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        findViewById(R.id.sign_in_button).setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:

                Log.e("SignINbutton Onclick", "clicked");
                signIn();

                break;

        }
    }

    private void signIn() {

        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            String person_email = acct.getEmail();
            personEmail = person_email;
            String ppersonid = acct.getId();
            String ppersondn = acct.getDisplayName();
            String ppersonfn = acct.getFamilyName();
            String ppersongn = acct.getGivenName();

            Log.e("data", ppersondn + ppersonfn + ppersongn + ppersonid);

            Log.e(TAG, "Email:" + personEmail);

            Plus.PeopleApi.load(mGoogleApiClient, acct.getId()).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {

                @Override
                public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {

                    Person person = loadPeopleResult.getPersonBuffer().get(0);
                    personDisplayName = person.getDisplayName();
                    personGivenName = person.getName().getGivenName();
                    personFamilyName = person.getName().getFamilyName();
                    personId = person.getId();
                    int pg = person.getGender();
                    String personUrl = person.getImage().getUrl();

                    switch (pg) {
                        case 0:
                            personGender = "Male";
                            break;
                        case 1:
                            personGender = "Female";
                            break;
                        case 2:
                            personGender = "Other";
                            break;
                    }

                    SharedPreferences sharedPref;

                    sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();



                    editor.putString("personDisplayName", personDisplayName);
                    editor.putString("personFamilyName", personFamilyName);
                    editor.putString("personGivenName", personGivenName);
                    editor.putString("personEmail", personEmail);
                    editor.putString("personId", personId);
                    editor.putString("personGender", personGender);
                    editor.apply();



                    /*personUrl = personUrl.substring(0,
                            personUrl.length() - 2)
                            + PROFILE_PIC_SIZE;*/

                    //new LoadProfileImage(profile_pic).execute(personUrl);

                    Log.e("Data check", personDisplayName + personGender);
                    sendData();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                }
            });

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

   /* private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }*/


    private void sendData() {
        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String json_url;
        String json_checkurl, checkurl;
        String JSON_STRING;
        String ifEmailExists;

        @Override
        protected void onPreExecute() {

            checkurl = personEmail.replace("@", "%40");
            json_url = MainActivity.requestURL+"Fauxusers/";
            json_checkurl = json_url + checkurl + "/exists";
            Log.e("checkurl", json_checkurl);
        }

        @Override
        protected String doInBackground(Void... voids) {
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
                result_checkjson = stringBuilder.toString().trim();
                Log.e("result", result_checkjson);


                JSONObject jobject = new JSONObject(result_checkjson);
                ifEmailExists = jobject.getString("exists");

                Log.e("if exists", ifEmailExists);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (ifEmailExists == "true") {


                Log.e("checkemailexistence", "email exists: redirecting to main activity");
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else if (ifEmailExists == "false") {

                Log.e("checkemailexistence", "Email doesnot exist, give the post request");
                try {

                    URL url = new URL(json_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");


                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("Lastname", personFamilyName);
                    jsonObject.accumulate("Firstname", personGivenName);
                    jsonObject.accumulate("Email", personEmail);
                    jsonObject.accumulate("Socialid", personId);
                    jsonObject.accumulate("Gender", personGender);

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


            }

            return null;
        }




    }



}