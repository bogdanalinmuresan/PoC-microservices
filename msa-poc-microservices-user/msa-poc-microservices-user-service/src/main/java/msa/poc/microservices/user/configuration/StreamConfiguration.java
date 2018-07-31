package msa.poc.microservices.user.configuration;

import msa.poc.microservices.user.stream.NotificationStreamProcessor;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(NotificationStreamProcessor.class)
public class StreamConfiguration {
}
