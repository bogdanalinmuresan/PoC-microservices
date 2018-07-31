package msa.poc.microservices.messaging.controller;

import msa.poc.microservices.common.response.InternalStatus;
import msa.poc.microservices.communication.api.request.CommunicationRequest;
import msa.poc.microservices.messaging.api.dto.MessagingDto;
import msa.poc.microservices.messaging.api.request.MessagingRequest;
import msa.poc.microservices.messaging.api.response.MessagingResponse;
import msa.poc.microservices.messaging.service.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/messaging")
public class MessagingController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

	
    @PostMapping(path="/", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<MessagingResponse> request(@RequestBody CommunicationRequest communicationRequest) {
        
    	ResponseEntity<MessagingResponse> response;
        StringBuilder tracking = new StringBuilder();
        MessagingResponse messagingResponse= new MessagingResponse();
        
        messagingResponse.setStatus(InternalStatus.OK);
        
        messagingResponse.setInfo("Message sent correctly ");
        messagingResponse.setMessagingDto(new MessagingDto(
        		communicationRequest.getCommunicationDto().getFrom(),
        		communicationRequest.getCommunicationDto().getTo(),
        		communicationRequest.getCommunicationDto().getBody(),
        		communicationRequest.getCommunicationDto().getSubject()));
        
        

	    log.info("Email sent");
	    response = ResponseEntity.status(HttpStatus.OK).body(messagingResponse);
      
        
        tracking.append("[COMMUNICATION_REQUEST]:").append(communicationRequest.toString());
        MDC.put("TRACKING", tracking.toString());
        
        tracking.append("[MESSAGING_RESPONSE_REQUEST]:").append(messagingResponse.toString());
        MDC.put("TRACKING", tracking.toString());
        

        return response;
    }

    @StreamListener("msa-poc-input")
    public void notificationListener(CommunicationRequest communicationRequest) {
        log.info("Notification received from Kafka: " + communicationRequest.toString());

        log.info("Email sent");

    }
}
