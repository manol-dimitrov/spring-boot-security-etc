package com.zamrad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.zamrad.dto.events.NewEventDto;
import com.zamrad.dto.events.NewEventSlotDto;
import com.zamrad.dto.events.UpdateEventDto;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class EventsIT extends BaseIT {
    private final static String EVENTS_PATH = "http://localhost:8080/events/v1/";
    private final static String EVENTS_PATH_ME = "http://localhost:8080/profiles/v1/artists/me/events";
    private final static String PITCH_ACTION_PATH = "http://localhost:8080/profiles/v1/artists/me/actions/pitch/";
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public EventsIT() throws UnsupportedEncodingException {
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

    }

    @Override
    @After
    public void tearDown() {
        super.tearDown();
    }

    @Test
    public void shouldGetEventById() throws UnirestException, JsonProcessingException {
        final String newlyPostedEventId = doPostNewEvent().getBody().getObject().get("id").toString();

        HttpResponse<JsonNode> jsonResponse = Unirest.get(EVENTS_PATH + "{id}").routeParam("id", newlyPostedEventId).asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(200);
        assertThat(jsonResponse.getBody().getObject().get("title")).isEqualTo("Test event");
    }

    @Test
    public void shouldGetAllEvents() throws UnirestException, JsonProcessingException {
        doPostNewEvent();

        HttpResponse<JsonNode> jsonResponse = Unirest.get(EVENTS_PATH).asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(200);
    }

    @Test
    public void shouldCreateEvent() throws UnirestException, JsonProcessingException {
        doPostNewEvent();
    }

    @Test
    public void shouldNotBeAbleToCreateEventWithoutEventSlot() throws JsonProcessingException, UnirestException {
        doPostNewEventWithoutEventSlot();
    }

    private void doPostNewEventWithoutEventSlot() throws JsonProcessingException, UnirestException {
        NewEventDto newArtistDto = createEventDtoWithoutSlot();

        HttpResponse<JsonNode> jsonResponse = Unirest.post(EVENTS_PATH)
                .body(OBJECT_MAPPER.writeValueAsString(newArtistDto))
                .asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(400);
    }

    @Test
    public void shouldUpdateEvent() throws UnirestException, JsonProcessingException {
        final String id = doPostNewEvent().getHeaders().get("Location").get(0).substring(32);

        UpdateEventDto updateArtistDto = createEventUpdateDto();

        HttpResponse<JsonNode> jsonResponse = Unirest.patch(EVENTS_PATH + id)
                .body(updateArtistDto)
                .asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(200);
    }

    @Test
    public void shouldUpdateEventSlot(){

    }

    @Test
    public void shouldReturn404WhenEventDoesNotExist() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(EVENTS_PATH + "{id}").routeParam("id", UUID.randomUUID().toString()).asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(404);
    }

    @Test
    public void shouldGetMyEvents() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse = Unirest.get(EVENTS_PATH_ME).asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(200);
    }

    @Test
    public void shouldDeleteEvent() throws UnirestException, JsonProcessingException {
        final String id = doPostNewEvent().getHeaders().get("Location").get(0).substring(32);

        HttpResponse<JsonNode> jsonResponse = Unirest.delete(EVENTS_PATH + "{id}").routeParam("id", id).asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(204);
    }

    @Test
    public void shouldBeAbleToPitchForAnEvent() throws UnirestException, JsonProcessingException {
        //1. Create an artist
        doPostNewArtist();

        //2. Create an event
        final HttpResponse<JsonNode> response = doPostNewEvent();

        final String eventId = response.getBody().getObject().get("id").toString();
        final JSONObject eventSlot = (JSONObject) response.getBody().getObject().getJSONArray("event_slots").get(0);

        final String eventSlotId = eventSlot.get("id").toString();

        HttpResponse<JsonNode> jsonResponse = Unirest.post(PITCH_ACTION_PATH + "{eventSlotId}")
                .routeParam("eventSlotId", eventSlotId)
                .asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(201);
    }

    private HttpResponse<JsonNode> doPostNewEvent() throws UnirestException, JsonProcessingException {
        final String id = doPostNewBooker().substring(34);

        NewEventDto newEventDto = createEventDto();
        //newEventDto.setBookerId(id);

        HttpResponse<JsonNode> jsonResponse = Unirest.post(EVENTS_PATH)
                .body(OBJECT_MAPPER.writeValueAsString(newEventDto))
                .asJson();

        assertThat(jsonResponse.getStatus()).isEqualTo(201);
        assertThat(jsonResponse.getStatusText()).isEqualTo("Created");
        assertThat(jsonResponse.getHeaders().get("Location")).isNotNull();

        return jsonResponse;
    }

    private NewEventDto createEventDto() {
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setType("Venue");
        newEventDto.setAddress("Shad Thames");
        newEventDto.setDescription("Amazing event");
        newEventDto.setDate("08-09-2016");
        newEventDto.setTitle("Test event");
        newEventDto.setVenueCapacity("1000");
        newEventDto.setPaymentType("Electronic");

        //add 1 slot to event
        newEventDto.setNewEventSlotDto(craeteEventSlotDto());

        return newEventDto;
    }

    private NewEventDto createEventDtoWithoutSlot() {
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setType("Venue");
        newEventDto.setAddress("Shad Thames");
        newEventDto.setDescription("Amazing event");
        newEventDto.setDate("08-09-2016");
        newEventDto.setTitle("Test event");
        newEventDto.setVenueCapacity("1000");
        newEventDto.setPaymentType("Electronic");

        return newEventDto;
    }

    private UpdateEventDto createEventUpdateDto() {
        UpdateEventDto updateEventDto = new UpdateEventDto();
        updateEventDto.setAddress("New test address");
        updateEventDto.setDate("2016-12-25");
        updateEventDto.setTitle("Updated test title");
        updateEventDto.setType("CONFIRMED");
        return updateEventDto;
    }

    private NewEventSlotDto craeteEventSlotDto() {
        NewEventSlotDto newEventSlotDto = new NewEventSlotDto();
        newEventSlotDto.setArtistType("Test");
        newEventSlotDto.setGenre("Rock");
        newEventSlotDto.setStartTime("19:00");
        newEventSlotDto.setEndTime("23:00");
        newEventSlotDto.setSlotFee("100");
        return newEventSlotDto;
    }
}
