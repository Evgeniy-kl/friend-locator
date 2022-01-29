package com.example.myapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.CarrierConfigManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends AppCompatActivity implements LocListenerInterface, OnMapReadyCallback {

    private GoogleMap mMap;


    GPSTracker gpsTracker = new GPSTracker(MapsActivity.this);

    private TextView tvDistance, tvVelocity;
    private Location lastLocation;
    private LocationManager locationManager;
    private MyLocListener myLocListener;
    private double latitude, longitude;
    private int distance;
    LocListenerInterface locListenerInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if( latitude == 0 && longitude == 0)
        {
            //Toast.makeText(this, "0", Toast.LENGTH_SHORT).show();

        }
        if (gpsTracker.canGetLocation()) {

            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            Toast.makeText(MapsActivity.this, latitude + " @@@Works@@@ " + longitude + "", Toast.LENGTH_LONG).show();
        }
        // Add a marker in Sydney and move the camera
        LatLng minsk = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());

        mMap.addMarker(new MarkerOptions()
                .position(minsk)
                .title(user.getEmail()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(minsk));
    }

    private void init()
    {
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        myLocListener = new MyLocListener();
        myLocListener.setLocListenerInterface(locListenerInterface);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100 && grantResults[0] == RESULT_OK)
        {
            checkPermissions();
        }

    }

    private void checkPermissions()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 1, myLocListener);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if(location.hasSpeed() && lastLocation != null)
        {
            distance += lastLocation.distanceTo(location);
        }
        lastLocation = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();

    }
}