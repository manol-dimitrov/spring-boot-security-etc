package com.zamrad.service.photos;

import com.amazonaws.util.IOUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PhotoUtils {
    private static final int MAXIMUM_UPLOAD_CONTENT_LENGTH = 5_000_000;
    private static final int MAXIMUM_NUMBER_OF_IMAGES = 10;

    static List<String> getImageFormats(List<InputStream> originalInputStreams, List<InputStream> newInputStreams) {
        List<String> imageFormats = new ArrayList<>();

        for (InputStream inputStream : originalInputStreams) {
            InputStream newInputStream = checkContentLength(inputStream);
            newInputStreams.add(newInputStream);
            imageFormats.add(getImageFormat(newInputStream));
        }

        return imageFormats;
    }

    public static InputStream checkContentLength(InputStream inputStream) {
        InputStream newInputStream = new BufferedInputStream(inputStream);
        if (newInputStream.markSupported()) newInputStream.mark(Integer.MAX_VALUE);

        long contentLength;
        try {
            contentLength = IOUtils.toByteArray(newInputStream).length;
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve image content length: " + e.getMessage());

        }

        try {
            newInputStream.reset();
        } catch (IOException e) {
            throw new RuntimeException("Could not reset the original input stream: " + e.getMessage());
        }

        return newInputStream;
    }

    public static InputStream resetInputStream(InputStream inputStream){
        InputStream newInputStream = new BufferedInputStream(inputStream);
        if (newInputStream.markSupported()) newInputStream.mark(Integer.MAX_VALUE);

        try {
            newInputStream.reset();
        } catch (IOException e) {
            throw new RuntimeException("Could not reset the original input stream: " + e.getMessage());
        }

        return newInputStream;
    }

    public static String getImageFormat(InputStream inputStream) {
        try {
            ImageInputStream imageInputStream = ImageIO.createImageInputStream(inputStream);
            Iterator iterator = ImageIO.getImageReaders(imageInputStream);
            if (!iterator.hasNext()) {
                inputStream.reset();
                return null;
            }

            ImageReader reader = (ImageReader) iterator.next();

            String format = reader.getFormatName().toLowerCase();

            inputStream.reset();
            imageInputStream.close();

            return format;
        } catch (IOException ex) {
            throw new RuntimeException("Cannot create image input stream cache: " + ex.getMessage());
        }
    }
}
