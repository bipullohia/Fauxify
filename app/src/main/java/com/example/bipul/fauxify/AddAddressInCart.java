package com.example.bipul.fauxify;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Bipul Lohia on 9/17/2016.
 */
public class AddAddressInCart extends AppCompatActivity implements View.OnClickListener {


    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    EditText flatNo, fullAdress;
    Toolbar toolbar;
    String flatNum, fullAdd;
    Button submitButton;
    Integer addPosition;
    String inputAddress, defaultAddress, totalAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_addressinfo_layout);

        toolbar =(Toolbar) findViewById(R.id.toolbar_addaddressinfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Address");

        flatNo = (EditText) findViewById(R.id.flat_no);
        fullAdress = (EditText) findViewById(R.id.full_address);
        submitButton = (Button) findViewById(R.id.submit_button);

        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.Areas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                addPosition = position;
                Log.e("Position", (String) parent.getItemAtPosition(position));

                if (position == 0) {
                    flatNo.setVisibility(View.GONE);
                    fullAdress.setVisibility(View.GONE);
                    submitButton.setVisibility(View.GONE);
                } else if ((position == 1) || (position == 2) || (position == 3)) {
                    flatNo.setVisibility(View.VISIBLE);
                    fullAdress.setVisibility(View.GONE);
                    submitButton.setVisibility(View.VISIBLE);

                } else if (((position == 4))) {
                    fullAdress.setVisibility(View.VISIBLE);
                    flatNo.setVisibility(View.GONE);
                    submitButton.setVisibility(View.VISIBLE);
                }


                Log.e("this is selected", String.valueOf(parent.getItemAtPosition(position)));

                switch (addPosition) {
                    case 1:
                        defaultAddress = "Abode Valley,\n54 Kakkan Street,\nPotheri, Kanchipuram,\nTamil Nadu 603203";
                        break;

                    case 2:
                        defaultAddress = "Estancia,\nGrand Southern Trunk Rd,\nPotheri, Guduvanchery,\nTamil Nadu 603203";
                        break;

                    case 3:
                        defaultAddress = "SRM Green Pearl,\nAmman Koil Street,\nPotheri, Guduvanchery,\nTamil Nadu 603203";
                        break;
                    default:
                        defaultAddress = "";
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        flatNum = flatNo.getText().toString();
        fullAdd = fullAdress.getText().toString();

        if (addPosition == (1) || addPosition == (2) || addPosition == (3)) {
            inputAddress = flatNum;
        } else if (addPosition == 4) {
            inputAddress = fullAdd;
        }
        Log.e("flat no", flatNum);

        totalAddress = inputAddress + "\n" + defaultAddress;

        Intent intent = new Intent(this, AddressConfirmationInCart.class);
        intent.putExtra("Address", totalAddress);
        startActivity(intent);

    }
}

