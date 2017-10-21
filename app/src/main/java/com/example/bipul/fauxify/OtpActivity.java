package com.example.bipul.fauxify;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import static com.example.bipul.fauxify.R.id.button_submit;
import static com.example.bipul.fauxify.R.id.button_verify;

public class OtpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mOtpInputEditText, mNumberInputEditText;
    Button mSubmitButton, mVerifyButton;
    LinearLayout mOtpInputLinearLayout, mNumberInputLinearLayout;

    private FirebaseAuth mAuth;
    String mPhoneNumber;

    private static final String TAG = "PhoneAuthActivity";
    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    ProgressDialog mProgressDialog;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_otpactivity);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.phone_number_verification);

        mOtpInputEditText = (EditText) findViewById(R.id.otp_edittext);
        mNumberInputEditText = (EditText) findViewById(R.id.edittext_phone_number);
        mSubmitButton = (Button) findViewById(button_submit);
        mVerifyButton = (Button) findViewById(R.id.button_verify);
        mOtpInputLinearLayout = (LinearLayout) findViewById(R.id.otp_input_linearlayout);
        mNumberInputLinearLayout = (LinearLayout) findViewById(R.id.number_input_linear_layout);

        mAuth = FirebaseAuth.getInstance();

        mSubmitButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);

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

                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(OtpActivity.this, R.string.verification_failed_invalid_num, Toast.LENGTH_SHORT).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(OtpActivity.this, R.string.verification_failed_quota_exceeded, Toast.LENGTH_SHORT).show();
                }

                mNumberInputLinearLayout.setVisibility(View.VISIBLE);
                mOtpInputLinearLayout.setVisibility(View.GONE);

                mProgressDialog.dismiss();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                //Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(OtpActivity.this, R.string.verification_code_sent, Toast.LENGTH_SHORT).show();

                // Saving verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                //mResendToken = token;
                mNumberInputLinearLayout.setVisibility(View.GONE);
                mOtpInputLinearLayout.setVisibility(View.VISIBLE);

                mProgressDialog.dismiss();
            }
        };
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case button_submit:
                mPhoneNumber = mNumberInputEditText.getText().toString();
                mProgressDialog = ProgressDialog.show(OtpActivity.this, "", getString(R.string.processing_request), false);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91"+ mPhoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        OtpActivity.this,  // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks
                break;

            case button_verify:
                mProgressDialog = ProgressDialog.show(OtpActivity.this, "", getString(R.string.processing_request), false);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, mOtpInputEditText.getText().toString());
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

                            Toast.makeText(OtpActivity.this, R.string.phone_verification_success, Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                            mAuth.signOut();

                            PostUserContactDetails();

                        } else {
                            mProgressDialog.dismiss();
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(OtpActivity.this, R.string.verification_failed_invalid_code,
                                                Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void PostUserContactDetails() {
        new BGTaskPostUserContactDetail().execute();
    }

    private class BGTaskPostUserContactDetail extends AsyncTask<Void, Void, String> {

        boolean exceptioncaught = false;
        boolean issuccess = true;
        String userId, userToken, urlFinal, JSON_STRING;
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {

            pd = ProgressDialog.show(OtpActivity.this, "", getString(R.string.saving_contact_info), false);
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
                jsonObject.accumulate("Contact", mPhoneNumber);

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

                Log.e("test-otpActivity", json);

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

                editor.putString("personContactNumber", mPhoneNumber);
                editor.apply();

                startActivity(new Intent(OtpActivity.this, MainActivity.class));
                finish();

            } else {
                Toast.makeText(OtpActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                Log.e("login failed", "while posting contact info");
            }

            pd.dismiss();
        }
    }
}
