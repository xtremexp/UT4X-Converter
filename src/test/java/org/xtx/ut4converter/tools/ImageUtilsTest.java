package org.xtx.ut4converter.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

class ImageUtilsTest {

    @Test
    void testImageConversion() throws IOException {

        IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new com.realityinteractive.imageio.tga.TGAImageReaderSpi());
        registry.registerServiceProvider(new net.nikr.dds.DDSImageReaderSpi());

        Path pngFile = Files.createTempFile("test", ".png");

        try {
            // Test .pcx -> .png
            BufferedImage img = ImageIO.read(Objects.requireNonNull(ImageUtilsTest.class.getResource("/textures/warer3.pcx")));
            pngFile = Files.createTempFile("test", ".png");
            Assertions.assertTrue(ImageIO.write(img, "png", pngFile.toFile()));

            // Test .dds -> .png
            img = ImageIO.read(Objects.requireNonNull(ImageUtilsTest.class.getResource("/textures/UC2Texture.dds")));
            pngFile = Files.createTempFile("test", ".png");
            Assertions.assertTrue(ImageIO.write(img, "png", pngFile.toFile()));
        } finally {
            pngFile.toFile().delete();
        }
    }

}