package first.alexander.com.androidvolleyparser;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.v7.util.AsyncListUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;


public class CustomerInfoFragment extends ListFragment{

    ListFragmentItemClickListener ItemClickListener;

    final String URL = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";
    final int JSON_TIME_OUT = 15000; //Set JSON Request Connection Timeout

    /** An interface for defining the callback method */
    public interface ListFragmentItemClickListener {
        /** This method will be invoked when an item in the ListFragment is clicked */
        void onListFragmentItemClick(int position);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        /** Data source for the ListFragment */
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1);
        JSONRequestGetCustomers(adapter);


        /** Setting the data source to the ListFragment */
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        /** Invokes the implementation of the method onListFragmentItemClick in the hosting activity */
        //ItemClickListener.onListFragmentItemClick(position);

    }


    private void JSONRequestGetCustomers(ArrayAdapter adapter) {

        final ArrayList customers_list = new ArrayList();
        final ArrayAdapter final_adapter = adapter;

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

                                    // Get customer info
                                    try {
                                        JSONObject Customer = Order.getJSONObject("customer");// Customer might not exist

                                        // Get customer full name
                                        String name = Customer.getString("first_name") + " " + Customer.getString("last_name");

                                        // Check for duplicates and add the name into the list
                                        if (!customers_list.contains(name)) {
                                            customers_list.add(name);
                                            System.out.println(name);
                                        }


                                    } catch (JSONException JE) {

                                        // Get name from order if customer field does not exist
                                        String name = Order.getString("name");
                                        if (!customers_list.contains(name)) {
                                            customers_list.add(name);
                                        }
                                    }

                                }

                            }

                            final_adapter.clear();
                            final_adapter.addAll(customers_list);
                            final_adapter.notifyDataSetChanged();

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
                                /*Toast.makeText(getApplicationContext(),
                                        "Request Timeout Error!", Toast.LENGTH_LONG)
                                        .show();*/
                            } else {
                               /* Toast.makeText(getApplicationContext(),
                                        "Network Error. No Internet Connection", Toast.LENGTH_LONG)
                                        .show();*/
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
