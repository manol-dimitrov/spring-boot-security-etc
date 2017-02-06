package com.zamrad.domain.profiles;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "profile_showcase", schema = "zamrad_dev")
@Data
@Builder
@EqualsAndHashCode(exclude = "profileShowcaseImages")
@ToString(exclude = "profileShowcaseImages")
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class ProfileShowcase {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToMany(cascade = ALL, mappedBy = "profileShowcase")
    @JsonManagedReference
    private List<ProfileShowcaseImage> profileShowcaseImages;
}
