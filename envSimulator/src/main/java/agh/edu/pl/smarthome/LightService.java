package agh.edu.pl.smarthome;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Delivery;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

public class LightService extends HomeService{
    private LightState lightState;
    private class LightRabbitCallback implements RabbitCallback{
        private Sender sender;
        private LightState lightState;
        private LightRabbitCallback(Sender sender, LightState lightState){
            this.sender = sender;
            this.lightState = lightState;
        }
        @Override
        public void receiverFunction(String consumerTag, Delivery delivery) throws UnsupportedEncodingException {
            /* Proccesing message with response using s*/
            JsonObject receivedJson = new Gson().fromJson(new String(delivery.getBody(), "UTF-8"),JsonObject.class);
            System.out.println(" Temperature receiver received: " +
                    delivery.getEnvelope().getRoutingKey() + "':'" + receivedJson.toString() + "'");

            if (receivedJson.get("BlindsUsage") != null){
                this.lightState.setBlindsUsage(receivedJson.get("BlindsUsage").getAsInt());
            }
            if (receivedJson.get("LightsUsage") != null){
                this.lightState.setLightsUsage(receivedJson.get("LightsUsage").getAsInt());
            }
            try {
                this.sender.send(this.lightState.createJsonToSend().toString());
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private class LightState{
        private Integer blindsUsage;
        private Integer lightsUsage;
        private double externalLight;

        private LightState(){
            this.externalLight = new Random().nextDouble() * 7000;
            this.blindsUsage = 0;
            this.lightsUsage = 0;
        }

        public void setBlindsUsage(Integer blindsUsage) {
            this.blindsUsage = blindsUsage;
        }

        public void setLightsUsage(Integer lightsUsage) {
            this.lightsUsage = lightsUsage;
        }

        public void updateExternalLight() {
            this.externalLight += (new Random().nextDouble() * 1000 - 500);
        }

        public JsonObject createJsonToSend(){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("LightsUsage",this.lightsUsage);
            jsonObject.addProperty("ExternalLight",this.externalLight);
            jsonObject.addProperty("BlindsUsage",this.blindsUsage);
            return jsonObject;
        }
    }

    public LightService(String envName,String publishTopic) {
        super(envName,publishTopic);
    }

    public void run() {
        /* Implementation of this service functionality */
        /* Creating temperature state */
        System.out.println("Set starting configuration");
        this.lightState = new LightState();
        /* Creating sender && receiver */
        System.out.println("Create sender and receiver");
        this.createSenderAndReceiver();
        /* Setting receiver callback */
        System.out.println("Setting callback for receiver");
        this.rabbitCallback = new LightRabbitCallback(this.sender,this.lightState);
        this.receiver.setCallback(this.rabbitCallback);
        /* Running receiver */
        System.out.println("Start receiving thread");
        Thread receiverThread = new Thread(this.receiver);
        receiverThread.start();

        /* Running */
        while(true){
            try {
                Thread.sleep(5000);
                System.out.println(this.lightState.createJsonToSend().toString());
                this.lightState.updateExternalLight();
                this.sender.send(this.lightState.createJsonToSend().toString());
            }catch (InterruptedException e){
                System.out.println("Sleeping exception");
                e.printStackTrace();
            }catch (IOException e){
                System.out.println("Exception while sending");
                e.printStackTrace();
            }
        }
    }
}
