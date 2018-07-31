package msa.poc.microservices.user.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Service
public interface NotificationStreamProcessor {

    @Output("msa-poc-output")
    MessageChannel sendNotification();

}
