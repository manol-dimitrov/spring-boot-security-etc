package com.zamrad.dto.profiles;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class NewProfileDto {
    private static final String NOT_EMPTY_REGEXP = "^(?!\\s*$).+";
    private static final String LONG_REGEXP = "^-?\\d{1,19}$";

    @Size(max = 100)
    @Pattern(regexp = NOT_EMPTY_REGEXP, message = "Firstname must not be null.")
    @JsonProperty
    private String firstName;

    @Size(max = 100)
    @Pattern(regexp = NOT_EMPTY_REGEXP, message = "Surname must not be null.")
    @JsonProperty
    private String secondName;

    @Pattern(regexp = NOT_EMPTY_REGEXP, message = "Gender must not be null.")
    @JsonProperty
    private String gender;

    @JsonProperty
    @NotNull
    private Long facebookId;

    @Pattern(
            regexp = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$",
            message = "Not a valid e-mail format."
    )
    @JsonProperty
    private String email;

    @Size(max = 2000)
    @Pattern(regexp = NOT_EMPTY_REGEXP, message = "Photo url must not be null.")
    @JsonProperty
    private String photoUrl;

    @JsonProperty
    private String mobileNumber;

    @JsonProperty
    @Pattern(regexp = NOT_EMPTY_REGEXP, message = "Profile type must not be null.")
    private String profileType;
}
