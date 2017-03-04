package com.zamrad.domain.profiles;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zamrad.domain.reviews.Review;
import com.zamrad.domain.payments.StripeCustomer;
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
@Table(name = "profiles", schema = "zamrad_dev")
@Data
@Builder
@EqualsAndHashCode(exclude = {"genres", "reviews"})
@ToString(exclude = {"genres", "reviews"})
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class Profile {

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    @JsonBackReference
    public Set<Review> reviews = new HashSet<>();

    @ManyToMany(targetEntity = Genre.class, cascade = CascadeType.ALL)
    @JoinTable(
            name = "artist_genres", schema = "zamrad_dev",
            joinColumns = @JoinColumn(name = "profile_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id", referencedColumnName = "id")
    )
    @JsonManagedReference
    protected Set<Genre> genres = new HashSet<>();

    //@OneToOne
    //StripeCustomer stripeCustomer;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    @Column(name = "email")
    private String email;

    @Column(name = "facebook_id")
    private Long facebookId;

    @Column(name = "type")
    private String type;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "profile_type")
    private String profileType;

    @Column(name = "address")
    private String address;

    @Column(name = "max_travel_distance")
    private int maxTravelDistance;

    @Column(name = "minimum_fee")
    private BigDecimal minimumFee;

    @Column(name = "alias")
    private String alias;

    @Column(name = "years_experience")
    private int yearsExperience;

    @Column(name = "bio")
    private String bio;

    @Column(name = "gender")
    private String gender;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "average_rating")
    private Double averageRating;

    @Tolerate
    public Profile() {
    }
}
