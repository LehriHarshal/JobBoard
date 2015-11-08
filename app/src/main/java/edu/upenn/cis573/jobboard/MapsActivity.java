package edu.upenn.cis573.jobboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

//import com.google.android.gms.maps.CameraUpdateFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Marker point;
    private GoogleMap mMap;
    private boolean fromSearch;


    //static double latitude;
    //static double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        double latitude, longitude = 0;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.v("Here000", "Here000");
        //Intent i=getIntent();
        //latitude=Double.parseDouble(i.getStringExtra("Latitude"));
        //longitude=Double.parseDouble(i.getStringExtra("Longitude"));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String from = extras.getString("FROM");
            if (!TextUtils.isEmpty(from) && from.equalsIgnoreCase("search")) {
                fromSearch = true;
            } else {
                fromSearch = false;
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng currentLocation = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
        Log.v("Here111", "Here111");
        //LatLng currentLocation=new LatLng(39.9525653,-75.2055459);


        mMap.setMyLocationEnabled(true);


        Toast.makeText(getApplicationContext(), "Please press the button for current location on the top right corner",
                Toast.LENGTH_SHORT).show();
        LatLng currentLocation = new LatLng(39.9525653, -75.2055459);
        point = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker in currentLocation"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        point.setDraggable(true);
        //Log.v("Here222", "Here222");
        //Log.v("Position", point.getPosition().toString());

        GoogleMap.OnMarkerDragListener listener = new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.v("Mdragstart", "Mdragstart");

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                Log.v("Mdrag", "Mdrag");

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.v("Mdragend", "Mdragend");
            }
        };

        mMap.setOnMarkerDragListener(listener);
    }

    //public void selectLocation()
    //{

    //    }
    public void selectLocation(View view) {
        //Log.v("Position", point.getPosition().toString());
        LatLng pos = point.getPosition();
        //Toast.makeText(getApplicationContext(),"Location recorded",Toast.LENGTH_SHORT).show();
        JobCreationActivity.lat = pos.latitude;
        JobCreationActivity.lon = pos.longitude;
        SearchableActivity.USER_LOCATION.setLatitude(pos.latitude);
        SearchableActivity.USER_LOCATION.setLongitude(pos.longitude);
        Toast.makeText(getApplicationContext(), "Location recorded", Toast.LENGTH_SHORT).show();
        if (fromSearch) {
            Intent intent = new Intent(this, SearchableActivity.class);
            SearchableActivity.SEARCH_CATEGORY = 1;
            intent.setAction(Intent.ACTION_SEARCH);
            startActivity(intent);
        }
        finish();
    }
}
//can u call ok