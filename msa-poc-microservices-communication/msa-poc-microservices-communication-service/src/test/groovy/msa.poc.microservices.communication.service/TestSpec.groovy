package msa.poc.microservices.communication.service

import com.fasterxml.jackson.databind.ObjectMapper
import msa.poc.microservices.common.Action
import msa.poc.microservices.common.response.InternalStatus
import msa.poc.microservices.communication.api.request.CommunicationRequest
import msa.poc.microservices.communication.service.controller.CommunicationController
import msa.poc.microservices.communication.service.service.ComposeService
import msa.poc.microservices.communication.service.service.impl.CommunicationServiceImpl
import msa.poc.microservices.communication.service.service.impl.ComposeServiceImpl
import msa.poc.microservices.communication.service.stream.NotificationStreamProcessor
import msa.poc.microservices.messaging.api.response.MessagingResponse
import msa.poc.microservices.user.api.dto.UserDto
import msa.poc.microservices.user.api.request.UserRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.MessageBuilder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class TestSpec extends Specification {

    @Autowired
    NotificationStreamProcessor notificationStreamProcessor

    /* ComposeService */
    def "ComposeService"() {
        given:
        ComposeService composeService = new ComposeServiceImpl(messageSource: Mock(MessageSource))

        expect:
        def r = composeService.compose(action,userDto).getCommunicationDto()
        r.getFrom() == "info@ust-global.com"
        r.getTo() == userDto.getEmail()
        r.getAction() == action

        where:
        [action, userDto] << ComposeServiceData()
    }

    def "ComposeServiceData"() {
        UserDto userDto1 = new UserDto()
        userDto1.setName("ComposeService - Name")
        userDto1.setSurname1("ComposeService - Surname1")
        userDto1.setSurname2("ComposeService - Surname2")
        userDto1.setEmail("ComposeService - Email")

        [[Action.ADD,userDto1]]
    }

    /* CommunicationController */
    def "CommunicationController"() {

        given:
        MessagingResponse messagingResponse = new MessagingResponse()
        messagingResponse.setResponse(InternalStatus.OK,"OK")
        RestTemplate restTemplate = Mock{
            exchange(_, _, _, MessagingResponse.class) >> ResponseEntity.status(HttpStatus.OK).body(messagingResponse);
        }
        CommunicationController communicationController =
                new CommunicationController(
                        messagingService: new CommunicationServiceImpl(restTemplate: restTemplate),
                        composeService: Mock(ComposeServiceImpl),
                        notificationStreamProcessor: Mock(NotificationStreamProcessor)
                )

        UserRequest userRequest = new UserRequest()
        UserDto userDto = new UserDto()
        userRequest.setUserDto(userDto)

        ObjectMapper objectMapper = new ObjectMapper()
        String s = objectMapper.writeValueAsString(userRequest)

        MockMvc mockMvc = standaloneSetup(communicationController).build()

        expect:
        def response = mockMvc.perform(MockMvcRequestBuilders.post("/comms").
                contentType(MediaType.APPLICATION_JSON).content(s)).andExpect(MockMvcResultMatchers.status().isOk())
    }

    /* CommunicationService */
    def "CommunicationService"() {
        given:
        CommunicationServiceImpl communicationService = new CommunicationServiceImpl()
        CommunicationRequest communicationRequest = new CommunicationRequest()

        expect:
        def r = communicationService.sendNotificationFallback(communicationRequest)
        r.getStatus() == InternalStatus.ERROR
        r.getInfo().equals("Messaging service is not available")
    }

    /*   -------   */
    def "NotificationStream"() {
        given:
        MessageChannel messageChannel = Mock{
            send(_) >> true
        }
        NotificationStreamProcessor notificationStreamProcessor = Mock{
            sendNotification() >> messageChannel
        }

        expect:
        notificationStreamProcessor.sendNotification().send(MessageBuilder.withPayload("Hola").build())
    }
}