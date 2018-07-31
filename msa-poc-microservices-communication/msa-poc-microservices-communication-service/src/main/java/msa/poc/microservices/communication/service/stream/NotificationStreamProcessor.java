package msa.poc.microservices.communication.service.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Service;

@Service
public interface NotificationStreamProcessor {

    @Input("msa-poc-input")
    SubscribableChannel readNotification();

    @Output("msa-poc-output")
    MessageChannel sendNotification();

}
