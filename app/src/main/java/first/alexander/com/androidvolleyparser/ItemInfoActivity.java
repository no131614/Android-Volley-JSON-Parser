package first.alexander.com.androidvolleyparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
import org.json.JSONObject;



/**
 * Activity of Item Stats Information page
 *
 * This activity consists of list view of all of the items with a custom dialog displaying
 * the item information.
 *
 * @author Alexander Julianto (no131614)
 * @version 1.0
 * @since API 21
 */
public class ItemInfoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    final String URL = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";

    // Set JSON Request Connection Timeout (15 seconds)
    final int JSON_TIME_OUT = 15000;

    final Context context = this;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        getSupportActionBar().setIcon(R.drawable.item_i);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

        // SwipeRefreshLayout to refresh the items list view
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_item_info);
        swipeRefreshLayout.setOnRefreshListener(this);

        ArrayList itemList = new ArrayList();
        final ItemInfoListAdapter adapter = new ItemInfoListAdapter(ItemInfoActivity.this, itemList);

        // Start a refresh onCreate and initialize JSON Volley Request for item list view
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        JSONRequestGetProducts(adapter);
                                    }
                                }
        );

        list = (ListView) findViewById(R.id.item_listView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // Initialize custom dialog for item information
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.item_info_dialog);
                dialog.setTitle("Product Item Info");

                // Set the custom dialog text view for item information
                TextView tvItemInfo = (TextView) dialog.findViewById(R.id.item_TextViewDialog);

                // Get the item information to display on the text view
                JSONRequestGetItemInfo(parent.getItemAtPosition(position).toString(), tvItemInfo);

                Button buttonClose = (Button) dialog.findViewById(R.id.buttonClose);
                buttonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });


    }

    /**
     * JSON Volley Request to get all of the different item products and
     * add it to an adapter to be displayed on the list view.
     *
     * @param adapter - Adapter to be displayed on the list view
     */
    private void JSONRequestGetProducts(ItemInfoListAdapter adapter) {

        final ArrayList product_list = new ArrayList();
        final ItemInfoListAdapter final_adapter = adapter;

        swipeRefreshLayout.setRefreshing(true);

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

                            // Sort customer by first name
                            Collections.sort(product_list, new Comparator<String>() {
                                @Override
                                public int compare(String s1, String s2) {
                                    return s1.compareToIgnoreCase(s2);
                                }
                            });

                            // Begin: Clear and add the product list into the adapter
                            final_adapter.clear();
                            final_adapter.addAll(product_list);
                            final_adapter.notifyDataSetChanged();
                            // End: Clear and add the product list into the adapter


                            swipeRefreshLayout.setRefreshing(false);

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
                                swipeRefreshLayout.setRefreshing(false);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Network Error. No Internet Connection", Toast.LENGTH_LONG)
                                        .show();
                                swipeRefreshLayout.setRefreshing(false);
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
     * JSON Volley Request to get the information of the item passed into the argument
     * and display it on the the text view.
     *
     * @param item - String contain the item name
     * @param tvItemInfo - 
     */
    private void JSONRequestGetItemInfo(String item, final TextView tvItemInfo) {


        final String ITEM_NAME = item;
        final Map item_info = new HashMap();

        JsonObjectRequest JsonObjectR = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            //Reset Amount
                            int item_count = 0;
                            double total_price = 0;

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

                                            // Begin: Get item info and put on map
                                            item_count += Item.getInt("quantity");
                                            total_price += (Item.getDouble("price") * Item.getInt("quantity"));

                                            item_info.put("item_count", item_count);
                                            item_info.put("product_id", Item.getString("product_id"));
                                            item_info.put("total_price", total_price);
                                            // End: Get customer info and put on map

                                        }

                                    }

                                }

                            }

                            // Begin: Display item information into the text view
                            tvItemInfo.setText(null);
                            tvItemInfo.append(" \n Name :" + ITEM_NAME);
                            tvItemInfo.append(" \n Product ID :" + item_info.get("product_id"));
                            tvItemInfo.append(" \n Number of Items Sold :" + item_info.get("item_count"));
                            tvItemInfo.append(" \n Total Price :" +
                                    String.format("%.2f", item_info.get("total_price")) + " CAD");
                            // End: Display item information into the text view

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

        // Add to JSON request Queue
        JSONVolleyController.getInstance().addToRequestQueue(JsonObjectR);
    }


    /**
     * Method inherited from SwipeRefreshLayout.OnRefreshListener. Executed on
     * SwipeRefreshLayout refresh
     */
    @Override
    public void onRefresh() {
        // Begin: Refresh the item list on swipe down
        ArrayList itemList = new ArrayList();
        final ItemInfoListAdapter adapter = new ItemInfoListAdapter(ItemInfoActivity.this, itemList);
        JSONRequestGetProducts(adapter);
        list = (ListView) findViewById(R.id.item_listView);
        list.setAdapter(adapter);
        // End: Refresh the item list on swipe down
    }
}


