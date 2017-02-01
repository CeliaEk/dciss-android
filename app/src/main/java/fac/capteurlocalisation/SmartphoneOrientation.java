package fac.capteurlocalisation;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.Matrix;

/**
 * Created by Sylvain on 28/11/2016.
 */

public class SmartphoneOrientation implements SensorEventListener{
    private SensorManager mSensorManager ;
    private Sensor mRotate ;
    private float[] mOrientationVals = new float[3];
    private float[] mRotationMatrixMagnetic = new float[16];
    private float[] mRotationMatrixMagneticToTrue = new float[16];
    private float[] mRotationMatrix = new float[16];
    private OrientationListener mOrientationListener;

    protected SmartphoneOrientation(SensorManager sensorManager) {
        mSensorManager = sensorManager;
        mRotate = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    public interface OrientationListener {
        void setAngleNord(float angleNord);
    }

    public void setOrientationListener(OrientationListener listener){
        mOrientationListener = listener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { /* Nothing to do */ }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Transforme le rotation vector en matrice de rotation
        SensorManager.getRotationMatrixFromVector(mRotationMatrixMagnetic, event.values);
        //Création de la matrice de passage du repère magnétique au repère classique
        Matrix.setRotateM(mRotationMatrixMagneticToTrue, 0, -1.83f, 0, 0, 1);
        //Change la matrice d'orientation du repère magnétique au repère classique
        Matrix.multiplyMM(mRotationMatrix, 0, mRotationMatrixMagnetic, 0, mRotationMatrixMagneticToTrue, 0);
        //Transforme la matrice de rotation en une succession de rotations autour de z  y et x
        SensorManager.getOrientation(mRotationMatrix, mOrientationVals);

        float Yaw = (float) Math.toDegrees(mOrientationVals[0]);
        if(mOrientationListener!=null)
            mOrientationListener.setAngleNord(Yaw);
    }

    public void start() {
        mSensorManager.registerListener(this , mRotate, SensorManager.SENSOR_DELAY_GAME); }

    public void stop () {
        mSensorManager . unregisterListener ( this ) ; }

}
