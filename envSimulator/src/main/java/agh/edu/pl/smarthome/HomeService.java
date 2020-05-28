package agh.edu.pl.smarthome;
import com.rabbitmq.client.Connection;
import java.io.IOException;

public abstract class HomeService implements Runnable{
    public String publishTopic;
    public String listenTopic;
    public Connection receiveConnection;
    public Connection senderConnection;
    public String exchange;
    public Sender sender;
    public Receiver receiver;
    public RabbitCallback rabbitCallback;

    public HomeService(String envName,String publishTopic){
        //publish topic like "room1.temperatur.environment" => listen topic like room1.temperatur.environment_feedback
        this.publishTopic = EnvironmentSimulator.getEnvOrDefault(envName,publishTopic); // this env should be different to allow multiple rooms
        this.listenTopic = EnvironmentSimulator.getEnvOrDefault(envName + "_FEEDBACK",publishTopic + "_feedback");
    }

    public void setConnection(Connection publishConnection, Connection receiveConnection){
        this.receiveConnection = receiveConnection;
        this.senderConnection = publishConnection;
    }

    public void createSenderAndReceiver(){
        try {
            this.sender = new Sender(this.senderConnection, this.exchange, this.publishTopic);
            this.receiver = new Receiver(this.receiveConnection, this.exchange, this.listenTopic);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void setExchange(String exchange){
        this.exchange = exchange;
    }
}