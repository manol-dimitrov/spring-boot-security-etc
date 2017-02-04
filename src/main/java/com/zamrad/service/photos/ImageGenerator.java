package com.zamrad.service.photos;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;
import com.zamrad.service.fileuploader.PhotoUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.auth0.jwt.internal.org.apache.commons.lang3.StringUtils.substringBeforeLast;

public class ImageGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageGenerator.class);

    @Autowired
    private final ImageRescaler imageScaler;

    @Autowired
    private final PhotoUploader photoUploader;
    private final MediaType mediaType;
    private String originalImagePath;

    public ImageGenerator(ImageRescaler imageScaler, PhotoUploader photoUploader, String originalImagePath, MediaType mediaType) {
        this.imageScaler = imageScaler;
        this.photoUploader = photoUploader;
        this.originalImagePath = originalImagePath;
        this.mediaType = mediaType;
    }

    public Map<ImageResolution, String> run() throws Exception {
        Stopwatch rescaleStopwatch = Stopwatch.createUnstarted();
        Stopwatch uploadStopwatch = Stopwatch.createUnstarted();

        ByteArrayOutputStream thumbnailPhotoOut = new ByteArrayOutputStream();

        try (InputStream src = photoUploader.get(originalImagePath);
             OutputStream thumbnailPhotoOutRef = thumbnailPhotoOut) {

            ImageRescaler.Target thumbnailPhotoTarget = new ImageRescaler.Target(ImageResolution.PROFILE_THUMBNAIL, thumbnailPhotoOutRef);

            rescaleStopwatch.start();
            imageScaler.rescale(src, thumbnailPhotoTarget);
            rescaleStopwatch.stop();
        }

        String unqualifiedAssetPath = substringBeforeLast(originalImagePath, ".");
        String name = unqualifiedAssetPath + "-thumbnail.jpg";

        uploadStopwatch.start();

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(thumbnailPhotoOut.toByteArray());
        photoUploader.upload(byteArrayInputStream, name, mediaType.toString(), (long) thumbnailPhotoOut.toByteArray().length);

        uploadStopwatch.stop();

        LOGGER.info("Elapsed rescale: {}ms, upload rescaled: {}ms", rescaleStopwatch.elapsed(TimeUnit.MILLISECONDS), uploadStopwatch.elapsed(TimeUnit.MILLISECONDS));

        return ImmutableMap.<ImageResolution, String>builder()
                .put(ImageResolution.ORIGINAL, photoUploader.get(originalImagePath))
                .put(ImageResolution.PROFILE_THUMBNAIL, fileStoreManager.getImageUrl(name))
                .build();
    }
}
