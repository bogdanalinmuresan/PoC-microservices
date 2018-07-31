package msa.poc.microservices.communication.service.service.impl;

import msa.poc.microservices.common.Action;
import msa.poc.microservices.communication.api.request.CommunicationRequest;
import msa.poc.microservices.communication.service.service.ComposeService;
import msa.poc.microservices.user.api.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ComposeServiceImpl implements ComposeService {

    @Autowired
    private MessageSource messageSource;

    public CommunicationRequest compose(Action action, UserDto userDto) {
        CommunicationRequest communicationRequest = new CommunicationRequest();

        Object [] args = {userDto.getEmail(),messageSource.getMessage("communication.email.action." + action.name(),null,LocaleContextHolder.getLocale())};

        communicationRequest.getCommunicationDto().setAction(action);
        communicationRequest.getCommunicationDto().setBody(messageSource.getMessage("communication.email.separator1",args, LocaleContextHolder.getLocale()));
        communicationRequest.getCommunicationDto().setFrom("info@ust-global.com");
        communicationRequest.getCommunicationDto().setTo(userDto.getEmail());

        return communicationRequest;
    }
}
