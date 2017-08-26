package first.alexander.com.androidvolleyparser;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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


/**
 * Main Activity of Android Volley Parser
 * <p>
 * This activity contains main implementation of the Dashboard
 *
 * @author Alexander Julianto (no131614)
 * @version 1.0
 * @since API 21
 */

public class MainActivity extends AppCompatActivity {

    // Set URL for JSON Volley Request
    final String URL = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";

    // Set JSON Request Connection Timeout (15 seconds)
    final int JSON_TIME_OUT = 15000;

    ImageButton imageButtonCustomerInfo;
    ImageButton imageButtonItemInfo;
    ImageButton imageButtonRefresh;
    ImageButton imageButtonExit;

    TextView tvNumItems;
    TextView tvPriceAmount;
    TextView tvFavCustomer;
    TextView tvTotalSpent;

    ProgressBar ProgressBarCustomer;
    ProgressBar ProgressBarItem;
    ProgressBar ProgressBarPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set an Icon in the app title bar
        getSupportActionBar().setIcon(R.drawable.online_shop);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

        ProgressBarCustomer = (ProgressBar) findViewById(R.id.progressBarCustomer);
        ProgressBarItem = (ProgressBar) findViewById(R.id.progressBarItem);
        ProgressBarPrice = (ProgressBar) findViewById(R.id.progressBarPrice);

        tvNumItems = (TextView) findViewById(R.id.textViewTotalNumItems);
        tvPriceAmount = (TextView) findViewById(R.id.textViewTotalPriceAmount);
        tvFavCustomer = (TextView) findViewById(R.id.textViewFavouriteCustomer);
        tvTotalSpent = (TextView) findViewById(R.id.textViewTotalSpent);

        // Begin: First JSON Volley Request for Dashboard
        JSONRequestTotalNumOfItemsAndPrice(tvPriceAmount, tvNumItems);
        JSONRequestGetFavouriteCustomer(tvFavCustomer, tvTotalSpent);
        // End: First JSON Volley Request for Dashboard


        // Begin: All Dashboard button implementation
        imageButtonCustomerInfo = (ImageButton) findViewById(R.id.imageButtonCustomerInfo);
        imageButtonCustomerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent customer_intent = new Intent(v.getContext(), CustomerActivity.class);
                startActivity(customer_intent);
            }

        });

        imageButtonItemInfo = (ImageButton) findViewById(R.id.imageButtonItemInfo);
        imageButtonItemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent customer_intent = new Intent(v.getContext(), ItemInfoActivity.class);
                startActivity(customer_intent);
            }

        });

        imageButtonRefresh = (ImageButton) findViewById(R.id.imageButtonRefresh);
        imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONRequestTotalNumOfItemsAndPrice(tvPriceAmount, tvNumItems);
                JSONRequestGetFavouriteCustomer(tvFavCustomer, tvTotalSpent);
            }

        });

        imageButtonExit = (ImageButton) findViewById(R.id.imageButtonExit);
        imageButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }

        });
        // End: All Dashboard button implementation

    }

    /**
     * JSON Volley Request to get number total price of all items and total number of items
     * and display on the specified text views.
     *
     * @param tvPrice - Text view to display the total price of all items
     * @param tvItem  - Text view to display total number of items
     */
    private void JSONRequestTotalNumOfItemsAndPrice(final TextView tvPrice, final TextView tvItem) {

        ProgressBarItem.setVisibility(View.VISIBLE);
        ProgressBarPrice.setVisibility(View.VISIBLE);

        JsonObjectRequest JsonObjectR = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            //Reset Amount
                            int item_count = 0;
                            double total_price_amount = 0;

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

                                        item_count += Item.getInt("quantity");
                                    }

                                    total_price_amount += Order.getDouble("total_price");

                                }

                            }

                            // Begin: Display total num items and price on text view
                            tvItem.setText(null);
                            tvItem.append("" + item_count);
                            tvPrice.setText(null);
                            tvPrice.append(String.format("%.2f", total_price_amount) + " CAD");
                            // End: Display total num items and price on text view

                            ProgressBarItem.setVisibility(View.INVISIBLE);
                            ProgressBarPrice.setVisibility(View.INVISIBLE);

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
                                ProgressBarItem.setVisibility(View.INVISIBLE);
                                ProgressBarPrice.setVisibility(View.INVISIBLE);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Network Error. No Internet Connection", Toast.LENGTH_LONG)
                                        .show();
                                tvItem.setText(null);
                                tvItem.append("Network Error");
                                tvPrice.setText(null);
                                tvPrice.append("Network Error");
                                ProgressBarItem.setVisibility(View.INVISIBLE);
                                ProgressBarPrice.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });

        JsonObjectR.setRetryPolicy(new DefaultRetryPolicy(JSON_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add to JSON request Queue
        JSONVolleyController.getInstance().addToRequestQueue(JsonObjectR);
    }

    /**
     * JSON Volley Request to get the favourite customer base on the top total spent
     * and display on the specified text views.
     *
     * @param tvFavCustomer - Text view to display the favourite customer
     * @param tvTotalSpent  - Text view to display the total spent of the favourite customer
     */
    private void JSONRequestGetFavouriteCustomer(final TextView tvFavCustomer, final TextView tvTotalSpent) {

        ProgressBarCustomer.setVisibility(View.VISIBLE);

        JsonObjectRequest JsonObjectR = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // Reset fields
                            double largest_total_spent = 0;
                            String favourite_customer = "Currently No One";

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
                                        JSONObject Customer = Order.getJSONObject("customer");// Customer object might not exist

                                        String name = Customer.getString("first_name") + " "
                                                + Customer.getString("last_name");

                                        double total_spent = Customer.getDouble("total_spent");

                                        // Getting the customer with the top total spent
                                        if (largest_total_spent < total_spent) {
                                            largest_total_spent = total_spent;
                                            favourite_customer = name;
                                        }


                                    } catch (JSONException JE) {
                                        // There is no customer object in the order
                                    }
                                }
                            }

                            // Begin: Display fav customer and total spent on text view
                            tvFavCustomer.setText(null);
                            tvFavCustomer.append(favourite_customer);
                            tvTotalSpent.setText(null);
                            tvTotalSpent.append("With Total Spent of " + largest_total_spent + " CAD");
                            // End: Display fav customer and total spent on text view

                            ProgressBarCustomer.setVisibility(View.INVISIBLE);

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
                                ProgressBarCustomer.setVisibility(View.INVISIBLE);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Network Error. No Internet Connection", Toast.LENGTH_LONG)
                                        .show();
                                tvFavCustomer.setText(null);
                                tvFavCustomer.append("Network Error");
                                tvTotalSpent.setText(null);
                                ProgressBarCustomer.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });

        JsonObjectR.setRetryPolicy(new DefaultRetryPolicy(JSON_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Send the JSON request
        JSONVolleyController.getInstance().addToRequestQueue(JsonObjectR);

    }

}
