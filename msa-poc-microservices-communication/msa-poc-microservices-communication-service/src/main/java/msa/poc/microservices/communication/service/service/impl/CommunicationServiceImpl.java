package msa.poc.microservices.communication.service.service.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import msa.poc.microservices.common.response.InternalStatus;
import msa.poc.microservices.communication.api.request.CommunicationRequest;
import msa.poc.microservices.communication.service.service.CommunicationService;
import msa.poc.microservices.messaging.api.response.MessagingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class CommunicationServiceImpl implements CommunicationService{

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ust.messaging.service.url:default}")
    private String serviceUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Override
    @HystrixCommand(fallbackMethod = "sendNotificationFallback")
    public MessagingResponse sendNotification(CommunicationRequest communicationRequest) {
        HttpEntity<CommunicationRequest> httpEntity = new HttpEntity<>(communicationRequest,buildRequestHeaders());
        final ResponseEntity<MessagingResponse> messagingResponse = restTemplate.exchange(serviceUrl, HttpMethod.POST,httpEntity, MessagingResponse.class);

        return messagingResponse.getBody();
    }

    public MessagingResponse sendNotificationFallback(CommunicationRequest communicationRequest) {
        MessagingResponse messagingResponse = new MessagingResponse();

        messagingResponse.setResponse(InternalStatus.ERROR,"Messaging service is not available");

        return messagingResponse;
    }
    
	 private HttpHeaders buildRequestHeaders() {
         HttpHeaders headers = new HttpHeaders();
         headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
         headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
         return headers;
     }
    
    
}
