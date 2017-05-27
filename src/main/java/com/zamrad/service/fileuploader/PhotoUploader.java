package com.zamrad.service.fileuploader;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.net.MediaType;
import com.zamrad.configuration.aws.AmazonS3Template;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.zamrad.service.photos.PhotoUtils.createFileName;
import static com.zamrad.service.photos.PhotoUtils.createMetadata;

@Component
public class PhotoUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoUploader.class);

    private static final String BUCKET = "zamrad-photos-dev";
    private final AmazonS3 amazonS3;

    @Autowired
    public PhotoUploader(AmazonS3Template amazonS3Template) {
        amazonS3 = amazonS3Template.getAmazonS3Client();
    }

    public URL upload(InputStream originalInputStream, String contentType, Long contentLength) throws IOException {
        InputStream originalImage = new BufferedInputStream(originalInputStream);
        if (originalImage.markSupported()) originalImage.mark(Integer.MAX_VALUE);

        final byte[] image = IOUtils.toByteArray(originalImage);
        final MediaType mediaType = MediaType.parse(contentType);
        final String name = createFileName(image, mediaType);

        originalImage.reset();

        final ObjectMetadata metadata = createMetadata(contentType, contentLength);
        final PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET, name, originalImage, metadata).withCannedAcl(CannedAccessControlList.PublicRead);

        amazonS3.putObject(putObjectRequest);

        return amazonS3.getUrl(BUCKET, name);
    }

    public URL upload(InputStream originalInputStream, String name, String contentType, Long contentLength) throws IOException {
        InputStream originalImage = new BufferedInputStream(originalInputStream);
        if (originalImage.markSupported()) originalImage.mark(Integer.MAX_VALUE);

        final byte[] image = IOUtils.toByteArray(originalImage);
        final MediaType mediaType = MediaType.parse(contentType);

        originalImage.reset();

        final ObjectMetadata metadata = createMetadata(contentType, contentLength);
        final PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET, name, originalImage, metadata).withCannedAcl(CannedAccessControlList.PublicRead);

        amazonS3.putObject(putObjectRequest);

        return amazonS3.getUrl(BUCKET, name);
    }

    public InputStream get(String name){
        return amazonS3.getObject(BUCKET, name).getObjectContent();
    }

    public String getImageUrl(String name) {
        return amazonS3.getUrl(BUCKET, name).toString();
    }
}
