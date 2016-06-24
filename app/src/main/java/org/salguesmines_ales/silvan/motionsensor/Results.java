package org.salguesmines_ales.silvan.motionsensor;

/**
 * Created by Psycus34 on 13/06/2016.
 */
public class Results {

    private float mTime, mLinearAccelerationX, mLinearAccelerationY, mLinearAccelerationZ;

    public void Results(float time, float accelX, float accelY, float accelZ){
        mTime=time;
        mLinearAccelerationX = accelX;
        mLinearAccelerationY = accelY;
        mLinearAccelerationZ = accelZ;
    }

    public float getmTime() {
        return mTime;
    }

    public void setmTime(float mTime) {
        this.mTime = mTime;
    }

    public float getmLinearAccelerationX() {
        return mLinearAccelerationX;
    }

    public void setmLinearAccelerationX(float mLinearAccelerationX) {
        this.mLinearAccelerationX = mLinearAccelerationX;
    }

    public float getmLinearAccelerationY() {
        return mLinearAccelerationY;
    }

    public void setmLinearAccelerationY(float mLinearAccelerationY) {
        this.mLinearAccelerationY = mLinearAccelerationY;
    }

    public float getmLinearAccelerationZ() {
        return mLinearAccelerationZ;
    }

    public void setmLinearAccelerationZ(float mLinearAccelerationZ) {
        this.mLinearAccelerationZ = mLinearAccelerationZ;
    }
}
