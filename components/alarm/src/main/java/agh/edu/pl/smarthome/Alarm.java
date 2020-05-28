package agh.edu.pl.smarthome;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class Alarm {

    private boolean isOn = true;
    private boolean isUp = false;

    public void setIsUp(boolean b) {
        this.isUp = b;
    }

    public void setIsOn(boolean b) {
        this.isOn = b;
    }

    public boolean getIsUp() {
        return this.isUp;
    }

    public boolean getIsOn() {
        return this.isOn;
    }

    public Alarm(
            String host,
            String password,
            String username,
            String vhost,
            String exchange,
            String listenTopicInfo,
            String listenTopicControl,
            String blindsControl,
            String lightsControl
    ) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPassword(password);
        factory.setUsername(username);
        factory.setVirtualHost(vhost);
        Connection connection = factory.newConnection();
        Connection connection2 = factory.newConnection();
        Connection connection3 = factory.newConnection();

        Thread sender = new Thread(new Sender(connection, exchange, blindsControl, lightsControl, this));
        Thread receiver = new Thread(new ReceiverControl(connection2, exchange, listenTopicControl, this));
        Thread receiver2 = new Thread(new ReceiverInfo(connection3, exchange, listenTopicInfo, this));

        sender.start();
        receiver.start();
        receiver2.start();
    }

    public static void main(String[] argv) throws Exception {
        String host = Alarm.getEnvOrDefault("RABBIT_HOST", "localhost");
        String password = Alarm.getEnvOrDefault("RABBIT_PASSWORD", "rabbitmq");
        String username = Alarm.getEnvOrDefault("RABBIT_USERNAME", "rabbitmq");
        String vhost = Alarm.getEnvOrDefault("RABBIT_VHOST", "smarthome");

        String exchange = Alarm.getEnvOrDefault("SMARTHOME_EXCHANGE", "smarthome");
        String listenTopicInfo = Alarm.getEnvOrDefault("LISTEN_TOPIC_INFO", "room1.move.info");
        String listenTopicControl = Alarm.getEnvOrDefault("LISTEN_TOPIC_CONTOLER", "room1.alarm.control");

        String blindsControl = Alarm.getEnvOrDefault("PUBLISH_BLINDS_CONTROL", "room1.blinds.control");
        String lightsControl = Alarm.getEnvOrDefault("PUBLISH_LIGHTS_CONTROL", "room1.lights.control");

        Alarm dev = new Alarm(host, password, username, vhost, exchange,  listenTopicInfo, listenTopicControl, blindsControl, lightsControl);
    }

    static String getEnvOrDefault(String env, String def) {
        String en = System.getenv(env);
        if(en != null) {
            return en;
        } else {
            return def;
        }
    }
}
