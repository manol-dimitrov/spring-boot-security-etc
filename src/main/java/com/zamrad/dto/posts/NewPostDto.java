package com.zamrad.dto.posts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
@JsonPropertyOrder(alphabetic = true)
public class NewPostDto {
    @JsonProperty
    @NotNull
    private String title;

    @JsonProperty
    @NotNull
    private String content;

    @JsonProperty
    @NotNull
    private String link;

    @JsonProperty
    @NotNull
    private String type;
}
