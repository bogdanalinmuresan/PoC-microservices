package msa.poc.microservices.communication.service.configuration;

import msa.poc.microservices.communication.service.stream.NotificationStreamProcessor;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(NotificationStreamProcessor.class)
public class StreamConfiguration {
}
