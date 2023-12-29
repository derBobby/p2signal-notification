package eu.planlos.p2signalnotification.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/*
 * Reason for this class:
 * https://stackoverflow.com/a/72172859/7350955
 */
@Configuration
@ComponentScan(basePackages = "eu.planlos.javaspringwebutilities")
public class SpringWebUtilsConfig {
}
