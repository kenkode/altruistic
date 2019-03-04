package com.kenkode.altruistic;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Model> availableBuses;

    public CustomAdapter(Activity activity, List<Model> availableBuses) {
        this.activity = activity;
        this.availableBuses = availableBuses;
    }

    @Override
    public int getCount() {
        return availableBuses.size();
    }

    @Override
    public Object getItem(int location) {
        return availableBuses.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.available_bus_model, null);

        TextView arrival_time = (TextView) convertView.findViewById(R.id.arrival_time);
        TextView departure_time = (TextView) convertView.findViewById(R.id.departure_time);
        TextView waiting_time = (TextView) convertView.findViewById(R.id.waiting_time);
        TextView vacant_seats = (TextView) convertView.findViewById(R.id.vacant_seats);

        // getting movie data for the row
        Model m = availableBuses.get(position);

        // Arrival Time
        arrival_time.setText(m.getArrival_time());

        // Departure Time
        departure_time.setText(m.getDeparture_time());

        // Vacant Seats
        vacant_seats.setText(String.valueOf(m.getVacant_seats()));

        // Vacant Seats
        waiting_time.setText(m.getWaiting_time());

        return convertView;
    }

}