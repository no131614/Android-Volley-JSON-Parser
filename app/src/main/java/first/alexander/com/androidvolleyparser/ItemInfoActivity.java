package first.alexander.com.androidvolleyparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

public class ItemInfoActivity extends AppCompatActivity {

    // All static variables
    final String URL = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";

    final Context context = this;

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);

        ArrayList itemList = new ArrayList();

        ItemInfoListAdapter adapter = new ItemInfoListAdapter(ItemInfoActivity.this, itemList);
        JSONRequestGetProducts(adapter);
        list=(ListView)findViewById(R.id.item_listView);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.item_info_dialog);
                dialog.setTitle("Product Item Info");

                // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.item_TextViewDialog);
                text.setText("Android custom dialog example!");

                Button buttonClose = (Button) dialog.findViewById(R.id.buttonClose);
                // if button is clicked, close the custom dialog
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

    private void JSONRequestGetProducts(ItemInfoListAdapter adapter) {

        final ArrayList product_list = new ArrayList();
        final ItemInfoListAdapter final_adapter = adapter;


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

                            final_adapter.clear();
                            final_adapter.addAll(product_list);
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
    }


}

