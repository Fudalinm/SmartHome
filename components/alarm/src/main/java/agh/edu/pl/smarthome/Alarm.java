package agh.edu.pl.smarthome;

public class Alarm {
    public static void main(String[] argv) throws Exception {
        for(int i=0;i<20;i++) {
            System.out.println("Alarm " + i);
            Thread.sleep(2000);
        }
    }
}
