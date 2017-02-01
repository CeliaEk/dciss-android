package fac.capteurlocalisation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Sylvain on 28/11/2016.
 */

public class Podometre implements SensorEventListener {
    private SensorManager mSensorManager ;
    private Sensor mAcce ;
    int pic =0;
    int front=0;
    int nombrePas=0;
    double tempsDetection =0;
    private AcceleratorListener mAcceleratorListener;

    public Podometre(SensorManager sensorManager) {
        mSensorManager = sensorManager;
        mAcce = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public interface AcceleratorListener {
        void pasDetecte(int nombreDePas);
    }

    public void setAcceleratorListener(AcceleratorListener listener){
        mAcceleratorListener = listener;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { /* Nothing to do */ }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float accéXMesurée = event.values[0];
        float accéYMesurée = event.values[1];
        float accéZMesurée = event.values[2];

        double accéExterne = Math.sqrt(Math.pow(accéXMesurée,2)+Math.pow(accéYMesurée,2)+Math.pow(accéZMesurée,2)) - SensorManager.GRAVITY_EARTH;

        if(accéExterne>3.0 && tempsDetection==0 && (mAcceleratorListener!=null)){
            front=1;
            tempsDetection = System.currentTimeMillis();
        }
        if(front==1) {
            //Log.d("tempsDetection :" + tempsDetection, "onSensorChanged: ");
            //Log.d("tempsActuel :" + tempsActuel, "onSensorChanged: ");
            if (System.currentTimeMillis() - tempsDetection > 500) {//détection de pas
                nombrePas += 1;
                tempsDetection=0;
                front=0;
                    mAcceleratorListener.pasDetecte(nombrePas);
            }
        }
    }

    public void start() {
        mSensorManager.registerListener(this , mAcce, SensorManager.SENSOR_DELAY_GAME); }

    public void stop () {
        mSensorManager . unregisterListener ( this ) ;}

}
