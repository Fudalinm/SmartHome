package agh.edu.pl.smarthome;

import com.google.gson.JsonObject;
import com.rabbitmq.client.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AlarmController extends DeviceController{

    public AlarmController(String name, Connection senderConnection, String exchange, String topic) {
        super(name, senderConnection, exchange, topic);
    }

    @Override
    public JsonObject buildRequest() {
        System.out.println("Entered control blinds");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        JsonObject toRet = new JsonObject();
        try {
            String input = reader.readLine();

            Integer set = Integer.parseInt(input);
            toRet.addProperty("Set", set);
        }catch (IOException e){
            System.out.println("Error while line processing");
            e.printStackTrace();
            return null;
        }
        return toRet;
    }

    @Override
    public String help() {
        return "Input string SET\n" +
                "\t CONTROL_TYPE (0 - OFF, 1 - ON)\n" +
                "\t Example 1";
    }
}
