package com.kenkode.altruistic;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ListView availableBusesListview;
    public static CustomAdapter adapter;
    private List<Model> availableBuses = new ArrayList<Model>();
    ApiInterface apiInterface;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    DatabaseHelper db;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHelper(this);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        locationTrack = new LocationTrack(MainActivity.this);

        if (locationTrack.canGetLocation()) {
            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();

            insertUser(latitude, longitude);
            boardUser(latitude, longitude);

            RequestQueue requestQueue= Volley.newRequestQueue(this);
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Loading...");
            pDialog.show();
            //  Create json array request
            // Creating volley request obj
            JsonArrayRequest availableBusesRequest = new JsonArrayRequest(Common.BASE_URL+Common.availableBusesUrl+"?lat=" + latitude + "&lng=" + longitude,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.e("Response", response.toString());
                            pDialog.hide();

                            // Parsing json
                            for (int i = 0; i < response.length(); i++) {
                                try {

                                    JSONObject obj = response.getJSONObject(i);
                                    Model available_buses = new Model();
                                    available_buses.setArrival_time(obj.getString("arrival_time"));
                                    available_buses.setDeparture_time(obj.getString("departure_time"));
                                    available_buses.setVacant_seats(obj.getInt("vacant_seats"));
                                    available_buses.setWaiting_time(String.valueOf(5+i));

                                    // adding movie to movies array
                                    availableBuses.add(available_buses);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            // notifying list adapter about data changes
                            // so that it renders the list view with updated data
                            adapter.notifyDataSetChanged();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error", "Error: " + error.getMessage());
                    Log.e("why",error.getMessage());
                    pDialog.hide();

                }
            });
            requestQueue.add(availableBusesRequest);

//            Log.e("URL", Common.BASE_URL+Common.availableBusesUrl+"?lat=" + latitude + "&lng=" + longitude);
//
//            Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {
            locationTrack.showSettingsAlert();
        }

        availableBusesListview = (ListView) findViewById(R.id.available_buses);
        adapter = new CustomAdapter(MainActivity.this, availableBuses);
        availableBusesListview.setAdapter(adapter);

        apiInterface = RetrofitClient.getClient().create(ApiInterface.class);

//        Call<BusesResponse> callAvailableBuses = apiInterface.getAvailableBuses();
//        callAvailableBuses.enqueue(new Callback<BusesResponse>() {
//            @Override
//            public void onResponse(Call<BusesResponse> call, Response<BusesResponse> response) {
//                Log.e("Bus", ""+response.body());
//                availableBuses = response.body().getResults();
//                adapter=new CustomAdapter(MainActivity.this, availableBuses);
//                adapter.notifyDataSetChanged();
//                availableBusesListview.setAdapter(adapter);
//            }
//
//            @Override
//            public void onFailure(Call<BusesResponse> call, Throwable t) {
//                Log.e("Error",t.toString());
//            }
//        });



        availableBusesListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Model available_buses = new Model();
                available_buses.setArrival_time(availableBuses.get(position).getArrival_time());
                available_buses.setDeparture_time(availableBuses.get(position).getDeparture_time());
                available_buses.setVacant_seats(availableBuses.get(position).getVacant_seats());
                available_buses.setWaiting_time(availableBuses.get(position).getWaiting_time());
                showInformationDialog(available_buses);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void showInformationDialog(Model availableBusesModel) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("VIEW BUS DETAILS");

        LayoutInflater inflater = LayoutInflater.from(this);
        View bus_layout = inflater.inflate(R.layout.layout_view_information,null);

        final TextView arrivalTime = bus_layout.findViewById(R.id.arrival_time);
        final TextView departureTime = bus_layout.findViewById(R.id.departure_time);
        final TextView vacantSeats = bus_layout.findViewById(R.id.vacant_seats);
        final TextView waitingTime = bus_layout.findViewById(R.id.waiting_time);

        arrivalTime.setText(availableBusesModel.getArrival_time());
        departureTime.setText(availableBusesModel.getDeparture_time());
        vacantSeats.setText(String.valueOf(availableBusesModel.getVacant_seats()));
        waitingTime.setText(availableBusesModel.getWaiting_time());

        dialog.setView(bus_layout);

        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void insertUser(final double lat, final double lng) {
        String url = Common.BASE_URL+Common.usersUrl;
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.e("User Response", response);
                        db.insertData(Integer.parseInt(response), MainActivity.this);

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lat", String.valueOf(lat));
                params.put("lng", String.valueOf(lng));

                Log.e("Coordinates",String.valueOf(lat)+" "+String.valueOf(lng));
                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    private void boardUser(final double lat, final double lng) {
        String url = Common.BASE_URL+Common.boardingsUrl;
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        JSONObject postparams = new JSONObject();
        try {
            postparams.put("lat", String.valueOf(lat));
            postparams.put("lng", String.valueOf(lng));
            postparams.put("userId", String.valueOf(db.getUserID()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("Boarding",String.valueOf(lat)+" "+String.valueOf(lng)+" "+String.valueOf(db.getUserID()));

        JsonObjectRequest postRequest = new JsonObjectRequest (Request.Method.POST, url, postparams,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.e("User Response", response.toString());
                        if(response != null) {
                            try {
                                db.updateData(response.getInt("userId"), response.getInt("busId"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("lat", String.valueOf(lat));
                params.put("lng", String.valueOf(lng));
                params.put("userId", String.valueOf(db.getUserID()));

                return params;
            }
        };
        requestQueue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_available_buses) {
            return true;
        } else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_available_buses) {
            // Handle the available bus action
        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }
}
