package msa.poc.microservices.messaging.service.impl;

import msa.poc.microservices.common.response.InternalStatus;
import msa.poc.microservices.messaging.api.request.MessagingRequest;
import msa.poc.microservices.messaging.api.response.MessagingResponse;
import msa.poc.microservices.messaging.service.MessagingService;
import org.springframework.stereotype.Service;

@Service
public class MessagingServiceImpl implements MessagingService{

	@Override
	public MessagingResponse sendNotification(MessagingRequest messagingRequest) {
		MessagingResponse messagingResponse = new MessagingResponse();
		messagingResponse.setResponse(InternalStatus.OK,"Email sent");

		return messagingResponse;
    }

}