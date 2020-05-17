package agh.edu.pl.smarthome;

import com.google.gson.JsonObject;
import java.io.IOException;
import com.rabbitmq.client.Connection;

public abstract class DeviceController {
    public Sender sender;
    public String deviceName;
    
    public abstract JsonObject buildRequest();

    public DeviceController(String name, Connection senderConnection,String exchange,String topic){
        this.deviceName = name;
        try {
            this.sender = new Sender(senderConnection, exchange, topic);
        }catch (IOException e){
            System.out.println("Error while creating sender");
            e.printStackTrace();
        }
    }
    
    public int controlDevice() throws IOException {
        JsonObject requestToSend = buildRequest();
        if (requestToSend == null){
            return -1;
        }
        sender.send(requestToSend.toString());
        return 1;
    }
    
    public abstract String help();
}
