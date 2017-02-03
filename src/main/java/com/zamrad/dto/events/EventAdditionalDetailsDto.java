package com.zamrad.dto.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventAdditionalDetailsDto {
    @JsonProperty
    @NotNull
    private String stageWidth;

    @JsonProperty
    @NotNull
    private String stageDepth;

    @JsonProperty
    @NotNull
    private String venueCapacity;

    @JsonProperty
    @NotNull
    private String paSystem;

    @JsonProperty
    @NotNull
    private String soundEngineer;

    @JsonProperty
    @NotNull
    private String stageLighting;

    @JsonProperty
    @NotNull
    private String djBooth;

    @JsonProperty
    @NotNull
    private String ageRestriction;

    @JsonProperty
    @NotNull
    private String noiseRestriction;

    @JsonProperty
    @NotNull
    private String wheelchair;
}
