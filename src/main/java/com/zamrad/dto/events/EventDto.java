package com.zamrad.dto.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto {
    @JsonProperty
    private String eventId;
    @JsonProperty
    private String bookerId;
    @JsonProperty
    private String title;
    @JsonProperty
    private String address;
    @JsonProperty
    private String date;
    @JsonProperty
    private String paymentType;
    @JsonProperty
    private String status;
    @JsonProperty
    private String type;
    @JsonProperty
    private String description;
    @JsonProperty
    private String photoUrl;
    @JsonProperty
    private String venueCapacity;
    @JsonProperty
    private List<EventSlotDto> eventSlots;
    @JsonProperty
    private EventAdditionalDetailsDto eventAdditionalDetailsDto;
}
