package com.zamrad.service.photos;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An image scaler implementation using the ImgScalr library.
 */
@Component
public class ImageRescaler {

    public void rescale(InputStream src, Target... targets) throws IOException {
        BufferedImage srcImage = ImageIO.read(src);

        if (srcImage == null) {
            throw new InvalidImageException("The original input stream cannot be converted to an image!");
        }

        try {
            for (Target target : targets) {
                switch (target.resolution) {
                    case PROFILE_THUMBNAIL:
                        rescale(srcImage, target.dst, 375, 272);
                        break;
                    case ARTIST_SQUARE_THUMBNAIL:
                        rescale(srcImage, target.dst, 99, 99);
                    default:
                        throw new IllegalArgumentException("Unable to rescale image to: " + target.resolution.name());
                }
            }
        } finally {
            dispose(srcImage);
        }
    }

    private void rescale(BufferedImage srcImage, OutputStream dst, int width, int height) throws IOException {
        BufferedImage croppedSrcImage = null;
        BufferedImage dstImage = null;
        try {
            BufferedImage srcImageRef = srcImage;
            if (srcImage.getWidth() > srcImage.getHeight()) {
                int trimWidth = srcImage.getWidth() - srcImage.getHeight();
                croppedSrcImage = Scalr.crop(srcImage, trimWidth / 2, 0, srcImage.getHeight(), srcImage.getHeight());
                srcImageRef = croppedSrcImage;
            } else if (srcImage.getWidth() < srcImage.getHeight()) {
                int trimHeight = srcImage.getHeight() - srcImage.getWidth();
                croppedSrcImage = Scalr.crop(srcImage, 0, trimHeight / 2, srcImage.getWidth(), srcImage.getWidth());
                srcImageRef = croppedSrcImage;
            }

            dstImage = Scalr.resize(srcImageRef, Scalr.Mode.FIT_EXACT, width, height);
            writeAsRGBJpeg(dstImage, dst);
        } finally {
            dispose(croppedSrcImage);
            dispose(dstImage);
        }
    }

    private void writeAsRGBJpeg(BufferedImage input, OutputStream dst) throws IOException {
        BufferedImage rgbImage = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_RGB);
        try {
            rgbImage.getGraphics().drawImage(input, 0, 0, Color.WHITE, null);
            ImageIO.write(rgbImage, "jpg", dst);
        } finally {
            dispose(rgbImage);
        }
    }

    private void dispose(BufferedImage image) {
        if (image != null) {
            image.flush();
        }
    }

    static final class Target {
        final ImageResolution resolution;
        final OutputStream dst;

        Target(ImageResolution resolution, OutputStream dst) {
            this.resolution = checkNotNull(resolution);
            this.dst = checkNotNull(dst);
        }
    }
}
