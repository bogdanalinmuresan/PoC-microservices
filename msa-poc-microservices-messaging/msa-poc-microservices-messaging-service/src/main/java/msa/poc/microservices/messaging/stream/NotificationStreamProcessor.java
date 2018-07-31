package msa.poc.microservices.messaging.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface NotificationStreamProcessor {

    @Input("msa-poc-input")
    SubscribableChannel readNotification();

}
