package com.zamrad.domain.events;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zamrad.domain.profiles.Profile;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "event_slot", schema = "zamrad_dev")
@Data
@Builder
@EqualsAndHashCode(exclude = "event")
@ToString(exclude = "event")
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class EventSlot {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "genre")
    private String genre;

    @Column(name = "artist_type")
    private String artistType;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    @JsonBackReference
    public Event event;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "event_slot_pitchers", schema = "zamrad_dev",
            joinColumns = @JoinColumn(name = "profile_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "event_slot_id", referencedColumnName = "id")
    )
    protected Set<Profile> pitchers = new HashSet<>();

    @Tolerate
    public EventSlot(){

    }
}
