package com.zamrad.service.fileuploader;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.net.MediaType;
import com.zamrad.configuration.aws.AmazonS3Template;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Component
public class PhotoUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoUploader.class);

    private final AmazonS3Template amazonS3Template;
    private static final String BUCKET = "zamrad-photos-dev";

    @Autowired
    public PhotoUploader(AmazonS3Template amazonS3Template) {
        this.amazonS3Template = amazonS3Template;
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

        final AmazonS3 amazonS3Client = amazonS3Template.getAmazonS3Client();
        amazonS3Client.putObject(putObjectRequest);
        
        return amazonS3Client.getUrl(BUCKET, name);
    }

    public URL upload(InputStream originalInputStream, String name, String contentType, Long contentLength) throws IOException {
        InputStream originalImage = new BufferedInputStream(originalInputStream);
        if (originalImage.markSupported()) originalImage.mark(Integer.MAX_VALUE);

        final byte[] image = IOUtils.toByteArray(originalImage);
        final MediaType mediaType = MediaType.parse(contentType);

        originalImage.reset();

        final ObjectMetadata metadata = createMetadata(contentType, contentLength);
        final PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET, name, originalImage, metadata).withCannedAcl(CannedAccessControlList.PublicRead);

        final AmazonS3 amazonS3Client = amazonS3Template.getAmazonS3Client();
        amazonS3Client.putObject(putObjectRequest);

        return amazonS3Client.getUrl(BUCKET, name);
    }

    public InputStream get(String name){
        final AmazonS3 amazonS3Client = amazonS3Template.getAmazonS3Client();
        return amazonS3Client.getObject(BUCKET,name).getObjectContent();
    }

    private ObjectMetadata createMetadata(String contentType, Long contentLength) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(contentLength);
        objectMetadata.setContentType(contentType);
        return objectMetadata;
    }

    private String createFileName(byte[] image, MediaType mediaType) {
        String filename = DigestUtils.shaHex(image);
        return String.format("%s/%s/%s.%s",
                filename.substring(0, 2),
                filename.substring(2, 4),
                filename,
                getExtensionForMediaType(mediaType));
    }

    private String getExtensionForMediaType(MediaType mediaType) {
        if (mediaType == MediaType.JPEG) {
            return "jpg";
        }
        if (mediaType == MediaType.PNG) {
            return "png";
        }
        if (mediaType == MediaType.GIF) {
            return "gif";
        }
        return "octets";
    }
}
