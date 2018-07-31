package msa.poc.microservices.communication.service

import msa.poc.microservices.common.Action
import msa.poc.microservices.communication.api.dto.CommunicationDto
import msa.poc.microservices.communication.api.request.CommunicationRequest
import spock.lang.Specification;

class TestSpec extends Specification {
    def "CommunicationDto"() {
        given:
        CommunicationDto communicationDto = new CommunicationDto()

        expect:
        communicationDto.setAction(action)
        communicationDto.setBody(body)
        communicationDto.setFrom(from)
        communicationDto.setTo(to)
        communicationDto.setSubject(subject)

        communicationDto.getAction() == action
        communicationDto.getBody() == body
        communicationDto.getFrom() == from
        communicationDto.getTo() == to
        communicationDto.getSubject() == subject

        where:
        action | body | from | to | subject
        Action.ADD | "Test body" | "Test from" | "Test to" | "Test subject"
        Action.DELETE | "Test body1" | "Test from1" | "Test to1" | "Test subject1"
        Action.MODIFY | "Test body2" | "Test from2" | "Test to2" | "Test subject2"
    }

    def "CommunicationRequest"() {
        given:
        CommunicationRequest request = new CommunicationRequest()

        expect:
        request.setCommunicationDto(communicationDto)

        request.getCommunicationDto() == communicationDto

        where:
        [communicationDto] << CommunicationRequestData()

    }

    def "CommunicationRequestData"() {
        CommunicationDto communicationDto = new CommunicationDto()
        CommunicationDto communicationDto1 = new CommunicationDto()

        [[communicationDto] ,[communicationDto1]]
    }

    def "CommunicationResponse"() {
        // Nada para probar
    }
}