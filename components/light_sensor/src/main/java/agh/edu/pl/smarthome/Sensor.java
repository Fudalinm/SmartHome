package agh.edu.pl.smarthome;

import com.google.gson.JsonObject;

import java.util.Random;

import static jdk.nashorn.internal.objects.NativeMath.max;

public class Sensor {
    private int lightsUsage;
    private int blindsUsage;

    private double internalLight;
    private double externalLight;

    public Sensor(){
        this.blindsUsage = 0;
        this.lightsUsage = 0;
        this.internalLight = 0;
        this.externalLight = 0;
    }

    public void setLightsUsage(int lightsUsage) {
        this.lightsUsage = lightsUsage;
    }

    public void setBlindsUsage(int blindsUsage) {
        this.blindsUsage = blindsUsage;
    }

    public void setExternalLight(double externalLight) {
        this.externalLight = externalLight;
    }

    public void updateInternalLight(){
        this.internalLight = Math.max(((100-this.blindsUsage)*this.externalLight)/100,this.lightsUsage*6000);
    }

    public JsonObject getJsonToSend(){
        JsonObject toRet = new JsonObject();

        toRet.addProperty("InternalLight",this.internalLight);
        toRet.addProperty("ExternalLight",this.externalLight);

        return toRet;
    }


}
