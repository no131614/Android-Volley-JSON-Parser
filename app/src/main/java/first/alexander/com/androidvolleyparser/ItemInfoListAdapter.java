package first.alexander.com.androidvolleyparser;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



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
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_row, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.item_name);

        txtTitle.setText(item_list.get(position).toString());

        return rowView;
    }

}
