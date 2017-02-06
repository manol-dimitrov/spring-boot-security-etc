package com.zamrad.domain.events;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "event_additional_details", schema = "zamrad_dev")
@Data
@Builder
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class EventAdditionalDetails {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private Long stageWidth;
    private Long stageDepth;
    private Long venueCapacity;
    private Character paSystem;
    private Character soundEngineer;
    private Character stageLighting;
    private Character djBooth;
    private Character ageRestriction;
    private Character noiseRestriction;
    private Character wheelchair;
}
