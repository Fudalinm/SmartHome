package agh.edu.pl.smarthome;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.io.IOException;


public class Receiver implements Runnable{

    private Connection connection;
    private Channel channel;
    private String exchange;
    private String listenTopic;
    private String queueName;
    private RabbitCallback rabbitCallback;

    public Receiver(
            Connection connection,
            String exchange,
            String listenTopic
    ) throws IOException {
        this.exchange = exchange;
        this.listenTopic = listenTopic;
        this.connection = connection;
        this.channel = connection.createChannel();

        channel.exchangeDeclare(exchange, "topic");
        this.queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, exchange, listenTopic);

    }
    
    public void setCallback(RabbitCallback rc){
        this.rabbitCallback = rc;
    }
    
    public void run(){
        try {
            channel.basicConsume(queueName, true, this.rabbitCallback::receiverFunction, consumerTag -> {});
        } catch (IOException e){
            System.out.println("Couldn't consume messages");
            e.printStackTrace();
        }
    }


}