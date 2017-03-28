package com.zamrad.dto.posts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zamrad.dto.Image;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {
    @JsonProperty
    private String id;
    @JsonProperty
    private String posterId;
    @JsonProperty
    private String content;
    @JsonProperty
    private String type;
    @JsonProperty
    private String title;
    @JsonProperty
    private String createdDateTime;
    @JsonProperty
    private String link;
    @JsonProperty
    private List<Image> images;
}
