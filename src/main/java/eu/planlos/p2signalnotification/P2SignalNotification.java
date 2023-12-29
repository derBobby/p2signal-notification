package eu.planlos.p2signalnotification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class P2SignalNotification {

    public static void main(String[] args) {
        SpringApplication.run(P2SignalNotification.class, args);
    }
}