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
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity { // Will show the string "data" that holds the results
    TextView textViewResult;

    Button buttonStart;

    String URL = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";

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

                                    //Get the Order JSON Array
                                    JSONArray OrderArray = response.getJSONArray("orders");

                                    //Tracing trough the Order array
                                    for (int order_index = 0; order_index < OrderArray.length(); order_index++) {

                                        //Get an order
                                        JSONObject Order = OrderArray.getJSONObject(order_index);

                                        //Get a line items array from an order
                                        JSONArray line_itemsArray = Order.getJSONArray("line_items");

                                        //Get total price for an order
                                        total_price_amount += Order.getDouble("total_price");

                                        //Tracing trough the line items array
                                        for (int line_index = 0; line_index < line_itemsArray.length(); line_index++) {

                                            //Get a line item
                                            JSONObject Item = line_itemsArray.getJSONObject(line_index);

                                            //Get the item title
                                            String item_title = Item.getString("title");

                                            if (item_title.equals("Awesome Bronze Bag")) {
                                                bronze_bag_count++;
                                            }

                                        }

                                    }

                                    textViewResult.append("Number of bronze bags :" + bronze_bag_count);
                                    textViewResult.append(" \n");
                                    textViewResult.append("Total Cost :" +String.format("%.2f", total_price_amount));

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

                requestQueue.add(JsonObjectR);
            }
        });


    }
}
