package com.zamrad.dto.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zamrad.dto.events.NewEventSlotDto;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class UpdateEventDto {
    @JsonProperty
    private String title;
    @JsonProperty
    private String date;
    @JsonProperty
    private String type;
    @JsonProperty
    private String description;
    @JsonProperty
    private String address;
    @JsonProperty
    List<NewEventSlotDto> slots;
}
