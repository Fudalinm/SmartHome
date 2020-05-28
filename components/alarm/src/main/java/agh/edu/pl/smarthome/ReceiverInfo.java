package agh.edu.pl.smarthome;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Delivery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class ReceiverInfo implements Runnable{

    private Connection connection;
    private Channel channel;
    private String exchange;
    private String listenTopicInfo;
    private String queueName;
    private Alarm alarm;

    public void deliverCallback(String consumerTag, Delivery delivery) throws UnsupportedEncodingException {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

        if(this.alarm.getIsOn()) {
            this.alarm.setIsUp(true);
        }
    }

    public ReceiverInfo(
            Connection connection,
            String exchange,
            String listenTopicInfo,
            Alarm alarm
    ) throws IOException {
        this.alarm = alarm;
        this.exchange = exchange;
        this.listenTopicInfo = listenTopicInfo;
        this.connection = connection;
        this.channel = connection.createChannel();
    }


    @Override
    public void run() {
        try {
            channel.exchangeDeclare(exchange, "topic");
            this.queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchange, listenTopicInfo);
            channel.basicConsume(queueName, true, this::deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}