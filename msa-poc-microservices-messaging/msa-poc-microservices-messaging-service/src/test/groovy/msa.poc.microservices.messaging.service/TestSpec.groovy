package msa.poc.microservices.messaging.service

import com.fasterxml.jackson.databind.ObjectMapper
import msa.poc.microservices.common.response.InternalStatus
import msa.poc.microservices.communication.api.dto.CommunicationDto
import msa.poc.microservices.communication.api.request.CommunicationRequest
import msa.poc.microservices.messaging.api.request.MessagingRequest
import msa.poc.microservices.messaging.api.response.MessagingResponse
import msa.poc.microservices.messaging.controller.MessagingController
import msa.poc.microservices.messaging.service.impl.MessagingServiceImpl
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

class TestSpec extends Specification {

    /* MessagingService */
    def "MessagingService"() {
        given:
        MessagingService messagingService = new MessagingServiceImpl()

        expect:
        def r = messagingService.sendNotification(messagingRequest)
        r.getInfo().equals("Email sent")
        r.getStatus() == InternalStatus.OK

        where:
        [messagingRequest] << MessagingServiceData()
    }

    def "MessagingServiceData"() {
        MessagingRequest messagingRequest = new MessagingRequest()

        [[messagingRequest]]
    }

    /* MessagingController */
    def "MessagingController"() {
        given:
        MessagingResponse messagingResponse = new MessagingResponse()
        messagingResponse.setResponse(InternalStatus.OK,"OK")
        MessagingServiceImpl messagingService = Mock{
            sendNotification(_) >> messagingResponse
        }
        MessagingController messagingController = new MessagingController(messagingService: messagingService)

        CommunicationRequest communicationRequest = new CommunicationRequest()
        communicationRequest.setCommunicationDto(new CommunicationDto())

        ObjectMapper objectMapper = new ObjectMapper()
        String s = objectMapper.writeValueAsString(communicationRequest)

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(messagingController).build()

        expect:
        def response = mockMvc.perform(MockMvcRequestBuilders.post("/messaging/").
                contentType(MediaType.APPLICATION_JSON).content(s)).andExpect(MockMvcResultMatchers.status().isOk())
    }
}