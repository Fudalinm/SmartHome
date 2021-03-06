package agh.edu.pl.smarthome;

import com.google.gson.JsonObject;
import com.rabbitmq.client.Connection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RadiatorController extends DeviceController {
    public RadiatorController(String name, Connection senderConnection, String exchange, String topic){
        super(name, senderConnection, exchange, topic);
    }

    @Override
    public JsonObject buildRequest() {
        System.out.println("Entered control radiator");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        JsonObject toRet = new JsonObject();
        try {
            String input = reader.readLine();
            String[] controlStrings = input.split(";",3);

            Integer controlTypeInt = Integer.parseInt(controlStrings[0]);
            String controlTypeString = controlTypeInt == 1 ? "automatic" : controlTypeInt == 2 ?  "manual" : "";
            Integer usage = Integer.parseInt(controlStrings[1]);
            Double targetTemperature = Double.parseDouble(controlStrings[2]);
            
            if (controlTypeString.length() > 1){
                toRet.addProperty("ControlType",controlTypeString);
            }
            toRet.addProperty("Usage",usage);
            toRet.addProperty("TargetTemperature",targetTemperature);
        }catch (IOException e){
            System.out.println("Error while line processing");
            e.printStackTrace();
            return null;
        }
        return toRet;
    }

    @Override
    public String help() {
    return "Input string CONTROL_TYPE;USAGE;TARGET_TEMPERATURE \n" +
            "\t CONTROL_TYPE (0 - previous, 1 - automatic, 2 - manual)\n" +
            "\t USAGE integer between 0 and 100 \n" +
            "\t TARGET_TEMPERATURE double\n" +
            "\t Example 2;80;25.5";
    }
}
