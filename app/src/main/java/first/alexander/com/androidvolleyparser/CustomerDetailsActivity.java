package first.alexander.com.androidvolleyparser;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


/**
 * Activity of Customer Info Detail Page
 *
 * This Activity is to display customer information upon selecting the
 * customer name in the list and the current android device orientation
 * is portrait.
 *
 * @author Alexander Julianto (no131614)
 * @version 1.0
 * @since API 21
 */
public class CustomerDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        // Set an Icon in the app title bar
        getSupportActionBar().setIcon(R.drawable.customer_i_small);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

        // Begin: Set up FragmentManager and get previous fragment (if exist)
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment prevFrag = fragmentManager.findFragmentById(R.id.customer_details_fragment_container);
        // End: Set up FragmentManager and get previous fragment (if exist)

        // Need to remove any previous existing fragments
        if(prevFrag!=null) {
            fragmentTransaction.remove(prevFrag);
        }

        // Instantiate new fragment CustomerDetailsFragment
        CustomerDetailsFragment detailsFragment = new CustomerDetailsFragment();

        // Bundle object to pass data to fragments the selected customer from list
        Bundle b = new Bundle();

        // Pass customer name to bundle from previous activity
        b.putString("name", getIntent().getStringExtra("name"));

        // Set the bundle object to the new fragment
        detailsFragment.setArguments(b);

        // Adding the new fragment to transaction
        fragmentTransaction.add(R.id.customer_details_fragment_container, detailsFragment);

        // Set fragment transaction
        fragmentTransaction.commit();

    }
}
