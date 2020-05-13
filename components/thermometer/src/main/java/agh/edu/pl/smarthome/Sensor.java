package agh.edu.pl.smarthome;
import com.google.gson.JsonObject;
import java.util.Random;

public class Sensor {
    private Integer radiatorUsage; /* Percent */
    /* I know it could be outside but in json we use external so we don't want to conflict names*/
    private Integer externalTemperature; /* 100 means 1.00 degree celcius */
    private Integer internalTemperature;
    public Sensor(){
        this.radiatorUsage = 0;
        this.externalTemperature = 0;
        this.internalTemperature = (new Random().nextInt(40) - 5)*100;
    }

    public void setExternalTemperature(int externalTemperature){
        this.externalTemperature = externalTemperature;
    }

    public void setExternalTemperature(double externalTemperature){
        this.externalTemperature = (int) externalTemperature*100;
    }

    public void setRadiatorUsage(Integer radiatorUsage) {
        this.radiatorUsage = radiatorUsage;
    }

    public double updateInternalTemperature(){
        this.internalTemperature = this.internalTemperature + (this.externalTemperature - this.internalTemperature)/20 + (int)((radiatorUsage/100.0)*60)/8;
        return this.internalTemperature/100.0;
    }

    public JsonObject getInternalTemperatureJson(){
        JsonObject temperatureJsonObject = new JsonObject();
        temperatureJsonObject.addProperty("InternalTemperature",this.internalTemperature/100.0);
        return temperatureJsonObject;
    }

}
