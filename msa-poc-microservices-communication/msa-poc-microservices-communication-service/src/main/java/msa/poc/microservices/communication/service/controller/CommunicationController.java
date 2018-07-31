package msa.poc.microservices.communication.service.controller;

import msa.poc.microservices.common.response.InternalStatus;
import msa.poc.microservices.communication.api.request.CommunicationRequest;
import msa.poc.microservices.communication.api.response.CommunicationResponse;
import msa.poc.microservices.communication.service.service.CommunicationService;
import msa.poc.microservices.communication.service.service.ComposeService;
import msa.poc.microservices.communication.service.stream.NotificationStreamProcessor;
import msa.poc.microservices.messaging.api.response.MessagingResponse;
import msa.poc.microservices.user.api.request.UserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

@RefreshScope
@RestController
public class CommunicationController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CommunicationService messagingService;

    @Autowired
    private ComposeService composeService;

    @Autowired
    private NotificationStreamProcessor notificationStreamProcessor;

    @Value("${message:Hello default}")
	private String message;
	
	@RequestMapping("/message")
	String getMessage() {
		return this.message;
	}
	
    @RequestMapping(value="/comms", method = RequestMethod.POST,
            consumes = { MediaType.APPLICATION_JSON_VALUE },
            produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<CommunicationResponse> request(@RequestBody UserRequest userRequest) {

        CommunicationRequest communicationRequest = new CommunicationRequest();
        CommunicationResponse communicationResponse = new CommunicationResponse();
        ResponseEntity<CommunicationResponse> response;
        MessagingResponse messagingResponse = new MessagingResponse();
        StringBuilder tracking = new StringBuilder();

        communicationRequest = this.composeService.compose(userRequest.getAction(),userRequest.getUserDto());

        tracking.append("[USER_REQUEST]:").append(userRequest.toString());
        MDC.put("TRACKING", tracking.toString());

        try {
        	/* Send notification to messaging microservices */
            messagingResponse = messagingService.sendNotification(communicationRequest);

            //send to user microservice the response from messaging microservice
            communicationResponse.setResponse(messagingResponse.getStatus(), messagingResponse.getInfo());
            
            response = ResponseEntity.status(HttpStatus.OK).body(communicationResponse);
            
        }catch(Exception e) {
        	response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(communicationResponse);
        	log.info("Exception trying to communicate with messaging" +e.getStackTrace());
        }
        
        log.info("messagingResponse info on communication" +messagingResponse.getStatus());
        
        /* Get and process response */
        if(messagingResponse.getStatus() == InternalStatus.OK) {
            log.info(" User information sent");
        }else{
        	 log.info(" User information NOT sent");
        }

        return response;
    }

    @StreamListener("msa-poc-input")
    public void notificationListener(UserRequest userRequest) {

        StringBuilder tracking = new StringBuilder();

        log.info("Notification received from Kafka: " + userRequest.toString());

        CommunicationRequest communicationRequest = this.composeService.compose(userRequest.getAction(), userRequest.getUserDto());

        tracking.append("[USER_REQUEST]:").append(userRequest.toString());
        MDC.put("TRACKING", tracking.toString());

        notificationStreamProcessor.sendNotification().send(MessageBuilder.withPayload(communicationRequest).build());

    }

}
