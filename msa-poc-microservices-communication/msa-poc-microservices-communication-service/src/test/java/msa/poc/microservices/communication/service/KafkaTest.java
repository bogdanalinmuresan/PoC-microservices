package msa.poc.microservices.communication.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class KafkaTest {

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, "communication-messaging-topic");

    @Test
    public void test() {

        Map<String, Object> senderProps = KafkaTestUtils.producerProps(KafkaTest.embeddedKafka);
        Map<String, Object> consumerProps =
                KafkaTestUtils.consumerProps("none", "false", embeddedKafka);
        consumerProps.put("auto.offset.reset", "earliest");

        KafkaProducer<Integer, String> producer = new KafkaProducer<>(senderProps);
        KafkaConsumer<Integer, String> kafkaConsumer = new KafkaConsumer<>(consumerProps);

        producer.send(new ProducerRecord<>("communication-messaging-topic", 0, 0, "Prueba1"));

        final CountDownLatch latch = new CountDownLatch(10);

        kafkaConsumer.subscribe(Collections.singletonList("communication-messaging-topic"));

        Iterator<ConsumerRecord<Integer,String>> iterator =  kafkaConsumer.poll(1000).iterator();
        while(iterator.hasNext()) {
            assertEquals("Prueba1", iterator.next().value());
        }
    }

}