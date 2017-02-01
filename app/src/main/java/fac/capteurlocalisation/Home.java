package fac.capteurlocalisation;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import static android.R.attr.gravity;
import static fac.capteurlocalisation.R.id.YawTV;
import static fac.capteurlocalisation.R.id.markerViewContainer;

public class Home extends AppCompatActivity implements SensorEventListener {
    public PDR mPDR;
    private MapView mapView;
    private MarkerViewOptions markerViewOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SensorManager mSensorManager = (SensorManager) getSystemService (Context.SENSOR_SERVICE);
        mPDR = new PDR(mSensorManager);
        mPDR.setPDRListener(mPDRListener);
        MapboxAccountManager.start(this, getString(R.string.access_token));
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        markerViewOptions = new MarkerViewOptions().position(new LatLng(45.192916, 5.773378));



        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                mapboxMap.setStyleUrl("http://taha.inrialpes.fr/shs.json");
                mapboxMap.addMarker(markerViewOptions);
                mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener(){
                    @Override
                    public void onMapClick(@NonNull LatLng point){
                        mPDR.setmCurrentLocation(point);
                        mapboxMap.clear();
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(point));

                    }
                });
            }
        });
    }


    //On écoute le PDR, dès qu'on a une nouvelle position on actualise le marker sur notre map
    private PDR.PDRListener mPDRListener = new PDR.PDRListener() {
        @Override
        public void newLocation(LatLng nextLocation) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final MapboxMap mapboxMap) {
                    //On supprime l'ancien marker
                    mapboxMap.clear();
                    //On centre la map sur notre nouvelle position
                    mapboxMap.setCameraPosition(new CameraPosition.Builder()
                            .target(mPDR.getmCurrentLocation())
                            .zoom(18)
                            .build());
                    //On place le marker
                    mapboxMap.addMarker(new MarkerOptions()
                            .position(mPDR.getmCurrentLocation()));
                }
            });
        }
    };


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { /* Nothing to do */ }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    protected void onResume() {
        super.onResume() ;
        mPDR.start();
        mapView.onResume();
    }

    @Override
    protected void onPause () {
        super.onPause () ;
        mPDR.stop();
        mapView.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}
