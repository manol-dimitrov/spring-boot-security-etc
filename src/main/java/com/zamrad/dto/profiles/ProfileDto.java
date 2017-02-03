package com.zamrad.dto.profiles;

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
public class ProfileDto {
    @JsonProperty
    private String firstName;
    @JsonProperty
    private String secondName;
    @JsonProperty
    private String email;
    @JsonProperty
    private Long facebookId;
    @JsonProperty
    private String type;
    @JsonProperty
    private String mobileNumber;
    @JsonProperty
    private String profileType;
}
