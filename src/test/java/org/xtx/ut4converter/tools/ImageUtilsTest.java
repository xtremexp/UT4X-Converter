package org.xtx.ut4converter.tools;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ImageUtilsTest {

    @Test
    void testImageConversion() throws IOException {

        IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new com.realityinteractive.imageio.tga.TGAImageReaderSpi());
        registry.registerServiceProvider(new net.nikr.dds.DDSImageReaderSpi());

        Path pngFile = Files.createTempFile("test", ".png");
        Path bmpFile = Files.createTempFile("test", ".bmp");

        try {
            // Test .pcx -> .png
            BufferedImage img = ImageIO.read(Objects.requireNonNull(ImageUtilsTest.class.getResource("/textures/warer3.pcx")));
            Assertions.assertTrue(ImageIO.write(img, "png", pngFile.toFile()));

            // Test .dds -> .png
            img = ImageIO.read(Objects.requireNonNull(ImageUtilsTest.class.getResource("/textures/UC2Texture.dds")));
            pngFile = Files.createTempFile("test", ".png");
            Assertions.assertTrue(ImageIO.write(img, "png", pngFile.toFile()));

            // Test .bmp -> resave as.bmp
            // unreal 2 exports in a weird bmp format that can't be imported in UT3 editor
            // resaving file with java makes import work
            img = ImageIO.read(Objects.requireNonNull(ImageUtilsTest.class.getResource("/textures/U2Texture.bmp")));
            bmpFile = Files.createTempFile("test", ".bmp");
            Assertions.assertTrue(ImageIO.write(img, "bmp", bmpFile.toFile()));
        } finally {
            FileUtils.deleteQuietly(pngFile.toFile());
            FileUtils.deleteQuietly(bmpFile.toFile());
        }
    }

}