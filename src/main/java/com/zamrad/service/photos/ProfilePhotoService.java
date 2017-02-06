package com.zamrad.service.photos;

import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;
import com.zamrad.domain.profiles.Profile;
import com.zamrad.dto.Image;
import com.zamrad.repository.ProfileRepository;
import com.zamrad.service.fileuploader.PhotoUploader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Component
public class ProfilePhotoService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProfilePhotoService.class);

    private final PhotoUploader photoUploader;
    private final ImageGenerator imageGenerator;
    private final ProfileRepository profileRepository;

    @Autowired
    public ProfilePhotoService(PhotoUploader photoUploader, ProfileRepository profileRepository, ImageGenerator imageGenerator) {
        this.photoUploader = photoUploader;
        this.profileRepository = profileRepository;
        this.imageGenerator = imageGenerator;
    }

    private Function<Map.Entry<ImageResolution, String>, Image> convertSquareThumbnail = image ->
            Image.builder()
                    .url(image.getValue())
                    .width(99)
                    .height(99)
                    .build();

    private Function<Map.Entry<ImageResolution, String>, Image> convertProfileThumbnail = image ->
            Image.builder()
                    .url(image.getValue())
                    .width(375)
                    .height(272)
                    .build();

    public List<Image> setNewProfilePhoto(MultipartFile photo, Profile profile) {
        try {
            final URL originalImageUrl = photoUploader.upload(photo.getInputStream(), photo.getContentType(), photo.getSize());
            profile.setPhotoUrl(originalImageUrl.toString());

            profileRepository.save(profile);

            final MediaType contentType = MediaType.parse(photo.getContentType());
            final String fileName = PhotoUtils.createFileName(IOUtils.toByteArray(photo.getInputStream()), contentType);

            return getFinalImages(originalImageUrl, contentType, fileName);
        } catch (IOException e) {
            throw new RuntimeException("Upload failed: ", e);
        }
    }

    public List<List<Image>> createShowcase(MultipartFile[] photos) {
        return uploadMultipleOriginalPhotos(photos);
    }

    //TODO: Update an image showcase
    public void updateShowcase() {

    }

    private List<List<Image>> uploadMultipleOriginalPhotos(@RequestPart MultipartFile[] photos) {
        return Arrays.stream(photos).map(this::uploadSingleOriginalPhoto).collect(toList());
    }

    /**
     * Uploads original phot photo.
     *
     * @param photo original photo
     * @return list of rescaled profileShowcaseImages + original
     */
    private List<Image> uploadSingleOriginalPhoto(MultipartFile photo) {
        try {
            final URL originalUrl = photoUploader.upload(photo.getInputStream(), photo.getContentType(), photo.getSize());

            final MediaType contentType = MediaType.parse(photo.getContentType());
            final String fileName = PhotoUtils.createFileName(IOUtils.toByteArray(photo.getInputStream()), contentType);

            return getFinalImages(originalUrl, contentType, fileName);
        } catch (IOException e) {
            LOGGER.error("Image input stream could not be read: ", e);
            return Collections.emptyList();
        }
    }

    private List<Image> getFinalImages(URL originalImageUrl, MediaType contentType, String fileName) {
        try {
            final Map<ImageResolution, String> rescaledImagesMap = imageGenerator.run(contentType, fileName);
            return getAll(originalImageUrl, getRescaledImages(rescaledImagesMap));
        } catch (Exception e) {
            throw new RuntimeException("Image rescaling failed: ", e);
        }
    }

    /**
     * Combines original photo and rescaled profileShowcaseImages.
     * @param originalImageUrl S3 url of original photo.
     * @param rescaledImages list of rescaled profileShowcaseImages.
     * @return all profileShowcaseImages
     */
    private List<Image> getAll(URL originalImageUrl, final List<Image> rescaledImages) {
        final Image image = Image.builder().original(true).url(originalImageUrl.toString()).build();

        rescaledImages.add(image);

        return ImmutableList.copyOf(rescaledImages);
    }

    /**
     * Fetches list of rescaled profileShowcaseImages.
     * @param rescaledImages map of rescaled image type and url.
     * @return list of all relevant rescaled profileShowcaseImages.
     */
    private List<Image> getRescaledImages(Map<ImageResolution, String> rescaledImages) {
        List<Image> rescaled = new ArrayList<>();

        for (Map.Entry<ImageResolution, String> entry : rescaledImages.entrySet()) {
            if (entry.getKey().equals(ImageResolution.SHOWCASE_THUMBNAIL)) {
                final Image squareThumbnail = convertSquareThumbnail.apply(entry);
                rescaled.add(squareThumbnail);
            }

            if (entry.getKey().equals(ImageResolution.PROFILE_THUMBNAIL)) {
                final Image profileThumbnail = convertProfileThumbnail.apply(entry);
                rescaled.add(profileThumbnail);
            }
        }

        return rescaled;
    }
}
