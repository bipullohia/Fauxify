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

public class MyOrderDetails extends AppCompatActivity {

    Toolbar toolbar;

    private ArrayList<CurrentOrder> dishesList = new ArrayList<>();
    private MyOrderDishesAdapter myOrderDishesAdapter;

    String customeremail, dishesdata, orderconfirmed, orderdelivered;
    private RecyclerView recyclerView;
    TextView customerAddress, orderId, totalPrice, totalItemPrice, totalItems, orderTime, deliveryTime, restName, deliveryFee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_details);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_orderdetails);

        myOrderDishesAdapter = new MyOrderDishesAdapter(dishesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myOrderDishesAdapter);

        toolbar = (Toolbar) findViewById(R.id.toolbar_cartactivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        customerAddress = (TextView) findViewById(R.id.orderdetails_deliveryaddress);
        orderId = (TextView) findViewById(R.id.orderdetails_orderid);
        orderTime = (TextView) findViewById(R.id.orderdetails_ordertime);
        totalItemPrice = (TextView) findViewById(R.id.orderdetails_totalitemsprice);
        //totalItems = (TextView) findViewById(R.id.orderdetails_totalitems);
        totalPrice = (TextView) findViewById(R.id.orderdetails_totalprice);
        deliveryTime = (TextView) findViewById(R.id.deliverytime);
        restName = (TextView) findViewById(R.id.orderdetails_restname);
        deliveryFee = (TextView) findViewById(R.id.orderdetails_deliveryfee);

        customerAddress.setText(getIntent().getStringExtra("customeraddress"));
        orderId.setText(getIntent().getStringExtra("orderid"));
        orderTime.setText(getIntent().getStringExtra("ordertime"));
        totalItemPrice.setText(getIntent().getStringExtra("totalitemprice"));
        totalPrice.setText(getIntent().getStringExtra("totalprice"));
        //totalItems.setText(getIntent().getStringExtra("totalitems"));
        restName.setText(getIntent().getStringExtra("RestName"));
        deliveryFee.setText(getIntent().getStringExtra("deliveryfee"));
        customeremail = getIntent().getStringExtra("ordercustemail");
        dishesdata = getIntent().getStringExtra("customerorder");
        orderconfirmed = getIntent().getStringExtra("orderconfirmed");
        orderdelivered = getIntent().getStringExtra("orderdelivered");

        try {
            JSONArray jsonArray = new JSONArray(dishesdata);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CurrentOrder currentOrder = new CurrentOrder(jsonObject.getString("dishname"), jsonObject.getString("dishprice"),
                        jsonObject.getInt("dishquantity"));
                dishesList.add(currentOrder);

            }

            myOrderDishesAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
