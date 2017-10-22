package com.example.bipul.fauxify;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MyOrderDetailsActivity extends AppCompatActivity {

    Toolbar mToolbar;
    private ArrayList<CurrentOrder> mDishesList = new ArrayList<>();

    String mCustomerEmail, mDishesData, mOrderConfirmed, mOrderDelivered;
    TextView mCustomerAddressTextView, mOrderIdTextView, mTotalPriceTextView, mTotalItemPriceTextView,
            mOrderTimeTextView, mDeliveryTimeTextView, mRestNameTextView, mDeliveryFeeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_details);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_orderdetails);

        MyOrderDishesAdapter myOrderDishesAdapter = new MyOrderDishesAdapter(mDishesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myOrderDishesAdapter);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_myorder_details);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.order_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCustomerAddressTextView = (TextView) findViewById(R.id.orderdetails_deliveryaddress);
        mOrderIdTextView = (TextView) findViewById(R.id.orderdetails_orderid);
        mOrderTimeTextView = (TextView) findViewById(R.id.orderdetails_ordertime);
        mTotalItemPriceTextView = (TextView) findViewById(R.id.orderdetails_totalitemsprice);
        mTotalPriceTextView = (TextView) findViewById(R.id.orderdetails_totalprice);
        mDeliveryTimeTextView = (TextView) findViewById(R.id.delivery_time);
        mRestNameTextView = (TextView) findViewById(R.id.orderdetails_restname);
        mDeliveryFeeTextView = (TextView) findViewById(R.id.orderdetails_deliveryfee);

        mCustomerAddressTextView.setText(getIntent().getStringExtra("customeraddress"));
        mOrderIdTextView.setText(getIntent().getStringExtra("orderid"));
        mOrderTimeTextView.setText(getIntent().getStringExtra("ordertime"));
        mTotalItemPriceTextView.setText(getIntent().getStringExtra("totalitemprice"));
        mTotalPriceTextView.setText(getIntent().getStringExtra("totalprice"));
        mRestNameTextView.setText(getIntent().getStringExtra("RestName"));
        mDeliveryFeeTextView.setText(getIntent().getStringExtra("deliveryfee"));
        mCustomerEmail = getIntent().getStringExtra("ordercustemail");
        mDishesData = getIntent().getStringExtra("customerorder");
        mOrderConfirmed = getIntent().getStringExtra("orderconfirmed");
        mOrderDelivered = getIntent().getStringExtra("orderdelivered");

        try {
            JSONArray jsonArray = new JSONArray(mDishesData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CurrentOrder currentOrder = new CurrentOrder(jsonObject.getString("dishname"), jsonObject.getString("dishprice"),
                        jsonObject.getInt("dishquantity"));
                mDishesList.add(currentOrder);
            }
            myOrderDishesAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
