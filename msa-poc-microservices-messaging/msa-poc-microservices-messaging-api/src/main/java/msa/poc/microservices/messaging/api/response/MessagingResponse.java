package msa.poc.microservices.messaging.api.response;

import msa.poc.microservices.common.response.BaseResponse;
import msa.poc.microservices.messaging.api.dto.MessagingDto;

public class MessagingResponse extends BaseResponse {
	
	private MessagingDto messagingDto;
	
	public MessagingDto getMessagingDto() {
		return messagingDto;
	}
	public void setMessagingDto(MessagingDto messagingDto) {
		this.messagingDto = messagingDto;
	}
	
	
}
