package utility;

import java.util.Random;

public class IPGenerator {

    public synchronized static String generateIPAddress() {
        Random ran = new Random();
        return ("226." + ran.nextInt(256) + "." + ran.nextInt(256) + "." + ran.nextInt(256));
    }
}