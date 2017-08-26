package first.alexander.com.androidvolleyparser;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


/**
 * Activity of Customer Info Page
 * <p>
 * (Landscape orientation) consist of a list fragment containing a list of customers and displays
 * the customer information fragment upon selecting a customer from the list.
 * <p>
 * (Portrait orientation) consist of a list containing customers and move to another activity
 * that displays customer information upon selecting a customer from the list.
 *
 * @author Alexander Julianto (no131614)
 * @version 1.0
 * @since API 21
 */
public class CustomerActivity extends AppCompatActivity implements CustomerInfoFragment.ListFragmentItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        // Set an Icon in the app title bar
        getSupportActionBar().setIcon(R.drawable.customer_i_small);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
    }

    @Override
    public void onListFragmentItemClick(String name) {

        // Check the current android device orientation (Landscape or Portrait)
        int orientation = getResources().getConfiguration().orientation;

        // If the android device orientation is Landscape
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            // Begin: Set up FragmentManager and get previous fragment (if exist)
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment prevFrag = fragmentManager.findFragmentById(R.id.detail_fragment_container);
            // End: Set up FragmentManager and get previous fragment (if exist)

            // Need to remove any previous existing fragments
            if (prevFrag != null) {
                fragmentTransaction.remove(prevFrag);
            }

            // Instantiate new fragment CustomerDetailsFragment
            CustomerDetailsFragment customerDetailsFragment = new CustomerDetailsFragment();

            // Bundle object to pass data to fragments
            Bundle b = new Bundle();

            // Pass customer name to bundle
            b.putString("name", name);

            // Set the bundle object to the new fragment
            customerDetailsFragment.setArguments(b);

            // Adding the new fragment to transaction
            fragmentTransaction.add(R.id.detail_fragment_container, customerDetailsFragment);

            // Set fragment transaction
            fragmentTransaction.commit();

        } else { // If the android device orientation is Portrait or other

            // Begin: Go and send selected customer name to CustomerDetailsActivity
            Intent intent = new Intent(this, CustomerDetailsActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
            // End: Go and send selected customer name to CustomerDetailsActivity

        }
    }

}
