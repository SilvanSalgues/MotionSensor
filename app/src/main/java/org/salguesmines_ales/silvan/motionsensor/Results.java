package org.salguesmines_ales.silvan.motionsensor;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Psycus34 on 13/06/2016.
 */
public class Results {

    private double mLinearAccelerationX, mLinearAccelerationY, mLinearAccelerationZ;
    private long mTime;

    // JSON
    private static final String JSON_TIME = "mTime";
    private static final String JSON_LINEAR_ACC_X = "mLinearAccelerationX";
    private static final String JSON_LINEAR_ACC_Y = "mLinearAccelerationY";
    private static final String JSON_LINEAR_ACC_Z = "mLinearAccelerationZ";

    //JSON default constructor
    public Results(JSONObject jo) throws JSONException{
        mTime = jo.getLong(JSON_TIME);
        mLinearAccelerationX = jo.getDouble(JSON_LINEAR_ACC_X);
        mLinearAccelerationY = jo.getDouble(JSON_LINEAR_ACC_Y);
        mLinearAccelerationZ = jo.getDouble(JSON_LINEAR_ACC_Z);
    }

    public Results(){
    }

    public JSONObject convertToJSON() throws JSONException{

        JSONObject jo = new JSONObject();

        jo.put(JSON_TIME, mTime);
        jo.put(JSON_LINEAR_ACC_X, mLinearAccelerationX);
        jo.put(JSON_LINEAR_ACC_Y, mLinearAccelerationY);
        jo.put(JSON_LINEAR_ACC_Z, mLinearAccelerationZ);

        return jo;
    }

    public void Results(long time, double accelX, double accelY, double accelZ){
        mTime=time;
        mLinearAccelerationX = accelX;
        mLinearAccelerationY = accelY;
        mLinearAccelerationZ = accelZ;
    }

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }

    public double getmLinearAccelerationX() {
        return mLinearAccelerationX;
    }

    public void setmLinearAccelerationX(double mLinearAccelerationX) {
        this.mLinearAccelerationX = mLinearAccelerationX;
    }

    public double getmLinearAccelerationY() {
        return mLinearAccelerationY;
    }

    public void setmLinearAccelerationY(double mLinearAccelerationY) {
        this.mLinearAccelerationY = mLinearAccelerationY;
    }

    public double getmLinearAccelerationZ() {
        return mLinearAccelerationZ;
    }

    public void setmLinearAccelerationZ(double mLinearAccelerationZ) {
        this.mLinearAccelerationZ = mLinearAccelerationZ;
    }
}
