package com.example.bipul.fauxify;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by Bipul Lohia on 9/17/2016.
 */

public class AddAddressInfoCart extends AppCompatActivity implements View.OnClickListener {

    Spinner mSpinner;
    ArrayAdapter<CharSequence> mSpinnerAdapter;
    EditText mFlatNoEditText, mFullAddressEditText;
    Toolbar mToolbar;
    String mFlatNum, mFullAddress;
    CardView mHouseNoCardView;
    Button mSubmitButton;
    Integer mAddressPositionInt;
    String mInputAddress, mDefaultAddress, mTotalAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_addressinfo_layout);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_addaddressinfo);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Address");

        mFlatNoEditText = (EditText) findViewById(R.id.flat_no);
        mFullAddressEditText = (EditText) findViewById(R.id.full_address);
        mSubmitButton = (Button) findViewById(R.id.submit_button);
        mHouseNoCardView = (CardView) findViewById(R.id.houseno_cardview);

        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.Areas, android.R.layout.simple_spinner_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mAddressPositionInt = position;
                Log.e("Position", (String) parent.getItemAtPosition(position));

                if (position == 0) {
                    mFlatNoEditText.setVisibility(View.GONE);
                    mFullAddressEditText.setVisibility(View.GONE);
                    mHouseNoCardView.setVisibility(View.GONE);

                } else if ((position == 1) || (position == 2) || (position == 3)) {
                    mFlatNoEditText.setVisibility(View.VISIBLE);
                    mHouseNoCardView.setVisibility(View.VISIBLE);
                    mFullAddressEditText.setVisibility(View.GONE);

                } else if (((position == 4))) {
                    mHouseNoCardView.setVisibility(View.VISIBLE);
                    mFullAddressEditText.setVisibility(View.VISIBLE);
                    mFlatNoEditText.setVisibility(View.GONE);
                }

                Log.e("selected address-pos", String.valueOf(parent.getItemAtPosition(position)));

                switch (mAddressPositionInt) {
                    case 1:
                        mDefaultAddress = "Abode Valley,\n54 Kakkan Street, Potheri-603203";
                        break;

                    case 2:
                        mDefaultAddress = "Estancia,\nGrand Southern Trunk Rd, Potheri-603203";
                        break;

                    case 3:
                        mDefaultAddress = "SRM Green Pearl,\nAmman Koil Street, Potheri-603203";
                        break;

                    default:
                        mDefaultAddress = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSubmitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        mFlatNum = mFlatNoEditText.getText().toString();
        mFullAddress = mFullAddressEditText.getText().toString();

        if (mAddressPositionInt == (1) || mAddressPositionInt == (2) || mAddressPositionInt == (3)) {
            mInputAddress = mFlatNum;

        } else if (mAddressPositionInt == 4) {
            mInputAddress = mFullAddress;
        }

        Log.e("flat no", mFlatNum);

        if (!mInputAddress.equals("")) {
            mTotalAddress = mInputAddress + "\n" + mDefaultAddress;

            Intent intent = new Intent(this, AddressConfirmationInCart.class);
            intent.putExtra("Address", mTotalAddress);
            startActivity(intent);

        } else {
            Toast.makeText(getApplicationContext(), "House/Flat No. cannot be empty", Toast.LENGTH_LONG).show();
        }
    }
}

