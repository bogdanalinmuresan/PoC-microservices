package msa.poc.microservices.messaging.service;

import msa.poc.microservices.messaging.api.request.MessagingRequest;
import msa.poc.microservices.messaging.api.response.MessagingResponse;

public interface MessagingService {

	MessagingResponse sendNotification(MessagingRequest messagingRequest);

}