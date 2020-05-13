package agh.edu.pl.smarthome;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;


public class Sender{

    private Connection connection;
    private Channel channel;
    private String exchange;
    private String publishTopic;

    public Sender(
            Connection connection,
            String exchange,
            String publishTopic
    ) throws IOException {
        this.exchange = exchange;
        this.publishTopic = publishTopic;
        this.connection = connection;
        this.channel = connection.createChannel();
        channel.exchangeDeclare(exchange, "topic");
    }

    public void send(String message) throws IOException {
        channel.basicPublish(exchange, publishTopic, null, message.getBytes());
    }
    
}