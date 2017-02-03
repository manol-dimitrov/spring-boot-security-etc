package com.zamrad.resources;

import com.zamrad.domain.Event;
import com.zamrad.domain.EventSlot;
import com.zamrad.dto.events.*;
import com.zamrad.service.event.EventNotFoundException;
import com.zamrad.service.event.EventService;
import com.zamrad.service.user.Auth0TokenService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/events/v1")
@CrossOrigin
@Api(value = "/events", description = "Create, update or delete a event.")
public class EventResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventResource.class);
    private static final String EVENT_MEDIA_TYPE = "application/json; charset=UTF-8";

    @Autowired
    private EventService eventService;

    @Autowired
    private Auth0TokenService auth0TokenService;

    @Autowired
    private Environment environment;

    @ApiOperation(value = "Retrieve an event by its id.", response = EventDto.class, produces = EVENT_MEDIA_TYPE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Event retrieved successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body."),
            @ApiResponse(code = 404, message = "No event with the given id exists.")
    })
    @RequestMapping(value = "/{eventId}", method = RequestMethod.GET, produces = EVENT_MEDIA_TYPE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<EventDto> getEvent(@PathVariable("eventId") UUID eventId, @ApiIgnore final Principal principal) {
        final EventDto event;
        try {
            event = eventService.getEvent(eventId);
        } catch (EventNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Retrieve all events.",
            response = EventDto.class,
            responseContainer = "List",
            produces = EVENT_MEDIA_TYPE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Events retrieved successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body.")
    })
    @RequestMapping(method = RequestMethod.GET, produces = EVENT_MEDIA_TYPE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<List<EventDto>> getAllEvents(@ApiIgnore final Principal principal) {
        final List<EventDto> allEvents = eventService.getAllEvents();
        return new ResponseEntity<>(allEvents, HttpStatus.OK);
    }

    @ApiOperation(value = "Create an event.", response = EventDto.class, consumes = EVENT_MEDIA_TYPE, produces = EVENT_MEDIA_TYPE)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Event created successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body."),
            @ApiResponse(code = 400, message = "The request body had some fields that violated constraints.")
    })
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<?> createEvent(@RequestBody NewEventDto newEvent,
                                         @ApiIgnore final Principal principal) {
        final Event event;
        try {
            event = eventService.createEvent(newEvent, getUserSocialId());
        } catch (Exception ex) {
            String error = String.format("{\"message\": \"%s\"}", ex.toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(event.getId()).toUri());

        return new ResponseEntity<>(event, headers, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Deletes an existing events and its slots.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted the event."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials."),
            @ApiResponse(code = 404, message = "No event was found with the given id"),
    })
    @RequestMapping(value = "/{eventId}", method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<?> deleteEvent(@PathVariable UUID eventId,
                                         @ApiIgnore final Principal principal) {
        try {
            eventService.deleteEvent(eventId);
        } catch (EventNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Deletes an existing event slot.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted the event slot."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials."),
            @ApiResponse(code = 404, message = "No event slot was found with the given id"),
    })
    @RequestMapping(value = "/{eventId}/{eventSlotId}", method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<?> deleteEventSlot(@PathVariable UUID eventId,
                                             @PathVariable UUID eventSlotId,
                                             @ApiIgnore final Principal principal) {
        try {
            eventService.deleteEventSlot(eventSlotId);
        } catch (EventNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @ApiOperation(
            value = "Add an event slot to an event.",
            response = EventSlotDto.class,
            consumes = EVENT_MEDIA_TYPE,
            produces = EVENT_MEDIA_TYPE)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Event slot created successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body."),
            @ApiResponse(code = 400, message = "The request body had some fields that violated constraints.")
    })
    @RequestMapping(value = "/{eventId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<EventSlot> createEventSlot(@PathVariable("eventId") UUID eventId,
                                                     @RequestBody NewEventSlotDto newEventSlotDto,
                                                     @ApiIgnore final Principal principal) {
        final EventSlot eventSlot;
        try {
            eventSlot = eventService.addEventSlot(eventId, newEventSlotDto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(eventId + "/{id}")
                .buildAndExpand(eventSlot.getId()).toUri());

        return new ResponseEntity<>(eventSlot, headers, HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "Retrieve list of pitches for an event slot.",
            response = Pitch.class,
            responseContainer = "List",
            consumes = EVENT_MEDIA_TYPE,
            produces = EVENT_MEDIA_TYPE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of pitches retrieved successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body."),
            @ApiResponse(code = 400, message = "The request body had some fields that violated constraints.")
    })
    @RequestMapping(value = "/{eventId}/{eventSlotId}/pitches", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<List<Pitch>> getPitchesForEvent(@PathVariable("eventId") UUID eventId,
                                                          @PathVariable("eventSlotId") UUID eventSlotId,
                                                          @ApiIgnore final Principal principal) {
        final List<Pitch> artists = eventService.getPitches(eventSlotId);
        return new ResponseEntity<>(artists, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Update an existing event",
            response = EventDto.class,
            consumes = EVENT_MEDIA_TYPE,
            produces = EVENT_MEDIA_TYPE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Event updated successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body."),
            @ApiResponse(code = 400, message = "The request body had some fields that violated constraints.")
    })
    @RequestMapping(value = "/{eventId}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<EventDto> updateEvent(@PathVariable("eventId") UUID eventId,
                                                @RequestBody UpdateEventDto updateEventDto,
                                                @ApiIgnore final Principal principal) {
        final EventDto event = eventService.updateEvent(eventId, updateEventDto);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Update an existing event slot.",
            response = EventSlotDto.class,
            consumes = EVENT_MEDIA_TYPE,
            produces = EVENT_MEDIA_TYPE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Event slot updated successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body."),
            @ApiResponse(code = 400, message = "The request body had some fields that violated constraints.")
    })
    @RequestMapping(value = "/{eventId}/{eventSlotId}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<EventSlotDto> updateEventSlot(@PathVariable("eventId") UUID eventId,
                                                        @PathVariable("eventSlotId") UUID eventSlotId,
                                                        @RequestBody UpdateEventSlotDto updateEventSlotDto,
                                                        @ApiIgnore final Principal principal) {
        final EventSlotDto eventSlot = eventService.updateEventSlot(eventId, eventSlotId, updateEventSlotDto);
        return new ResponseEntity<>(eventSlot, HttpStatus.OK);
    }

    private Long getUserSocialId() {
        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(profile -> Objects.equals(profile, "dev"))) {
            return 1392995950728274L;
        }
        return Long.valueOf(auth0TokenService.getSocialUserId().substring(9));
    }
}
