package msa.poc.microservices.messaging.api.request;

import msa.poc.microservices.common.request.BaseRequest;
import msa.poc.microservices.messaging.api.dto.MessagingDto;

public class MessagingRequest extends BaseRequest{
	
	private MessagingDto messagingDto;
	
	public MessagingDto getMessagingDto() {
		return messagingDto;
	}
	public void setMessagingDto(MessagingDto messagingDto) {
		this.messagingDto = messagingDto;
	}


}