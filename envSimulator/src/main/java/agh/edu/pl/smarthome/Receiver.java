package agh.edu.pl.smarthome;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Delivery;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


public class Receiver{

    private Connection connection;
    private Channel channel;
    private String exchange;
    private String listenTopic;
    private String queueName;

    public void deliverCallback(String consumerTag, Delivery delivery) throws UnsupportedEncodingException {
        System.out.println(consumerTag);
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [x] Received '" +
                delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
    }

    public Receiver(
            Connection connection,
            String exchange,
            String listenTopic,
            RabbitCallback rabbitCallback
    ) throws IOException {
        this.exchange = exchange;
        this.listenTopic = listenTopic;
        this.connection = connection;
        this.channel = connection.createChannel();

        channel.exchangeDeclare(exchange, "topic");
        this.queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, exchange, listenTopic);

        channel.basicConsume(queueName, true, rabbitCallback::receiverFunction, consumerTag -> { });
    }


}