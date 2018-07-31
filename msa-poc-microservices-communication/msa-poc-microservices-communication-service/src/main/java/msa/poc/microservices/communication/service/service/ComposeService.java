package msa.poc.microservices.communication.service.service;

import msa.poc.microservices.common.Action;
import msa.poc.microservices.communication.api.request.CommunicationRequest;
import msa.poc.microservices.user.api.dto.UserDto;

public interface ComposeService {

    CommunicationRequest compose(Action action, UserDto userDto);

}
