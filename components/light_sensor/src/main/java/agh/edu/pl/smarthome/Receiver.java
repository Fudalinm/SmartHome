package agh.edu.pl.smarthome;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
    private Sensor sensor;

    public void deliverCallback(String consumerTag, Delivery delivery) throws UnsupportedEncodingException {
        /* Receiving on .env so we can only receive information from simulator and the possible fields are
        *  BlindsUsage, LightsUsage, ExternalLight
        */

        JsonObject receivedJson = new Gson().fromJson(new String(delivery.getBody(), "UTF-8"),JsonObject.class);
        System.out.println(" Light sensor received: " +
                delivery.getEnvelope().getRoutingKey() + "':'" + receivedJson.toString() + "'");

        int blindsUsage = receivedJson.get("BlindsUsage").getAsInt();
        int lightsUsage = receivedJson.get("LightsUsage").getAsInt();
        double externalLight = receivedJson.get("ExternalLight").getAsDouble();

        this.sensor.setExternalLight(externalLight);
        this.sensor.setLightsUsage(lightsUsage);
        this.sensor.setBlindsUsage(blindsUsage);
        this.sensor.updateInternalLight();
    }

    public Receiver(
            Connection connection,
            String exchange,
            String listenTopic,
            Sensor sensor
    ) throws IOException {
        this.exchange = exchange;
        this.listenTopic = listenTopic;
        this.connection = connection;
        this.channel = connection.createChannel();
        this.sensor = sensor;

        channel.exchangeDeclare(exchange, "topic");
        this.queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, exchange, listenTopic);

        channel.basicConsume(queueName, true, this::deliverCallback, consumerTag -> { });
    }


}