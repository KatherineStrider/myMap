package com.example.kate.mymaps;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Set;
import java.util.TreeSet;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Set<String> markers;
    private SharedPreferences preferences;

    public static final String APP_PREFERENCES = "pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markers = new TreeSet<>();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        mMap = googleMap;
        mMap.setOnMapClickListener(onMapClickListener);
        getMarkers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_map_mode_normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        }

        if (item.getItemId() == R.id.menu_map_mode_satellite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            return true;
        }

        if (item.getItemId() == R.id.menu_map_mode_terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            return true;
        }

        if (item.getItemId() == R.id.menu_map_traffic) {
            mMap.setTrafficEnabled(!mMap.isTrafficEnabled());
            return true;
        }

        if (item.getItemId() == R.id.menu_map_location) {

            if (mMap.isMyLocationEnabled()) {

                LocationManager locationManager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                        (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                Location location = locationManager.getLastKnownLocation(locationManager
                        .getBestProvider(criteria, false));
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                LatLng target = new LatLng(latitude, longitude);

                CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(target, 15F);

                mMap.animateCamera(camUpdate);
            }
            return true;
        }

        if (item.getItemId() == R.id.menu_map_point_new) {

            MarkerOptions marker = new MarkerOptions();

            marker.icon(null);

            marker.anchor(0.0f, 0.0f);

            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            LatLng target = new LatLng(latitude, longitude);

            marker.title("My Location");

            marker.position(target);

            mMap.addMarker(marker);

        }
        return super.onOptionsItemSelected(item);

    }

    GoogleMap.OnMapClickListener onMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {

            MarkerOptions marker = new MarkerOptions();

            marker.icon(null);
            marker.anchor(0.0f, 0.0f);

            LatLng target = new LatLng(latLng.latitude, latLng.longitude);

            marker.title("My Location");

            markers.add(marker.getTitle() + "/;" + latLng.latitude + "/;" + latLng.longitude);

            marker.position(target);
            mMap.addMarker(marker);
        }
    };

    private void saveMarkers() {

        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("setKey", markers);
        editor.apply();
        editor.commit();
    }

    private void getMarkers() {

        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        try {
            Set<String> ret = preferences.getStringSet("setKey", new TreeSet<String>());
            for (String r : ret) {
                markers.add(r);
            }
            setMarkers();
        }catch (NullPointerException e){

            Log.d("eeeee", "все плохо");
        }
    }

    private void setMarkers() {

        for (String r : markers) {
            String[] s = r.split("/;");
            double lat;
            double lng;

            try {
                lat = Double.parseDouble(s[1]);
                lng = Double.parseDouble(s[2]);
            }catch (NumberFormatException e){
                Log.d(MapsActivity.class.getSimpleName(), "Невозможно получить координаты");
                return;
            }

            MarkerOptions marker = new MarkerOptions();

            marker.icon(null);
            marker.anchor(0.0f, 0.0f);

            LatLng target = new LatLng(lat, lng);

            marker.title(s[0]);

            marker.position(target);
            mMap.addMarker(marker);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        saveMarkers();
        Log.d("", " Pause");

    }

}
