package agh.edu.pl.smarthome;

import com.rabbitmq.client.Delivery;

import java.io.UnsupportedEncodingException;

public interface RabbitCallback {
    public void receiverFunction(String consumerTag, Delivery delivery) throws UnsupportedEncodingException;
}
