package com.zamrad.service.artist;

import com.zamrad.domain.Genre;
import com.zamrad.domain.Profile;
import com.zamrad.dto.profiles.GenreUpdateDto;
import com.zamrad.dto.profiles.NewProfileDto;
import com.zamrad.dto.profiles.UpdateProfileDto;
import com.zamrad.repository.GenreRepository;
import com.zamrad.repository.ProfileRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

@Component
public class ProfileService {
    private final static Logger LOGGER = Logger.getLogger(ProfileService.class);

    private final static Function<NewProfileDto, Profile> NEW_PROFILE_DTO_MAPPER = newProfileDto ->
            Profile.builder()
                    .firstName(newProfileDto.getFirstName())
                    .secondName(newProfileDto.getSecondName())
                    .gender(newProfileDto.getGender())
                    .photoUrl(newProfileDto.getPhotoUrl())
                    .facebookId(newProfileDto.getFacebookId())
                    .email(newProfileDto.getEmail())
                    .mobileNumber(newProfileDto.getMobileNumber())
                    .profileType(newProfileDto.getProfileType())
                    .build();

    private final ProfileRepository profileRepository;
    private final GenreRepository genreRepository;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, GenreRepository genreRepository) {
        this.profileRepository = profileRepository;
        this.genreRepository = genreRepository;
    }

    public Optional<Profile> getProfile(UUID artistId) {
        return Optional.ofNullable(profileRepository.findOne(artistId));
    }

    public List<Profile> getAllArtists() {
        return profileRepository.findByProfileType("artist");
    }

    public Optional<Profile> updateProfile(Long facebookId, UpdateProfileDto updateProfileDto) throws ArtistProfileNotFoundException, GenreNumberException {
        final UUID artistId = updateMyProfile(facebookId);
        if (Objects.isNull(artistId)) throw new ArtistProfileNotFoundException();

        final Profile profileToBeUpdated = profileRepository.findOne(artistId);
        final Profile updatedProfile = updateProfile(updateProfileDto, profileToBeUpdated);
        final Profile finalUpdatedProfile = profileRepository.save(updatedProfile);

        return Optional.ofNullable(finalUpdatedProfile);
    }

    @Transactional
    public Profile updateProfile(UpdateProfileDto updateProfileDto, Profile profileToBeUpdated) throws GenreNumberException {
        final String firstName = updateProfileDto.getFirstName();
        final String secondName = updateProfileDto.getSecondName();
        final String alias = updateProfileDto.getAlias();
        final String bio = updateProfileDto.getDescription();
        final String email = updateProfileDto.getEmail();
        final String gender = updateProfileDto.getGender();
        final Integer maxTravelDistance = updateProfileDto.getMaxTravelDistance();
        final String minimumFee = updateProfileDto.getMinimumFee();
        final String mobileNumber = updateProfileDto.getMobileNumber();
        final String yearsExperience = updateProfileDto.getYearsExperience();
        final String profileType = updateProfileDto.getProfileType();
        final String profileSubType = updateProfileDto.getType();
        final GenreUpdateDto genre = updateProfileDto.getGenreUpdateDto();

        //will update genre and save new genre entity
        Profile profileWithGenres = null;
        if (genre != null) {
            profileWithGenres = updateSingleArtistGenre(profileToBeUpdated, genre);
        }

        if (profileWithGenres != null)
            profileToBeUpdated = profileWithGenres;

        if (firstName != null) profileToBeUpdated.setFirstName(firstName);
        if (secondName != null) profileToBeUpdated.setSecondName(secondName);
        if (alias != null) profileToBeUpdated.setAlias(alias);
        if (bio != null) profileToBeUpdated.setBio(bio);
        if (email != null) profileToBeUpdated.setEmail(email);
        if (gender != null) profileToBeUpdated.setGender(gender);
        if (maxTravelDistance != null) profileToBeUpdated.setMaxTravelDistance(maxTravelDistance);
        if (minimumFee != null) profileToBeUpdated.setMinimumFee(new BigDecimal(minimumFee));
        if (mobileNumber != null) profileToBeUpdated.setMobileNumber(mobileNumber);
        if (yearsExperience != null) profileToBeUpdated.setYearsExperience(Integer.valueOf(yearsExperience));
        if (profileType != null) profileToBeUpdated.setProfileType(profileType);
        if (profileSubType != null) profileToBeUpdated.setType(profileSubType);

        return profileToBeUpdated;
    }

    @Transactional
    private Profile updateSingleArtistGenre(Profile profileToBeUpdated, GenreUpdateDto genreUpdateDto) throws GenreNumberException {
        if (genreUpdateDto.getCurrentGenre() == null) {
            return addNewGenreToArtist(profileToBeUpdated, genreUpdateDto.getNewGenre());
        } else if (genreUpdateDto.getCurrentGenre() != null) {
            return updateGenreArtist(profileToBeUpdated, genreUpdateDto.getNewGenre(), genreUpdateDto.getCurrentGenre());
        } else {
            throw new RuntimeException("Genre to be update must be specified or 'none'");
        }
    }

    private Profile updateGenreArtist(Profile profileToBeUpdated, String newGenre, String currentGenre) {
        Set<Genre> artistGenres = profileToBeUpdated.getGenres();

        if (artistGenres == null || artistGenres.isEmpty()) {
            throw new RuntimeException("Artist has no genres currently associated.");
        }

        //remove old genre
        final Optional<Genre> oldGenre = artistGenres.stream().filter(genre -> genre.getName().equals(currentGenre)).findFirst();
        artistGenres.remove(oldGenre.orElseThrow(GenreNotFoundException::new));

        if (!newGenre.equals("none")) {
            //add new genre
            Genre newArtistGenre = Genre.builder().name(newGenre).build();
            artistGenres.add(newArtistGenre);
        }

        profileToBeUpdated.setGenres(artistGenres);
        return profileRepository.save(profileToBeUpdated);
    }

    private Profile addNewGenreToArtist(Profile profileToBeUpdated, String name) throws GenreNumberException {
        Genre newArtistGenre = Genre.builder().name(name).build();

        Set<Genre> artistGenres = profileToBeUpdated.getGenres();

        if (artistGenres == null) artistGenres = new HashSet<>();

        //an artist can only have 3 genres associated
        if (artistGenres.size() >= 3) throw new GenreNumberException("Artist has 3 genres associated.");

        //only add new genre if not associated to artist already
        if (!artistGenres.contains(newArtistGenre)) artistGenres.add(newArtistGenre);

        profileToBeUpdated.setGenres(artistGenres);

        return profileRepository.save(profileToBeUpdated);
    }

    public Profile createProfile(NewProfileDto newProfileDto) throws ProfileAlreadyExistsException {
        final Optional<Profile> existentProfile = Optional.ofNullable(profileRepository.findByFacebookId(newProfileDto.getFacebookId()));

        if (existentProfile.isPresent()) {
            throw new ProfileAlreadyExistsException("Artist profile has already been registered.");
        }

        final Profile profile = NEW_PROFILE_DTO_MAPPER.apply(newProfileDto);
        return profileRepository.save(profile);
    }

    public List<Profile> getAllArtists(String location, String budget) {
        return Collections.emptyList();
    }

    private UUID updateMyProfile(Long facebookId) {
        final Optional<Profile> artistProfile = Optional.ofNullable(profileRepository.findByFacebookId(facebookId));
        return artistProfile.map(Profile::getId).orElse(null);
    }

    public Optional<Profile> getMyProfile(Long facebookId) {
        return Optional.ofNullable(profileRepository.findByFacebookId(facebookId));
    }

    public void deleteProfile(UUID profileId) {
        profileRepository.delete(profileId);
    }
}
