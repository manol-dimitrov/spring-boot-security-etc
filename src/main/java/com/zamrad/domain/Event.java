package com.zamrad.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "event", schema = "zamrad_dev")
@Data
@Builder
@EqualsAndHashCode(exclude = "eventSlots")
@ToString(exclude = "eventSlots")
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class Event {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    public Profile profile;

    @OneToMany(cascade = ALL, mappedBy = "event")
    @JsonManagedReference
    public Set<EventSlot> eventSlots;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "event_additional_details_id")
    public EventAdditionalDetails eventAdditionalDetails;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "address")
    private String address;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "status")
    private String status;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "venue_capacity")
    private Long venueCapacity;

    @Column(name = "cancellation_policy")
    private String cancellationPolicy;

    @Tolerate
    public Event() {
    }
}
