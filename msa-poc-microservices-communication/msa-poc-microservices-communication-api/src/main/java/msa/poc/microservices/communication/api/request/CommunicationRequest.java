package msa.poc.microservices.communication.api.request;



import msa.poc.microservices.common.request.BaseRequest;
import msa.poc.microservices.communication.api.dto.CommunicationDto;

public class CommunicationRequest extends BaseRequest {

	private CommunicationDto communicationDto;

	public CommunicationRequest() {
		super();
		this.communicationDto = new CommunicationDto();
	}
	public CommunicationRequest(CommunicationDto communicationDto) {
		super();
		this.communicationDto = communicationDto;
	}

	public CommunicationDto getCommunicationDto() {
		return communicationDto;
	}

	public void setCommunicationDto(CommunicationDto communicationDto) {
		this.communicationDto = communicationDto;
	}

}
