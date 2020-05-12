package agh.edu.pl.smarthome;

import com.rabbitmq.client.Delivery;

import java.io.UnsupportedEncodingException;

public class TemperatureService extends HomeService {

    private class TemperatureRabbitCallback implements RabbitCallback{
        private Sender sender;
        private TemperatureRabbitCallback(Sender s){
            this.sender = s;
        }
        @Override
        public void receiverFunction(String consumerTag, Delivery delivery) throws UnsupportedEncodingException {
            /* Proccesing message with response using s*/
        }
    }


    public TemperatureService(String envName, String publishTopic) {
        super(envName,publishTopic);
    }

    public void run(){
        /* Implementation of this service functionality */
            /* Creating sender */
            /* Creating receiver */
            /* Running them */
    }
}
