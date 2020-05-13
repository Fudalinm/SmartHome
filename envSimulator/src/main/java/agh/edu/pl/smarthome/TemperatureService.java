package agh.edu.pl.smarthome;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Delivery;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

public class TemperatureService extends HomeService {
    public TemperatureState temperatureState;
    private class TemperatureRabbitCallback implements RabbitCallback{
        private Sender sender;
        private TemperatureState temperatureState;
        private TemperatureRabbitCallback(Sender sender,TemperatureState temperatureState){
            this.sender = sender;
            this.temperatureState = temperatureState;
        }
        @Override
        public void receiverFunction(String consumerTag, Delivery delivery) throws UnsupportedEncodingException {
            /* Proccesing message with response using s*/
            JsonObject receivedJson = new Gson().fromJson(new String(delivery.getBody(), "UTF-8"),JsonObject.class);
            System.out.println(" Temperature receiver received: " +
                    delivery.getEnvelope().getRoutingKey() + "':'" + receivedJson.toString() + "'");
            
            int radiatorUsage = receivedJson.get("RadiatorUsage").getAsInt();
            this.temperatureState.setRadiatorUsage(radiatorUsage);
            try {
                
                this.sender.send(this.temperatureState.createJsonToSend().toString());
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private class TemperatureState {
        private Integer externalTemperature; //
        private Integer radiatorUsage; //

        private TemperatureState(){
            this.externalTemperature = (new Random().nextInt(25)+5)*100; //initial temp between 5 and 30 celcius degrees
            this.radiatorUsage = 0;
        }

        public void setRadiatorUsage(int radiatorUsage) {
            this.radiatorUsage = radiatorUsage;
        }

        public void updateExternalTemperature(){
            this.externalTemperature += (new Random().nextInt(200)-100);
        }

        public double getExternalTemperature(){
            return this.externalTemperature/100.0;
        }

        public JsonObject createJsonToSend(){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("ExternalTemperature",this.getExternalTemperature());
            jsonObject.addProperty("RadiatorUsage",this.radiatorUsage);

            return jsonObject;
        }
    }


    public TemperatureService(String envName, String publishTopic) {
        super(envName,publishTopic);
    }

    public void run(){
        /* Implementation of this service functionality */
            /* Creating temperature state */
        this.temperatureState = new TemperatureState();
            /* Creating sender && receiver */
        this.createSenderAndReceiver();
            /* Setting receiver callback */
        this.rabbitCallback = new TemperatureRabbitCallback(this.sender,this.temperatureState);
        this.receiver.setCallback(this.rabbitCallback);
            /* Running receiver */
        Thread receiverThread = new Thread(this.receiver);
        receiverThread.start();
        
            /* Running */
        while(true){
            try {
                Thread.sleep(5000);
                this.temperatureState.updateExternalTemperature();
                this.sender.send(this.temperatureState.createJsonToSend().toString());
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
