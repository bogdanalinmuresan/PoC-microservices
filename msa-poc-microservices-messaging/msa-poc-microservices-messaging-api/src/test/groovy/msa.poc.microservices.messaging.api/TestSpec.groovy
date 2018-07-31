package msa.poc.microservices.messaging.api

import msa.poc.microservices.messaging.api.dto.MessagingDto
import msa.poc.microservices.messaging.api.request.MessagingRequest
import msa.poc.microservices.messaging.api.response.MessagingResponse
import spock.lang.Specification;

class TestSpec extends Specification {

    /* MessagingDto */
    def "MessagingDto"() {
        given:
        MessagingDto messagingDto = new MessagingDto()

        expect:
        messagingDto.setFrom(from)
        messagingDto.setTo(to)
        messagingDto.setSubject(subject)
        messagingDto.setBody(body)

        messagingDto.getFrom() == from
        messagingDto.getTo() == to
        messagingDto.getSubject() == subject
        messagingDto.getBody() == body

        where:
        from | to | subject | body
        "From 1" | "To 1" | "Subject 1" | "Body 1"
        "From 2" | "To 2" | "Subject 2" | "Body 2"
        "From 3" | "To 3" | "Subject 3" | "Body 3"
        "From 4" | "To 4" | "Subject 4" | "Body 4"
    }

    /* MessagingRequest */
    def "MessagingRequest"() {
        given:
        MessagingRequest messagingRequest = new MessagingRequest()

        expect:
        messagingRequest.setMessagingDto(messagingDto)

        where:
        [messagingDto] << MessagingRequestData()
    }

    def "MessagingRequestData"() {
        MessagingDto messagingDto = new MessagingDto()

        [[messagingDto]]
    }

    /* MessagingResponse */
    def "MessagingResponse"() {
        given:
        MessagingResponse messagingResponse = new MessagingResponse()

        expect:
        messagingResponse.setMessagingDto(messagingDto)

        where:
        [messagingDto] << MessagingResponseData()
    }

    def "MessagingResponseData"() {
        MessagingDto messagingDto = new MessagingDto()

        [[messagingDto]]
    }
}