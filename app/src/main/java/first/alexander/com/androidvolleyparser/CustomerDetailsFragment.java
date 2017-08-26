package first.alexander.com.androidvolleyparser;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.Map;


/**
 * CustomerDetailsFragment.java - an android fragment class to display customer information.
 *
 * @author Alexander Julianto (no131614)
 */
public class CustomerDetailsFragment extends Fragment {

    final String URL = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";

    final int JSON_TIME_OUT = 15000; //Set JSON Request Connection Timeout

    ProgressBar ProgressBarCustomerDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.customer_details_fragment_layout, null);

        ProgressBarCustomerDetails = (ProgressBar) v.findViewById(R.id.progressBarCustomerDetails);

        TextView tvCustomerInfo = (TextView) v.findViewById(R.id.customer_details);

        // Passed bundle object to get customer name
        Bundle b = getArguments();

        // Get the customer info and display on the text view
        JSONRequestGetCustomerInfo(b.getString("name"), tvCustomerInfo);

        return v;
    }


    /**
     * JSON Volley Request to get a customer information base on their name
     * and display on the specified text view
     *
     * @param name           - String name of the customer
     * @param tvCustomerInfo - Text view to display customer info
     */
    private void JSONRequestGetCustomerInfo(String name, final TextView tvCustomerInfo) {

        ProgressBarCustomerDetails.setVisibility(View.VISIBLE);

        // Begin: Parse first name and last name
        String lastName = "";
        String firstName = "";
        if (name.split("\\w+").length > 1) {

            lastName = name.substring(name.lastIndexOf(" ") + 1);
            firstName = name.substring(0, name.lastIndexOf(' '));
        } else {
            firstName = name;
        }

        final String FIRST_NAME = firstName;
        final String LAST_NAME = lastName;
        // End: Parse first name and last name

        final Map customer_info = new HashMap();

        JsonObjectRequest JsonObjectR = new JsonObjectRequest
                (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // Reset Amount
                            double total_price_amount = 0;

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
                                        JSONObject Customer = Order.getJSONObject("customer");// Customer info might not exist

                                        String first_name = Customer.getString("first_name");
                                        String last_name = Customer.getString("last_name");
                                        if (first_name.equals(FIRST_NAME) && last_name.equals(LAST_NAME)) {

                                            // Begin: Get customer info and put on map
                                            total_price_amount += Order.getDouble("total_price");
                                            customer_info.put("total_price", total_price_amount);
                                            customer_info.put("id", Customer.getString("id") == "null" ? "-" : Customer.getString("id"));
                                            customer_info.put("email", Customer.getString("email") == "null" ? "-" : Customer.getString("email"));
                                            customer_info.put("phone", Customer.getString("phone") == "null" ? "-" : Customer.getString("phone"));
                                            customer_info.put("note", Customer.getString("note") == "null" ? "-" : Customer.getString("note"));
                                            customer_info.put("total_spent",
                                                    Customer.getString("total_spent") == "null" ? "-" : Customer.getString("total_spent"));
                                            // End: Get customer info and put on map

                                        }

                                    } catch (JSONException JE) {
                                        // Need to catch error if no customer info field
                                    }
                                }
                            }

                            // Begin: Display customer info on text view
                            tvCustomerInfo.setText(null);
                            tvCustomerInfo.append(" \n First Name: " + FIRST_NAME);
                            tvCustomerInfo.append(" \n Last Name: " + LAST_NAME);
                            tvCustomerInfo.append(" \n ID: " + customer_info.get("id"));
                            tvCustomerInfo.append(" \n Email: " + customer_info.get("email"));
                            tvCustomerInfo.append(" \n Phone: " + customer_info.get("phone"));
                            tvCustomerInfo.append(" \n Note: " + customer_info.get("note"));
                            tvCustomerInfo.append(" \n Total price : " +
                                    String.format("%.2f", customer_info.get("total_price")) + " CAD");
                            tvCustomerInfo.append(" \n Total spent: " + customer_info.get("total_spent") + " CAD");
                            // End: Display customer info on text view

                            ProgressBarCustomerDetails.setVisibility(View.INVISIBLE);


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
                                ProgressBarCustomerDetails.setVisibility(View.INVISIBLE);

                            } else {
                                Toast.makeText(getActivity(), "Network Error. No Internet Connection!", Toast.LENGTH_SHORT).show();
                                ProgressBarCustomerDetails.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });

        JsonObjectR.setRetryPolicy(new DefaultRetryPolicy(JSON_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add to JSON request Queue
        JSONVolleyController.getInstance().addToRequestQueue(JsonObjectR);

    }


}
