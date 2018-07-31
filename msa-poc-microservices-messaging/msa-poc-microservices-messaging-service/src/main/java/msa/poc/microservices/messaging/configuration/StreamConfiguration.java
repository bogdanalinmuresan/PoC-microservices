package msa.poc.microservices.messaging.configuration;

import msa.poc.microservices.messaging.stream.NotificationStreamProcessor;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(NotificationStreamProcessor.class)
public class StreamConfiguration {
}
