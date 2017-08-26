package first.alexander.com.androidvolleyparser;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class CustomerInfoFragment extends ListFragment {

    ListFragmentItemClickListener itemClickListener;

    final String URL = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";

    final int JSON_TIME_OUT = 15000; //Set JSON Request Connection Timeout

    /**
     * Interface for callback method. Will be invoked when an item in the
     * ListFragment is clicked.
     */
    public interface ListFragmentItemClickListener {
        void onListFragmentItemClick(String name);
    }

    /**
     * A callback function, executed when this fragment is attached to an activity
     *
     * @param activity - the activity attached on
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Ensures the activity implements ListFragmentItemClickListener
            itemClickListener = (ListFragmentItemClickListener) activity;
        } catch (Exception e) {
            Toast.makeText(activity.getBaseContext(), "Exception", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Set the adapter for the list of customers
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1);

        // Get the list of customers for the adapter
        JSONRequestGetCustomers(adapter);

        // Set the adapter to the list fragment
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        // Invokes the implementation of onListFragmentItemClick in the hosting activity
        itemClickListener.onListFragmentItemClick(l.getItemAtPosition(position).toString());

    }


    /**
     * JSON Volley Request to get all of the different customers and
     * add it to an adapter to be displayed on the list view.
     *
     * @param adapter - Adapter to be displayed on the list view
     */
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

                            // Sort customer by first name
                            Collections.sort(customers_list, new Comparator<String>() {
                                @Override
                                public int compare(String s1, String s2) {
                                    return s1.compareToIgnoreCase(s2);
                                }
                            });

                            // Clear and add the customer list into the adapter
                            final_adapter.clear();
                            final_adapter.addAll(customers_list);

                            // Notify the adapter to be updated
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
                                Toast.makeText(getActivity(), "Request Timeout Error!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Network Error. No Internet Connection!", Toast.LENGTH_SHORT).show();
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
