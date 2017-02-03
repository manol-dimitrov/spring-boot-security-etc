package com.zamrad.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.zamrad.dto.events.NewEventDto
import com.zamrad.dto.events.NewEventSlotDto
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class EventsDtoSerializationTest extends Specification {

    private ObjectMapper objectMapper;

    def setup() {
        objectMapper = new ObjectMapper()
    }

    def 'should serialize event and event slot dtos to json'() {
        given:
        def dto = createEventDto()

        when:
        def json = objectMapper.writeValueAsString(dto)
        String expectedJson = new String(Files.readAllBytes(Paths.get("src/test/resources/events-dto.json")))

        then:
        assert json != expectedJson
    }

    def 'should deserialize event and event slot to dto'() {
        given:
        String jsonToDeserialize = new String(Files.readAllBytes(Paths.get("src/test/resources/events-dto.json")))

        when:
        def expectedObject = objectMapper.readValue(jsonToDeserialize, NewEventDto.class)
        def dto = createEventDto()

        then:
        assert expectedObject == dto
    }

    NewEventDto createEventDto() {
        NewEventDto newEventDto = new NewEventDto()
        newEventDto.setType("Venue")
        newEventDto.setAddress("Shad Thames")
        newEventDto.setDescription("Amazing event")
        newEventDto.setDate("08-09-2016")
        newEventDto.setTitle("Test event")

        //add 1 slot to event
        newEventDto.setNewEventSlotDto(craeteEventSlotDto())

        return newEventDto
    }

    NewEventSlotDto craeteEventSlotDto() {
        NewEventSlotDto newEventSlotDto = new NewEventSlotDto()
        newEventSlotDto.setArtistType("Test")
        newEventSlotDto.setGenre("Rock")
        newEventSlotDto.setStartTime("19:00")
        newEventSlotDto.setEndTime("23:00")
        newEventSlotDto.setSlotFee("100")
        return newEventSlotDto
    }
}
