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


public class GoogleSignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 0;

    ProgressDialog processdialog;
    public static String personDisplayName, personGivenName, personFamilyName, personId, personEmail, personGender;
    String result_checkjson;
    Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = false;
    boolean ifUserEmailExists;
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
        Toast.makeText(this, R.string.tap_back_again_exit, Toast.LENGTH_SHORT).show();

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

        setContentView(R.layout.activity_signin_google);

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

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        findViewById(R.id.sign_in_button).setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:

                processdialog = ProgressDialog.show(this, "", getString(R.string.logging_in), false);
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
        //Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        //Log.d(TAG, "handlesignInResult:" + result.getStatus());

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

                    switch (pg) {
                        case 0:
                            personGender = getString(R.string.male);
                            break;
                        case 1:
                            personGender = getString(R.string.female);
                            break;
                        case 2:
                            personGender = getString(R.string.other);
                            break;

                        default:
                            personGender = getString(R.string.unknown);
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

                    checkIfExists();
                }
            });
        }

        processdialog.dismiss();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, R.string.login_failed, Toast.LENGTH_SHORT).show();
        processdialog.dismiss();
    }

    private void checkIfExists() {
        new BGTaskCheckIfExists().execute();
    }

    private class BGTaskCheckIfExists extends AsyncTask<Void, Void, String> {

        ProgressDialog pd;
        String JSON_STRING;
        boolean exceptioncaught = false;
        String urlfind;

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(GoogleSignInActivity.this, "", getString(R.string.checking_user), false);
        }

        @Override
        protected String doInBackground(Void... params) {

            String url = getString(R.string.request_url) + "Fauxusers/";

            try {
                urlfind = URLEncoder.encode(personEmail, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String jsonUrl = url + urlfind + "/exists";

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
                //Log.d(TAG, "result: " + result_checkjson);

                JSONObject jobject = new JSONObject(result_checkjson);
                ifUserEmailExists = jobject.getBoolean("exists");
                Log.d(TAG, "If exists: " + String.valueOf(ifUserEmailExists));

            } catch (JSONException | IOException e) {

                exceptioncaught = true;
                Log.e(TAG, "Login failed");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!exceptioncaught) {
                if (!ifUserEmailExists) {

                    postUserData();

                    Log.d(TAG, "User doesn't exist, posting needed");

                } else {
                    LoginUser();
                    Log.d(TAG, "User exists, Login required");
                }

            } else Toast.makeText(GoogleSignInActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();

            pd.dismiss();
        }
    }

    private void LoginUser() {
        new BGTaskLoginUser().execute();
    }

    private class BGTaskLoginUser extends AsyncTask<Void, Void, String> {

        String jsonUrl = getString(R.string.request_url) + "Fauxusers/login"; // undefined url
        boolean exceptioncaught = false;
        boolean issuccess = true;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(GoogleSignInActivity.this, "", getString(R.string.logging_in), false);
        }

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

                    System.out.println("" + sb.toString());
                } else {
                    System.out.println(httpURLConnection.getResponseMessage());
                    issuccess = false;
                }

                //Log.d(TAG, "test-data: " + json);

            } catch (IOException | JSONException e) {

                exceptioncaught = true;
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!exceptioncaught && issuccess) {

                checkIfContactNumberExists();

            } else if (!issuccess) {
                Toast.makeText(GoogleSignInActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
            }

            pd.dismiss();
        }
    }

    private void postUserData() {
        new BGTaskPostNewUser().execute();
    }

    private class BGTaskPostNewUser extends AsyncTask<Void, Void, String> {

        String jsonUrl = getString(R.string.request_url) + "Fauxusers"; // undefined url
        boolean exceptioncaught = false;
        boolean issuccess = true;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(GoogleSignInActivity.this, "", getString(R.string.logging_in), false);
        }

        @Override
        protected String doInBackground(Void... voids) {

            // post new user data
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
                jsonObject.accumulate("Firstname", personGivenName);
                jsonObject.accumulate("SecureX", "radhchdfhhsncfmd");
                jsonObject.accumulate("Socialid", personId);
                jsonObject.accumulate("Gender", personGender);
                jsonObject.accumulate("username", personDisplayName);
                jsonObject.accumulate("Contact", "");
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

                //Log.d(TAG, "test-data: " + json);

            } catch (IOException | JSONException e) {

                exceptioncaught = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (!exceptioncaught && issuccess) {
                LoginUser();

            } else {
                Toast.makeText(GoogleSignInActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Login failed while posting info");
            }
            pd.dismiss();
        }
    }

    private void checkIfContactNumberExists() {
        new BGTaskCheckContactNo().execute();
    }

    private class BGTaskCheckContactNo extends AsyncTask<Void, Void, String> {

        String urlFinal;
        String JSON_STRING;
        int status;
        ProgressDialog pd;
        String phoneNumber;

        @Override
        protected void onPreExecute() {

            pd = ProgressDialog.show(GoogleSignInActivity.this, "", getString(R.string.checking_contact_info), false);

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

            urlFinal = getString(R.string.request_url) + "Fauxusers/" + utfUserId + "?access_token=" + userToken;
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
                //Log.d(TAG, "result: " + resultjson);

                JSONObject jobject = new JSONObject(resultjson);
                phoneNumber = jobject.getString("Contact");

                status = 1;

            } catch (IOException | JSONException e) {

                status = 0;
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if(status==0){ //during development, if any account was registered without contact no., that will show status code = 0
                           // and not redirect to Otpactivity class. Fix it or keep that in mind.

                Toast.makeText(GoogleSignInActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Login failed while enquiring about contact number existence");
            }

            else if(status==1 && phoneNumber != null && !phoneNumber.matches("")){

                SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("personContactNumber", phoneNumber);
                editor.apply();
                startActivity(new Intent(GoogleSignInActivity.this, MainActivity.class));
                finish();

            }else if(status==1 && (phoneNumber==null || phoneNumber.matches(""))){
               startActivity(new Intent(GoogleSignInActivity.this, OtpActivity.class));
               finish();
            }

            pd.dismiss();
        }
    }
}