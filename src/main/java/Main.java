import utils.ConfigurationUtil;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println(ConfigurationUtil.getConfigurationEntry("ONE"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}