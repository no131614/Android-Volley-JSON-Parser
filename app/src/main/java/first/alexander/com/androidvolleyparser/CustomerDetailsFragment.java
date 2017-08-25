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


public class CustomerDetailsFragment extends Fragment {

    final String URL = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";
    final int JSON_TIME_OUT = 15000; //Set JSON Request Connection Timeout

    ProgressBar ProgressBarCustomerDetails;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        /** Inflating the layout country_details_fragment_layout to the view object v */
        View v = inflater.inflate(R.layout.customer_details_fragment_layout, null);

        ProgressBarCustomerDetails = (ProgressBar) v.findViewById(R.id.progressBarCustomerDetails);

        /** Getting the textview object of the layout to set the details */
        TextView tv = (TextView) v.findViewById(R.id.customer_details);

        /** Getting the bundle object passed from MainActivity ( in Landscape mode )  or from
         *  CountryDetailsActivity ( in Portrait Mode )
         * */
        Bundle b = getArguments();

        /** Getting the clicked item's position and setting corresponding details in the textview of the detailed fragment */
        JSONRequestGetCustomerInfo(b.getString("name"), tv);

        return v;
    }


    private void JSONRequestGetCustomerInfo(String name, final TextView textView) {

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
                                            customer_info.put("email",Customer.getString("email") == "null" ? "-" : Customer.getString("email"));
                                            customer_info.put("phone", Customer.getString("phone") == "null" ? "-" : Customer.getString("phone"));
                                            customer_info.put("note", Customer.getString("note") == "null" ? "-" : Customer.getString("note"));
                                            customer_info.put("total_spent", Customer.getString("total_spent") == "null" ? "-" : Customer.getString("total_spent"));
                                            // End: Get customer info and put on map

                                        }

                                    } catch (JSONException JE) {
                                        // Need to catch error if no customer info field
                                    }
                                }
                            }

                            textView.setText(null);
                            textView.append(" \n First Name: " + FIRST_NAME);
                            textView.append(" \n Last Name: " + LAST_NAME);
                            textView.append(" \n ID: " + customer_info.get("id"));
                            textView.append(" \n Email: " + customer_info.get("email"));
                            textView.append(" \n Phone: " + customer_info.get("phone"));
                            textView.append(" \n Note: " + customer_info.get("note"));
                            textView.append(" \n Total price : " + String.format("%.2f", customer_info.get("total_price")) + " CAD");
                            textView.append(" \n Total spent: " + customer_info.get("total_spent") + " CAD");

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
