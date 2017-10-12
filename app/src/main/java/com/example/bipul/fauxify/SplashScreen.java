package com.example.bipul.fauxify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by bipul on 22-04-2016.
 */
public class SplashScreen extends Activity {

    SharedPreferences shapre = null;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {

        super.onResume();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.splash); don't uncomment this

        shapre = getSharedPreferences("myshared", MODE_PRIVATE);

        if (shapre.getBoolean("firstrun", true)) {

            Log.i("firstrun", "true");
            Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
            startActivity(intent);
            shapre.edit().putBoolean("firstrun", false).apply();
        } else {

            Log.i("firstrun", "false");
            Intent intent = new Intent(this, GoogleSignIn.class);
            startActivity(intent);
            launchHomeScreen();
        }

//        Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
//        startActivity(intent);

    }

    private void launchHomeScreen() {

        Log.e("im in", "launchhomescreen");
        SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        String personEmail = sharedPref.getString("personEmail", null);
        String personId = sharedPref.getString("personId", null);
        String personFamilyName = sharedPref.getString("personFamilyName", null);
        String personGivenName = sharedPref.getString("personGivenName", null);
        String personDisplayName = sharedPref.getString("personDisplayName", null);
        String personUserId = sharedPref.getString("userId", null);
        String personToken = sharedPref.getString("userToken", null);
        String personContactNumber = sharedPref.getString("personContactNumber", null);

        if ((personEmail == null) || (personDisplayName == null) || (personId == null) || (personUserId == null)
                || (personToken == null) || (personContactNumber == null)) {
            Log.e("Data status", "some/all info are null");

            Intent intent = new Intent(this, GoogleSignIn.class);
            startActivity(intent);
        } else {
            Log.e("Data status", "none is null");

            Log.i("token", personToken);
            Log.i("id", personUserId);
            Log.i("number", personContactNumber);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


//
//        Intent intent = new Intent(this, GoogleSignIn.class);
//        startActivity(intent);
//        finish();
    }
}
