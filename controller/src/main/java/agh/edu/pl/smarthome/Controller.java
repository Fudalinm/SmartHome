package agh.edu.pl.smarthome;

import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class Controller{
    private List<DeviceController> deviceControllers;

    public static void main(String[] argv) throws Exception {
        /* Get envs */
        String host = Controller.getEnvOrDefault("RABBIT_HOST", "rabbitmq");
        String password = Controller.getEnvOrDefault("RABBIT_PASSWORD", "rabbitmq");
        String username = Controller.getEnvOrDefault("RABBIT_USERNAME", "rabbitmq");
        String vhost = Controller.getEnvOrDefault("RABBIT_VHOST", "smarthome");

        String exchange = Controller.getEnvOrDefault("SMARTHOME_EXCHANGE", "smarthome");

        System.out.println("Host: " + host);
        System.out.println("password: " + password);
        System.out.println("username: " + username);
        System.out.println("vhost: " + vhost);
        System.out.println("exchange: " + exchange);

        /* Init Controller */
        Controller controller = new Controller(host,password,username,vhost,exchange);

        controller.controlLoop();

    }

    private void controlLoop() throws IOException, InterruptedException{
        while(true){
            System.out.println("Chose device: \n");
            for(int i=0;i<deviceControllers.size();i++){
                System.out.println(i + "- " + deviceControllers.get(i).deviceName);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int deviceNumber = -1;
            try {
                deviceNumber = Integer.parseInt(reader.readLine());
            }catch (NumberFormatException e){
                System.out.println("Wrong format. Try again");
                //For now because input badly held
                Thread.sleep(60*1000);
                continue;
            }
            if(deviceNumber < 0 || deviceNumber > deviceControllers.size() - 1){
                System.out.println("Wrong int. try again");
                continue;
            }
            DeviceController currentDevice = deviceControllers.get(deviceNumber);
            System.out.println(currentDevice.help());

            if ( 0 > currentDevice.controlDevice()){
                System.out.println("Error while sending request");
            }
        }
    }

    public Controller(String host,String password,String username,  String vhost, String exchange) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPassword(password);
        factory.setUsername(username);
        factory.setVirtualHost(vhost);

        System.out.println("Creating controllers\n");

        /* Like lots of things its not nice but it works :) */
        AlarmController alarmController = new AlarmController("alarmController1",factory.newConnection(),exchange,Controller.getEnvOrDefault("ALARM_CONTROL", "room1.alarm.control"));
        BlindsController blindsController = new BlindsController("blindsController1",factory.newConnection(),exchange,Controller.getEnvOrDefault("BLINDS_ROOM1_CONTROL", "room1.blinds.control"));
        LightsController lightsController = new LightsController("lightsController1",factory.newConnection(),exchange,Controller.getEnvOrDefault("LIGHTS_ROOM1_CONTROL", "room1.lights.control"));
        RadiatorController radiatorController = new RadiatorController("RadiatorController1",factory.newConnection(),exchange,Controller.getEnvOrDefault("RADIATOR_ROOM1_CONTROL", "room1.temperature.control"));

        this.deviceControllers = new LinkedList<>();
        deviceControllers.add(alarmController);
        deviceControllers.add(blindsController);
        deviceControllers.add(lightsController);
        deviceControllers.add(radiatorController);
    }

    public static String getEnvOrDefault(String env, String def) {
        String en = System.getenv(env);
        if(en != null) {
            return en;
        } else {
            return def;
        }
    }

}
