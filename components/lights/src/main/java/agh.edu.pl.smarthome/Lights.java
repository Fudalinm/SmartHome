package agh.edu.pl.smarthome;

public class Lights {
    public static void main(String[] argv) throws Exception {
        for(int i=0;i<20;i++) {
            System.out.println("Lights " + i);
            Thread.sleep(2000);
        }
    }
}
