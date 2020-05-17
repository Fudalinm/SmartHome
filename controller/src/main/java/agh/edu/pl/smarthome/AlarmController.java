package agh.edu.pl.smarthome;

import com.google.gson.JsonObject;
import com.rabbitmq.client.Connection;

public class AlarmController extends DeviceController{

    public AlarmController(String name, Connection senderConnection, String exchange, String topic) {
        super(name, senderConnection, exchange, topic);
    }

    @Override
    public JsonObject buildRequest() {
        return null;
    }

    @Override
    public String help() {
        return null;
    }
}
