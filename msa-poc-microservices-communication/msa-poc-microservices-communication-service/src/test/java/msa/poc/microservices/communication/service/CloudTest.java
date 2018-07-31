package msa.poc.microservices.communication.service;

import msa.poc.microservices.communication.service.stream.NotificationStreamProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties={"con.prueba=prueba"})
public class CloudTest {

    @Autowired
    private NotificationStreamProcessor notificationStreamProcessor;

    @Autowired
    private MessageCollector messageCollector;

    @Test
    public void test() {
        notificationStreamProcessor.sendNotification().send(MessageBuilder.withPayload("Hola").build());

        Message<String> message = (Message<String>) messageCollector.forChannel(notificationStreamProcessor.sendNotification()).poll();
        assertThat(message.getPayload()).isEqualTo("Hola");
    }

}
