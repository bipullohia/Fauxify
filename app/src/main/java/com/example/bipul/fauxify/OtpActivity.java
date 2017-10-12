package com.example.bipul.fauxify;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import static com.example.bipul.fauxify.R.id.submitButton;
import static com.example.bipul.fauxify.R.id.verifyButton;

public class OtpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText otpInput, numberInput;
    Button submit, verify;
    LinearLayout otpInputLL, numberInputLL;

    private FirebaseAuth mAuth;
    String phoneNumber;

    private static final String TAG = "PhoneAuthActivity";
    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpInput = (EditText) findViewById(R.id.OTPEdittext);
        numberInput = (EditText) findViewById(R.id.phoneNumberEdittext);
        submit = (Button) findViewById(submitButton);
        verify = (Button) findViewById(R.id.verifyButton);
        otpInputLL = (LinearLayout) findViewById(R.id.OTPInputLinearLayout);
        numberInputLL = (LinearLayout) findViewById(R.id.numberInputLinearLayout);

        mAuth = FirebaseAuth.getInstance();

        submit.setOnClickListener(this);
        verify.setOnClickListener(this);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(OtpActivity.this, "Verification Failed. Invalid Phone Number", Toast.LENGTH_SHORT).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(OtpActivity.this, "Verification Failed. Quota exceeded!", Toast.LENGTH_SHORT).show();
                    // The SMS quota for the project has been exceeded
                }

                numberInputLL.setVisibility(View.VISIBLE);
                otpInputLL.setVisibility(View.GONE);

                pd.dismiss();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(OtpActivity.this, "Verification Code sent!", Toast.LENGTH_SHORT).show();

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                //mResendToken = token;
                numberInputLL.setVisibility(View.GONE);
                otpInputLL.setVisibility(View.VISIBLE);

                pd.dismiss();
            }
        };
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case submitButton:

                phoneNumber = numberInput.getText().toString();

                pd = ProgressDialog.show(OtpActivity.this, "", "Processing request...", false);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91"+phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        OtpActivity.this,  // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks

                break;

            case verifyButton:

                pd = ProgressDialog.show(OtpActivity.this, "", "Processing request...", false);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpInput.getText().toString());
                signInWithPhoneAuthCredential(credential);

                break;
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(OtpActivity.this, "Phone number verification is successful!", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                            mAuth.signOut();

                            PostUserContactDetails();

                        } else {

                            pd.dismiss();
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(OtpActivity.this, "Invalid code!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void PostUserContactDetails() {

        new bgTaskPostUserContactDetail().execute();
    }

    private class bgTaskPostUserContactDetail extends AsyncTask<Void, Void, String> {

        boolean exceptioncaught = false;
        boolean issuccess = true;
        String userId, userToken, urlFinal, JSON_STRING;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            pd = ProgressDialog.show(OtpActivity.this, "", "Saving contact info...", false);
            SharedPreferences sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);

            userId = sharedPref.getString("userId", null);
            userToken = sharedPref.getString("userToken", null);

            String utfUserId = null;
            try {
                utfUserId = URLEncoder.encode(userId, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            urlFinal = "http://fauxify.com/api/" + "Fauxusers/" + utfUserId + "?access_token=" + userToken;
            Log.e("checkurl", urlFinal);
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {

                URL url = new URL(urlFinal);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                httpURLConnection.setRequestMethod("PUT");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("Contact", phoneNumber);

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
                Log.i("status", "loginfailed-while postcontactdetails");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (!exceptioncaught && issuccess) {

                SharedPreferences sharedPref;
                sharedPref = getSharedPreferences("User Preferences Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putString("personContactNumber", phoneNumber);
                editor.apply();

                startActivity(new Intent(OtpActivity.this, MainActivity.class));

            } else {
                Toast.makeText(OtpActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                Log.e("login failed", "while posting contact info");
            }

            pd.dismiss();
        }
    }


}
