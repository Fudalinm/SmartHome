package agh.edu.pl.smarthome;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;


public class Sender implements Runnable {

    private Connection connection;
    private Channel channel;
    private String exchange;
    private String publishTopicBlinds;
    private String publishTopicLights;
    private Alarm alarm;
    private boolean previousIsUpState = false;
    private boolean lastLightState = true;

    public Sender(
            Connection connection,
            String exchange,
            String publishTopicBlinds,
            String publishTopicLights,
            Alarm alarm
    ) throws IOException {
        this.alarm = alarm;
        this.exchange = exchange;
        this.publishTopicBlinds = publishTopicBlinds;
        this.publishTopicLights = publishTopicLights;
        this.connection = connection;
        this.channel = connection.createChannel();
        channel.exchangeDeclare(exchange, "topic");
    }

    public void sendBlinds(String message) throws IOException {
        channel.basicPublish(exchange, publishTopicBlinds, null, message.getBytes());
    }

    public void sendLights(String message) throws IOException {
        channel.basicPublish(exchange, publishTopicLights, null, message.getBytes());
    }

    @Override
    public void run() {
        try {
            while(true) {
                    if(alarm.getIsUp()) {
                        if(!previousIsUpState) {
                            JsonObject toRet = new JsonObject();
                            toRet.addProperty("ControlType","manual");
                            toRet.addProperty("Usage", 0);
                            toRet.addProperty("TargetLight",7000);
                            this.sendBlinds(toRet.toString());
                        }
                        JsonObject toRet = new JsonObject();
                        toRet.addProperty("ControlType","manual");
                        toRet.addProperty("Property", lastLightState ? 0 : 1);
                        this.sendLights(toRet.toString());
                        previousIsUpState = true;
                    } else {
                        previousIsUpState = false;
                    }
                    Thread.sleep(5000);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}