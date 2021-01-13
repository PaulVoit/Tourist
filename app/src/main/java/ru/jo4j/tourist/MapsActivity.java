package ru.jo4j.tourist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.Objects;

import ru.jo4j.tourist.database.SQLStore;
import ru.jo4j.tourist.model.Mark;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Location location;
    private final int LIST_OF_MARK = 1;
    private SQLStore mStore;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LIST_OF_MARK && resultCode == RESULT_OK) {
            int id = 0;
            if (data != null) {
                id = data.getIntExtra("mark", 0);
            }
            Mark mark = mStore.findMarkByID(id);
            map.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(mark.getLatitude(), mark.getLongitude()), 15));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mStore = new SQLStore(getApplicationContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button current = findViewById(R.id.current);
        current.setOnClickListener(this::getCurrentLocation);
        Button list = findViewById(R.id.list);
        list.setOnClickListener(this::showMarkList);
        if (isMapPermissionGranted()) {
            initMap();
        }
    }

    public void getCurrentLocation(View view) {
        if (location != null) {
            String title = "Place on the map";
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng coordinates = new LatLng(latitude, longitude);
            MarkerOptions marker = new MarkerOptions().position(coordinates).title(title);
            marker.flat(true);
            map.addMarker(marker);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
            mStore.addMark(new Mark(latitude, longitude, title));
        }
    }

    private boolean isMapPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            initMap();
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setAllMarks() {
        mStore.getMarks().stream().forEach(mark -> {
            LatLng coordinates = new LatLng(mark.getLatitude(), mark.getLongitude());
            MarkerOptions marker = new MarkerOptions().position(coordinates).title(mark.getTitle());
            marker.flat(true);
            map.addMarker(marker);
        });
    }

    private void showMarkList(View view) {
        Intent intent = new Intent(this, MarkListActivity.class);
        startActivityForResult(intent, LIST_OF_MARK);
    }

    @Override
    public void onMapReady(GoogleMap gmap) {
        map = gmap;
        setAllMarks();
        LocationListener loc = new LocationListener() {
            @Override
            public void onLocationChanged(Location lct) {
                location = lct;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Objects.requireNonNull(locationManager)
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, loc);
        Places.initialize(this, getString(R.string.google_maps_key));
        AutocompleteSupportFragment search = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if (search != null) {
            search.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        }
        if (search != null) {
            search.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    LatLng pos = place.getLatLng();
                    MarkerOptions marker = new MarkerOptions().position(pos).title("Hello Maps");
                    map.addMarker(marker);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.i("MainActivity", "An error occurred: " + status);
                }
            });
        }
    }
}