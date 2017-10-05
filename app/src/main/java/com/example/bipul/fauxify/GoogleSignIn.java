package com.example.bipul.fauxify;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Bipul Lohia on 8/26/2016.
 */
public class GoogleSignIn extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int RC_SIGN_IN = 0;
    ProgressDialog processdialog;
    //private static final int PROFILE_PIC_SIZE = 400;
    private static final String TAG = "data ";
    public static String personDisplayName, personGivenName, personFamilyName, personId, personEmail, personGender;
    //private ImageView profile_pic;
    String result_checkjson;
    Toolbar toolbar;

    boolean doubleBackToExitPressedOnce = false;
    Integer noOfAddress;
    boolean ifUserEmailExists;
    private SignInButton signInButton;
    public GoogleApiClient mGoogleApiClient;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap 'back' again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signin_google_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
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

                // processdialog = ProgressDialog.show(this, "", "Logging In", false);
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
        Log.d(TAG, "handlesignInResult:" + result.getStatus());


        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.

            GoogleSignInAccount acct = result.getSignInAccount();

            assert acct != null;   // person's google data cannot be null

            personEmail = acct.getEmail();

            Plus.PeopleApi.load(mGoogleApiClient, acct.getId()).setResultCallback(new ResultCallback<People.LoadPeopleResult>() {

                @Override
                public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {

                    Person person = loadPeopleResult.getPersonBuffer().get(0);

                    personDisplayName = person.getDisplayName();
                    personGivenName = person.getName().getGivenName();
                    personFamilyName = person.getName().getFamilyName();
                    personId = person.getId();
                    int pg = person.getGender();
                    // String personUrl = person.getImage().getUrl();

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

                        default:
                            personGender = "Unknown";
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

                    //Log.e("Data check", personDisplayName + personGender);
                    Log.e("i reached", "here");
                    checkIfExists();


//
//                    sendData();
//
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(intent);
                    // processdialog.dismiss();

                }
            });

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, "Login failed- onconnecfailed", Toast.LENGTH_SHORT).show();
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

    private void checkIfExists() {
        new BGTaskCheckIfExists().execute();
    }

    private class BGTaskCheckIfExists extends AsyncTask<Void, Void, String> {

// personEmail.replace("@", "%40")

        //String postconcat = personId.substring(3, 6);

        String JSON_STRING;
        boolean exceptioncaught = false;
        String urlfind;

        @Override
        protected String doInBackground(Void... params) {

            String url = "http://fauxify.com/api/Fauxusers/";

            try {
                urlfind = URLEncoder.encode(personEmail, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String jsonUrl = url + urlfind + "/exists";

            Log.i("url", jsonUrl);

            try {

                URL urll = new URL(jsonUrl);
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
                ifUserEmailExists = jobject.getBoolean("exists");

                Log.e("if exists", String.valueOf(ifUserEmailExists));

            } catch (JSONException | IOException e) {

                exceptioncaught = true;
                Log.i("status", "Login failed");
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!exceptioncaught) {
                if (!ifUserEmailExists) {

//                    Intent intent = new Intent(getApplicationContext(), P.class);
//                    startActivity(intent);

                    postUserData();

                    Log.i("user doesnt exist", "Posting needed");
                } else {
                    LoginUser();
                    Log.i("user exists", "Login required");
                }
            } else Toast.makeText(GoogleSignIn.this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void LoginUser() {

        //Log.i("Status", "Develop the login genius!");
        new BackGroundTaskLoginUser().execute();
    }

    private class BackGroundTaskLoginUser extends AsyncTask<Void, Void, String> {

        String jsonUrl = "http://fauxify.com/api/Fauxusers/login"; // undefined url
        boolean exceptioncaught = false;
        boolean issuccess = true;

        @Override
        protected String doInBackground(Void... params) {

            //post login data

            try {

                URL url = new URL(jsonUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonObject = new JSONObject();

                String preconcat = personId.substring(8, 12);
                String postconcat = personId.substring(3, 6);

                jsonObject.accumulate("email", personEmail);
                jsonObject.accumulate("password", preconcat + personId + postconcat);

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
                        sb.append(line).append("\n");
                    }

                    br.close();

                    String ss = String.valueOf(sb);

                    JSONObject jsoDetails = new JSONObject(ss);

                    String token = String.valueOf(jsoDetails.get("id"));
                    String userId = String.valueOf(jsoDetails.get("userId"));


                    // saving essential info
                    SharedPreferences sharedPref;
                    sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putString("userToken", token);
                    editor.putString("userId", userId);
                    editor.apply();


                    Log.i("details", token + "   " + userId);

                    System.out.println("" + sb.toString());
                } else {
                    System.out.println(httpURLConnection.getResponseMessage());
                    issuccess = false;
                }

                Log.e("test", json);

            } catch (IOException | JSONException e) {

                exceptioncaught = true;
                Log.i("Status", "loginfailed3");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!exceptioncaught && issuccess) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            } else if (!issuccess) {
                Toast.makeText(GoogleSignIn.this, "Unauthorised User, Login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void postUserData() {
        new BackgroundTaskPostNewUser().execute();
    }

    private class BackgroundTaskPostNewUser extends AsyncTask<Void, Void, String> {

        String jsonUrl = "http://fauxify.com/api/Fauxusers"; // undefined url
        boolean exceptioncaught = false;
        boolean issuccess = true;

        @Override
        protected String doInBackground(Void... voids) {

            // post new user data
            try {

                Log.i("jsonurl", jsonUrl);

                URL url = new URL(jsonUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonObject = new JSONObject();

                String preconcat = personId.substring(8, 12);
                String postconcat = personId.substring(3, 6);

                jsonObject.accumulate("email", personEmail);
                jsonObject.accumulate("password", preconcat + personId + postconcat);
                jsonObject.accumulate("Firstname", personGivenName);
                jsonObject.accumulate("SecureX", "radhchdfhhsncfmd");
                jsonObject.accumulate("Socialid", personId);
                jsonObject.accumulate("Gender", personGender);
                jsonObject.accumulate("username", personDisplayName);
                //jsonObject.accumulate("id", personId + "afr");

                if (!personFamilyName.equals("")) {
                    jsonObject.accumulate("Lastname", personFamilyName);
                }

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
                    issuccess = false;
                }

                Log.e("test", json);

            } catch (IOException | JSONException e) {

                exceptioncaught = true;
                Log.i("status", "loginfailed2");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (!exceptioncaught && issuccess) {
                LoginUser();
            } else Toast.makeText(GoogleSignIn.this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }
}