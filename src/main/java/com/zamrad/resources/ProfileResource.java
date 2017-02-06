package com.zamrad.resources;

import com.zamrad.domain.profiles.Profile;
import com.zamrad.dto.events.ArtistCalendarEvent;
import com.zamrad.dto.events.BookerCalendarEvent;
import com.zamrad.dto.profiles.NewProfileDto;
import com.zamrad.dto.profiles.UpdateProfileDto;
import com.zamrad.service.artist.ArtistProfileNotFoundException;
import com.zamrad.service.artist.ProfileAlreadyExistsException;
import com.zamrad.service.artist.ProfileService;
import com.zamrad.service.event.EventService;
import com.zamrad.service.user.Auth0TokenService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/profiles/v1")
@CrossOrigin
@Api(value = "/profiles", description = "Create, update or delete a profile.")
public class ProfileResource {
    private static final String PROFILE_MEDIA_TYPE = "application/json;charset=UTF-8";
    private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    @Autowired
    private ProfileService profileService;

    @Autowired
    private EventService eventService;

    @Autowired
    private Auth0TokenService auth0TokenService;

    @Autowired
    private Environment environment;

    @ApiOperation(value = "Get my profile.", response = Profile.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Profile retrieved successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body. Check error message for more details"),
            @ApiResponse(code = 404, message = "No artist with the associated facebook id exists.")
    })
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    @RequestMapping(value = "/me", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Profile> getMyProfile(@ApiIgnore final Principal principal) {

        final Optional<Profile> myProfile = getProfile(principal);

        return myProfile
                .map(profile -> new ResponseEntity<>(profile, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @ApiOperation(value = "Get all artists.", response = Profile.class, responseContainer = "list", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Artist profiles retrieved successfully"),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials"),
            @ApiResponse(code = 400, message = "Invalid request")
    })
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    @RequestMapping(value = "/artists", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Profile>> getAllArtists(@ApiIgnore final Principal principal) {
        Objects.requireNonNull(principal, "Bearer token cannot be null.");

        final List<Profile> allArtists = profileService.getAllArtists();
        return new ResponseEntity<>(allArtists, HttpStatus.OK);
    }

    @ApiOperation(value = "Get artist's events.", response = ArtistCalendarEvent.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Artist events retrieved successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body. Check error message for more details"),
            @ApiResponse(code = 404, message = "No artist with the associated facebook id exists.")
    })
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    @RequestMapping(value = "/artists/me/events", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ArtistCalendarEvent>> getArtistEvents(@ApiIgnore final Principal principal) {
        final Optional<Profile> artistProfileOptional = getProfile(principal);

        if (artistProfileOptional.isPresent()) {
            final Profile profile = artistProfileOptional.get();
            final List<ArtistCalendarEvent> events = eventService.getArtistsEvents(profile.getId());
            return new ResponseEntity<>(events, HttpStatus.OK);

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @ApiOperation(value = "Get booker's events.", response = BookerCalendarEvent.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Booker events retrieved successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body. Check error message for more details"),
            @ApiResponse(code = 404, message = "No artist with the associated facebook id exists.")
    })
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    @RequestMapping(value = "/bookers/me/events", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BookerCalendarEvent>> getBookerEvents(@ApiIgnore final Principal principal) {
        Objects.requireNonNull(principal, "Bearer token cannot be null.");
        final Optional<Profile> artistProfileOptional = getProfile(principal);

        if (artistProfileOptional.isPresent()) {
            final Profile profile = artistProfileOptional.get();
            final List<BookerCalendarEvent> events = eventService.getBookerEvents(profile.getId());
            return new ResponseEntity<>(events, HttpStatus.OK);

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @ApiOperation(value = "Confirm an artist pitch for an event slot.", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Pitch for event slot confirmed successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials."),
            @ApiResponse(code = 404, message = "Event slot or artist does not exist."),
            @ApiResponse(code = 422, message = "The request body had some fields that violated constraints.")
    })
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    @RequestMapping(value = "/bookers/me/actions/confirm/{eventSlotId}/{artistId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> pitchForEvent(@PathVariable("eventSlotId") UUID eventSlotId,
                                           @PathVariable("artistId") UUID artistId,
                                           @ApiIgnore final Principal principal) {
        Objects.requireNonNull(principal, "Bearer token cannot be null.");

        final Optional<Profile> myProfile = profileService.getMyProfile(getUserSocialId());

        if (myProfile.isPresent()) {
            eventService.confirmPitch(eventSlotId, artistId);
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Get a profile by its id.", response = Profile.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Profile retrieved successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body. Check error message for more details"),
            @ApiResponse(code = 404, message = "No profile with the given id exists.")
    })
    @RequestMapping(value = "/{profileId:" + UUID_REGEX + "}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<Profile> getArtist(@PathVariable("profileId") UUID profileId, @ApiIgnore final Principal principal) {

        Objects.requireNonNull(profileId, "Profile id cannot be null.");
        Objects.requireNonNull(principal, "Bearer token cannot be null.");

        final Optional<Profile> profileOptional = profileService.getProfile(profileId);
        return profileOptional
                .map(profile -> new ResponseEntity<>(profile, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @ApiOperation(value = "Create a profile .", consumes = PROFILE_MEDIA_TYPE, produces = PROFILE_MEDIA_TYPE)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Profile created successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials/request body."),
            @ApiResponse(code = 400, message = "The request body had some fields that violated constraints.")
    })
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<?> createArtist(@RequestBody @Valid NewProfileDto newProfileDto, @ApiIgnore final Principal principal) {

        Objects.requireNonNull(newProfileDto, "ArtistDto cannot be null.");
        Objects.requireNonNull(principal, "Bearer token cannot be null.");
        ;

        Profile newlyCreatedProfile;

        try {
            newlyCreatedProfile = profileService.createProfile(newProfileDto);
        } catch (ProfileAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        final UUID artistId = newlyCreatedProfile.getId();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(artistId).toUri());

        return new ResponseEntity<>(newlyCreatedProfile, httpHeaders, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update my profile.", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Profile updated successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials."),
            @ApiResponse(code = 404, message = "Profile does not exist."),
            @ApiResponse(code = 422, message = "The request body had some fields that violated constraints.")
    })
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    @RequestMapping(value = "/me", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Profile> updateMyProfile(@RequestBody @Valid UpdateProfileDto updateProfileDto,
                                                   @ApiIgnore final Principal principal) {

        Objects.requireNonNull(updateProfileDto, "UpdateProfileDto cannot be null.");
        Objects.requireNonNull(principal, "Bearer token cannot be null.");

        Long facebookId = getUserSocialId();

        final Optional<Profile> artistProfile;
        try {
            artistProfile = profileService.updateProfile(facebookId, updateProfileDto);
            return new ResponseEntity<>(artistProfile.get(), HttpStatus.OK);
        } catch (ArtistProfileNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

    @ApiOperation(value = "Pitch for event slot.", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Artist pitch created successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials."),
            @ApiResponse(code = 404, message = "Event slot pitched for does not exist."),
            @ApiResponse(code = 422, message = "The request body had some fields that violated constraints.")
    })
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    @RequestMapping(value = "/artists/me/actions/pitch/{eventSlotId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> pitchForEvent(@PathVariable("eventSlotId") UUID eventSlotId,
                                           @ApiIgnore final Principal principal) {
        Objects.requireNonNull(principal, "Bearer token cannot be null.");

        final Optional<Profile> myProfile = profileService.getMyProfile(getUserSocialId());
        if (myProfile.isPresent()) {
            eventService.pitchForEventSlot(eventSlotId, myProfile.get());
            return new ResponseEntity<>(null, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Deletes an existing profile.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted the profile."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials."),
            @ApiResponse(code = 404, message = "No event was found with the given id"),
    })
    @RequestMapping(value = "{profileId}", method = RequestMethod.DELETE)
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    public ResponseEntity<?> deleteProfile(@PathVariable UUID profileId,
                                           @ApiIgnore final Principal principal) {
        Objects.requireNonNull(principal, "Bearer token cannot be null.");

        try {
            profileService.deleteProfile(profileId);
        } catch (ArtistProfileNotFoundException ex) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    private Long getUserSocialId() {
        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(profile -> Objects.equals(profile, "dev"))) {
            return 1392995950728274L;
        }
        return Long.valueOf(auth0TokenService.getSocialUserId().substring(9));
    }

    private Optional<Profile> getProfile(Principal principal) {
        Objects.requireNonNull(principal, "Bearer token cannot be null.");
        Long facebookId = getUserSocialId();
        return profileService.getMyProfile(facebookId);
    }
}
