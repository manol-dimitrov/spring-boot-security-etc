package com.zamrad.domain.posts;

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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "post", schema = "zamrad_dev")
@Data
@Builder
@EqualsAndHashCode(exclude = "images")
@ToString(exclude = "images")
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class Post {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(columnDefinition = "BINARY(16)")
    private UUID posterId;

    private String title;
    private String type;
    private String content;
    private LocalDateTime dateTime;
    private String taggedProfiles;
    private String link;

    @OneToMany(cascade = ALL, mappedBy = "post")
    @JsonManagedReference
    private Set<PostImage> images;

    @Tolerate
    public Post(){}
}
