package com.zamrad.dto.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pitch {
    @JsonProperty
    private String artistId;
    @JsonProperty
    private String artistName;
    @JsonProperty
    private String photoUrl;
    @JsonProperty
    private String eventSlotId;
    @JsonProperty
    private String eventSlotFee;
}
