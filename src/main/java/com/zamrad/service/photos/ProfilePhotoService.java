package com.zamrad.service.photos;

import com.zamrad.domain.Profile;
import com.zamrad.dto.Image;
import com.zamrad.repository.ProfileRepository;
import com.zamrad.service.fileuploader.PhotoUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

@Component
public class ProfilePhotoService {

    private final PhotoUploader photoUploader;
    private final ProfileRepository profileRepository;

    @Autowired
    public ProfilePhotoService(PhotoUploader photoUploader, ProfileRepository profileRepository) {
        this.photoUploader = photoUploader;
        this.profileRepository = profileRepository;
    }

    public List<Image> setNewProfilePhoto(MultipartFile photo, Profile profile) {
        try {
            final URL originalImageUrl = photoUploader.upload(photo.getInputStream(), photo.getContentType(), photo.getSize());
            profile.setPhotoUrl(originalImageUrl.toString());

            profileRepository.save(profile);

            return createImage(originalImageUrl);
        } catch (IOException e) {
            throw new RuntimeException("Upload failed: ", e);
        }
    }

    private List<Image> createImage(URL originalImageUrl) {
        final Image image = Image.builder()
                .original(true)
                .height(375)
                .width(272)
                .url(originalImageUrl.toString()).build();
        return Collections.singletonList(image);
    }
}
