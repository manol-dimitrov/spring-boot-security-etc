package com.zamrad.dto.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import javax.persistence.JoinColumn;
import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookerCalendarEvent {
    @JsonProperty
    private String eventId;
    @JsonProperty
    private String eventPhotoUrl;
    @JsonProperty
    private String eventTitle;
    @JsonProperty
    private String eventAddress;
    @JsonProperty
    private String eventStatus;
    @JsonProperty
    private String eventDate;
    @JsonProperty
    private String venueType;
    @JsonProperty
    private List<EventSlotDto> eventSlots;
}
