package com.zamrad.dto.profiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import javax.validation.constraints.Pattern;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class UpdateProfileDto {

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String secondName;

    @JsonProperty
    private String profileType;

    @JsonProperty
    private String gender;

    @Pattern(
            regexp = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$",
            message = "Not a valid e-mail format."
    )
    @JsonProperty
    private String email;

    @JsonProperty
    private String mobileNumber;

    @JsonProperty
    private String description;

    @JsonProperty
    private String alias;

    @JsonProperty
    private Integer maxTravelDistance;

    @JsonProperty
    private String yearsExperience;

    @JsonProperty
    private String minimumFee;

    @JsonProperty
    private GenreUpdateDto genreUpdateDto;

    @JsonProperty
    private String type;

    @Tolerate
    public UpdateProfileDto() {
    }
}