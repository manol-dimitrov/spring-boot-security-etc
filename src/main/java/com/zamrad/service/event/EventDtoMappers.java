package com.zamrad.service.event;

import com.zamrad.domain.Event;
import com.zamrad.domain.EventAdditionalDetails;
import com.zamrad.domain.EventSlot;
import com.zamrad.dto.events.EventAdditionalDetailsDto;
import com.zamrad.dto.events.EventDto;
import com.zamrad.dto.events.EventSlotDto;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class EventDtoMappers {
    final static Function<EventSlot, EventSlotDto> EVENT_SLOT_DTO_MAPPER = eventSlot ->
            EventSlotDto.builder()
                    .eventSlotId(eventSlot.getId().toString())
                    .status(eventSlot.getStatus())
                    .eventSlotPrice(eventSlot.getPrice().toString())
                    .startTime(eventSlot.getStartTime())
                    .endTime(eventSlot.getEndTime())
                    .genre(eventSlot.getGenre())
                    .build();

    private final static Function<EventAdditionalDetails, EventAdditionalDetailsDto> EVENT_ADDITIONAL_DETAILS_DTO_MAPPER = eventAdditionalDetails ->
            EventAdditionalDetailsDto.builder()
                    .djBooth(String.valueOf(eventAdditionalDetails.getDjBooth()))
                    .ageRestriction(String.valueOf(eventAdditionalDetails.getAgeRestriction()))
                    .noiseRestriction(String.valueOf(eventAdditionalDetails.getNoiseRestriction()))
                    .paSystem(String.valueOf(eventAdditionalDetails.getPaSystem()))
                    .soundEngineer(String.valueOf(eventAdditionalDetails.getSoundEngineer()))
                    .stageDepth(String.valueOf(eventAdditionalDetails.getStageDepth()))
                    .stageWidth(String.valueOf(eventAdditionalDetails.getStageWidth()))
                    .stageLighting(String.valueOf(eventAdditionalDetails.getStageLighting()))
                    .venueCapacity(String.valueOf(eventAdditionalDetails.getVenueCapacity()))
                    .wheelchair(String.valueOf(eventAdditionalDetails.getWheelchair()))
                    .build();

    final static Function<Event, EventDto> EVENT_DTO_MAPPER = event -> {
        final Stream<EventSlotDto> eventSlots = event.getEventSlots().stream().map(EVENT_SLOT_DTO_MAPPER);

        final Optional<EventAdditionalDetails> additionalDetailsOptional = Optional.ofNullable(event.getEventAdditionalDetails());

        final EventDto.EventDtoBuilder eventDtoBuilder = EventDto.builder()
                .eventId(event.getId().toString())
                .bookerId(event.getProfile().getId().toString())
                .title(event.getTitle())
                .address(event.getAddress())
                .photoUrl(event.getPhotoUrl())
                .status(event.getStatus())
                .paymentType(event.getPaymentType())
                .date(event.getDate().toString())
                .type(event.getType())
                .eventSlots(eventSlots.collect(toList()));

        additionalDetailsOptional.ifPresent(eventAdditionalDetails -> eventDtoBuilder.eventAdditionalDetailsDto(EVENT_ADDITIONAL_DETAILS_DTO_MAPPER.apply(eventAdditionalDetails)));

        return eventDtoBuilder.build();
    };
}
