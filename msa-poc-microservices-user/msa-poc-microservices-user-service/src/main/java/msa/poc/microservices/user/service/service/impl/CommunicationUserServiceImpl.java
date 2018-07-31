package msa.poc.microservices.user.service.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import msa.poc.microservices.common.response.InternalStatus;
import msa.poc.microservices.communication.api.response.CommunicationResponse;
import msa.poc.microservices.user.api.request.UserRequest;
import msa.poc.microservices.user.service.service.CommunicationUserService;


@Service
public class CommunicationUserServiceImpl implements CommunicationUserService {

    @Value("${ust.communication.service.url}")
    private String serviceUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    @HystrixCommand(fallbackMethod = "sendNotificationFallback")
    public CommunicationResponse sendNotification(UserRequest userRequest) {
        final ResponseEntity<CommunicationResponse> response;
        HttpEntity<?> httpEntity = new HttpEntity<>(userRequest, buildRequestHeaders());
            response = restTemplate.exchange(serviceUrl, HttpMethod.POST, httpEntity,
                    CommunicationResponse.class);

        return response.getBody();

    }

    public CommunicationResponse sendNotificationFallback(UserRequest userRequest) {
        CommunicationResponse communicationResponse = new CommunicationResponse();

        communicationResponse.setResponse(InternalStatus.ERROR,"Communication service is not available");

        return communicationResponse;
    }

    private HttpHeaders buildRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }


}