package agh.edu.pl.smarthome;
import java.io.IOException;
import java.util.Random;

public class MoveService extends HomeService{

    public MoveService(String envName, String publishTopic) {
        super(envName, publishTopic);
    }

    public void run() {
        /* Implementation of this service functionality */

        /* Creating receiver */
        this.createSenderAndReceiver();
        /* Constantly sending  */
        while(true) {
            try {
                double x = new Random().nextDouble();
                if (x > 0.70) {
                    this.sender.send("x");
                }
                Thread.sleep(1000);
            } catch (InterruptedException | IOException e){
                e.printStackTrace();
            }
        }
        /* Running them */
    }
}
