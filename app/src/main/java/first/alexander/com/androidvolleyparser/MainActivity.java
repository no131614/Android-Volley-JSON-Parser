package first.alexander.com.androidvolleyparser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.app.TabActivity;


public class MainActivity extends AppCompatActivity {
    TextView textViewResult;

    final String URL = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";
    String name_number = null;

    Button buttonStart;


    final int JSON_TIME_OUT = 15000; //Set JSON Request Connection Timeout

    int item_count = 0;
    double total_price_amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent customer_intent = new Intent(v.getContext(), CustomerActivity.class);
                startActivity(customer_intent);
            }

        });





    }


    private void JSONRequestNumOfItems(String item) {

        final String ITEM_NAME = item;

        JsonObjectRequest JsonObjectR = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            //Reset Amount
                            item_count = 0;

                            // Get the Order JSON Array
                            JSONArray OrderArray = response.getJSONArray("orders");

                            // Tracing trough the Order array
                            for (int order_index = 0; order_index < OrderArray.length(); order_index++) {

                                // Get an order
                                JSONObject Order = OrderArray.getJSONObject(order_index);

                                // Make sure order is not cancelled
                                if (Order.getString("cancel_reason").equals("null")) {

                                    // Get a line items array from an order
                                    JSONArray line_itemsArray = Order.getJSONArray("line_items");

                                    // Tracing trough the line items array
                                    for (int line_index = 0; line_index < line_itemsArray.length(); line_index++) {

                                        // Get a line item
                                        JSONObject Item = line_itemsArray.getJSONObject(line_index);

                                        // Get the item title
                                        String item_title = Item.getString("title");

                                        if (item_title.equals(ITEM_NAME)) {
                                            item_count += Item.getInt("quantity");
                                        }

                                    }

                                }

                            }

                            textViewResult.append("Number of bronze bags :" + item_count);
                            textViewResult.append(" \n");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");

                        // Handle network related Errors
                        if (error.networkResponse == null) {

                            // Handle network Timeout error
                            if (error.getClass().equals(TimeoutError.class)) {
                                Toast.makeText(getApplicationContext(),
                                        "Request Timeout Error!", Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Network Error. No Internet Connection", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }
                });

        JsonObjectR.setRetryPolicy(new DefaultRetryPolicy(JSON_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Send the JSON request
        JSONVolleyController.getInstance().addToRequestQueue(JsonObjectR);
    }

    private void JSONRequestTotalPrice(String first_name, String last_name) {

        final String FIRST_NAME = first_name;
        final String LAST_NAME = last_name;
        JsonObjectRequest JsonObjectR = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // Reset Amount
                            total_price_amount = 0;

                            // Get the Order JSON Array
                            JSONArray OrderArray = response.getJSONArray("orders");

                            // Tracing trough the Order array
                            for (int order_index = 0; order_index < OrderArray.length(); order_index++) {

                                // Get an order
                                JSONObject Order = OrderArray.getJSONObject(order_index);

                                // Make sure order is not cancelled
                                if (Order.getString("cancel_reason").equals("null")) {

                                    // Get customer info
                                    try {
                                        JSONObject Customer = Order.getJSONObject("customer");// Customer might not exist

                                        // Begin: Check if it is Napoleon Batz and calculate his total price
                                        String first_name = Customer.getString("first_name");
                                        String last_name = Customer.getString("last_name");
                                        if (first_name.equals(FIRST_NAME) && last_name.equals(LAST_NAME)) {

                                            //Get total price for an order
                                            total_price_amount += Order.getDouble("total_price");

                                        }
                                        // End: Check if it is Napoleon Batz and calculate his total price
                                    } catch (JSONException JE) {
                                        name_number = Order.getString("name");
                                    }

                                }

                            }

                            textViewResult.append(FIRST_NAME + " " + LAST_NAME + " Total Cost :" +
                                    String.format("%.2f", total_price_amount));
                            textViewResult.append(" \n");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");

                        // Handle network related Errors
                        if (error.networkResponse == null) {

                            // Handle network Timeout error
                            if (error.getClass().equals(TimeoutError.class)) {
                                Toast.makeText(getApplicationContext(),
                                        "Request Timeout Error!", Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Network Error. No Internet Connection", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }
                });

        JsonObjectR.setRetryPolicy(new DefaultRetryPolicy(JSON_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Send the JSON request
        JSONVolleyController.getInstance().addToRequestQueue(JsonObjectR);
    }

    private ArrayList JSONRequestGetProducts() {

        final ArrayList product_list = new ArrayList();

        JsonObjectRequest JsonObjectR = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // Get the Order JSON Array
                            JSONArray OrderArray = response.getJSONArray("orders");

                            // Tracing trough the Order array
                            for (int order_index = 0; order_index < OrderArray.length(); order_index++) {

                                // Get an order
                                JSONObject Order = OrderArray.getJSONObject(order_index);

                                // Make sure order is not cancelled
                                if (Order.getString("cancel_reason").equals("null")) {

                                    // Get a line items array from an order
                                    JSONArray line_itemsArray = Order.getJSONArray("line_items");


                                    // Tracing trough the line items array
                                    for (int line_index = 0; line_index < line_itemsArray.length(); line_index++) {

                                        // Get a line item
                                        JSONObject Item = line_itemsArray.getJSONObject(line_index);

                                        // Get the item title
                                        String item_title = Item.getString("title");

                                        // Check for duplicates and add the name into the list
                                        if (!product_list.contains(item_title)) {
                                            product_list.add(item_title);
                                            System.out.println(item_title);
                                        }

                                    }

                                }

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");

                        // Handle network related Errors
                        if (error.networkResponse == null) {

                            // Handle network Timeout error
                            if (error.getClass().equals(TimeoutError.class)) {
                                Toast.makeText(getApplicationContext(),
                                        "Request Timeout Error!", Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Network Error. No Internet Connection", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                        return;
                    }
                });


        // Send the JSON request
        JSONVolleyController.getInstance().addToRequestQueue(JsonObjectR);

        return product_list;
    }

    private Map JSONRequestGetCustomerInfo(String first_name, String last_name) {

        final String FIRST_NAME = first_name;
        final String LAST_NAME = last_name;

        // Map will contain customer info
        final Map customer_info = new HashMap();

        JsonObjectRequest JsonObjectR = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // Reset Amount
                            total_price_amount = 0;

                            // Get the Order JSON Array
                            JSONArray OrderArray = response.getJSONArray("orders");

                            // Tracing trough the Order array
                            for (int order_index = 0; order_index < OrderArray.length(); order_index++) {

                                // Get an order
                                JSONObject Order = OrderArray.getJSONObject(order_index);

                                // Make sure order is not cancelled
                                if (Order.getString("cancel_reason").equals("null")) {

                                    // Get customer info
                                    try {
                                        JSONObject Customer = Order.getJSONObject("customer");// Customer might not exist


                                        String first_name = Customer.getString("first_name");
                                        String last_name = Customer.getString("last_name");
                                        if (first_name.equals(FIRST_NAME) && last_name.equals(LAST_NAME)) {

                                            // Begin: Get customer info and put on map
                                            total_price_amount += Order.getDouble("total_price");
                                            customer_info.put("total_price",total_price_amount);
                                            customer_info.put("id",Customer.getString("id"));
                                            customer_info.put("email",Customer.getString("email"));
                                            customer_info.put("phone",Customer.getString("phone"));
                                            customer_info.put("note",Customer.getString("note"));
                                            customer_info.put("total_spent",Customer.getString("total_spent"));
                                            // End: Get customer info and put on map

                                        }

                                    } catch (JSONException JE) {
                                        name_number = Order.getString("name");
                                    }
                                }
                            }

                            System.out.println("Customer id: " + customer_info.get("id"));
                            System.out.println("Customer email: " + customer_info.get("email"));
                            System.out.println("Customer phone: " + customer_info.get("phone"));
                            System.out.println("Customer note: "+ customer_info.get("note"));
                            System.out.println("Customer total price: " + customer_info.get("total_price"));
                            System.out.println("Customer total spent: " + customer_info.get("total_spent"));


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");

                        // Handle network related Errors
                        if (error.networkResponse == null) {

                            // Handle network Timeout error
                            if (error.getClass().equals(TimeoutError.class)) {
                                Toast.makeText(getApplicationContext(),
                                        "Request Timeout Error!", Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Network Error. No Internet Connection", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }
                });

        JsonObjectR.setRetryPolicy(new DefaultRetryPolicy(JSON_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Send the JSON request
        JSONVolleyController.getInstance().addToRequestQueue(JsonObjectR);

        return customer_info;
    }

    private Map JSONRequestGetItemInfo(String item) {

        final String ITEM_NAME  = item;
  

        // Map will contain item info
        final Map item_info = new HashMap();

        JsonObjectRequest JsonObjectR = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            
                            // Get the Order JSON Array
                            JSONArray OrderArray = response.getJSONArray("orders");

                            // Tracing trough the Order array
                            for (int order_index = 0; order_index < OrderArray.length(); order_index++) {

                                // Get an order
                                JSONObject Order = OrderArray.getJSONObject(order_index);

                                // Make sure order is not cancelled
                                if (Order.getString("cancel_reason").equals("null")) {

                                    // Get a line items array from an order
                                    JSONArray line_itemsArray = Order.getJSONArray("line_items");

                                    // Tracing trough the line items array
                                    for (int line_index = 0; line_index < line_itemsArray.length(); line_index++) {

                                        // Get a line item
                                        JSONObject Item = line_itemsArray.getJSONObject(line_index);

                                        // Get the item title
                                        String item_title = Item.getString("title");

                                        if (item_title.equals(ITEM_NAME)) {
                                            item_count += Item.getInt("quantity");
                                            item_info.put("item_count",item_count);
                                            item_info.put("product_id",Item.getString("product_id"));
                                            item_info.put("price",Item.getString("price"));
                                            item_info.put("variant_title",Item.getString("variant_title"));
                                            item_info.put("grams",Item.getString("grams"));
                                            item_info.put("vendor",Item.getString("vendor"));

                                        }

                                    }
                                }
                            }

                            System.out.println("Item product id: " + item_info.get("product_id"));
                            System.out.println("Item price: " + item_info.get("price"));
                            System.out.println("Item variant title: " + item_info.get("variant_title"));
                            System.out.println("Item grams: " + item_info.get("grams"));
                            System.out.println("Item vendor: " + item_info.get("vendor"));
                            System.out.println("Item count: " + item_info.get("item_count"));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");

                        // Handle network related Errors
                        if (error.networkResponse == null) {

                            // Handle network Timeout error
                            if (error.getClass().equals(TimeoutError.class)) {
                                Toast.makeText(getApplicationContext(),
                                        "Request Timeout Error!", Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Network Error. No Internet Connection", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }
                });

        JsonObjectR.setRetryPolicy(new DefaultRetryPolicy(JSON_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Send the JSON request
        JSONVolleyController.getInstance().addToRequestQueue(JsonObjectR);

        return item_info;
    }


}
