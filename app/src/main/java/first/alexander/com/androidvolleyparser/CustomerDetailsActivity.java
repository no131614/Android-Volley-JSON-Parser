package first.alexander.com.androidvolleyparser;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CustomerDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);


        /** Setting the layout for this activity */
        setContentView(R.layout.activity_customer_details);

        /** Getting the fragment manager for fragment related operations */
        FragmentManager fragmentManager = getFragmentManager();

        /** Getting the fragment transaction object, which can be used to add, remove or replace a fragment */
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

///////////////// Need To Delete previous fragment !!!!
        Fragment prevFrag = fragmentManager.findFragmentById(R.id.customer_details_fragment_container);

        /** Remove the existing detailed fragment object if it exists */
        if(prevFrag!=null) {
            fragmentTransaction.remove(prevFrag);
        }
/////////////////

        /** Instantiating the fragment CountryDetailsFragment */
        CustomerDetailsFragment detailsFragment = new CustomerDetailsFragment();

        /** Creating a bundle object to pass the data(the clicked item's position) from the activity to the fragment */
        Bundle b = new Bundle();

        /** Setting the data to the bundle object from the Intent*/
        b.putInt("position", getIntent().getIntExtra("position", 0));

        /** Setting the bundle object to the fragment */
        detailsFragment.setArguments(b);

        /** Adding the fragment to the fragment transaction */
        fragmentTransaction.add(R.id.customer_details_fragment_container, detailsFragment);

        /** Making this transaction in effect */
        fragmentTransaction.commit();

    }
}
