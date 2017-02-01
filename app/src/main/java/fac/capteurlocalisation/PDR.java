package fac.capteurlocalisation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by Sylvain on 30/11/2016.
 */

public class PDR implements SensorEventListener {
    private LatLng mCurrentLocation;
    private float angle;
    private PDRListener mPDRListener;
    private Podometre mPodometre;
    private SmartphoneOrientation mSmartphoneOrientation;
    private SensorManager mSensorManager;


    public PDR(SensorManager mSensorManager){
        mPodometre = new Podometre(mSensorManager);
        mPodometre.setAcceleratorListener(mAccelerometerListener);
        mSmartphoneOrientation= new SmartphoneOrientation(mSensorManager);
        mSmartphoneOrientation.setOrientationListener(mOrientationListener);


    }

    //On déclare notre Listener avec une méthode
    public interface PDRListener{
         void newLocation(LatLng nextLocation);

    }

    //On crée le listenener
    public void setPDRListener(PDRListener listener){
        mPDRListener = listener;

    }

    public void setmCurrentLocation(LatLng mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }

    public LatLng getmCurrentLocation() {
        return mCurrentLocation;
    }

    private SmartphoneOrientation.OrientationListener mOrientationListener = new SmartphoneOrientation.OrientationListener() {
            @Override
            public void setAngleNord(float angleNord) {
                angle = angleNord;
            }
    };

    public void computeNextStep(double stepSize, double bearing){
        double bearingRad = Math.toRadians(bearing);
        LatLng nextLocation=new LatLng();



        nextLocation.setLatitude(Math.toDegrees(Math.asin( Math.sin(Math.toRadians(mCurrentLocation.getLatitude()))*
                Math.cos(stepSize/6371000) + Math.cos(Math.toRadians(mCurrentLocation.getLatitude()))*Math.sin(stepSize/6371000)
                *Math.cos(bearingRad))));

        nextLocation.setLongitude(Math.toDegrees((Math.toRadians(mCurrentLocation.getLongitude()) + Math.atan2(Math.sin(bearingRad)
                *Math.sin(stepSize/6371000)*Math.cos(Math.toRadians(mCurrentLocation.getLatitude())),
                Math.cos(stepSize/6371000)-Math.sin(Math.toRadians(mCurrentLocation.getLatitude()))*Math.sin(Math.toRadians(mCurrentLocation.getLatitude()))))));

        setmCurrentLocation( nextLocation);

        //On déclanche le listener avec notre nouvelle position
        mPDRListener.newLocation(nextLocation);
    }

    public void start() {
        mSmartphoneOrientation.start();
        mPodometre.start();
    }

    public void stop() {
        mSmartphoneOrientation.stop();
        mPodometre.stop();
    }

    //on écoute le podomètre et à chaque pas on recalcule la position
    private Podometre.AcceleratorListener mAccelerometerListener = new Podometre.AcceleratorListener() {
        @Override
        public void pasDetecte(int nombreDePas) {
            computeNextStep(0.70,angle);
            Log.v("msg","un pas");
        }
    };


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

