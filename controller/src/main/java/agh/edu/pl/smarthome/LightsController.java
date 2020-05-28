package agh.edu.pl.smarthome;

import com.google.gson.JsonObject;
import com.rabbitmq.client.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LightsController extends DeviceController{
    public LightsController(String name, Connection senderConnection, String exchange, String topic) {
        super(name, senderConnection, exchange, topic);
    }

    @Override
    public JsonObject buildRequest() {
        System.out.println("Entered control blinds");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        JsonObject toRet = new JsonObject();
        try {
            String input = reader.readLine();
            String[] controlStrings = input.split(";",2);

            Integer controlTypeInt = Integer.parseInt(controlStrings[0]);
            String controlTypeString = controlTypeInt == 1 ? "automatic" : controlTypeInt == 2 ?  "manual" : "";
            Integer property = Integer.parseInt(controlStrings[1]);

            if (controlTypeString.length() > 1){
                toRet.addProperty("ControlType",controlTypeString);
            }
            toRet.addProperty("Property", property);
        }catch (IOException e){
            System.out.println("Error while line processing");
            e.printStackTrace();
            return null;
        }
        return toRet;
    }

    @Override
    public String help() {
        return "Input string CONTROL_TYPE;LIGHT_BORDER_SWITCH_OR_ONN_OFF \n" +
                "\t CONTROL_TYPE (0 - previous, 1 - automatic, 2 - manual)\n" +
                "\t LIGHT_BORDER_SWITCH  automatic - integer between 0 and 7000 OR If manula 1 or 0 for on and off \n" +
                "\t Example 2;0";
    }
}
