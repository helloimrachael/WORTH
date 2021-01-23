package utility;

import java.util.Random;

public class IPGenerator {
    public static String generateIPAddress() {
        Random ran = new Random();
        return (ran.nextInt(256) + "." + ran.nextInt(256) + "." + ran.nextInt(256) + "." + ran.nextInt(256));
    }
}