package com.eventu.login_and_registration.school_selection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Custom adapter in order to store SchoolObjects within our ListView. Although only the school name
 * is displayed, the domain information can be retrieved from the given SchoolInfo.
 */
public class SchoolSelectionListAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<SchoolInfo> schools;

    public SchoolSelectionListAdapter(Context context, List<SchoolInfo> schools) {
        this.inflater = LayoutInflater.from(context);
        this.schools = schools;
    }

    @Override
    public int getCount() {
        return schools.size();
    }

    @Override
    public SchoolInfo getItem(int location) {
        return schools.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SchoolInfo school = getItem(position);

        if (convertView == null) {
            // If convertView is null we have to inflate a new simple list layout
            convertView = this.inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            final TextView textView = convertView.findViewById(android.R.id.text1);
            convertView.setTag(textView);
        }

        // Retrieve the TextView from the convertView
        final TextView textview = (TextView) convertView.getTag();

        // Bind the new display value to the TextView
        textview.setText(school.getName());

        return convertView;
    }
}


