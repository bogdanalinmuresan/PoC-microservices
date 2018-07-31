package msa.poc.microservices.user.service.service;

import msa.poc.microservices.communication.api.response.CommunicationResponse;
import msa.poc.microservices.user.api.request.UserRequest;

public interface CommunicationUserService {

    CommunicationResponse sendNotification(UserRequest userRequest);

}