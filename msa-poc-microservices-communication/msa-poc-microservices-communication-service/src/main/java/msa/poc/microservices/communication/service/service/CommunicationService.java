package msa.poc.microservices.communication.service.service;


import msa.poc.microservices.communication.api.request.CommunicationRequest;
import msa.poc.microservices.messaging.api.response.MessagingResponse;

public interface CommunicationService {
    MessagingResponse sendNotification(CommunicationRequest communicationRequest);
}
