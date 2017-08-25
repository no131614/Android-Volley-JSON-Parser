package first.alexander.com.androidvolleyparser;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * ItemInfoListAdapter.java - a class contains implementation of custom rows for
 * item list view.
 *
 * @author Alexander Julianto (no131614)
 */
public class ItemInfoListAdapter extends ArrayAdapter {

    private final Activity context;
    private final ArrayList item_list;

    public ItemInfoListAdapter(Activity context, ArrayList item_list) {
        super(context, R.layout.list_row, item_list);
        this.context = context;
        this.item_list = item_list;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        // Begin: Set up list_row layout as custom item list view row
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_row, null, true);

        TextView tvTitle = (TextView) rowView.findViewById(R.id.item_name);
        tvTitle.setText(item_list.get(position).toString());
        // End: Set up list_row layout as custom item list view row

        return rowView;
    }

}
