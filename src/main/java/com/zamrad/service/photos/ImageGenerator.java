package com.zamrad.service.photos;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;
import com.zamrad.service.fileuploader.PhotoUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils.substringBeforeLast;

@Component
public class ImageGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageGenerator.class);

    private final ImageRescaler imageScaler;
    private final PhotoUploader photoUploader;

    @Autowired
    public ImageGenerator(ImageRescaler imageScaler, PhotoUploader photoUploader) {
        this.imageScaler = imageScaler;
        this.photoUploader = photoUploader;
    }

    public Map<ImageResolution, String> run(MediaType mediaType, String fileName) throws Exception {
        Stopwatch rescaleStopwatch = Stopwatch.createUnstarted();
        Stopwatch uploadStopwatch = Stopwatch.createUnstarted();

        ByteArrayOutputStream thumbnailPhotoOut = new ByteArrayOutputStream();

        try (InputStream src = photoUploader.get(fileName);
             OutputStream thumbnailPhotoOutRef = thumbnailPhotoOut) {

            ImageRescaler.Target thumbnailPhotoTarget = new ImageRescaler.Target(ImageResolution.PROFILE_THUMBNAIL, thumbnailPhotoOutRef);

            rescaleStopwatch.start();
            imageScaler.rescale(src, thumbnailPhotoTarget);
            rescaleStopwatch.stop();
        }

        String unqualifiedAssetPath = substringBeforeLast(fileName, ".");
        String thumbnailName = unqualifiedAssetPath + "-thumbnail.jpg";

        uploadStopwatch.start();

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(thumbnailPhotoOut.toByteArray());
        photoUploader.upload(byteArrayInputStream, thumbnailName, mediaType.toString(), (long) thumbnailPhotoOut.toByteArray().length);

        uploadStopwatch.stop();

        LOGGER.info("Rescaling took: {}ms, uploading rescaled image took: {}ms", rescaleStopwatch.elapsed(TimeUnit.MILLISECONDS), uploadStopwatch.elapsed(TimeUnit.MILLISECONDS));

        return ImmutableMap.<ImageResolution, String>builder()
                .put(ImageResolution.ORIGINAL, photoUploader.getImageUrl(fileName))
                .put(ImageResolution.PROFILE_THUMBNAIL, photoUploader.getImageUrl(thumbnailName))
                .build();
    }
}
