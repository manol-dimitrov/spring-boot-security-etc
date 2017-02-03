package com.zamrad.dto.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class NewEventSlotDto {
    @JsonProperty
    private String startTime;
    @JsonProperty
    private String endTime;
    @JsonProperty
    private String slotFee;
    @JsonProperty
    private String genre;
    @JsonProperty
    private String artistType;
}
