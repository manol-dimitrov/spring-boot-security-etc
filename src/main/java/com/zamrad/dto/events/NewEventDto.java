package com.zamrad.dto.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zamrad.domain.EventAdditionalDetails;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
@JsonPropertyOrder(alphabetic = true)
public class NewEventDto {
    @JsonProperty
    @NotNull
    private String title;

    @JsonProperty
    @NotNull
    private String date;

    @JsonProperty
    @NotNull
    private String type;

    @JsonProperty
    @NotNull
    private String description;

    @JsonProperty
    @NotNull
    private String address;

    @JsonProperty
    @NotNull
    private String paymentType;

    @JsonProperty
    @NotNull
    private String venueCapacity;

    @JsonProperty("new_event_slot")
    @NotNull
    private NewEventSlotDto newEventSlotDto;

    @JsonProperty("event_additional_details")
    private EventAdditionalDetailsDto eventAdditionalDetailsDto;
}
