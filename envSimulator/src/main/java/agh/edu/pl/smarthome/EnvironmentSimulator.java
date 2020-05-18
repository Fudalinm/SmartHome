package agh.edu.pl.smarthome;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class EnvironmentSimulator {
    private List<HomeService> homeServices;

    public EnvironmentSimulator(String host,String password,String username,  String vhost, String exchange,List<HomeService> services) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPassword(password);
        factory.setUsername(username);
        factory.setVirtualHost(vhost);

        this.homeServices = services;
        
        System.out.println("Connecting services\n");
        
        for(HomeService s: homeServices){
            s.setConnection(factory.newConnection(),factory.newConnection());
            s.setExchange(exchange);
        }

    }

    public static void main(String[] argv) throws Exception {
        String host = EnvironmentSimulator.getEnvOrDefault("RABBIT_HOST", "localhost");
        String password = EnvironmentSimulator.getEnvOrDefault("RABBIT_PASSWORD", "rabbitmq");
        String username = EnvironmentSimulator.getEnvOrDefault("RABBIT_USERNAME", "rabbitmq");
        String vhost = EnvironmentSimulator.getEnvOrDefault("RABBIT_VHOST", "smarthome");
        
        String exchange = EnvironmentSimulator.getEnvOrDefault("SMARTHOME_EXCHANGE", "smarthome");

        System.out.println("Host: " + host);
        System.out.println("password: " + password);
        System.out.println("username: " + username);
        System.out.println("vhost: " + vhost);
        System.out.println("exchange: " + exchange);


        /* Defining services with callbacks */
        List<HomeService> homeServices = new LinkedList<HomeService>();
        
        HomeService temperatureService = new TemperatureService("TEMPERATURE_ROOM_1","room1.temperature.env");
        homeServices.add(temperatureService);

        HomeService lightService = new LightService("LIGHT_ROOM_1","room1.light.env");
        homeServices.add(lightService);

        /*
        HomeService moveService = new HomeService("room1.move.env");
        moveService.setFunctionCallbacks();
        homeServices.add(moveService);
        */

        /* Creating main class */
        EnvironmentSimulator simulator = new EnvironmentSimulator(host,password,username,vhost,exchange,homeServices);

        /* Running simulator */
        simulator.runSimulator();

        while (true){
            try{
                Thread.sleep(2000);
                System.out.println("Still alive");
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }


    public static String getEnvOrDefault(String env, String def) {
        String en = System.getenv(env);
        if(en != null) {
            return en;
        } else {
            return def;
        }
    }

    public void runSimulator(){
        List<Thread> homeServicesThread = new LinkedList<>();
        for(HomeService service: this.homeServices){
            Thread t = new Thread(service);
            t.start();
            homeServicesThread.add(t);
        }
    }

}
