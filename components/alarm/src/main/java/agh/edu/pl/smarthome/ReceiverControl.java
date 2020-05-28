package agh.edu.pl.smarthome;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Delivery;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class ReceiverControl implements Runnable{

    private Connection connection;
    private Channel channel;
    private String exchange;
    private String listenTopicControl;
    private String queueName;
    private Alarm alarm;

    public void deliverCallback(String consumerTag, Delivery delivery) throws UnsupportedEncodingException {
        JsonObject receivedJson = new Gson().fromJson(new String(delivery.getBody(), "UTF-8"),JsonObject.class);
        System.out.println(" Alarm received: " +
                delivery.getEnvelope().getRoutingKey() + "':'" + receivedJson.toString() + "'");

        int set = receivedJson.get("Set").getAsInt();

        if (set == 1) {
            this.alarm.setIsOn(true);
        } else {
            this.alarm.setIsOn(false);
            this.alarm.setIsUp(false);
        }

    }

    public ReceiverControl(
            Connection connection,
            String exchange,
            String listenTopicControl,
            Alarm alarm
    ) throws IOException {
        this.alarm = alarm;
        this.exchange = exchange;
        this.listenTopicControl = listenTopicControl;
        this.connection = connection;
        this.channel = connection.createChannel();


    }

    @Override
    public void run() {
        try {
            this.channel.exchangeDeclare(exchange, "topic");
            this.queueName = channel.queueDeclare().getQueue();
            this.channel.queueBind(queueName, exchange, this.listenTopicControl);
            this.channel.basicConsume(queueName, true, this::deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}