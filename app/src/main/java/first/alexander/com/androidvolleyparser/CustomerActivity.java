package first.alexander.com.androidvolleyparser;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CustomerActivity extends AppCompatActivity implements CustomerInfoFragment.ListFragmentItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        getSupportActionBar().setIcon(R.drawable.customer);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
    }

    /** This method will be executed when the user clicks on an item in the listview */
    @Override
    public void onListFragmentItemClick(String name) {

        /** Getting the orientation ( Landscape or Portrait ) of the screen */
        int orientation = getResources().getConfiguration().orientation;

        /** Landscape Mode */
        if(orientation == Configuration.ORIENTATION_LANDSCAPE ){
            /** Getting the fragment manager for fragment related operations */
            FragmentManager fragmentManager = getFragmentManager();

            /** Getting the fragment transaction object, which can be used to add, remove or replace a fragment */
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            /** Getting the existing detailed fragment object, if it already exists.
             *  The fragment object is retrieved by its tag name
             * */
            Fragment prevFrag = fragmentManager.findFragmentById(R.id.detail_fragment_container);

            /** Remove the existing detailed fragment object if it exists */
            if(prevFrag!=null) {
                fragmentTransaction.remove(prevFrag);
            }

            /** Instantiating the fragment CustomerDetailsFragment */
            CustomerDetailsFragment customerDetailsFragment = new CustomerDetailsFragment();

            /** Creating a bundle object to pass the data(the clicked item's position) from the activity to the fragment */
            Bundle b = new Bundle();

            /** Setting the data to the bundle object */
            b.putString("name", name);

            /** Setting the bundle object to the fragment */
            customerDetailsFragment.setArguments(b);

            /** Adding the fragment to the fragment transaction */
            fragmentTransaction.add(R.id.detail_fragment_container, customerDetailsFragment);

            /** Adding this transaction to back stack */
            fragmentTransaction.addToBackStack(null);

            /** Making this transaction in effect */
            fragmentTransaction.commit();

        }else{
            /** Portrait Mode or Square mode */
            /** Creating an intent object to start the CountryDetailsActivity */
            Intent intent = new Intent(this, CustomerDetailsActivity.class);

            /** Setting data ( the clicked item's position ) to this intent */
            intent.putExtra("name", name);

            /** Starting the activity by passing the implicit intent */
            startActivity(intent);
        }
    }
}
