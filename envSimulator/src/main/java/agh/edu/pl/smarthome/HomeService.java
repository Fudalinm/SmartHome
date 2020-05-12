package agh.edu.pl.smarthome;
import com.rabbitmq.client.Connection;

public abstract class HomeService implements Runnable{
    public String publishTopic;
    public String listenTopic;
    public Connection connection;
    public String exchange;
    public Sender sender;
    public Receiver receiver;
    public RabbitCallback callback;

    public HomeService(String envName,String publishTopic){
        //publish topic like "room1.temperatur.environment" => listen topick like room1.temperatur.environment_feedback
        this.publishTopic = EnvironmentSimulator.getEnvOrDefault(envName,publishTopic); // this env should be different to allow multiple rooms
        this.listenTopic = EnvironmentSimulator.getEnvOrDefault(envName + "_FEEDBACK",publishTopic + "_feedback");
    }

    public void setConnection(Connection c){
        this.connection = c;
    }

    public void setExchange(String exchange){
        this.exchange = exchange;
    }
}