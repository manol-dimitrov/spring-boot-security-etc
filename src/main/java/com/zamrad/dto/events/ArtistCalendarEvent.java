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
public class ArtistCalendarEvent {
    @JsonProperty
    private String eventId;
    @JsonProperty
    private String eventPhotoUrl;
    @JsonProperty
    private String eventTitle;
    @JsonProperty
    private List<EventSlotDto> eventSlots;

}
