package agh.edu.pl.smarthome;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Thermometer {

    private Thread sender;
    private Receiver receiver;
    private Sensor sensor;

    public Thermometer(
            String host,
            String password,
            String username,
            String vhost,
            String exchange,
            String publishTopic,
            String listenTopic
    ) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPassword(password);
        factory.setUsername(username);
        factory.setVirtualHost(vhost);
        Connection connection = factory.newConnection();
        Connection connection2 = factory.newConnection();

        this.sensor = new Sensor();
        this.sender = new Thread(new Sender(connection, exchange, publishTopic,sensor));
        this.sender.start();
        this.receiver = new Receiver(connection2, exchange, listenTopic,sensor);
    }

    public static void main(String[] argv) throws Exception {
        String host = Thermometer.getEnvOrDefault("RABBIT_HOST", "localhost");
        String password = Thermometer.getEnvOrDefault("RABBIT_PASSWORD", "rabbitmq");
        String username = Thermometer.getEnvOrDefault("RABBIT_USERNAME", "rabbitmq");
        String vhost = Thermometer.getEnvOrDefault("RABBIT_VHOST", "smarthome");

        String exchange = Thermometer.getEnvOrDefault("SMARTHOME_EXCHANGE", "smarthome");
        String publishTopic = Thermometer.getEnvOrDefault("SMARTHOME_PUBLISH", "room1.temperature.info");
        String listenTopic = Thermometer.getEnvOrDefault("SMARTHOME_EXCHANGE", "room1.temperature.env");

        Thermometer dev = new Thermometer(host, password, username, vhost, exchange, publishTopic, listenTopic);
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