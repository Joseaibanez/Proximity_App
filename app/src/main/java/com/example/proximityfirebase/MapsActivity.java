package com.example.proximityfirebase;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import java.io.IOException;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private UsuarioA userA;


    public MapsActivity() {
        userA = new UsuarioA();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getPermisos();

    }

    private void getPermisos() {
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permiso == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        ArrayList<LatLng> poligono = userA.drawPolygon(mMap);
        userA.drawCircle(mMap, new LatLng(42.62148265388385, -5.618857367403332), 20);
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        MediaPlayer player = MediaPlayer.create(getApplicationContext(), ringtone);

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
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                //COMPROBANDO LA PROXIMIDAD AL CIRCULO
                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location myLocation) {
                        float[] distance = new float[2];
                        Circle perim = userA.getCircle();
                        Location.distanceBetween( myLocation.getLatitude(), myLocation.getLongitude(),
                                perim.getCenter().latitude, perim.getCenter().longitude, distance);
                        // Comprobacion area
                        if( distance[0] < perim.getRadius() ){
                            Vibrator vib = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vib.vibrate(1000);
                        }

                        //COMPROBACION POLIGONO
                        LatLng myLocat = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        if(PolyUtil.containsLocation(myLocat, poligono, false)) {
                            if (!player.isPlaying()) {
                                player.start();
                            }
                        }
                        else {
                            if (player.isPlaying()) {
                                player.stop();
                                try {
                                    player.prepare();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        //FIN POLÍGONO
                    }
                });
                //FIN DE PERÍMETRO
            }

        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2, 1000, locListener);
    }
}