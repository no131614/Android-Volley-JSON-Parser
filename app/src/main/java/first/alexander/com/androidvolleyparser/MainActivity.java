package first.alexander.com.androidvolleyparser;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView textViewResult;

    Button buttonStart;

    String URL = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";
    String name_number = null;

    RequestQueue requestQueue;

    int bronze_bag_count = 0;
    double total_price_amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Creates the Volley request queue
        requestQueue = Volley.newRequestQueue(this);


        textViewResult = (TextView) findViewById(R.id.textViewResult);

        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsonObjectRequest JsonObjectR = new JsonObjectRequest
                        (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    //Reset Amount
                                    bronze_bag_count = 0;
                                    total_price_amount = 0;

                                    //Get the Order JSON Array
                                    JSONArray OrderArray = response.getJSONArray("orders");

                                    //Tracing trough the Order array
                                    for (int order_index = 0; order_index < OrderArray.length(); order_index++) {

                                        //Get an order
                                        JSONObject Order = OrderArray.getJSONObject(order_index);

                                        //Get a line items array from an order
                                        JSONArray line_itemsArray = Order.getJSONArray("line_items");

                                        //Get customer info
                                        try {
                                            JSONObject Customer = Order.getJSONObject("customer");// Customer might not exist

                                            // Begin: Check if it is Napoleon Batz and calculate his total price
                                            String first_name = Customer.getString("first_name");
                                            String last_name = Customer.getString("last_name");
                                            if (first_name.equals("Napoleon") && last_name.equals("Batz")) {

                                                //Get total price for an order
                                                total_price_amount += Order.getDouble("total_price");

                                            }
                                            // End: Check if it is Napoleon Batz and calculate his total price
                                        } catch (JSONException JE) {

                                            name_number = Order.getString("name");

                                        }


                                        //Tracing trough the line items array
                                        for (int line_index = 0; line_index < line_itemsArray.length(); line_index++) {

                                            //Get a line item
                                            JSONObject Item = line_itemsArray.getJSONObject(line_index);

                                            //Get the item title
                                            String item_title = Item.getString("title");

                                            if (item_title.equals("Awesome Bronze Bag")) {
                                                bronze_bag_count += Item.getInt("quantity");
                                            }

                                        }

                                    }

                                    textViewResult.setText(null);
                                    textViewResult.append("Number of bronze bags :" + bronze_bag_count);
                                    textViewResult.append(" \n");
                                    textViewResult.append("Napoleon Batz Total Cost :" + String.format("%.2f", total_price_amount));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("VOLLEY", "ERROR");
                            }
                        });

                // Send the JSON request
                JSONVolleyController.getInstance().addToRequestQueue(JsonObjectR);
            }
        });


    }
}
