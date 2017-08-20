package first.alexander.com.androidvolleyparser;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class CustomerDetailsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /** Inflating the layout country_details_fragment_layout to the view object v */
        View v = inflater.inflate(R.layout.customer_details_fragment_layout, null);

        /** Getting the textview object of the layout to set the details */
        TextView tv = (TextView) v.findViewById(R.id.customer_details);

        /** Getting the bundle object passed from MainActivity ( in Landscape mode )  or from
         *  CountryDetailsActivity ( in Portrait Mode )
         * */
        Bundle b = getArguments();

        /** Getting the clicked item's position and setting corresponding details in the textview of the detailed fragment */
        tv.setText("Details of " + b.getInt("position"));

        return v;
    }
}
