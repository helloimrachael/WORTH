package utility;

import java.util.Random;

public class UDPportGenerator {

    public synchronized static int generatePortUDP() {
        Random ran = new Random();
        int portUDP = ran.nextInt(65535);
        while (portUDP < 49152) {
            portUDP = ran.nextInt(65535);
        }
        return portUDP;
    }
}
