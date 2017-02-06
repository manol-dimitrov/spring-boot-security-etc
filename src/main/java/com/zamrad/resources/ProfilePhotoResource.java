package com.zamrad.resources;

import com.google.common.net.MediaType;
import com.zamrad.domain.profiles.Profile;
import com.zamrad.dto.Image;
import com.zamrad.service.artist.ProfileService;
import com.zamrad.service.photos.ProfilePhotoService;
import com.zamrad.service.user.Auth0TokenService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.net.URI;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.zamrad.service.photos.PhotoUtils.findOriginalImage;
import static org.apache.http.HttpStatus.SC_REQUEST_TOO_LONG;
import static org.apache.http.HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/photos/v1")
@CrossOrigin
@Api(value = "/photos", description = "Upload new photos.")
public class ProfilePhotoResource {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProfilePhotoResource.class);

    private static final int MAXIMUM_UPLOAD_CONTENT_LENGTH = 4_194_304;


    @Autowired
    private ProfilePhotoService profilePhotoService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private Environment environment;

    @Autowired
    private Auth0TokenService auth0TokenService;

    @ApiOperation(value = "Upload a new profile photo.")
    @ApiImplicitParams({
            @ApiImplicitParam(required = true, name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "New photo asset successfully uploaded."),
            @ApiResponse(code = 400, message = "Bad request (photo unreadable using indicated media type)."),
            @ApiResponse(code = 413, message = "Uploaded photo content is too large."),
            @ApiResponse(code = 415, message = "Content-Type is not a supported image media type."),
            @ApiResponse(code = 500, message = "Internal error reading or writing image asset."),
    })
    @RequestMapping(
            value = "/me",
            method = RequestMethod.POST,
            produces = APPLICATION_JSON_VALUE,
            consumes = {MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> setProfilePhoto(@RequestPart MultipartFile photo, @ApiIgnore final Principal principal) {

        final Long size = photo.getSize();
        final String contentType = photo.getContentType();

        if (size > MAXIMUM_UPLOAD_CONTENT_LENGTH) {
                return ResponseEntity.status(SC_REQUEST_TOO_LONG).build();
        }
        if (!isAcceptableMediaType(contentType)) {
                return ResponseEntity.status(SC_UNSUPPORTED_MEDIA_TYPE).build();
        }

        final Optional<Profile> profile = getProfile(principal);
        final List<Image> images = profilePhotoService.setNewProfilePhoto(photo, profile.get());
        Optional<Image> originalImage = findOriginalImage(images);

        final Image image;
        if (originalImage.isPresent()) {
            image = originalImage.get();
            return ResponseEntity.created(URI.create(image.getUrl()))
                        .contentType(APPLICATION_JSON_UTF8)
                        .body(images);
        }

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
    }

    @ApiOperation(value = "Upload photos to profile showcase.")
    @ApiImplicitParams({
            @ApiImplicitParam(required = true, name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")})
    @ApiResponses({
            @ApiResponse(code = 201, message = "New photo assets successfully uploaded."),
            @ApiResponse(code = 400, message = "Bad request (photo unreadable using indicated media type)."),
            @ApiResponse(code = 413, message = "Photo is too large."),
            @ApiResponse(code = 415, message = "Content-Type is not a supported image media type."),
            @ApiResponse(code = 500, message = "Internal error reading or writing image asset."),
    })
    @RequestMapping(
            value = "/me/showcase",
            method = RequestMethod.POST,
            produces = APPLICATION_JSON_VALUE,
            consumes = {MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadShowcasePhotos(@RequestPart MultipartFile[] photos, @ApiIgnore final Principal principal) {
        if (photos.length > 6) {
            return ResponseEntity.status(SC_REQUEST_TOO_LONG).build();
        }

        final Optional<Profile> profile = getProfile(principal);

        if (profile.isPresent()) {
            profilePhotoService.createShowcase(photos);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
    }

    //me/showcase/actions/delete_photo?photoid=xxxx

    private boolean isAcceptableMediaType(String contentType) {
        if (contentType != null) {
            try {
                MediaType mediaType = MediaType.parse(contentType);
                return mediaType == MediaType.JPEG || mediaType == MediaType.PNG;
            } catch (IllegalArgumentException ex) {
                return false;
            }
        } else {
            return false;
        }
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
