package com.example.bipul.fauxify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by bipul on 22-04-2016.
 */
public class SplashScreen extends Activity {


    SharedPreferences shapre = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        shapre = getSharedPreferences("myshared", MODE_PRIVATE);


        final ImageView iv = (ImageView) findViewById(R.id.logo);
        Animation an = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);
        iv.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                iv.startAnimation(an2);
                SplashScreen.this.finish();

                if (shapre.getBoolean("firstrun", true)) {
                    Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                    startActivity(intent);
                    shapre.edit().putBoolean("firstrun", false).commit();
                } else {
                    Log.e("im in", "launchhomescreen222");
                    launchHomeScreen();
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });


    }


    private void launchHomeScreen() {

        Log.e("im in", "launchhomescreen");
        SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
        String personEmail = sharedPref.getString("personEmail", null);
        String personId = sharedPref.getString("personId", null);
        String personFamilyName = sharedPref.getString("personFamilyName", null);
        String personGivenName = sharedPref.getString("personGivenName", null);
        String personDisplayName = sharedPref.getString("personDisplayName", null);

        if ((personEmail == null) || (personDisplayName == null) || (personId == null)) {
            Log.e("Data status", "all are null");
            Intent intent = new Intent(this, GoogleSignIn.class);
            startActivity(intent);
        } else if ((personEmail != null) && (personDisplayName != null) && (personId != null)) {
            Log.e("Data status", "none is null");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Log.e("Data status", "complicated relation");
            Intent intent = new Intent(this, GoogleSignIn.class);
            startActivity(intent);
        }

        finish();
    }
}
