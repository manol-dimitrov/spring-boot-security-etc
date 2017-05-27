package com.zamrad.service.artist

import com.google.common.collect.Sets
import com.zamrad.domain.profiles.Genre
import com.zamrad.domain.profiles.Profile
import com.zamrad.dto.profiles.UpdateProfileDto
import com.zamrad.repositories.ProfileRepository
import com.zamrad.repositories.GenreRepository
import spock.lang.Ignore
import spock.lang.Specification

class ProfileServiceTest extends Specification {

    Long facebookId = Long.valueOf("1392995950728274")
    UUID artistId = UUID.fromString("463776f1-cb7b-4b02-9323-931f62939de1");
    UUID genreId = UUID.fromString("463776f1-cb1b-4b02-9323-931f62939de1")

    Profile artistProfileToBeUpdated = Profile.builder()
            .id(artistId)
            .firstName("Manol")
            .secondName("Dimitrov")
            .email("manoldimitrov@yahoo.com")
            .facebookId(facebookId)
            .build()


    ProfileService artistService
    ProfileRepository artistRepository
    GenreRepository genreRepository

    def setup() {
        artistRepository = Mock(ProfileRepository);
        genreRepository = Mock(GenreRepository);
        artistService = new ProfileService(artistRepository, genreRepository);
    }

    @Ignore
    def 'should update an artist who has no genres associated at time of update'() {
        given:
        Set<String> genres = new HashSet<>()
        genres.add("pop")

        UpdateProfileDto updateArtistDto = UpdateProfileDto.builder().genres(genres).build()

        Set<Profile> artistProfiles = new HashSet<>()
        and:
        Genre expectedGenres = Genre.builder().id(genreId).name("pop").artists(artistProfiles).build()
        Profile expectedUpdatedProfile = Profile.builder()
                .id(artistId)
                .firstName("Manol")
                .secondName("Dimitrov")
                .email("manoldimitrov@yahoo.com")
                .facebookId(facebookId)
                .genres(Sets.newHashSet(expectedGenres))
                .build()

        expectedGenres.getArtists().addAll(Sets.newHashSet(expectedUpdatedProfile))

        when:
        def artist = artistService.updateProfile(updateArtistDto, artistProfileToBeUpdated)

        then:
        0 * artistRepository.findByFacebookId(facebookId) >> artistProfileToBeUpdated
        0 * artistRepository.findOne(artistId) >> artistProfileToBeUpdated
        0 * artistRepository.save(artistProfileToBeUpdated) >> expectedUpdatedProfile

        and:
        assert artist == expectedUpdatedProfile
    }

    def 'should update an artist who already has genres associated at time of update'() {

    }

    def 'should update an artist who has deleted genres from their profile'() {

    }
}
