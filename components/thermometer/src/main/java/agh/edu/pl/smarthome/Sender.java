package agh.edu.pl.smarthome;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.io.IOException;


public class Sender implements Runnable {
    private Connection connection;
    private Channel channel;
    private String exchange;
    private String publishTopic;
    private Sensor sensor;

    public Sender(
            Connection connection,
            String exchange,
            String publishTopic,
            Sensor sensor
    ) throws IOException {
        this.exchange = exchange;
        this.publishTopic = publishTopic;
        this.connection = connection;
        this.channel = connection.createChannel();
        this.sensor = sensor;
        channel.exchangeDeclare(exchange, "topic");
    }

    public void send(String message) throws IOException {
        channel.basicPublish(exchange, publishTopic, null, message.getBytes());
    }

    @Override
    public void run() {
        try {
            while(true) {
                    JsonObject jsonTemperature = sensor.getInternalTemperatureJson();
                    this.send(jsonTemperature.toString());
                    Thread.sleep(5000);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}